package edu.fdu.se.base.links.linkbean;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.actions.model.Update;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.Tree;
import edu.fdu.se.base.common.Global;
import edu.fdu.se.base.generatingactions.JavaParserVisitorC;
import edu.fdu.se.base.miningactions.util.MyList;
import edu.fdu.se.base.miningchangeentity.base.StatementPlusChangeEntity;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedData;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedDataC;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNewExpression;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangkaifeng on 2018/4/7.
 */
public class StmtData extends LinkBean {

    public List<String> variableLocal;

    public List<String> variableField;

    public List<String> methodInvocation;

    public List<String> classCreation;


    public StmtData(StatementPlusChangeEntity ce, PreprocessedDataC preprocessedData) {
        this.variableLocal = new MyList<>();
        this.methodInvocation = new MyList<>();
        this.variableField = new MyList<>();
        this.classCreation = new MyList<>();
        if (ce.clusteredActionBean.curAction instanceof Move) {
            parseMove(ce.clusteredActionBean.curAction, preprocessedData);
        } else {
            parseNonMove(ce.clusteredActionBean.actions, preprocessedData);
        }
    }

    public int isContainSameVar(StmtData stmtData) {
        int flagA = 0;
        int flagB = 0;
        for (String tmp : stmtData.variableLocal) {
            if (this.variableLocal.contains(tmp)) {
                flagA++;
            }
        }
        for (String tmp : stmtData.variableField) {
            if (this.variableField.contains(tmp)) {
                flagB++;
            }
        }
        if (flagA != 0 && flagB != 0) {
            return 3;
        }
        if (flagA != 0 && flagA == 0) {
            return 1;
        }
        if (flagA == 0 && flagB != 0) {
            return 2;
        }
        return 0;
    }


    private void parseMove(Action a, PreprocessedDataC preprocessedData) {
        Tree tree = (Tree) a.getNode();
        List<Tree> simpleNames = new ArrayList<>();
        for (ITree tmp : tree.preOrder()) {
            Tree t = (Tree) tmp;
            if (JavaParserVisitorC.getNodeTypeId(t.getAstNodeC()) == JavaParserVisitorC.NAME
                    || t.getAstNodeC().getClass().getSimpleName().endsWith("Literal")) {
                simpleNames.add(t);
            }
        }
        for (Tree aa : simpleNames) {
            if (JavaParserVisitorC.getNodeTypeId(aa.getAstNodeC()) == JavaParserVisitorC.NAME
                    || aa.getAstNode().getClass().getSimpleName().endsWith("Literal")) {
                IASTNode exp = findExpression(tree);
                if (exp == null || !(exp instanceof CPPASTFunctionCallExpression)) {
                    if (preprocessedData.prevCurrFieldNames.contains(tree.getLabel())) {
                        this.variableField.add(tree.getLabel());
                    } else {
                        variableLocal.add(tree.getLabel());
                    }
                    continue;
                }
                if (isMethodInvocationName((CPPASTFunctionCallExpression) exp, tree.getLabel())) {
                    methodInvocation.add(tree.getLabel());
                }
            }
        }
    }

    private void parseNonMove(List<Action> actions, PreprocessedDataC preprocessedData) {
        for (Action a : actions) {
            Tree tree = (Tree) a.getNode();
            String updateVal = null;
            boolean updateFlag = false;
            if (a instanceof Update) {
                updateVal = ((Update) a).getValue();
                updateFlag = true;
            }
            ITree dstNode = Global.ced.mad.getMappedDstOfSrcNode(tree);

            if (JavaParserVisitorC.getNodeTypeId(tree.getAstNodeC()) == JavaParserVisitorC.NAME
                    || tree.getAstNodeC().getClass().getSimpleName().endsWith("Literal")) {
                IASTNode exp = findExpression(tree);
                boolean flag = true;
                if (exp != null && exp instanceof CPPASTFunctionCallExpression) {
                    if (isMethodInvocationName((CPPASTFunctionCallExpression) exp, tree.getLabel())) {
                        if (updateVal != null) {
                            methodInvocation.add(updateVal);
                        }
                        methodInvocation.add(tree.getLabel());
                        flag = false;
                    }
                }
                if (exp != null && exp instanceof CPPASTNewExpression) {
                    if (isClassCreationName((CPPASTNewExpression) exp, tree.getLabel())) {
                        this.classCreation.add(tree.getLabel());
                        flag = false;
                    }
                }
                if (flag)
                    if (preprocessedData.prevCurrFieldNames.contains(tree.getLabel())) {
                        variableField.add(tree.getLabel());
                    } else {
                        variableLocal.add(tree.getLabel());
                    }
                if (updateVal != null) {
                    if (preprocessedData.prevCurrFieldNames.contains(updateVal)) {
                        variableField.add(updateVal);
                    } else {
                        variableLocal.add(updateVal);
                    }
                }

            }
            if (updateFlag) {
                Tree dstTree = (Tree) dstNode;
                if (JavaParserVisitorC.getNodeTypeId(dstTree.getAstNodeC()) == JavaParserVisitorC.NAME
                        || dstTree.getAstNodeC().getClass().getSimpleName().endsWith("Literal")) {
                    IASTNode exp = findExpression(dstTree);
                    if (exp != null && exp instanceof CPPASTFunctionCallExpression) {
                        if (isMethodInvocationName((CPPASTFunctionCallExpression) exp, updateVal)) {
                            if (updateVal != null) {
                                methodInvocation.add(updateVal);
                            }
                        }
                    }
                }
            }

        }
    }


