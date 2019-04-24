package edu.fdu.se.base.preprocessingfile;

import java.util.*;
import java.util.Map.Entry;

import edu.fdu.se.base.preprocessingfile.data.*;
import edu.fdu.se.javaparser.CDTParserFactory;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.jdt.core.dom.*;

import edu.fdu.se.javaparser.JDTParserFactory;

/**
 * 两个文件 预处理
 * 删除一摸一样的方法
 * 删除一摸一样的field
 * 删除一摸一样的内部类
 * 删除add method
 * 删除remove method
 * 删除内部类中的add / remove method
 * 保留 remove field 和add field 因为需要识别是否是refactor
 *
 * prefx 为 method field等所属的class，如果是内部类A, 那么prfix写到X.X.X.A.为止
 */
public class FilePairPreDiffC {


    public FilePairPreDiffC() {
        preprocessedData = new PreprocessedDataC();
        preprocessedTempData = new PreprocessedTempDataC();
        queue = new LinkedList<>();
    }

    private PreprocessedDataC preprocessedData;
    private PreprocessedTempDataC preprocessedTempData;

    class SrcDstPair{
        TypeDeclaration tpSrc;
        TypeDeclaration tpDst;
    }
    private Queue<SrcDstPair> queue;

    public void initFilePath(String prevPath,String currPath){
//        preprocessedData.srcCu = JDTParserFactory.getCompilationUnit(prevPath);
//        preprocessedData.dstCu = JDTParserFactory.getCompilationUnit(currPath);
        try {
            preprocessedData.srcTu = CDTParserFactory.getTranslationUnit(prevPath);
            preprocessedData.dstTu = CDTParserFactory.getTranslationUnit(currPath);
            preprocessedData.loadTwoTranslationUnits(preprocessedData.srcTu, preprocessedData.dstTu, prevPath, currPath);
        }catch (Exception e){

        }
    }
    public void initFileContent(byte[] prevContent,byte[] currContent){
        try {
            preprocessedData.srcTu = CDTParserFactory.getTranslationUnit(prevContent);
            preprocessedData.dstTu = CDTParserFactory.getTranslationUnit(currContent);
            preprocessedData.loadTwoTranslationUnits(preprocessedData.srcTu, preprocessedData.dstTu, prevContent, currContent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void test(IASTTranslationUnit cu){
        List<IASTNode> bd = Arrays.asList(cu.getChildren());
    }
    public int compareTwoFile() {
        IASTTranslationUnit cuSrc = preprocessedData.srcTu;
        IASTTranslationUnit cuDst = preprocessedData.dstTu;
//        test(cuSrc);
//        fileOutputLog.writeFileBeforeProcess(preprocessedData);
//        if ("true".equals(ProjectProperties.getInstance().getValue(PropertyKeys.DEBUG_PREPROCESSING))) {
//            fileOutputLog = new FileOutputLog(outputDirName);
//
//        }
        preprocessedTempData.removeAllSrcComments(cuSrc, preprocessedData.srcLines);
        preprocessedTempData.removeAllDstComments(cuDst, preprocessedData.dstLines);
//        if(cuSrc.types().size() != cuDst.types().size()){
//            return -1;
//        }
//        for(int i = 0;i<cuSrc.types().size();i++){
//            BodyDeclaration bodyDeclarationSrc = (BodyDeclaration) cuSrc.types().get(i);
//            BodyDeclaration bodyDeclarationDst = (BodyDeclaration) cuDst.types().get(i);
//            if ((bodyDeclarationSrc instanceof TypeDeclaration) && (bodyDeclarationDst instanceof TypeDeclaration)) {
//                SrcDstPair srcDstPair = new SrcDstPair();
//                srcDstPair.tpSrc = (TypeDeclaration) bodyDeclarationSrc;
//                srcDstPair.tpDst = (TypeDeclaration) bodyDeclarationDst;
//                this.queue.offer(srcDstPair);
//            }else{
//                return -1;
//            }
//        }
//        while(queue.size()!=0){
//            SrcDstPair tmp = queue.poll();
//            compare(cuSrc,cuDst,tmp.tpSrc,tmp.tpDst);
//        }
        TypeNodesTraversalC astTraversal = new TypeNodesTraversalC();
//        addSuperClass(tdSrc,preprocessedData.getInterfacesAndFathers());
//        addSuperClass(tdDst,preprocessedData.getInterfacesAndFathers());
        astTraversal.traverseSrcTypeDeclarationInit(preprocessedData, preprocessedTempData, (IASTNode)cuSrc, "");
        astTraversal.traverseDstTypeDeclarationCompareSrc(preprocessedData, preprocessedTempData, (IASTNode)cuDst, "");
        iterateVisitingMap();
        preprocessedTempData.removeSrcRemovalList(cuSrc, preprocessedData.srcLines);
        preprocessedTempData.removeDstRemovalList(cuDst, preprocessedData.dstLines);
        iterateVisitingMap2LoadContainerMap();
        return 0;
    }

//    public void addSuperClass(IASTNode type,List<String> list){
//        List<Type> aa  = ((IASTCompositeTypeSpecifier)((IASTSimpleDeclaration)type).getDeclSpecifier()).get
//        List<ASTNode> modifiers = type.modifiers();
//        for(ASTNode node:modifiers){
//            if(node instanceof Modifier){
//                Modifier modifier = (Modifier)node;
//                if(modifier.toString().equals("abstract")){
//                    list.add("abstract---"+type.getName().toString());
//                }
//            }
//        }
//        if(aa!=null) {
//            for (Type aaa : aa) {
//                list.add("interface---"+aaa.toString());
//            }
//        }
//
//        if(type.getSuperclassType()!=null) {
//            list.add("superclass---"+type.getSuperclassType().toString());
//        }
//    }

//    private void compare(CompilationUnit cuSrc,CompilationUnit cuDst,TypeDeclaration tdSrc,TypeDeclaration tdDst){
//        TypeNodesTraversal astTraversal = new TypeNodesTraversal();
//        addSuperClass(tdSrc,preprocessedData.getInterfacesAndFathers());
//        addSuperClass(tdDst,preprocessedData.getInterfacesAndFathers());
//
//        astTraversal.traverseSrcTypeDeclarationInit(preprocessedData, preprocessedTempData, tdSrc, tdSrc.getName().toString() + ".");
//        astTraversal.traverseDstTypeDeclarationCompareSrc(preprocessedData, preprocessedTempData, tdDst, tdDst.getName().toString() + ".");
//        // 考虑后面的识别 method name变化，这里把remove的注释掉
//        iterateVisitingMap();
//        undeleteSignatureChange();
//        preprocessedTempData.removeSrcRemovalList(cuSrc, preprocessedData.srcLines);
//        preprocessedTempData.removeDstRemovalList(cuDst, preprocessedData.dstLines);
//        iterateVisitingMap2LoadContainerMap();
////        astTraversal.traverseSrcTypeDeclaration2Keys(preprocessedData,preprocessedTempData,tdSrc,tdSrc.getName().toString() + ".");
////        if (fileOutputLog != null) {
////            fileOutputLog.writeFileAfterProcess(preprocessedData);
////        }
//
//    }


    private void iterateVisitingMap() {
        for (Entry<BodyDeclarationPairC, Integer> item : preprocessedTempData.srcNodeVisitingMap.entrySet()) {
            BodyDeclarationPairC bdp = item.getKey();
            int value = item.getValue();
            IASTNode bd = bdp.getBodyDeclaration();
            if (bd instanceof IASTTranslationUnit ||(bd instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration) bd).getDeclSpecifier() instanceof IASTCompositeTypeSpecifier)) {
                switch (value) {
//                    case PreprocessedTempData.BODY_DIFFERENT_RETAIN:
//                    case PreprocessedTempData.BODY_FATHERNODE_REMOVE:
//                        break;
                    case PreprocessedTempData.BODY_INITIALIZED_VALUE:
                        this.preprocessedData.addBodiesDeleted(bdp);
                        this.preprocessedTempData.addToSrcRemoveList(bd);
                        TypeNodesTraversalC.traverseTypeDeclarationSetVisited(preprocessedTempData,  bd, bdp.getLocationClassString());
                        break;
                    case PreprocessedTempData.BODY_SAME_REMOVE:
                        this.preprocessedTempData.addToSrcRemoveList(bd);
                        TypeNodesTraversalC.traverseTypeDeclarationSetVisited(preprocessedTempData,  bd, bdp.getLocationClassString());
                        break;
                }
            }
        }
        for (Entry<BodyDeclarationPairC, Integer> item : preprocessedTempData.srcNodeVisitingMap.entrySet()) {
            BodyDeclarationPairC bdp = item.getKey();
            int value = item.getValue();
            IASTNode bd = bdp.getBodyDeclaration();

            if (!(bd instanceof IASTTranslationUnit ||(bd instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration) bd).getDeclSpecifier() instanceof IASTCompositeTypeSpecifier))) {
                switch (value) {
                    case PreprocessedTempData.BODY_DIFFERENT_RETAIN:
                    case PreprocessedTempData.BODY_FATHERNODE_REMOVE:
                        break;
                    case PreprocessedTempData.BODY_INITIALIZED_VALUE:
                        this.preprocessedData.addBodiesDeleted(bdp);
                        preprocessedTempData.addToSrcRemoveList(bd);
                        break;
                    case PreprocessedTempData.BODY_SAME_REMOVE:
                        preprocessedTempData.addToSrcRemoveList(bd);
                        break;
                }
            }
//            if(bd instanceof MethodDeclaration){
//                MethodDeclaration md = (MethodDeclaration) bd;
////                if(md.getName().toString().equals("create")){
////                    System.out.println("aa");
//                    break;
//                }
//            }
        }
    }

    private void iterateVisitingMap2LoadContainerMap() {
        for (Entry<BodyDeclarationPairC, Integer> item : preprocessedTempData.srcNodeVisitingMap.entrySet()) {
            BodyDeclarationPairC bdp = item.getKey();
            int value = item.getValue();
//            System.out.println(bdp.getBodyDeclaration().toString());
//            System.out.println(bdp.getLocationClassString());
//            System.out.println(value);
            switch (value) {
                case PreprocessedTempData.BODY_DIFFERENT_RETAIN:
                    this.preprocessedData.entityContainer.addKey(bdp);
                    break;
                case PreprocessedTempData.BODY_FATHERNODE_REMOVE:
                case PreprocessedTempData.BODY_INITIALIZED_VALUE:
                case PreprocessedTempData.BODY_SAME_REMOVE:
                    break;
            }
        }
        this.preprocessedData.entityContainer.sortKeys();

    }

    public PreprocessedDataC getPreprocessedData() {
        return preprocessedData;
    }

    public PreprocessedTempDataC getPreprocessedTempData() {
        return preprocessedTempData;
    }


    public void undeleteSignatureChange() {
        List<BodyDeclarationPairC> addTmp = new ArrayList<>();
        for (BodyDeclarationPairC bdpAdd : preprocessedData.getmBodiesAdded()) {
            if (bdpAdd.getBodyDeclaration() instanceof IASTFunctionDefinition) {
                IASTFunctionDefinition md = (IASTFunctionDefinition) bdpAdd.getBodyDeclaration();
                String methodName = md.getDeclarator().getName().toString();
                List<BodyDeclarationPairC> bdpDeleteList = new ArrayList<>();
                for (BodyDeclarationPairC bdpDelete : preprocessedData.getmBodiesDeleted()) {
                    if (bdpDelete.getBodyDeclaration() instanceof IASTFunctionDefinition) {
                        IASTFunctionDefinition md2 = (IASTFunctionDefinition) bdpDelete.getBodyDeclaration();
                        String methodName2 = md2.getDeclarator().getName().toString();
                        if (potentialMethodNameChange(methodName, methodName2)) {
                            bdpDeleteList.add(bdpDelete);
                        }
                    }
                }
                if (bdpDeleteList.size() > 0) {
                    //remove的时候可能会有hashcode相同但是一个是在内部类的情况，但是这种情况很少见，所以暂时先不考虑
                    preprocessedTempData.dstRemovalNodes.remove(bdpAdd.getBodyDeclaration());
                    addTmp.add(bdpAdd);
                    for (BodyDeclarationPairC bdpTmp : bdpDeleteList) {
                        this.preprocessedTempData.srcRemovalNodes.remove(bdpTmp.getBodyDeclaration());
                        this.preprocessedData.getmBodiesDeleted().remove(bdpTmp);
                        this.preprocessedData.entityContainer.addKey(bdpTmp);
                    }
                }
            }

        }
        for (BodyDeclarationPairC tmp : addTmp) {
            this.preprocessedData.getmBodiesAdded().remove(tmp);
        }
    }

    public boolean potentialMethodNameChange(String name1, String name2) {
        if (name1.length() == 0) return false;
        String tmp;
        if (name1.length() > name2.length()) {
            tmp = name1;
            name1 = name2;
            name2 = tmp;
        }
        int i;
        for (i = 0; i < name1.length(); i++) {
            char ch1 = name1.charAt(i);
            char ch2 = name2.charAt(i);
            if (ch1 != ch2) {
                break;
            }
        }
        double ii = (i * 1.0) / name1.length();
        if (ii > 0.7) {
//            System.out.println("Potential:"+name1+" "+name2);
            return true;
        }
        return false;
    }


}
