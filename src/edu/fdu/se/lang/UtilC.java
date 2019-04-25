package edu.fdu.se.lang;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.tree.AbstractTree;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.Tree;
import edu.fdu.se.base.common.Global;
import edu.fdu.se.base.miningactions.Body.MatchClass;
import edu.fdu.se.base.miningactions.Body.MatchEnum;
import edu.fdu.se.base.miningactions.Body.MatchFieldDeclaration;
import edu.fdu.se.base.miningactions.Body.MatchMethod;
import edu.fdu.se.base.miningactions.bean.MiningActionData;
import edu.fdu.se.base.miningactions.statement.*;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntity;
import edu.fdu.se.base.preprocessingfile.FilePairPreDiff;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedData;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedTempData;
import edu.fdu.se.lang.generatingactions.CParserVisitor;
import edu.fdu.se.lang.parser.CDTParserFactory;
import edu.fdu.se.lang.parser.JDTParserFactory;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTranslationUnit;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNewExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTUsingDirective;
import org.eclipse.cdt.internal.core.model.FunctionDeclaration;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UtilC implements Util{
    @Override
    public IASTTranslationUnit getSrcCu(PreprocessedData data){
        return (IASTTranslationUnit) data.getSrcCu();
    }
    @Override
    public IASTTranslationUnit getDstCu(PreprocessedData data){
        return (IASTTranslationUnit) data.getDstCu();
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
                return i+1;
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
    public int getPositionFromLine(Object o,int line){
        IASTTranslationUnit cu = (IASTTranslationUnit)o;
        String[] s= cu.getRawSignature().split("\n");
        int[] lineCnt = new int[s.length];
        for(int i = 0;i<s.length;i++){
            lineCnt[i] = s[i].length()+1;
        }
        if(line>s.length){
            return -1;
        }
        int cnt = 0;
        for(int i = 0;i<line;i++){
            cnt += lineCnt[i];
        }
        return cnt;
    }

    @Override
    public int getNodeTypeId(Object o){
        IASTNode node = (IASTNode)o;
        return CParserVisitor.getNodeTypeId(node);
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
    public String BodyDeclarationToString(Object o) {
        IASTNode node = (IASTNode)o;
        return node.getRawSignature().toString();
    }

    @Override
    public boolean isIf(Object o){
        if(getNodeTypeId(o)==CParserVisitor.IF_STATEMENT){
            return true;
        }
        return false;
    }

    @Override
    public boolean isTypeDeclaration(Object o){
        if(o instanceof IASTTranslationUnit||(o instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration) o).getDeclSpecifier() instanceof IASTCompositeTypeSpecifier)){
            return true;
        }
        return false;
    }

    @Override
    public boolean isMethodDeclaration(Object o){
        if(o instanceof IASTFunctionDefinition){
            return true;
        }
        return false;
    }

    @Override
    public boolean isClassInstanceCreation(Object o){
        if(o instanceof CPPASTNewExpression){
            return true;
        }
        return false;
    }

    @Override
    public boolean isFieldDeclaration(Object o){
        IASTNode n = (IASTNode)o;
        if(n instanceof IASTSimpleDeclaration && (((IASTSimpleDeclaration)n).getDeclSpecifier() instanceof IASTSimpleDeclSpecifier ||((IASTSimpleDeclaration)n).getDeclSpecifier() instanceof IASTNamedTypeSpecifier)){
            return true;
        }
        return false;
    }

    @Override
    public boolean isEnumDeclaration(Object o){
        IASTNode n = (IASTNode)o;
        if(n instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration)n).getDeclSpecifier() instanceof IASTEnumerationSpecifier){
            return true;
        }
        return false;
    }

    @Override
    public boolean isMethodInvocation(Object o){
        IASTNode n = (IASTNode)o;
        if(n instanceof CPPASTFunctionCallExpression){
            return true;
        }
        return false;
    }

    @Override
    public boolean isSingleVariableDeclaration(Object o){
        IASTNode n = (IASTNode)o;
        if(n instanceof IASTParameterDeclaration){
            return true;
        }
        return false;
    }

    @Override
    public boolean isLiteral(Tree tree){
        return getNodeTypeId(tree.getNode()) == CParserVisitor.NAME
                || tree.getNode().getClass().getSimpleName().endsWith("Literal");
    }

    @Override
    public boolean isCompilationUnit(Object o){
        IASTNode n = (IASTNode)o;
        if(n instanceof IASTTranslationUnit){
            return true;
        }
        return false;
    }

    @Override
    public boolean isBlock(Object o){
        IASTNode n = (IASTNode)o;
        if(n instanceof IASTCompoundStatement){
            return true;
        }
        return false;
    }

    @Override
    public boolean isSwitchCase(Object o){
        IASTNode n = (IASTNode)o;
        if(n instanceof IASTCaseStatement){
            return true;
        }
        return false;
    }



    @Override
    public String getMethodName(Object o){
        IASTFunctionDefinition md = (IASTFunctionDefinition) o;
        return md.getDeclarator().getName().toString();
    }

    @Override
    public String getMethodInvocationName(Object o){
        CPPASTFunctionCallExpression mi = (CPPASTFunctionCallExpression)o;
        return mi.getFunctionNameExpression().toString();
    }

    @Override
    public String getClassCreationName(Object o){
        CPPASTNewExpression n = (CPPASTNewExpression)o;
        return n.getImplicitNames()[0].toString();
    }

    @Override
    public String getFieldType(Object o){
        IASTSimpleDeclaration fd = (IASTSimpleDeclaration) o;
        return fd.getDeclSpecifier().toString();
    }

    @Override
    public List<Object> getSingleVariableDeclarations(Object o){
        IASTFunctionDefinition md = (IASTFunctionDefinition)o;
        return Arrays.asList(md.getDeclarator().getChildren());
    }

    @Override
    public String getSingleVariableDeclarationName(Object o){
        IASTParameterDeclaration n = (IASTParameterDeclaration)o;
        return n.getDeclarator().getName().toString();
    }

    @Override
    public String getSingleVariableDeclarationTypeName(Object o){
        IASTParameterDeclaration n = (IASTParameterDeclaration)o;
        return n.getDeclSpecifier().toString();
    }

    @Override
    public Object getMethodType(Object o){
        IASTFunctionDefinition md = (IASTFunctionDefinition)o;
        return md.getDeclSpecifier();
    }

    @Override
    public String getTypeName(Object o){
        IASTSimpleDeclaration sd = (IASTSimpleDeclaration) o;
        return ((IASTCompositeTypeSpecifier)sd.getDeclSpecifier()).getName().toString();
    }

    @Override
    public List<String> getBaseTypeName(Object o){
        IASTSimpleDeclaration sd = (IASTSimpleDeclaration) o;
        List<String> s = new ArrayList<String>();
        List<IASTNode> nodes= Arrays.asList(((ICPPASTCompositeTypeSpecifier)sd.getDeclSpecifier()).getBaseSpecifiers());
        for(IASTNode node:nodes){
            s.add(nodes.toString());
        }
        return s;
    }