    private IASTNode findExpression(Tree tree) {
        int flag = 0;
        while (!tree.getAstNodeC().getClass().getSimpleName().endsWith("Statement")) {
            tree = (Tree) tree.getParent();
            switch (JavaParserVisitorC.getNodeTypeId(tree.getAstNodeC())) {
                // TO ADD
                case JavaParserVisitorC.EQUALS_INITIALIZER:
                case JavaParserVisitorC.FUNCTION_CALL_EXPRESSION:
                case JavaParserVisitorC.NEW_EXPRESSION:
//                case ASTNode.NORMAL_ANNOTATION:
//                case ASTNode.MARKER_ANNOTATION:
//                case ASTNode.SINGLE_MEMBER_ANNOTATION:
//                case ASTNode.ARRAY_CREATION:
//                case ASTNode.ARRAY_INITIALIZER:
//                case ASTNode.ASSIGNMENT:
//                case ASTNode.BOOLEAN_LITERAL:
//                case ASTNode.CAST_EXPRESSION:
//                case ASTNode.CHARACTER_LITERAL:
//                case ASTNode.CLASS_INSTANCE_CREATION:
//                case ASTNode.CONDITIONAL_EXPRESSION:
//                case ASTNode.CREATION_REFERENCE:
//                case ASTNode.EXPRESSION_METHOD_REFERENCE:
//                case ASTNode.FIELD_ACCESS:
//                case ASTNode.INFIX_EXPRESSION:
//                case ASTNode.INSTANCEOF_EXPRESSION:
//                case ASTNode.LAMBDA_EXPRESSION:
//                case ASTNode.METHOD_INVOCATION:
//                case ASTNode.SIMPLE_NAME:
//                case ASTNode.QUALIFIED_NAME:
//                case ASTNode.NULL_LITERAL:
//                case ASTNode.NUMBER_LITERAL:
//                case ASTNode.PARENTHESIZED_EXPRESSION:
//                case ASTNode.POSTFIX_EXPRESSION:
//                case ASTNode.PREFIX_EXPRESSION:
//                case ASTNode.STRING_LITERAL:
//                case ASTNode.SUPER_FIELD_ACCESS:
//                case ASTNode.SUPER_METHOD_INVOCATION:
//                case ASTNode.SUPER_METHOD_REFERENCE:
//                case ASTNode.THIS_EXPRESSION:
//                case ASTNode.TYPE_LITERAL:
//                case ASTNode.TYPE_METHOD_REFERENCE:
//                case ASTNode.VARIABLE_DECLARATION_EXPRESSION:
                    flag = 1;
                    break;
            }
            if (flag == 1) {
                return tree.getAstNodeC();
            }
        }
        return null;
    }

//    private void setInfixExpression(InfixExpression infixExpression, List<String> methodInvocationSet, List<String> varNameSet){
//        ASTNode leftOp = infixExpression.getLeftOperand();
//        ASTNode rightOp = infixExpression.getRightOperand();
//        List<ASTNode> tmp = new ArrayList<>();
//        tmp.add(leftOp);
//        tmp.add(rightOp);
////        traverseASTNodeList(tmp,methodInvocationSet,varNameSet);
//    }

//    public void traverseASTNodeList(List<ASTNode> list,List<String> methodInvocationSet,List<String> varNameSet) {
//        for (int i = 0; i < list.size(); i++) {
//            ASTNode tmp = list.get(i);
//            if(tmp.getNodeType() == ASTNode.METHOD_INVOCATION) {
//                setMethodInvocation((MethodInvocation)tmp,methodInvocationSet,varNameSet);
//            }else if(tmp.getNodeType() == ASTNode.INFIX_EXPRESSION) {
//                setInfixExpression((InfixExpression)tmp,methodInvocationSet,varNameSet);
//            }else {
//                varNameSet.add(tmp.toString());
//            }
//        }
//    }
    @Deprecated
    public boolean isClassCreationName(ClassInstanceCreation classInstanceCreation, String clazzName) {
        String clazz = classInstanceCreation.getType().toString();
        if (clazzName.equals(clazz)) {
            return true;
        }
        return false;
    }

