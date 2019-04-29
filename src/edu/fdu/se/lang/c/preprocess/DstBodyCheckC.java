package edu.fdu.se.lang.c.preprocess;

import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedData;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedTempData;
import edu.fdu.se.cldiff.CUtil;
import edu.fdu.se.lang.DstBodyCheck;
import edu.fdu.se.lang.TypeNodesTraversalC;
import org.eclipse.cdt.core.dom.ast.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by huangkaifeng on 2018/4/2.
 *
 */
public class DstBodyCheckC implements DstBodyCheck{

//    public BodyDeclarationPair getExactBodyDeclarationPair(List<BodyDeclarationPairC> bodyDeclarationPairs,Class clazz){
//        for(BodyDeclarationPair bodyDeclarationPair:bodyDeclarationPairs){
//            if(bodyDeclarationPair.getBodyDeclaration().getClass().equals(clazz)){
//                return bodyDeclarationPair;
//            }
//        }
//        return null;
//    }

//    public BodyDeclarationPairC getExactBodyDeclarationPair(List<BodyDeclarationPairC> bodyDeclarationPairs,Class clazz1,Class[] clazz2){
//        for(BodyDeclarationPairC bodyDeclarationPair:bodyDeclarationPairs){
//            if(bodyDeclarationPair.getBodyDeclaration().getClass().equals(clazz1)){
//                if(clazz2 == null)
//                    return bodyDeclarationPair;
//            }
//            if(bodyDeclarationPair.getBodyDeclaration() instanceof  IASTTranslationUnit){
//                return bodyDeclarationPair;
//            }
//            if(bodyDeclarationPair.getBodyDeclaration() instanceof IASTSimpleDeclaration ){
//                for(Class c:clazz2){
//                    Class clazz = ((IASTSimpleDeclaration) bodyDeclarationPair.getBodyDeclaration()).getDeclSpecifier().getClass();
//                    if(((IASTSimpleDeclaration) bodyDeclarationPair.getBodyDeclaration()).getDeclSpecifier().getClass().equals(c)){
//                        return bodyDeclarationPair;
//                    }
//                }
//            }
//        }
//        return null;
//    }

    enum Type{
        TypeDeclaration,FieldDeclaration,FunctionDeclaration,EnumDeclaration
    }

    public BodyDeclarationPair getExactBodyDeclarationPair(List<BodyDeclarationPair> bodyDeclarationPairs, Type type){
        for(BodyDeclarationPair bodyDeclarationPair:bodyDeclarationPairs){
            IASTNode node = (IASTNode) bodyDeclarationPair.getBodyDeclaration();
            switch(type){
                case TypeDeclaration:
                    if(node instanceof IASTTranslationUnit)
                        return  bodyDeclarationPair;
                    if(node instanceof  IASTSimpleDeclaration && ((IASTSimpleDeclaration)node).getDeclSpecifier() instanceof IASTCompositeTypeSpecifier)
                        return bodyDeclarationPair;
                    break;
                case FieldDeclaration:
                    if(CUtil.isFieldDeclaration(node))
                            return bodyDeclarationPair;
                    break;
                case FunctionDeclaration:
                    if(node instanceof  IASTFunctionDefinition)
                        return bodyDeclarationPair;
                    break;
                case EnumDeclaration:
                    if(node instanceof  IASTSimpleDeclaration && ((IASTSimpleDeclaration)node).getDeclSpecifier() instanceof IASTEnumerationSpecifier){
                        return bodyDeclarationPair;
                    }
                    break;
            }
        }
        return null;
    }

    /**
     * visited
     */
    public int checkFieldDeclarationInDst(PreprocessedData compareResult, PreprocessedTempData compareCache, IASTNode fd, String prefix) {

        List<IASTDeclarator> vdList = Arrays.asList(((IASTSimpleDeclaration) fd).getDeclarators());
        for (IASTDeclarator vd : vdList) {
            String key = prefix + vd.getName().toString();
            compareResult.currFieldNames.add(vd.getName().toString());
            compareResult.prevCurrFieldNames.add(vd.getName().toString());
            boolean newFieldFlag = true;
            if (compareCache.srcNodeBodyNameMap.containsKey(key)) {
                List<BodyDeclarationPair> srcBodyPairs = compareCache.srcNodeBodyNameMap.get(key);
                BodyDeclarationPair srcBody = getExactBodyDeclarationPair(srcBodyPairs, Type.FieldDeclaration);
                if(srcBody != null){
                    newFieldFlag = false;
                    if (((IASTNode)srcBody.getBodyDeclaration()).getRawSignature().toString().hashCode() == fd.getRawSignature().toString().hashCode()
                            && srcBody.getLocationClassString().hashCode() == prefix.hashCode()) {
                        compareCache.addToDstRemoveList(fd);
                        compareCache.setBodySrcNodeMap(srcBody, PreprocessedTempData.BODY_SAME_REMOVE);
                        return 1;
                    } else {
                        // variable相同，设置为不删除
                        if (PreprocessedTempData.BODY_SAME_REMOVE != compareCache.getNodeMapValue(srcBody)) {
                            compareCache.setBodySrcNodeMap(srcBody, PreprocessedTempData.BODY_DIFFERENT_RETAIN);
                        }
                        return 2;
                    }
                }else {
                    newFieldFlag = true;
                    System.err.println("[ERROR]");
                }
            }
            if(newFieldFlag){
                //new field
                compareResult.addBodiesAdded(fd, prefix);
                compareCache.addToDstRemoveList(fd);
            }
        }
        return 33;
    }

