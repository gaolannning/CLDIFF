package edu.fdu.se.lang.c.generatingactions;

import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import edu.fdu.se.base.common.Global;
import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.c.ICASTDesignator;
import org.eclipse.cdt.core.dom.ast.cpp.*;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTExpressionStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by huangkaifeng on 2018/1/23.
 *
 */
public class CParserVisitor extends ASTVisitor {


    private int treeType;

    public CParserVisitor(int treeType) {
        super();
        this.treeType = treeType;
    }

    public CParserVisitor() {
        super();
    }

//    @Override
//    public void preVisit(IASTNode n) {
//        pushNode(n, getLabel(n));
//    }

    private boolean inRemoveList(IASTNode node){

        for(Object o: Global.removal){
            IASTNode n = (IASTNode)o;
            if(n == node)
                return true;
        }
        return false;
    }

    //Visit leave Pairs
    //1
    @Override
    public int visit(IASTTranslationUnit node){
        return visit0(node);
    }

    @Override
    public int leave(IASTTranslationUnit node){
        return leave0(node);
    }

    //2
    @Override
    public int 	visit(IASTArrayModifier node) {
        return visit0(node);
    }

    @Override
    public int 	leave(IASTArrayModifier node) {
        return leave0(node);
    }


    //3
    @Override
    public int 	visit(IASTAttribute node) {
        return visit0(node);
    }

    @Override
    public int 	leave(IASTAttribute node) {
        return leave0(node);
    }



    //4
    @Override
    public int 	visit(IASTDeclaration node) {
        return visit0(node);
    }

    @Override
    public int 	leave(IASTDeclaration node) {
        return leave0(node);
    }


    //5
    @Override
    public int 	visit(IASTDeclarator node) {
        return visit0(node);
    }

    @Override
    public int 	leave(IASTDeclarator node) {
        return leave0(node);
    }



    //6
    @Override
    public int 	visit(IASTDeclSpecifier node) {
        return visit0(node);
    }

    @Override
    public int 	leave(IASTDeclSpecifier node) {
        return leave0(node);
    }


    //7
    @Override
    public int 	visit(IASTEnumerationSpecifier.IASTEnumerator node) {
        return visit0(node);
    }

    @Override
    public int 	leave(IASTEnumerationSpecifier.IASTEnumerator node) {
        return leave0(node);
    }


    //8
    @Override
    public int 	visit(IASTExpression node) {
        return visit0(node);
    }

    @Override
    public int 	leave(IASTExpression node) {
        return leave0(node);
    }



    //9
    @Override
    public int 	visit(IASTInitializer node) {
        return visit0(node);
    }

    @Override
    public int 	leave(IASTInitializer node) {
        return leave0(node);
    }



    //10
    @Override
    public int 	visit(IASTName node) {
        return visit0(node);
    }

    @Override
    public int 	leave(IASTName node) {
        return leave0(node);
    }



    //11
    @Override
    public int 	visit(IASTParameterDeclaration node) {
        return visit0(node);
    }

    @Override
    public int 	leave(IASTParameterDeclaration node) {
        return leave0(node);
    }



    //12
    @Override
    public int 	visit(IASTPointerOperator node) {
        return visit0(node);
    }

    @Override
    public int 	leave(IASTPointerOperator node) {
        return leave0(node);
    }



    //13
    @Override
    public int 	visit(IASTProblem node) {
        return visit0(node);
    }

    @Override
    public int 	leave(IASTProblem node) {
        return leave0(node);
    }



    //14
    @Override
    public int 	visit(IASTStatement node) {
        return visit0(node);
    }

    @Override
    public int 	leave(IASTStatement node) {
        return leave0(node);
    }



    //15
    @Override
    public int 	visit(IASTToken node) {
        return visit0(node);
    }

    @Override
    public int 	leave(IASTToken node) {
        return leave0(node);
    }



    //16
    @Override
    public int 	visit(IASTTypeId node) {
        return visit0(node);
    }

    @Override
    public int 	leave(IASTTypeId node) {
        return leave0(node);
    }



    //17
    @Override
    public int 	visit(ICASTDesignator node) {
        return visit0(node);
    }

    @Override
    public int 	leave(ICASTDesignator node) {
        return leave0(node);
    }



    //18
    @Override
    public int 	visit(ICPPASTCapture node) {
        return visit0(node);
    }

    @Override
    public int 	leave(ICPPASTCapture node) {
        return leave0(node);
    }



    //19
    @Override
    public int 	visit(ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier node) {
        return visit0(node);
    }

    @Override
    public int 	leave(ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier node) {
        return leave0(node);
    }


    //20
    @Override
    public int 	visit(ICPPASTNamespaceDefinition node) {
        return visit0(node);
    }

    @Override
    public int 	leave(ICPPASTNamespaceDefinition node) {
        return leave0(node);
    }



    //21
    @Override
    public int 	visit(ICPPASTTemplateParameter node) {
        return visit0(node);
    }

    @Override
    public int 	leave(ICPPASTTemplateParameter node) {
        return leave0(node);
    }