    public boolean isClassCreationName(CPPASTNewExpression classInstanceCreation, String clazzName) {
        String clazz = classInstanceCreation.getImplicitNames()[0].toString();
        if (clazzName.equals(clazz)) {
            return true;
        }
        return false;
    }


    private boolean isMethodInvocationName(CPPASTFunctionCallExpression methodInvocation, String methodName) {
        String methodName1 = methodInvocation.getFunctionNameExpression().toString();
        if (methodName.equals(methodName1)) {
            return true;
        }
//        methodInvocationSet.add(methodName);
//        Expression exp = methodInvocation.getExpression();
//        if(exp!=null){
//            if(exp instanceof ThisExpression){
//                //this
//            }else if(exp instanceof SimpleName){
//                // 应该是field
//                varNameSet.add(exp.toString());
//            }
//        }
//        List arguments = methodInvocation.arguments();
//        for (int i = 0; i < arguments.size(); i++) {
//            ASTNode tmp = (ASTNode) arguments.get(i);
//            if(tmp.getNodeType() == ASTNode.METHOD_INVOCATION) {
////                setMethodInvocation((MethodInvocation)tmp,methodInvocationSet,varNameSet);
//            }else if(tmp.getNodeType() == ASTNode.INFIX_EXPRESSION) {
////                setInfixExpression((InfixExpression)tmp,methodInvocationSet,varNameSet);
//            }else {
//                varNameSet.add(tmp.toString());
//            }
//        }

//        if(arguments!=null) {
//            traverseASTNodeList(arguments, methodInvocationSet, varNameSet);
//        }
        return false;

    }


    @Deprecated
    private boolean isMethodInvocationName(MethodInvocation methodInvocation, String methodName) {
        String methodName1 = methodInvocation.getName().toString();
        if (methodName.equals(methodName1)) {
            return true;
        }
//        methodInvocationSet.add(methodName);
//        Expression exp = methodInvocation.getExpression();
//        if(exp!=null){
//            if(exp instanceof ThisExpression){
//                //this
//            }else if(exp instanceof SimpleName){
//                // 应该是field
//                varNameSet.add(exp.toString());
//            }
//        }
//        List arguments = methodInvocation.arguments();
//        for (int i = 0; i < arguments.size(); i++) {
//            ASTNode tmp = (ASTNode) arguments.get(i);
//            if(tmp.getNodeType() == ASTNode.METHOD_INVOCATION) {
////                setMethodInvocation((MethodInvocation)tmp,methodInvocationSet,varNameSet);
//            }else if(tmp.getNodeType() == ASTNode.INFIX_EXPRESSION) {
////                setInfixExpression((InfixExpression)tmp,methodInvocationSet,varNameSet);
//            }else {
//                varNameSet.add(tmp.toString());
//            }
//        }

//        if(arguments!=null) {
//            traverseASTNodeList(arguments, methodInvocationSet, varNameSet);
//        }
        return false;

    }


    private List<String> addCommonNames(MyList<String> a, MyList<String> b) {
        List<String> result = new ArrayList<>();
        for (String tmp : a) {
            if (b.contains(tmp)) {
                result.add(tmp);
            }
        }
        return result;
    }

    /**
     * Bean Move情况
     *
     * @param moveAction
     */

    @Deprecated
    public void move(Action moveAction) {
        Tree moveTree = (Tree) moveAction.getNode();
        for (ITree t : moveTree.preOrder()) {
            Tree tree = (Tree) t;
            if (tree.getAstNode().getNodeType() == ASTNode.METHOD_INVOCATION) {
                ASTNode methodInvocation = tree.getAstNode();
//                setMethodInvocation((MethodInvocation) methodInvocation,this.methodNames,this.variables);
            }
        }
    }

    @Deprecated
    public void move(Tree moveTree) {
        for (ITree t : moveTree.preOrder()) {
            Tree tree = (Tree) t;
            if (tree.getAstNode().getNodeType() == ASTNode.METHOD_INVOCATION) {
                ASTNode methodInvocation = tree.getAstNode();
//                setMethodInvocation((MethodInvocation) methodInvocation,this.methodNames,this.variables);
            }
        }
    }
}