    /**
     * @param cod             内部类
     * @param prefixClassName classname到cod的name前一个为止
     * @return 1 2
     */
    public int checkTypeDeclarationInDst(PreprocessedData compareResult, PreprocessedTempData compareCache, IASTNode cod, String prefixClassName) {
        String curName = null;
        if(cod instanceof  IASTTranslationUnit) {
            curName = "Root.";
        } else{
            IASTSimpleDeclaration sd = (IASTSimpleDeclaration) cod;
            int type = ((IASTCompositeTypeSpecifier)(sd.getDeclSpecifier())).getKey();
            curName = prefixClassName+(type==3?"class:":"struct:")+((IASTCompositeTypeSpecifier)sd.getDeclSpecifier()).getName().toString()+".";
        }
        String key = curName;
        if (compareCache.srcNodeBodyNameMap.containsKey(key)) {
            List<BodyDeclarationPair> srcNodeList = compareCache.srcNodeBodyNameMap.get(key);
            BodyDeclarationPair srcBody = getExactBodyDeclarationPair(srcNodeList, Type.TypeDeclaration);
            if(srcBody != null) {
                boolean b1 = ((IASTNode)srcBody.getBodyDeclaration()).getRawSignature().toString().hashCode() == cod.getRawSignature().toString().hashCode();
                boolean b2 = curName.hashCode() == srcBody.getLocationClassString().hashCode();
                if (((IASTNode)srcBody.getBodyDeclaration()).getRawSignature().toString().hashCode() == cod.getRawSignature().toString().hashCode()
                        && curName.hashCode() == srcBody.getLocationClassString().hashCode()) {
//                System.out.println(srcBody.getBodyDeclaration().toString());
//                System.out.println(cod.toString());
                    compareCache.addToDstRemoveList(cod);
                    compareCache.setBodySrcNodeMap(srcBody, PreprocessedTempData.BODY_SAME_REMOVE);
                    new TypeNodesTraversalC().traverseTypeDeclarationSetVisited(compareCache, (IASTNode) srcBody.getBodyDeclaration(), curName);
                    return 1;
                } else {
                    compareCache.setBodySrcNodeMap(srcBody, PreprocessedTempData.BODY_DIFFERENT_RETAIN);
                    return 2;
                }
            }else{
                System.err.println("[ERROR]");
            }
        }
        // new class
        compareResult.addBodiesAdded(cod, curName);
        compareCache.addToDstRemoveList(cod);
        return 3;
    }

