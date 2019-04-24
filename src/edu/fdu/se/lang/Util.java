package edu.fdu.se.lang;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.Tree;
import edu.fdu.se.base.miningactions.bean.MiningActionData;
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
    int getPositionFromLine(Object cu,int line);

    int getNodeTypeId(Object node);

    @Deprecated
    default boolean typeIdInList(int id,Integer[] list){
        for(int item:list){
            if(id == item){
                return true;
            }
        }
        return false;
    }

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
    boolean isFieldDeclaration(Object o);
    boolean isEnumDeclaration(Object o);
    boolean isMethodInvocation(Object o);
    boolean isClassInstanceCreation(Object o);
    boolean isSingleVariableDeclaration(Object o);
    boolean isLiteral(Tree tree);
    boolean isCompilationUnit(Object o);
    String getMethodName(Object o);
    String getMethodInvocationName(Object o);
    String getClassCreationName(Object o);
    List<String> getFieldDeclaratorNames(Object fd);
    String getFieldType(Object fd);
    List<Object> getSingleVariableDeclarations(Object o);
    String getSingleVariableDeclarationName(Object o);
    String getSingleVariableDeclarationTypeName(Object o);
    Object getMethodType(Object o);


    void preProcess(PreprocessedTempData tempData);
    Tree findFafatherNode(ITree node);
    void matchNodeNewEntity(MiningActionData fp, Action a, Tree queryFather, int treeType, Tree traverseFather);
    String getLocationString(Object node);

    Object findExpression(Tree tree);
    int processBigAction(MiningActionData fp,Action a,int type);

}
