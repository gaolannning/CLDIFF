package edu.fdu.se.base.preprocessingfile;

import edu.fdu.se.base.preprocessingfile.data.*;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.jdt.core.dom.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by huangkaifeng on 2018/3/12.
 *
 */
public class TypeNodesTraversalC {

    private DstBodyCheckC dstBodyCheck;

    public TypeNodesTraversalC(){
        dstBodyCheck = new DstBodyCheckC();
    }
    /**
     * curr
     * @param cod             class 节点
     * @param prefixClassName class 节点为止的prefix ， root节点的class prefix 为classname
     */
    public void traverseDstTypeDeclarationCompareSrc(PreprocessedDataC compareResult, PreprocessedTempDataC compareCache, IASTNode cod, String prefixClassName) {
        compareResult.addTypeDeclaration(prefixClassName, cod, prefixClassName);
        int status = dstBodyCheck.checkTypeDeclarationInDst(compareResult, compareCache, cod, prefixClassName);
        if(status == 1|| status==3){
            return;
        }
        List<IASTNode> nodeList = null;
        if(cod instanceof IASTTranslationUnit){             //最外部
            nodeList = Arrays.asList(((IASTTranslationUnit)cod).getChildren());
        }
        else if(cod instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration) cod).getDeclSpecifier() instanceof IASTCompositeTypeSpecifier){      //class,struct
            nodeList = Arrays.asList(((IASTCompositeTypeSpecifier)((IASTSimpleDeclaration) cod).getDeclSpecifier()).getMembers());
        }
        assert(nodeList!=null);
        for (int i = nodeList.size() - 1; i >= 0; i--) {
            IASTNode node = nodeList.get(i);
            if (node instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration)node).getDeclSpecifier() instanceof IASTCompositeTypeSpecifier) {
                IASTSimpleDeclaration cod2 = ( IASTSimpleDeclaration) node;
                String name = ((IASTCompositeTypeSpecifier)((IASTSimpleDeclaration)node).getDeclSpecifier()).getName().toString();
                traverseDstTypeDeclarationCompareSrc(compareResult, compareCache, cod2, prefixClassName + name + ".");
            } else if ( node instanceof IASTFunctionDefinition) {
                dstBodyCheck.checkMethodDeclarationOrInitializerInDst(compareResult, compareCache, node, prefixClassName);
            } else if (node instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) node;
                dstBodyCheck.checkFieldDeclarationInDst(compareResult, compareCache, fd, prefixClassName);
            } else if (node instanceof AnnotationTypeDeclaration) {
                compareCache.addToDstRemoveList(node);
            } else if (node instanceof EnumDeclaration) {
                EnumDeclaration ed = (EnumDeclaration) node;
                dstBodyCheck.checkEnumDeclarationInDst(compareResult,compareCache,ed,prefixClassName);
            } else {
                System.err.println("[ERR]Error:" + node.getClass().getSimpleName());
            }
        }

    }

    /**
     * 设置该cod下的孩子节点为访问，因为father已经被remove了，所以不需要remove
     *
     * @param cod             该节点
     * @param prefixClassName 该节点为止的preix ClassName
     */
    public static void traverseTypeDeclarationSetVisited(PreprocessedTempDataC compareCache, IASTNode cod, String prefixClassName) {
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
            if (n instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration)n).getDeclSpecifier() instanceof IASTCompositeTypeSpecifier) {
                IASTSimpleDeclaration next = (IASTSimpleDeclaration) n;
                String name = ((IASTCompositeTypeSpecifier)(next.getDeclSpecifier())).getName().toString();
                BodyDeclarationPairC bdp = new BodyDeclarationPairC(n, prefixClassName+name+".");
                if (compareCache.srcNodeVisitingMap.containsKey(bdp)) {
                    compareCache.setBodySrcNodeMap(bdp, PreprocessedTempData.BODY_FATHERNODE_REMOVE);
                }
                traverseTypeDeclarationSetVisited(compareCache, next, prefixClassName + name+".");
            }else {
                BodyDeclarationPairC bdp = new BodyDeclarationPairC(n, prefixClassName);
                if (compareCache.srcNodeVisitingMap.containsKey(bdp)) {
                    compareCache.setBodySrcNodeMap(bdp, PreprocessedTempData.BODY_FATHERNODE_REMOVE);
                }
            }
        }
    }


    public void traverseSrcTypeDeclarationInit(PreprocessedDataC compareResult, PreprocessedTempDataC compareCache, IASTNode typeDeclaration, String prefixClassName) {
        List<IASTNode> nodeList = Arrays.asList(typeDeclaration.getChildren());
        compareResult.addTypeDeclaration(prefixClassName, typeDeclaration, prefixClassName);
        BodyDeclarationPairC typeBodyDeclarationPair = new BodyDeclarationPairC(typeDeclaration, prefixClassName);
        compareCache.addToMapBodyName(typeBodyDeclarationPair, prefixClassName);
        compareCache.initBodySrcNodeMap(typeBodyDeclarationPair);
        for (int i = nodeList.size() - 1; i >= 0; i--) {
            IASTNode bodyDeclaration = nodeList.get(i);
            //class or struct
            if (bodyDeclaration instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration)bodyDeclaration).getDeclSpecifier() instanceof IASTCompositeTypeSpecifier) {
                IASTSimpleDeclaration cod2 = (IASTSimpleDeclaration) bodyDeclaration;
                String name = ((IASTCompositeTypeSpecifier)(cod2.getDeclSpecifier())).getName().toString();
                int type = ((IASTCompositeTypeSpecifier)(cod2.getDeclSpecifier())).getKey();    //key = 3表示class,key = 1表示struct
                String subCodName = prefixClassName + (type==3?"class:":"struct:") + name + ".";
                traverseSrcTypeDeclarationInit(compareResult, compareCache, cod2, subCodName);
                continue;
            }
            BodyDeclarationPairC bdp = new BodyDeclarationPairC(bodyDeclaration, prefixClassName);
            compareCache.initBodySrcNodeMap(bdp);
            if (bodyDeclaration instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration)bodyDeclaration).getDeclSpecifier() instanceof IASTEnumerationSpecifier) {
                String name = ((IASTEnumerationSpecifier) ((IASTSimpleDeclaration) bodyDeclaration).getDeclSpecifier()).getName().toString();
                compareCache.addToMapBodyName(bdp, prefixClassName + name);
                continue;
            }
