package edu.fdu.se.base.generatingactions;

import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import edu.fdu.se.base.common.Global;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.c.ICASTDesignator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCapture;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.jdt.core.dom.ASTNode;


import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by huangkaifeng on 2018/1/23.
 *
 */
public class JavaParserVisitorC  extends ASTVisitor {


    private int treeType;

    public JavaParserVisitorC(int treeType) {
        super();
        this.treeType = treeType;
    }

    public JavaParserVisitorC() {
        super();
    }

//    @Override
//    public void preVisit(IASTNode n) {
//        pushNode(n, getLabel(n));
//    }

    private boolean inRemoveList(IASTNode node){
        for(IASTNode n: Global.removal){
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
        pushNode(node,getLabel((node)));
        return 3;
    }

    protected String getLabel(IASTNode n) {
        if (n instanceof IASTName) return ((IASTName) n).toString();
        if (n instanceof IASTTranslationUnit) return "*TranslationUnit*";
        if (n instanceof Modifier) return n.toString();
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

    public static int getNodeTypeId(IASTNode n){
        // To Do
        return 1;
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
        push(type, typeName, label, n.getFileLocation().getNodeOffset(), n.getFileLocation().getNodeLength(), n);
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