    public int leave0(IASTNode node){
        popNode();
        return 3;
    }

    public int visit0(IASTNode node){
        if(inRemoveList(node)){
            return 1;
        }
        if(node.getFileLocation() == null)
            return 1;
        pushNode(node,getLabel((node)));
        return 3;
    }

    protected String getLabel(IASTNode n) {
        if (n instanceof IASTName) return ((IASTName) n).toString();
        if (n instanceof IASTTranslationUnit) return "*TranslationUnit*";
        if (n instanceof IASTLiteralExpression) return String.valueOf(((IASTLiteralExpression) n).getValue());
        if (n instanceof IASTBinaryExpression) return String.valueOf(((IASTBinaryExpression)n).getOperator());
        if(n instanceof  IASTUnaryExpression) return String.valueOf(((IASTUnaryExpression) n).getOperator());
//        if (n instanceof StringLiteral) return ((StringLiteral) n).getEscapedValue();
//        if (n instanceof NumberLiteral) return ((NumberLiteral) n).getToken();
//        if (n instanceof CharacterLiteral) return ((CharacterLiteral) n).getEscapedValue();
//        if (n instanceof BooleanLiteral) return ((BooleanLiteral) n).toString();
//        if (n instanceof InfixExpression) return ((InfixExpression) n).getOperator().toString();
//        if (n instanceof PrefixExpression) return ((PrefixExpression) n).getOperator().toString();
//        if (n instanceof PostfixExpression) return ((PostfixExpression) n).getOperator().toString();
//        if (n instanceof Assignment) return ((Assignment) n).getOperator().toString();
//        if (n instanceof TextElement) return n.toString();
//        if (n instanceof TagElement) return ((TagElement) n).getTagName();
        return "";
    }
    public static final int UNKNOWN = 0;
    public static final int TYPE_DECLARATION = 1;
    public static final int METHOD_DECLARATION = 2;
    public static final int FIELD_DECLARATION = 3;
    public static final int ENUM_DECLARATION = 4;
    public static final int RETURN_STATEMENT = 5;
    public static final int DO_STATEMENT = 6;
    public static final int IF_STATEMENT = 7;
    public static final int WHILE_STATEMENT = 8;
    public static final int FOR_STATEMENT = 9;
    public static final int TRY_STATEMENT = 10;
    public static final int SWITCH_STATEMENT = 11;
    public static final int SWITCH_CASE = 12;
    public static final int CATCH_CLAUSE = 13;
    public static final int EXPRESSION_STATEMENT = 14;
    public static final int LABELED_STATEMENT = 15;
    public static final int BLOCK_SCOPE = 16;
    public static final int DECLARATION_STATEMENT = 17;
    public static final int COMPOUND_STATEMENT = 18;
    public static final int NAME = 19;
    public static final int BREAK_STATEMENT = 20;
    public static final int CONTINUE_STATEMENT = 21;
    public static final int CAST_EXPRESSION = 22;
    public static final int EQUALS_INITIALIZER = 23;
    public static final int FUNCTION_CALL_EXPRESSION = 24;
    public static final int NEW_EXPRESSION = 25;
    public static final int CONSTRUCTOR_INITIALIZER = 26;
    public static final int BINARY_EXPRESSION = 27;
    public static final int ASSIGNMENT = 28;
    public static final int CONDITIONAL_EXPRESSION = 29;
    public static final int FIELD_REFERENCE = 30;
    public static final int LAMBDA_EXPRESSION = 31;
    public static final int LITERAL_EXPRESSION = 32;
    public static final int UNARY_EXPRESSION = 33;

