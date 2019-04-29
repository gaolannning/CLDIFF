package edu.fdu.se.lang.c.preprocess;

import edu.fdu.se.base.common.Global;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedData;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedTempData;
import edu.fdu.se.cldiff.CUtil;
import edu.fdu.se.lang.DstBodyCheckC;
import edu.fdu.se.lang.TypeNodesTraversal;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;

import java.util.Arrays;
import java.util.List;

/**
 * Created by huangkaifeng on 2018/3/12.
 *
 */
public class TypeNodesTraversalC implements TypeNodesTraversal{

    private DstBodyCheckC dstBodyCheck;

    public TypeNodesTraversalC(){
        dstBodyCheck = new DstBodyCheckC();
    }
    /**
     * curr
     * @param cod             class 节点
     * @param prefixClassName class 节点为止的prefix ， root节点的class prefix 为classname
     */
    public void traverseDstTypeDeclarationCompareSrc(PreprocessedData compareResult, PreprocessedTempData compareCache, IASTNode cod, String prefixClassName) {
        String curName = null;
        List<IASTNode> nodeList = null;
        if(cod instanceof  IASTTranslationUnit) {
            nodeList = Arrays.asList(((IASTTranslationUnit)cod).getChildren());
            curName = "Root.";
        } else{
            nodeList = Arrays.asList(((IASTCompositeTypeSpecifier)((IASTSimpleDeclaration) cod).getDeclSpecifier()).getMembers());
            IASTSimpleDeclaration sd = (IASTSimpleDeclaration) cod;
            int type = ((IASTCompositeTypeSpecifier)(sd.getDeclSpecifier())).getKey();
            curName = prefixClassName+(type==3?"class:":"struct:")+((IASTCompositeTypeSpecifier)sd.getDeclSpecifier()).getName().toString()+".";
        }
        compareResult.addTypeDeclaration(curName, cod, curName);
        int status = dstBodyCheck.checkTypeDeclarationInDst(compareResult, compareCache, cod, prefixClassName);
        if(status == 1|| status==3){
            return;
        }
        assert(nodeList!=null);
        for (int i = nodeList.size() - 1; i >= 0; i--) {
            IASTNode node = nodeList.get(i);
            if (CUtil.isTypeDeclaration(node)) {
                IASTSimpleDeclaration cod2 = ( IASTSimpleDeclaration) node;
                String kind = CUtil.getTypeKind(node);
                String name = kind+CUtil.getTypeName(node);
                traverseDstTypeDeclarationCompareSrc(compareResult, compareCache, cod2, curName);
            } else if ( node instanceof IASTFunctionDefinition) {
                dstBodyCheck.checkMethodDeclarationOrInitializerInDst(compareResult, compareCache, node, curName);
            } else if (node instanceof IASTSimpleDeclaration) {
                IASTSimpleDeclaration node1 = (IASTSimpleDeclaration) node;
                if (node1.getDeclSpecifier() instanceof IASTEnumerationSpecifier) {
                    dstBodyCheck.checkEnumDeclarationInDst(compareResult, compareCache, node, curName);
                } else if(CUtil.isFieldDeclaration(node1)){
                    dstBodyCheck.checkFieldDeclarationInDst(compareResult, compareCache, node, curName);
                }
                else{
                        System.err.println("[ERR]Error:" + node.getClass().getSimpleName());
                }
            } else if(node instanceof ICPPASTUsingDirective) {
                //To Do
            }
            else if(node instanceof  ICPPASTVisibilityLabel){
                // To Do
            }
             else {
                 int line = Global.util.getLineNumber(compareResult.getDstCu(),node.getFileLocation().getNodeOffset());
                System.err.println("[ERR]Error:" + node.getClass().getSimpleName());
            }
        }

    }

