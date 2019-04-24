package edu.fdu.se.lang;

import edu.fdu.se.base.preprocessingfile.FilePairPreDiff;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedData;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedTempData;
import org.eclipse.jdt.core.dom.BodyDeclaration;

import java.util.List;

public interface Util {
    Object getSrcCu(PreprocessedData data);
    Object getDstCu(PreprocessedData data);
    Object parseCu(String path);
    Object parseCu(byte[] raw);

    int getLineNumber(Object cu,int num);
    int getStartPosition(Object node);
    int getNodeLength(Object node);

    void removeAllSrcComments(PreprocessedTempData tempData, Object cu, List<Integer> lineList);
    void removeAllDstComments(PreprocessedTempData tempData, Object cu, List<Integer> lineList);

    List<Object> getChildrenFromCu(Object cu);
    int compareTwoFile(FilePairPreDiff preDiff,PreprocessedTempData tempData, PreprocessedData data);
//    int getNode(BodyDeclarationPair bd);
    String BodyDeclarationPairToString(BodyDeclarationPair pair);
//    void removeSrcRemovelList(Object o, List<Integer> lineList,PreprocessedTempData data);
//    void removeDstRemovelList(Object o, List<Integer> lineList,PreprocessedTempData data);

    boolean isTypeDeclaration(Object o);
    boolean isMethodDeclaration(Object o);
    String getMethodName(Object o);
}