    public static int getNodeTypeId(IASTNode n){
//        if(n instanceof IASTCompoundStatement){
//            int i = 1;
//        }
        // To Do
        if (n instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration)n).getDeclSpecifier() instanceof IASTCompositeTypeSpecifier){
            return TYPE_DECLARATION;
        }
        if(n instanceof IASTFunctionDefinition){
            return METHOD_DECLARATION;
        }
        if (n instanceof IASTSimpleDeclaration && (((IASTSimpleDeclaration)n).getDeclSpecifier() instanceof IASTSimpleDeclSpecifier||((IASTSimpleDeclaration)n).getDeclSpecifier() instanceof IASTNamedTypeSpecifier)){
            return FIELD_DECLARATION;
        }
        if (n instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration)n).getDeclSpecifier() instanceof IASTEnumerationSpecifier){
            return ENUM_DECLARATION;
        }
        if(n instanceof IASTReturnStatement){
            return RETURN_STATEMENT;
        }
        if(n instanceof IASTDoStatement){
            return DO_STATEMENT;
        }
        if(n instanceof IASTIfStatement){
            return IF_STATEMENT;
        }
        if(n instanceof IASTWhileStatement){
            return WHILE_STATEMENT;
        }
        if(n instanceof IASTForStatement){
            return FOR_STATEMENT;
        }
        if(n instanceof ICPPASTTryBlockStatement){
            return TRY_STATEMENT;
        }
        if(n instanceof IASTSwitchStatement){
            return SWITCH_STATEMENT;
        }
        if(n instanceof IASTCaseStatement){
            return SWITCH_CASE;
        }
        if(n instanceof ICPPASTCatchHandler){
            return CATCH_CLAUSE;
        }
        if(n instanceof IASTExpressionStatement){
            return EXPRESSION_STATEMENT;
        }
        if(n instanceof IASTLabelStatement){
            return LABELED_STATEMENT;
        }
        if(n instanceof  ICPPBlockScope){
            return  BLOCK_SCOPE;
        }
        if(n instanceof CPPASTDeclarationStatement){
            return DECLARATION_STATEMENT;
        }
        if(n instanceof CPPASTCompoundStatement){
            return COMPOUND_STATEMENT;
        }
        if(n instanceof IName){
            return NAME;
        }
        if(n instanceof IASTBreakStatement){
            return BREAK_STATEMENT;
        }
        if(n instanceof IASTContinueStatement){
            return CONTINUE_STATEMENT;
        }
        if(n instanceof CASTExpressionStatement) {
            return CAST_EXPRESSION;
        }
        if(n instanceof CPPASTEqualsInitializer){
            return EQUALS_INITIALIZER;
        }
        if(n instanceof IASTFunctionCallExpression){
            return FUNCTION_CALL_EXPRESSION;
        }
        if(n instanceof  ICPPASTNewExpression){
            return NEW_EXPRESSION;
        }
        if(n instanceof  ICPPASTNewExpression){
            return NEW_EXPRESSION;
        }
        if(n instanceof CPPASTConstructorInitializer){
            return CONSTRUCTOR_INITIALIZER;
        }
        if(n instanceof IASTBinaryExpression){
            if(((IASTBinaryExpression) n).getOperator() == 17){
                return ASSIGNMENT;
            }
            return BINARY_EXPRESSION;
        }
        if(n instanceof IASTConditionalExpression){
            return CONDITIONAL_EXPRESSION;
        }
        if(n instanceof IASTFieldReference){
            return FIELD_REFERENCE;
        }
        if(n instanceof CPPASTLambdaExpression){
            return LAMBDA_EXPRESSION;
        }
        if(n instanceof IASTLiteralExpression) {
            return LITERAL_EXPRESSION;
        }
        if(n instanceof IASTUnaryExpression) {
            return UNARY_EXPRESSION;
        }

        return UNKNOWN;
    }

//    @Override
//    public boolean visit(TagElement e) {
//        return true;
//    }

//    @Override
//    public boolean visit(QualifiedName name) {
//        return false;
//    }

//    @Override
//    public boolean visit(MethodInvocation methodInvocation){
////        System.out.println(methodInvocation.toString());
////        if(methodInvocation.getName()!=null)
////            System.out.println("Method Name:"+methodInvocation.getName().toString());
////        if(methodInvocation.getExpression()!=null)
////            System.out.println("Expression:"+methodInvocation.getExpression().toString()+" "+methodInvocation.getExpression().getClass().getSimpleName());
////        if(methodInvocation.arguments()!=null)
////            System.out.println("Arguments:"+methodInvocation.arguments().toString());
////        System.out.println();
//        return true;
//    }


    //    @Override
//    public void postVisit(ASTNode n) {
//        popNode();
//    }
//
    protected TreeContext context = new TreeContext();

    private Deque<ITree> trees = new ArrayDeque<>();


    public TreeContext getTreeContext() {
        return context;
    }
    //
    protected void pushNode(IASTNode n, String label) {
        int type = getNodeTypeId(n);
        String typeName = n.getClass().getSimpleName();
        if(n.getFileLocation() != null) {
            push(type, typeName, label, n.getFileLocation().getNodeOffset(), n.getFileLocation().getNodeLength(), n);
        }
        else{
            System.out.println("anomaly found"+ label);
        }
    }
    //
    private void push(int type, String typeName, String label, int startPosition, int length, IASTNode node) {
        ITree t = context.createTree(type, label, node);
        t.setPos(startPosition);
        Tree tree = (Tree) t;
        tree.setTreeSrcOrDst(this.treeType);
        t.setLength(length);
        if (trees.isEmpty())
            context.setRoot(t);
        else {
            ITree parent = trees.peek();
            t.setParentAndUpdateChildren(parent);
        }

        trees.push(t);
    }
    private void push(int type, String typeName, String label, int startPosition, int length) {
        ITree t = context.createTree(type, label, typeName);
        t.setPos(startPosition);
        t.setLength(length);

        if (trees.isEmpty())
            context.setRoot(t);
        else {
            ITree parent = trees.peek();
            t.setParentAndUpdateChildren(parent);
        }

        trees.push(t);
    }

    //    protected ITree getCurrentParent() {
//        return trees.peek();
//    }
//
    protected void popNode() {
        trees.pop();
    }

    //    protected void pushFakeNode(EntityType n, int startPosition, int length) {
//        int type = -n.ordinal(); // Fake types have negative types (but does it matter ?)
//        String typeName = n.name();
//        push(type, typeName, "", startPosition, length);
//    }
}