    /**
     * 设置该cod下的孩子节点为访问，因为father已经被remove了，所以不需要remove
     *
     * @param o             该节点
     * @param prefixClassName 该节点为止的preix ClassName
     */
    @Override
    public void traverseTypeDeclarationSetVisited(PreprocessedTempData compareCache, Object o, String prefixClassName) {
        IASTNode cod = (IASTNode)o;
        List<IASTNode> tmpList = null;
        if(cod instanceof IASTTranslationUnit){             //最外部
            tmpList = Arrays.asList(((IASTTranslationUnit)cod).getChildren());
        }
        else if(cod instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration) cod).getDeclSpecifier() instanceof IASTCompositeTypeSpecifier){      //class,struct
            tmpList = Arrays.asList(((IASTCompositeTypeSpecifier)((IASTSimpleDeclaration) cod).getDeclSpecifier()).getMembers());
        }

        assert(tmpList!=null);
        for (int m = tmpList.size() - 1; m >= 0; m--) {
            IASTNode n = tmpList.get(m);
            if (CUtil.isTypeDeclaration(n)) {
                IASTSimpleDeclaration next = (IASTSimpleDeclaration) n;
                String name = CUtil.getTypeName(n);
                BodyDeclarationPair bdp = new BodyDeclarationPair(n, prefixClassName+name+".");
                if (compareCache.srcNodeVisitingMap.containsKey(bdp)) {
                    compareCache.setBodySrcNodeMap(bdp, PreprocessedTempData.BODY_FATHERNODE_REMOVE);
                }
                traverseTypeDeclarationSetVisited(compareCache, next, prefixClassName + name+".");
            }else {
                BodyDeclarationPair bdp = new BodyDeclarationPair(n, prefixClassName);
                if (compareCache.srcNodeVisitingMap.containsKey(bdp)) {
                    compareCache.setBodySrcNodeMap(bdp, PreprocessedTempData.BODY_FATHERNODE_REMOVE);
                }
            }
        }
    }


    public void traverseSrcTypeDeclarationInit(PreprocessedData compareResult, PreprocessedTempData compareCache, IASTNode typeDeclaration, String prefixClassName) {
        List<IASTNode> nodeList = null;
        String curName = null;
        if(typeDeclaration instanceof  IASTTranslationUnit) {
            nodeList = Arrays.asList(typeDeclaration.getChildren());
            curName = "Root.";
        } else{
            nodeList = Arrays.asList(((IASTCompositeTypeSpecifier)((IASTSimpleDeclaration)typeDeclaration).getDeclSpecifier()).getMembers());
            IASTSimpleDeclaration sd = (IASTSimpleDeclaration) typeDeclaration;
            int type = ((IASTCompositeTypeSpecifier)(sd.getDeclSpecifier())).getKey();
            curName = prefixClassName+(type==3?"class:":"struct:")+((IASTCompositeTypeSpecifier)sd.getDeclSpecifier()).getName().toString()+".";
        }
        compareResult.addTypeDeclaration(curName, typeDeclaration, curName);
        BodyDeclarationPair typeBodyDeclarationPair = new BodyDeclarationPair(typeDeclaration, curName);
        compareCache.addToMapBodyName(typeBodyDeclarationPair, curName);
        compareCache.initBodySrcNodeMap(typeBodyDeclarationPair);
        for (int i = nodeList.size() - 1; i >= 0; i--) {
            IASTNode bodyDeclaration = nodeList.get(i);
            //class or struct
            if (bodyDeclaration instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration)bodyDeclaration).getDeclSpecifier() instanceof IASTCompositeTypeSpecifier) {
                IASTSimpleDeclaration cod2 = (IASTSimpleDeclaration) bodyDeclaration;
                String name = CUtil.getTypeName(cod2);
                String type = CUtil.getTypeKind(cod2);    //key = 3表示class,key = 1表示struct
                String subCodName = curName;
                traverseSrcTypeDeclarationInit(compareResult, compareCache, cod2, subCodName);
                continue;
            }

            BodyDeclarationPair bdp = new BodyDeclarationPair(bodyDeclaration, curName);
