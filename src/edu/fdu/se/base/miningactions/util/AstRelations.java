package edu.fdu.se.base.miningactions.util;


import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.Tree;
import edu.fdu.se.base.common.Global;
import edu.fdu.se.base.links.MyRange;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntityDesc;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTranslationUnit;
import org.eclipse.cdt.internal.core.model.TranslationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class AstRelations {

	public static boolean isFatherXXXStatement(Action a,int astNodeType) {
		Tree parentTree = (Tree) a.getNode().getParent();
		int type = parentTree.getAstNode().getNodeType();
		if (astNodeType == type) {
			return true;
		}
		return false;
	}

	public static boolean isFatherXXXStatement(Tree node,int astNodeType){
		int type =((Tree) node.getParent()).getAstNode().getNodeType();
		if (astNodeType == type) {
			return true;
		}
		return false;
	}



	public static MyRange getMyRange(Tree tree, int treeType){
		Integer[] range =  tree.getRange();

		int start = range[0];
		int end = range[1];
		MyRange myRange = null;
		if(treeType == ChangeEntityDesc.StageITreeType.SRC_TREE_NODE){
			myRange = new MyRange(start,end,ChangeEntityDesc.StageITreeType.SRC_TREE_NODE);
		}else{
			myRange = new MyRange(start,end,ChangeEntityDesc.StageITreeType.SRC_TREE_NODE);
		}
		return myRange;
	}

	public static String getLocationString(ITree tree){
		Object node = ((Tree)tree).getNode();
		return Global.util.getLocationString(node);
	}

}