//            if (bodyDeclaration instanceof IASTFunctionDefinition && !((IASTFunctionDefinition) bodyDeclaration).getDeclSpecifier().toString().equals("")) {
            if (bodyDeclaration instanceof IASTFunctionDefinition) {
                IASTFunctionDefinition md = (IASTFunctionDefinition) bodyDeclaration;
                String name = md.getDeclarator().getName().toString();
                compareCache.addToMapBodyName(bdp, prefixClassName + name);
                continue;
            }
            if (bodyDeclaration instanceof IASTSimpleDeclaration && (((IASTSimpleDeclaration)bodyDeclaration).getDeclSpecifier() instanceof IASTSimpleDeclSpecifier||((IASTSimpleDeclaration)bodyDeclaration).getDeclSpecifier() instanceof IASTNamedTypeSpecifier)) {
                IASTSimpleDeclaration fd = (IASTSimpleDeclaration) bodyDeclaration;
                List<IASTDeclarator> mmList = Arrays.asList(fd.getDeclarators());
                for (IASTDeclarator vd : mmList) {
                    compareCache.addToMapBodyName(bdp, prefixClassName + vd.getName().toString());
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


    public void traverseSrcTypeDeclaration2Keys(PreprocessedData compareResult, PreprocessedTempData compareCache, TypeDeclaration typeDeclaration, String prefixClassName) {
        List<BodyDeclaration> nodeList = typeDeclaration.bodyDeclarations();
        BodyDeclarationPair typeBodyDeclarationPair = new BodyDeclarationPair(typeDeclaration, prefixClassName);
        compareResult.entityContainer.addKey(typeBodyDeclarationPair);
        for (int i = nodeList.size() - 1; i >= 0; i--) {
            BodyDeclaration bodyDeclaration = nodeList.get(i);
            if (bodyDeclaration instanceof TypeDeclaration) {
                TypeDeclaration cod2 = (TypeDeclaration) bodyDeclaration;
                String subCodName = prefixClassName + cod2.getName().toString() + ".";
                traverseSrcTypeDeclarationInit(compareResult, compareCache, cod2, subCodName);
                continue;
            }
            BodyDeclarationPair bdp = new BodyDeclarationPair(bodyDeclaration, prefixClassName);
            compareResult.entityContainer.addKey(bdp);
        }
    }




}