//            compareCache.initBodySrcNodeMap(bdp);
            if (bodyDeclaration instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration)bodyDeclaration).getDeclSpecifier() instanceof IASTEnumerationSpecifier) {
                String name = ((IASTEnumerationSpecifier) ((IASTSimpleDeclaration) bodyDeclaration).getDeclSpecifier()).getName().toString();
//                BodyDeclarationPairC bdp = new BodyDeclarationPairC(bodyDeclaration, prefixClassName);
//                compareCache.initBodySrcNodeMap(bdp);
                compareCache.initBodySrcNodeMap(bdp);
                compareCache.addToMapBodyName(bdp, curName + name);
                continue;
            }
//            if (bodyDeclaration instanceof IASTFunctionDefinition && !((IASTFunctionDefinition) bodyDeclaration).getDeclSpecifier().toString().equals("")) {
            if (bodyDeclaration instanceof IASTFunctionDefinition) {
                IASTFunctionDefinition md = (IASTFunctionDefinition) bodyDeclaration;
                String name = md.getDeclarator().getName().toString();
//                BodyDeclarationPairC bdp = new BodyDeclarationPairC(bodyDeclaration, prefixClassName);
//                compareCache.initBodySrcNodeMap(bdp);
                compareCache.initBodySrcNodeMap(bdp);
                compareCache.addToMapBodyName(bdp, curName + name);
                continue;
            }
//            if(bodyDeclaration instanceof IASTSimpleDeclaration) {
//                IASTNode node = ((IASTSimpleDeclaration) bodyDeclaration).getDeclSpecifier();
//                int n = 1;
//            }
            if (CUtil.isFieldDeclaration(bodyDeclaration)) {
                IASTSimpleDeclaration fd = (IASTSimpleDeclaration) bodyDeclaration;
                List<IASTDeclarator> mmList = Arrays.asList(fd.getDeclarators());
                if(mmList.size()!=0)
                    compareCache.initBodySrcNodeMap(bdp);
                for (IASTDeclarator vd : mmList) {
//                    BodyDeclarationPairC bdp = new BodyDeclarationPairC(bodyDeclaration, prefixClassName);
//                    compareCache.initBodySrcNodeMap(bdp);
                    compareCache.addToMapBodyName(bdp, curName + vd.getName().toString());
                    compareResult.prevFieldNames.add(vd.getName().toString());
                    compareResult.prevCurrFieldNames.add(vd.getName().toString());
                }
                continue;
            }

//            if (bodyDeclaration instanceof IASTFunctionDefinition && ((IASTFunctionDefinition) bodyDeclaration).getDeclSpecifier().toString().equals("")) {
//                //内部类不会有static
//                IASTFunctionDefinition idd = (IASTFunctionDefinition) bodyDeclaration;
//                String iddStr;
//                if (idd.getDeclSpecifier().contains("static")) {
//                    iddStr = "static";
//                } else {
//                    iddStr = "{";
//                }
//                compareCache.addToMapBodyName(bdp, prefixClassName + iddStr);
//                continue;
//            }
//            if (bodyDeclaration instanceof AnnotationTypeDeclaration) {
//                compareCache.addToSrcRemoveList(bodyDeclaration);
//            }

        }
    }


//    public void traverseSrcTypeDeclaration2Keys(PreprocessedDataC compareResult, PreprocessedTempDataC compareCache, TypeDeclaration typeDeclaration, String prefixClassName) {
//        List<BodyDeclaration> nodeList = typeDeclaration.bodyDeclarations();
//        BodyDeclarationPair typeBodyDeclarationPair = new BodyDeclarationPair(typeDeclaration, prefixClassName);
//        compareResult.entityContainer.addKey(typeBodyDeclarationPair);
//        for (int i = nodeList.size() - 1; i >= 0; i--) {
//            BodyDeclaration bodyDeclaration = nodeList.get(i);
//            if (bodyDeclaration instanceof TypeDeclaration) {
//                TypeDeclaration cod2 = (TypeDeclaration) bodyDeclaration;
//                String subCodName = prefixClassName + cod2.getName().toString() + ".";
//                traverseSrcTypeDeclarationInit(compareResult, compareCache, cod2, subCodName);
//                continue;
//            }
//            BodyDeclarationPair bdp = new BodyDeclarationPair(bodyDeclaration, prefixClassName);
//            compareResult.entityContainer.addKey(bdp);
//        }
//    }




}