    public int checkEnumDeclarationInDst(PreprocessedData compareResult, PreprocessedTempData compareCache, IASTNode ed, String prefixClassName){
        String name= ((IASTEnumerationSpecifier)((IASTSimpleDeclaration)ed).getDeclSpecifier()).getName().toString();
        String key = prefixClassName + name;
        if(compareCache.srcNodeBodyNameMap.containsKey(key)){
            List<BodyDeclarationPair> srcNodeList = compareCache.srcNodeBodyNameMap.get(key);
            BodyDeclarationPair srcBody = getExactBodyDeclarationPair(srcNodeList, Type.EnumDeclaration);
            if(srcBody != null) {
                if (((IASTNode)srcBody.getBodyDeclaration()).getRawSignature().toString().hashCode() == ed.getRawSignature().toString().hashCode()
                        && prefixClassName.hashCode() == srcBody.getLocationClassString().hashCode()) {
                    compareCache.addToDstRemoveList(ed);
                    compareCache.setBodySrcNodeMap(srcBody, PreprocessedTempData.BODY_SAME_REMOVE);
                    return 1;
                } else {
//                    MyRange myRange1,myRange2;
//                    int s1, e1,s2,e2;
//                    s1 = srcBody.getBodyDeclaration().getFileLocation().getStartingLineNumber();
//                    e1 = srcBody.getBodyDeclaration().getFileLocation().getEndingLineNumber();
//                    s2 = ed.getFileLocation().getStartingLineNumber();
//                    e2 = ed.getFileLocation().getEndingLineNumber();
//                    myRange1= new MyRange(s1, e1, ChangeEntityDesc.StageITreeType.SRC_TREE_NODE);
//                    myRange2 = new MyRange(s2, e2, ChangeEntityDesc.StageITreeType.SRC_TREE_NODE);
//                    EnumChangeEntityC code = new EnumChangeEntityC(srcBody, ChangeEntityDesc.StageIIOpt.OPT_CHANGE, myRange1,myRange2);
//                    IASTNode fd = (IASTNode) srcBody.getBodyDeclaration();
//                    PreprocessUtil.generateEnumChangeEntityC(code, fd, ed);
//                    if (compareResult.getPreprocessChangeEntity() == null) {
//                        compareResult.setPreprocessChangeEntity(new ArrayList<>());
//                    }
//                    compareResult.getPreprocessChangeEntity().add(code);
//                    compareCache.addToDstRemoveList(ed);
//                    compareCache.setBodySrcNodeMap(srcBody, PreprocessedTempData.BODY_SAME_REMOVE);
                compareCache.setBodySrcNodeMap(srcBody,PreprocessedTempData.BODY_DIFFERENT_RETAIN);
                    return 2;
                }
            }else{
                System.err.println("[ERROR]");
            }
        }
        compareResult.addBodiesAdded(ed,prefixClassName);
        compareCache.addToDstRemoveList(ed);
        return 3;
    }

    /**
     * curr的节点去prev的map里check
     */
    public int checkMethodDeclarationOrInitializerInDst(PreprocessedData compareResult, PreprocessedTempData compareCache, IASTNode bd, String prefixClassName) {
        String methodNameKey = null;
//        if (bd instanceof Initializer) {
//            Initializer idd = (Initializer) bd;
//            methodNameKey = prefixClassName;
//            if (idd.modifiers().contains("static")) {
//                methodNameKey += "static";
//            } else {
//                methodNameKey += "{";
//            }
//        } else if (bd instanceof MethodDeclaration) {
        if (bd instanceof IASTFunctionDefinition) {
            IASTFunctionDefinition md = (IASTFunctionDefinition) bd;
            String name = md.getDeclarator().getName().toString();
            methodNameKey = prefixClassName + name;
//            if(md.getName().toString().equals("setPoolSize")){
//                System.out.print("a");
//            }

        }
//        else if (bd instanceof EnumDeclaration) {
//            EnumDeclaration ed = (EnumDeclaration) bd;
//            methodNameKey = prefixClassName + ed.getName().toString();
//        }
        else {
            System.err.println("[ERR] ---------------------------");
        }

        if (compareCache.srcNodeBodyNameMap.containsKey(methodNameKey)) {
            List<BodyDeclarationPair> srcNodeList = compareCache.srcNodeBodyNameMap.get(methodNameKey);
            boolean findSame = false;
            for (BodyDeclarationPair srcBody : srcNodeList) {
                int hashCode1 = (String.valueOf(bd.getRawSignature().toString().hashCode()) + String.valueOf(prefixClassName.hashCode())).hashCode();
                int hashCode2 = srcBody.hashCode();
                if (srcBody.hashCode() == (String.valueOf(bd.getRawSignature().toString().hashCode()) + String.valueOf(prefixClassName.hashCode())).hashCode()) {
                    compareCache.setBodySrcNodeMap(srcBody, PreprocessedTempData.BODY_SAME_REMOVE);
                    compareCache.addToDstRemoveList(bd);
                    findSame = true;
                    break;
                }
            }
            if (findSame) {
                return 1;
            } else {
                for (BodyDeclarationPair srcBody : srcNodeList) {
                    if (PreprocessedTempData.BODY_SAME_REMOVE != compareCache.getNodeMapValue(srcBody)) {
                        compareCache.setBodySrcNodeMap(srcBody, PreprocessedTempData.BODY_DIFFERENT_RETAIN);
                    }
                }
                return 2;
            }

        } else {
            //new method
            compareResult.addBodiesAdded(bd, prefixClassName);
            compareCache.addToDstRemoveList(bd);
            return 5;
        }
    }

}
