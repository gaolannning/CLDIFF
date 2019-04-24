package edu.fdu.se.lang;

import edu.fdu.se.base.preprocessingfile.FilePairPreDiff;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedData;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedTempData;
import edu.fdu.se.lang.parser.CDTParserFactory;
import edu.fdu.se.lang.parser.JDTParserFactory;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTUsingDirective;
import org.eclipse.cdt.internal.core.model.FunctionDeclaration;

import java.util.Arrays;
import java.util.List;

public class UtilC implements Util{
    @Override
    public IASTTranslationUnit getSrcCu(PreprocessedData data){
        return (IASTTranslationUnit) data.getSrcCu();
    }
    @Override
    public IASTTranslationUnit getDstCu(PreprocessedData data){
        return (IASTTranslationUnit) data.getSrcCu();
    }

    @Override
    public Object parseCu(String path){
        Object o = null;
        try {
            o = CDTParserFactory.getTranslationUnit(path);
        }catch (Exception e){

        }
        assert (o!=null);
        return o;

    }

    @Override
    public Object parseCu(byte[] raw){
        Object o = null;
        try {
            o = CDTParserFactory.getTranslationUnit(raw);
        }catch (Exception e){

        }
        assert (o!=null);
        return o;
    }

    @Override
    public int getLineNumber(Object o,int num){
        assert(o instanceof IASTTranslationUnit);
        IASTTranslationUnit cu = (IASTTranslationUnit) o;
        String[] s= cu.getRawSignature().split("\n");
        int[] lineCnt = new int[s.length];
        for(int i = 0;i<s.length;i++){
            lineCnt[i] = s[i].length()+1;
        }
        int cnt = 0;
        for(int i = 0;i<s.length;i++){
            cnt += lineCnt[i];
            if(cnt>num){
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getStartPosition(Object o){
        IASTNode node =  (IASTNode)o;
        return node.getFileLocation().getNodeOffset();
    }

    @Override
    public int getNodeLength(Object o){
        IASTNode node =  (IASTNode)o;
        return node.getFileLocation().getNodeLength();
    }


    @Override
    public void removeAllSrcComments(PreprocessedTempData tempData, Object o, List<Integer> lineList) {
        IASTTranslationUnit cu = (IASTTranslationUnit) o;
        for(IASTNode item:cu.getChildren()){
            if(item instanceof CPPASTUsingDirective){
                tempData.addToSrcRemoveList(item);
            }
        }

        List<IASTNode> commentList = Arrays.asList(cu.getComments());
        for (int i = commentList.size() - 1; i >= 0; i--) {
            tempData.addToSrcRemoveList(commentList.get(i));
        }
        List<IASTNode> includes = Arrays.asList(cu.getIncludeDirectives());
        for (int i = includes.size() - 1; i >= 0; i--) {
            tempData.addToSrcRemoveList(includes.get(i));
        }
        removeSrcRemovalList(tempData,cu,lineList);
    }
    @Override
    public void removeAllDstComments(PreprocessedTempData tempData, Object o, List<Integer> lineList) {
        IASTTranslationUnit cu = (IASTTranslationUnit) o;
        IASTNode[] tst = cu.getChildren();
        for(IASTNode item:cu.getChildren()){
            if(item instanceof CPPASTUsingDirective){
                tempData.addToDstRemoveList(item);
            }
        }

        List<IASTNode> commentList = Arrays.asList(cu.getComments());
        for (int i = commentList.size() - 1; i >= 0; i--) {
            tempData.addToDstRemoveList(commentList.get(i));
        }
        List<IASTNode> includes = Arrays.asList(cu.getIncludeDirectives());
        for (int i = includes.size() - 1; i >= 0; i--) {
            tempData.addToDstRemoveList(includes.get(i));
        }
        removeDstRemovalList(tempData,cu,lineList);
    }
    private void removeSrcRemovalList(PreprocessedTempData tempData,IASTTranslationUnit cu, List<Integer> lineList){
        for (Object o : tempData.srcRemovalNodes) {
            IASTNode item = (IASTNode) o;
            assert(item.getNodeLocations()[0] instanceof IASTFileLocation);
            tempData.setLinesFlag(lineList, ((IASTFileLocation) item.getNodeLocations()[0]).getStartingLineNumber(),
                    ((IASTFileLocation) item.getNodeLocations()[0]).getStartingLineNumber());
//            ASTRewrite rewriter = ASTRewrite.create(cu);
//            rewriter.remove(item,null);
//            item.setParent(null);


        }
    }
    private void removeDstRemovalList(PreprocessedTempData tempData, IASTTranslationUnit cu, List<Integer> lineList){
        for (Object o : tempData.dstRemovalNodes) {
            IASTNode item = (IASTNode) o;
            assert(item.getNodeLocations()[0] instanceof IASTFileLocation);
            tempData.setLinesFlag(lineList, ((IASTFileLocation) item.getNodeLocations()[0]).getStartingLineNumber(),
                    ((IASTFileLocation) item.getNodeLocations()[0]).getStartingLineNumber());
//            ASTRewrite rewriter = ASTRewrite.create(cu);
//            rewriter.remove(item,null);
//            item.setParent(null);
        }
    }

    @Override
    public List<Object> getChildrenFromCu(Object o) {
        IASTTranslationUnit cu = (IASTTranslationUnit)o;
        return Arrays.asList(cu.getChildren());
    }

    @Override
    public int compareTwoFile(FilePairPreDiff preDiff,PreprocessedTempData tempData,PreprocessedData data){
        IASTTranslationUnit cuSrc = (IASTTranslationUnit) data.getSrcCu();
        IASTTranslationUnit cuDst = (IASTTranslationUnit) data.getDstCu();
        TypeNodesTraversalC astTraversal = new TypeNodesTraversalC();
//        addSuperClass(tdSrc,preprocessedData.getInterfacesAndFathers());
//        addSuperClass(tdDst,preprocessedData.getInterfacesAndFathers());
        astTraversal.traverseSrcTypeDeclarationInit(data, tempData, (IASTNode)cuSrc, "");
        astTraversal.traverseDstTypeDeclarationCompareSrc(data, tempData, (IASTNode)cuDst, "");
        preDiff.iterateVisitingMap();
        preDiff.undeleteSignatureChange();
        removeSrcRemovalList(tempData,cuSrc, data.srcLines);
        removeDstRemovalList(tempData,cuDst, data.dstLines);
        preDiff.iterateVisitingMap2LoadContainerMap();
        return 0;
    }

    @Override
    public String BodyDeclarationPairToString(BodyDeclarationPair pair) {
        String result = pair.getLocationClassString() +" ";
        return result;
    }

    @Override
    public boolean isTypeDeclaration(Object o){
        if(o instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration) o).getDeclSpecifier() instanceof IASTCompositeTypeSpecifier){
            return true;
        }
        return false;
    }

    public boolean isMethodDeclaration(Object o){
        if(o instanceof IASTFunctionDefinition){
            return true;
        }
        return false;
    }

    @Override
    public String getMethodName(Object o){
        IASTFunctionDefinition md = (IASTFunctionDefinition) o;
        return md.getDeclarator().getName().toString();
    }


}
