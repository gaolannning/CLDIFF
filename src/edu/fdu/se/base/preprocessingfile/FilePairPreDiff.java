package edu.fdu.se.base.preprocessingfile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;

import edu.fdu.se.base.common.Global;
import org.eclipse.jdt.core.dom.*;

import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedData;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedTempData;

import edu.fdu.se.lang.TypeNodesTraversal;

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
public class FilePairPreDiff {


    public FilePairPreDiff() {
        preprocessedData = new PreprocessedData();
        preprocessedTempData = new PreprocessedTempData();
//        queue = new LinkedList<>();
    }

    private PreprocessedData preprocessedData;
    private PreprocessedTempData preprocessedTempData;

    class SrcDstPair{
        TypeDeclaration tpSrc;
        TypeDeclaration tpDst;
    }
    private Queue<SrcDstPair> queue;

    public void initFilePath(String prevPath,String currPath){
        preprocessedData.setSrcCu(Global.util.parseCu(prevPath));
        preprocessedData.setDstCu(Global.util.parseCu(currPath));
        preprocessedData.loadTwoCompilationUnits(preprocessedData.getSrcCu(), preprocessedData.getDstCu(), prevPath, currPath);
    }
    public void initFileContent(byte[] prevContent,byte[] currContent){
        try {
            preprocessedData.setSrcCu(Global.util.parseCu(prevContent));
            preprocessedData.setDstCu(Global.util.parseCu(currContent));
            preprocessedData.loadTwoCompilationUnits(preprocessedData.getSrcCu(), preprocessedData.getDstCu(), prevContent, currContent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void test(CompilationUnit cu){
        List<BodyDeclaration> bd = cu.types();
    }
    public int compareTwoFile() {
        Object cuSrc = preprocessedData.getSrcCu();
        Object cuDst = preprocessedData.getDstCu();
//        test(cuSrc);
//        fileOutputLog.writeFileBeforeProcess(preprocessedData);
//        if ("true".equals(ProjectProperties.getInstance().getValue(PropertyKeys.DEBUG_PREPROCESSING))) {
//            fileOutputLog = new FileOutputLog(outputDirName);
//
//        }
        Global.util.removeAllSrcComments(preprocessedTempData,cuSrc, preprocessedData.srcLines);
        Global.util.removeAllDstComments(preprocessedTempData,cuDst, preprocessedData.dstLines);
        return Global.util.compareTwoFile(this,preprocessedTempData,preprocessedData);

//        if(cuSrc.types().size() != cuDst.types().size()){
//            return -1;
//        }
////        if(Global.util.getChildrenFromCu(Global.util))
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
//        return 0;
    }
//    public void addSuperClass(TypeDeclaration type,List<String> list){
//        List<Type> aa  = type.superInterfaceTypes();
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


    public void iterateVisitingMap() {
        for (Entry<BodyDeclarationPair, Integer> item : preprocessedTempData.srcNodeVisitingMap.entrySet()) {
            BodyDeclarationPair bdp = item.getKey();
            int value = item.getValue();
            Object bd = bdp.getBodyDeclaration();
            TypeNodesTraversal traversal= null;
            try {
                Class clazz = Class.forName("edu.fdu.se.lang.TypeNodesTraversal" + Global.lang);
                traversal = (TypeNodesTraversal) clazz.newInstance();
            }catch(Exception e){
                assert(traversal!=null);
            }
            if (Global.util.isTypeDeclaration(bd)) {
                switch (value) {
//                    case PreprocessedTempData.BODY_DIFFERENT_RETAIN:
//                    case PreprocessedTempData.BODY_FATHERNODE_REMOVE:
//                        break;
                    case PreprocessedTempData.BODY_INITIALIZED_VALUE:
                        preprocessedData.addBodiesDeleted(bdp);
                        preprocessedTempData.addToSrcRemoveList(bd);
                        traversal.traverseTypeDeclarationSetVisited(preprocessedTempData, bd, bdp.getLocationClassString());
                        break;
                    case PreprocessedTempData.BODY_SAME_REMOVE:
                        preprocessedTempData.addToSrcRemoveList(bd);
                        traversal.traverseTypeDeclarationSetVisited(preprocessedTempData, (TypeDeclaration) bd, bdp.getLocationClassString());
                        break;
                }
            }
        }
        for (Entry<BodyDeclarationPair, Integer> item : preprocessedTempData.srcNodeVisitingMap.entrySet()) {
            BodyDeclarationPair bdp = item.getKey();
            int value = item.getValue();
            Object bd = bdp.getBodyDeclaration();

            if (!Global.util.isTypeDeclaration(bd)) {
                switch (value) {
                    case PreprocessedTempData.BODY_DIFFERENT_RETAIN:
                    case PreprocessedTempData.BODY_FATHERNODE_REMOVE:
                        break;
                    case PreprocessedTempData.BODY_INITIALIZED_VALUE:
                        preprocessedData.addBodiesDeleted(bdp);
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

    public void iterateVisitingMap2LoadContainerMap() {
        for (Entry<BodyDeclarationPair, Integer> item : preprocessedTempData.srcNodeVisitingMap.entrySet()) {
            BodyDeclarationPair bdp = item.getKey();
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

    public PreprocessedData getPreprocessedData() {
        return preprocessedData;
    }

    public void undeleteSignatureChange() {
        List<BodyDeclarationPair> addTmp = new ArrayList<>();
        for (BodyDeclarationPair bdpAdd : preprocessedData.getmBodiesAdded()) {
            if (Global.util.isMethodDeclaration(bdpAdd.getBodyDeclaration())) {
                Object md = bdpAdd.getBodyDeclaration();
                String methodName = Global.util.getMethodName(md);
                List<BodyDeclarationPair> bdpDeleteList = new ArrayList<>();
                for (BodyDeclarationPair bdpDelete : preprocessedData.getmBodiesDeleted()) {
                    if (Global.util.isMethodDeclaration(bdpDelete.getBodyDeclaration())){
                        Object md2 = bdpAdd.getBodyDeclaration();
                        String methodName2 = Global.util.getMethodName(md2);
                        if (potentialMethodNameChange(methodName, methodName2)) {
                            bdpDeleteList.add(bdpDelete);
                        }
                    }
                }
                if (bdpDeleteList.size() > 0) {
                    //remove的时候可能会有hashcode相同但是一个是在内部类的情况，但是这种情况很少见，所以暂时先不考虑
                    preprocessedTempData.dstRemovalNodes.remove(bdpAdd.getBodyDeclaration());
                    addTmp.add(bdpAdd);
                    for (BodyDeclarationPair bdpTmp : bdpDeleteList) {
                        this.preprocessedTempData.srcRemovalNodes.remove(bdpTmp.getBodyDeclaration());
                        this.preprocessedData.getmBodiesDeleted().remove(bdpTmp);
                        this.preprocessedData.entityContainer.addKey(bdpTmp);
                    }
                }
            }

        }
        for (BodyDeclarationPair tmp : addTmp) {
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