//    @Override
    public List<Object> getChildren(Object o){
        IASTNode n = (IASTNode) o;
        return Arrays.asList(n.getChildren());
    }

    @Override
    public List<Object> getFunctionFromType(Object o){
        IASTNode n = (IASTNode) o;
        IASTNode[] nodes = n.getChildren();
        List<Object> rst = new ArrayList<Object>();
        for(IASTNode node :nodes){
            if(node instanceof IASTFunctionDefinition) {
                rst.add(node);
            }
        }
        return rst;
    }

    @Override
    public List<Object> getFieldFromType(Object o){
        IASTNode n = (IASTNode) o;
        IASTNode[] nodes = n.getChildren();
        List<Object> rst = new ArrayList<Object>();
        for(IASTNode node :nodes){
            if(isFieldDeclaration(node)) {
                rst.add(node);
            }
        }
        return rst;
    }


    @Override
    public void preProcess(PreprocessedTempData tempData){
        Global.removal = new ArrayList<Object>();
        Global.removal.addAll(tempData.srcRemovalNodes);
        Global.removal.addAll(tempData.dstRemovalNodes);
    }

    @Override
    public Tree findFafatherNode(ITree node){
        int type;
        Tree curNode = (Tree)node;
        while (true) {
            type = Global.util.getNodeTypeId(curNode.getNode());
            boolean isEnd = false;
            switch (type) {
                case CParserVisitor.TYPE_DECLARATION:
                case CParserVisitor.METHOD_DECLARATION:
                case CParserVisitor.FIELD_DECLARATION:
                case CParserVisitor.ENUM_DECLARATION:
//                case ASTNode.BLOCK:
//                case ASTNode.ASSERT_STATEMENT:
//                case ASTNode.THROW_STATEMENT:
                case CParserVisitor.RETURN_STATEMENT:
                case CParserVisitor.DO_STATEMENT:
                case CParserVisitor.IF_STATEMENT:
                case CParserVisitor.WHILE_STATEMENT:
//                case ASTNode.ENHANCED_FOR_STATEMENT:
                case CParserVisitor.FOR_STATEMENT:
                case CParserVisitor.TRY_STATEMENT:
                case CParserVisitor.SWITCH_STATEMENT:
                case CParserVisitor.SWITCH_CASE:
                case CParserVisitor.CATCH_CLAUSE:
                case CParserVisitor.EXPRESSION_STATEMENT:
//                case ASTNode.VARIABLE_DECLARATION_STATEMENT:
//                case ASTNode.SYNCHRONIZED_STATEMENT:
//                case ASTNode.CONSTRUCTOR_INVOCATION:
//                case ASTNode.SUPER_CONSTRUCTOR_INVOCATION:
                case CParserVisitor.LABELED_STATEMENT:
                    isEnd = true;
                default:break;
            }

            if(isEnd){
                break;
            }
            assert(! (curNode.getParent() instanceof AbstractTree.FakeTree));
//            try {
            curNode = (Tree) curNode.getParent();
//            }catch(Exception e){
//                System.out.println("a");
//            }
        }
        return curNode;
    }

    @Override
    public void matchNodeNewEntity(MiningActionData fp, Action a, Tree queryFather, int treeType, Tree traverseFather){
        int nodeType = Global.util.getNodeTypeId(traverseFather.getNode());
        switch (nodeType) {
            case CParserVisitor.TYPE_DECLARATION:
                MatchClass.matchClassSignatureNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case CParserVisitor.FIELD_DECLARATION:
                MatchFieldDeclaration.matchFieldDeclarationChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case CParserVisitor.METHOD_DECLARATION:
                if (Global.util.getNodeTypeId(((Tree)a.getNode()).getNode()) != CParserVisitor.COMPOUND_STATEMENT) {
                    MatchMethod.matchMethodSignatureChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                }
                break;
            case CParserVisitor.ENUM_DECLARATION:
                MatchEnum.matchEnumDeclarationNewEntity(fp,a,queryFather,treeType,traverseFather);
                break;
            case CParserVisitor.IF_STATEMENT:
                MatchIfElse.matchIfPredicateChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case CParserVisitor.FOR_STATEMENT:
                MatchForStatement.matchForConditionChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case CParserVisitor.WHILE_STATEMENT:
                MatchWhileStatement.matchWhileConditionChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case CParserVisitor.DO_STATEMENT:
                MatchWhileStatement.matchDoConditionChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case CParserVisitor.EXPRESSION_STATEMENT:
                MatchExpressionStatement.matchExpressionChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case CParserVisitor.RETURN_STATEMENT:
                MatchReturnStatement.matchReturnChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case CParserVisitor.CATCH_CLAUSE:
                MatchTry.matchCatchChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case CParserVisitor.SWITCH_STATEMENT:
                MatchSwitch.matchSwitchNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case CParserVisitor.SWITCH_CASE:
                MatchSwitch.matchSwitchCaseNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case CParserVisitor.LABELED_STATEMENT:
                MatchLabeledStatement.matchLabeledStatementNewEntity(fp,a,queryFather,treeType,traverseFather);
                break;
            default:
                break;
        }
    }

    @Override
    public void matchXXXChangeCurEntity(MiningActionData fp, Action a, ChangeEntity changeEntity, int nodeType, Tree traverseFather){
        switch (nodeType) {
            case CParserVisitor.TYPE_DECLARATION:
                MatchClass.matchClassSignatureCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case CParserVisitor.FIELD_DECLARATION:
                MatchFieldDeclaration.matchFieldDeclarationChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
//            case ASTNode.INITIALIZER:
//                break;
            case CParserVisitor.METHOD_DECLARATION:
                if (!isBlock(((Tree) a.getNode()).getNode())) {
                    MatchMethod.matchMethodSignatureChangeCurrEntity(fp, a, changeEntity, traverseFather);
                }
                break;
            case CParserVisitor.ENUM_DECLARATION:
//            case CParserVisitor.ENUM_CONSTANT_DECLARATION:
                MatchEnum.matchEnumDeclarationCurrEntity(fp,a,changeEntity,traverseFather);
                break;
            case CParserVisitor.IF_STATEMENT:
                MatchIfElse.matchIfPredicateChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case CParserVisitor.FOR_STATEMENT:
                MatchForStatement.matchForConditionChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case CParserVisitor.WHILE_STATEMENT:
                MatchWhileStatement.matchWhileConditionChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case CParserVisitor.DO_STATEMENT:
                MatchWhileStatement.matchDoConditionChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
//            case CParserVisitor.ENHANCED_FOR_STATEMENT:
//                MatchForStatement.matchEnhancedForConditionChangeCurrEntity(fp, a, changeEntity, traverseFather);
//                break;
//            case CParserVisitor.VARIABLE_DECLARATION_STATEMENT:
//                MatchVariableDeclarationExpression.matchVariableDeclarationCurrEntity(fp, a, changeEntity, traverseFather);
//                break;
            case CParserVisitor.EXPRESSION_STATEMENT:
                MatchExpressionStatement.matchExpressionChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;

            case CParserVisitor.RETURN_STATEMENT:
                MatchReturnStatement.matchReturnChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
//            case CParserVisitor.ASSERT_STATEMENT:
//                MatchAssert.matchAssertChangeCurrEntity(fp, a, changeEntity, traverseFather);
//                break;
            case CParserVisitor.CATCH_CLAUSE:
                MatchTry.matchCatchChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
//            case CParserVisitor.SYNCHRONIZED_STATEMENT:
//                MatchSynchronized.matchSynchronizedChangeCurrEntity(fp, a, changeEntity, traverseFather);
//                break;
            case CParserVisitor.SWITCH_STATEMENT:
                MatchSwitch.matchSwitchCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case CParserVisitor.SWITCH_CASE:
                MatchSwitch.matchSwitchCaseCurrEntity(fp, a, changeEntity, traverseFather);
                break;
//            case CParserVisitor.SUPER_CONSTRUCTOR_INVOCATION:
//                MatchConstructorInvocation.matchSuperConstructorInvocationCurrEntity(fp,a,changeEntity,traverseFather);
//                break;
//            case CParserVisitor.CONSTRUCTOR_INVOCATION:
//                MatchConstructorInvocation.matchConstructorInvocationCurrEntity(fp,a,changeEntity,traverseFather);
//                break;
            case CParserVisitor.LABELED_STATEMENT:
                MatchLabeledStatement.matchLabeledStatementCurrEntity(fp,a,changeEntity,traverseFather);
                break;
//            case CParserVisitor.THROW_STATEMENT:
//                MatchTry.matchThrowStatementCurrEntity(fp, a, changeEntity, traverseFather);
//                break;
            default:
                break;
        }
    }

    @Override
    public String getLocationString(Object o) {
        String result="";
        IASTNode node = (IASTNode)o;
        while(!(node instanceof ICPPASTTranslationUnit)){
            if(isTypeDeclaration(node)){
                IASTCompositeTypeSpecifier tp  = (IASTCompositeTypeSpecifier) ((IASTSimpleDeclaration)node).getDeclSpecifier();
                result = tp.getName().toString()+"."+ result;
            }
            node = node.getParent();
        }
        return result;
    }

    @Override
    public List<String> getFieldDeclaratorNames(Object o){
        IASTSimpleDeclaration fd = (IASTSimpleDeclaration)o;
        List<IASTDeclarator> list = Arrays.asList(fd.getDeclarators());
        List<String> s = new ArrayList<String>();
        for(IASTDeclarator vd:list){
            s.add(vd.getName().toString());
        }
        return s;
    }

    @Override
    public Object findExpression(Tree tree){
        int flag = 0;
        while (!tree.getNode().getClass().getSimpleName().endsWith("Statement")) {
            tree = (Tree) tree.getParent();
            switch (Global.util.getNodeTypeId(tree.getNode())) {
                // TO ADD
                case CParserVisitor.EQUALS_INITIALIZER:
                case CParserVisitor.FUNCTION_CALL_EXPRESSION:
                case CParserVisitor.NEW_EXPRESSION:
                    flag = 1;
                    break;
            }
            if (flag == 1) {
                return tree.getNode();
            }
        }
        return null;
    }

    @Override
    public int processBigAction(MiningActionData fp,Action a,int type){
        int res = 0;
        switch (type) {
            // 外面
            case CParserVisitor.TYPE_DECLARATION:
                MatchClass.matchClassDeclaration(fp, a);
                break;
            case CParserVisitor.FIELD_DECLARATION:
                MatchFieldDeclaration.matchFieldDeclaration(fp, a);
                break;
            case CParserVisitor.METHOD_DECLARATION:
                MatchMethod.matchMethdDeclaration(fp, a);
                break;
            case CParserVisitor.ENUM_DECLARATION:
                MatchEnum.matchEnum(fp,a);
                break;
            // 里面
            case CParserVisitor.IF_STATEMENT:
                MatchIfElse.matchIf(fp, a);
                break;
            case CParserVisitor.COMPOUND_STATEMENT:
                MatchBlock.matchBlock(fp, a);
                break;
            case CParserVisitor.BREAK_STATEMENT:
                MatchControlStatements.matchBreakStatements(fp,a);
                break;
            case CParserVisitor.CONTINUE_STATEMENT:
                MatchControlStatements.matchContinueStatements(fp,a);
                break;
            case CParserVisitor.RETURN_STATEMENT:
                MatchReturnStatement.matchReturnStatement(fp, a);
                break;
            case CParserVisitor.FOR_STATEMENT:
                //增加for语句
                MatchForStatement.matchForStatement(fp, a);
                break;
            case CParserVisitor.WHILE_STATEMENT:
                //增加while语句
                MatchWhileStatement.matchWhileStatement(fp, a);
                break;
            case CParserVisitor.DO_STATEMENT:
                //增加do while语句
                MatchWhileStatement.matchDoStatement(fp, a);
                break;
            case CParserVisitor.TRY_STATEMENT:
                MatchTry.matchTry(fp, a);
                break;
            case CParserVisitor.CATCH_CLAUSE:
                MatchTry.matchCatchClause(fp,a);
                break;
            case CParserVisitor.SWITCH_STATEMENT:
                MatchSwitch.matchSwitch(fp, a);
                break;
            case CParserVisitor.SWITCH_CASE:
                MatchSwitch.matchSwitchCase(fp, a);
                break;
            case CParserVisitor.LABELED_STATEMENT:
                MatchLabeledStatement.matchLabeledStatement(fp,a);
                break;
            case CParserVisitor.DECLARATION_STATEMENT:
                MatchVariableDeclarationExpression.matchVariableDeclaration(fp, a);
                break;
            default:
                res =1;
                break;
        }
        return  res;
    }

    // To Do
    @Override
    public int getGeneratingExpressionsType(int type){
        int flag = 0;
        switch(type){
            case CParserVisitor.EQUALS_INITIALIZER:
            case CParserVisitor.ASSIGNMENT:
            case CParserVisitor.NEW_EXPRESSION:
            case CParserVisitor.CONDITIONAL_EXPRESSION:
            case CParserVisitor.FIELD_REFERENCE:
            case CParserVisitor.LAMBDA_EXPRESSION:
            case CParserVisitor.FUNCTION_CALL_EXPRESSION:
                flag = 2; break;


            case CParserVisitor.NAME:
            case CParserVisitor.LITERAL_EXPRESSION:
                flag = 1;break;
            default:break;
        }
        return flag;

    }

    @Override
    public void matchBlock(MiningActionData fp, Action a,int type,Tree fatherNode){
        switch (type) {
            case CParserVisitor.SWITCH_STATEMENT:
//                MatchSwitch.matchSwitchCaseNewEntity(fp,a);
                fp.setActionTraversedMap(a);
                break;
            case CParserVisitor.IF_STATEMENT:
                //Pattern 1.2 Match else
                if (fatherNode.getChildPosition(a.getNode()) == 2) {
                    MatchIfElse.matchElse(fp, a);
                }
                fp.setActionTraversedMap(a);
                break;
            case CParserVisitor.TRY_STATEMENT:
                ////Finally块
                if (fatherNode.getChildPosition(a.getNode()) == fatherNode.getChildren().size() - 1) {
                    MatchTry.matchFinally(fp, a);
                }
                fp.setActionTraversedMap(a);
                break;
            default:
                fp.setActionTraversedMap(a);
                break;
        }
    }

}
