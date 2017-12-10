package edu.fdu.se.astdiff.miningactions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.dom.MethodInvocation;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Delete;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.actions.model.Update;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;

import edu.fdu.se.astdiff.generatingactions.ActionConstants;
import edu.fdu.se.astdiff.generatingactions.ConsolePrint;
import edu.fdu.se.gumtree.MyTreeUtil;

/**
 * Most of our basic work on this java class Input: 4 kinds of edit action,
 * insert,move,update,delete Output: sort of summary of actions
 * 
 * @author huangkaifeng
 *
 */
public class FindPattern {

	public FindPattern(MiningActionBean bean) {
		this.mMiningActionBean = bean;
	}

	private MiningActionBean mMiningActionBean;

	/**
	 * level III insert 操作中的新增方法 ok
	 * 
	 * 
	 * @param a
	 * @return
	 */
	public String matchNewOrDeleteMethod(Action a) {
		String result = null;
		List<Action> actions = new ArrayList<Action>();
		boolean flag = MyTreeUtil.traverseAllChilrenCheckIfSameAction(a, actions);
		if (flag) {
			if (a instanceof Insert) {
				result = "[PATTREN] Add method";
			} else if (a instanceof Delete) {
				result = "[PATTERN] Delete Method";
			}
		} else {
			System.out.println("What? Not New Method?");
		}
		this.mMiningActionBean.setActionTraversedMap(actions);
		return result;

	}

	/**
	 * level III if 操作识别两种 一种是 原来语句包一个if 一种是直接新增if语句 if -> children update 可能
	 * insert 可能 新增if/else if + body是否也是新 2*2 = 4种情况 null 目前没有找到反例
	 * 
	 * @param a
	 * @param type
	 * @return
	 */
	public int matchIf(Action a, String type) {
		String ifOrElseif = "";
		if (AstRelations.isFatherIfStatement(a, this.mMiningActionBean.mDstTree)) {
			ifOrElseif = "else if clause";
		} else {
			ifOrElseif = "if clause";
		}
		String changeType = ActionConstants.getInstanceStringName(a);
		String summary = "[PATTERN] " + changeType + " " + ifOrElseif;
		List<Action> ifSubActions = new ArrayList<Action>();
		boolean flag = MyTreeUtil.traverseAllChilrenCheckIfSameAction(a, ifSubActions);
		boolean nullCheck = AstRelations.isNullCheck(a.getNode(), this.mMiningActionBean.mDstTree);
		this.mMiningActionBean.setActionTraversedMap(ifSubActions);
		if (flag) {
			summary += " and body";
		} else {
			if (a instanceof Insert) {
				summary += " to api calls";
			} else if (a instanceof Delete) {
				summary += " and leave out api calls";
			}

		}
		if (nullCheck) {
			System.out.println("5.Adding a null checker." + summary);
		}

		System.out.println(summary);
		return ifSubActions.size();
	}

	/**
	 * level III precondition father是ifstatement
	 * 
	 * @param a
	 * @return
	 */
	public int matchElse(Action a) {
		String changeType = ActionConstants.getInstanceStringName(a);
		String summary = changeType + " else clause ";
		Tree root = (Tree) a.getNode();
		List<ITree> children = root.getChildren();
		List<Action> result = new ArrayList<Action>();
		boolean flag = MyTreeUtil.traverseAllChilrenCheckIfSameAction(a, result);
		if (flag) {
			summary += "and body";
		} else {
			summary += " to wrap/unwrap api calls";
		}
		this.mMiningActionBean.setActionTraversedMap(result);
		System.out.println(summary);
		return result.size();
	}

	/**
	 * level III
	 * 
	 * @param a
	 * @return
	 */
	public int matchTry(Action a) {
		String summary = "[PATTERN] "+ActionConstants.getInstanceStringName(a);
		List<Action> tryAction = new ArrayList<Action>();
		boolean flag = MyTreeUtil.traverseAllChilrenCheckIfSameAction(a, tryAction);
		if(flag){
			summary += " try catch clause and body";
		} else {
			summary += " try catch clause wrapper";
		}
		this.mMiningActionBean.setActionTraversedMap(tryAction);
		System.out.println(summary);
		return tryAction.size();
	}

	/**
	 * level III VARIABLEDECLARATIONSTATEMENT
	 * 
	 * @param a
	 * @return
	 */
	public int matchVariableDeclaration(Action a) {
		TreeContext con = null;
		if(a instanceof Insert){
			con = this.mMiningActionBean.mDstTree;
		}else if(a instanceof Delete){
			con = this.mMiningActionBean.mSrcTree;
		}
		String summary = "[PATTERN]";
		summary += ActionConstants.getInstanceStringName(a);
		List<Action> subActions = new ArrayList<Action>();
		boolean flag = MyTreeUtil.traverseAllChilrenCheckIfSameAction(a, subActions);
		this.mMiningActionBean.setActionTraversedMap(subActions);
		if (flag) {
			if (AstRelations.isClassCreation(subActions, con)) {
				summary += " object Initializing - variable declaration";
			} else {
				summary += " variable declaration";
			}
		} else {
			System.err.println("Unexpected Condition 7");
		}

		System.out.println(summary);
		return subActions.size();

	}

	/**
	 * level III
	 * 
	 * @param a
	 * @return
	 */
	public int matchExpression(Action a) {
		TreeContext con = null;
		if(a instanceof Insert){
			con = this.mMiningActionBean.mDstTree;
		}else if(a instanceof Delete){
			con = this.mMiningActionBean.mSrcTree;
		}
		String summary = "[PATTERN]";
		summary += ActionConstants.getInstanceStringName(a);
		List<Action> subActions = new ArrayList<Action>();
		boolean flag = MyTreeUtil.traverseAllChilrenCheckIfSameAction(a, subActions);
		this.mMiningActionBean.setActionTraversedMap(subActions);
		if (flag) {
			if (AstRelations.isClassCreation(subActions, con)) {
				summary += " object Initializing - expression assignment";
			} else {
				summary += " expression assignment";
			}
		} else {
			System.err.println("Unexpected Condition 2");
		}

		System.out.println(summary);
		return subActions.size();
	}

	/**
	 * level IV For EnchancedFor
	 * 
	 * @param a
	 * @param treeContext
	 * @return
	 */
	public int matchForPredicate(Action a, TreeContext treeContext) {
		// TODO
		return 0;
	}

	/**
	 * level IV 因为往上找如果是if body那么匹配不是if statement 所以这部分应该就是predicate
	 * 
	 * @param a
	 * @param treeContext
	 * @return
	 */
	public int matchIfPredicate(Action a, TreeContext treeContext, ITree fafafatherNode) {
		// fafafatherNode是if 那么 第一个孩子是if里的内容
		ITree srcParent = null;
		List<Action> allActions = MyTreeUtil.traverseNodeGetSameEditActions(a, fafafatherNode.getChild(0));
		if (a instanceof Insert) {
			if (this.mMiningActionBean.mMapping.getSrc(fafafatherNode) != null) {
				srcParent = this.mMiningActionBean.mMapping.getSrc(fafafatherNode);
				List<Action> tmp = MyTreeUtil.traverseNodeGetAllEditAction(srcParent);
				allActions.addAll(tmp);
			} else {
				System.err.println("ERerererR");
			}
		} else {
			srcParent = fafafatherNode;
		}
		for (Action item : allActions) {
			this.mMiningActionBean.mActionGeneratorBean.getAllActionMap().put(item, 1);
			this.mMiningActionBean.mapIfPredicateAndAction(item.getNode().getParent(), item);
		}
		return allActions.size();
	}

	/**
	 * level IV
	 * 
	 * @param a
	 * @param treeContext
	 * @return
	 */
	public int matchExpressionStatementAndVariableDeclarationStatement(Action a, TreeContext treeContext) {
		String result = null;

		if (AstRelations.ifFatherStatementSame(a, treeContext, StatementConstants.METHODINVOCATION)) {
			ITree tree = a.getNode();
			int pos = tree.getParent().getChildPosition(tree);
			if (pos == 1) {
				System.out.println("2.Calling another method with the same parameters");
				return 1;
			} else if (pos == 0) {
				System.out.println("X.Change variable");
				return 1;
			}
			List<Action> list = MyTreeUtil.traverseNodeChildrenGetSameEditAction(a);
			for (Action item : list) {
				if (item instanceof Insert) {
					this.mMiningActionBean.mActionGeneratorBean.getAllActionMap().put(item, 1);
					if (this.mMiningActionBean.mMapping.getSrc(tree.getParent()) != null) {
						ITree srcParent = this.mMiningActionBean.mMapping.getSrc(tree.getParent());
						this.mMiningActionBean.mapMethodInvocationAndAction(srcParent, item);
					} else {
						System.err.println("ERerererR");
					}
				} else if (item instanceof Update) {
					this.mMiningActionBean.mActionGeneratorBean.getAllActionMap().put(item, 1);
					this.mMiningActionBean.mapMethodInvocationAndAction(item.getNode().getParent(), item);
				} else if (item instanceof Delete) {
					this.mMiningActionBean.mActionGeneratorBean.getAllActionMap().put(item, 1);
					this.mMiningActionBean.mapMethodInvocationAndAction(item.getNode().getParent(), item);
				} else {
					System.err.println("Unexpected Condition 5");
				}

			}
			return list.size();
		} else {
			System.err.println("直接亲属不是method invocation：" + AstRelations.fatherStatement(a, treeContext));
			return 0;
		}
	}

	/**
	 * Sub sub level-III 访问到simple name节点或者literal 节点分两种情况，一种是father
	 * 为ifStatement 另一种是father不是ifstatement type 为simple name action可以为update
	 * 可以为insert match 之后标记action为已读 ， 目前思路： 按照parent为key存储map，insert update
	 * delete 遍历操作之后再进行判断 放入新的list再一起处理
	 * 
	 * 针对下面的switch 情况，因为修改不止一个action，而且可能有删有减有update有move 所以需要按照parent为key
	 * ，存储action，到最后再做操作。
	 * 
	 * @param a
	 * @return
	 */
	public int matchSimplenameOrLiteral(Action a, TreeContext curContext) {
		// if predicate
		ITree fafafatherNode = AstRelations.findFafafatherNode(a.getNode(), curContext);
		String fafafatherType = curContext.getTypeLabel(fafafatherNode);
		int returnVal = -1;
		switch (fafafatherType) {
		case StatementConstants.IFSTATEMENT:
			System.out.println("If predicate");
			returnVal = this.matchIfPredicate(a, curContext, fafafatherNode);
			break;
		case StatementConstants.FORSTATEMENT:
			System.out.println("For predicate");
			returnVal = this.matchForPredicate(a, curContext);
			break;
		case StatementConstants.VARIABLEDECLARATIONSTATEMENT:
		case StatementConstants.EXPRESSIONSTATEMENT:
			System.out.println("variable/expression");
			returnVal = this.matchExpressionStatementAndVariableDeclarationStatement(a, curContext);
			break;
		default:
			System.err.println("Default:" + fafafatherType);
			break;
		}
		return returnVal;
	}

	/**
	 * 方法签名改变的情况
	 * 
	 * @param a
	 * @param fafafather
	 * @return
	 */
	public int matchMethodSignatureChange(Action a, ITree fafafather) {
		ITree srcfafafather = null;
		ITree dstfafafather = null;
		if (a instanceof Insert) {
			dstfafafather = fafafather;
			srcfafafather = this.mMiningActionBean.mMapping.getSrc(dstfafafather);
			if (srcfafafather == null) {
				System.err.println("err null mapping");
			}
		} else {
			srcfafafather = fafafather;
			dstfafafather = this.mMiningActionBean.mMapping.getDst(srcfafafather);
			if (dstfafafather == null) {
				System.err.println("err null mapping");
			}
		}
		List<Action> signatureChidlren = new ArrayList<Action>();
		int insertCnt = MyTreeUtil.traverseMethodSignatureChildrenWithoutBlock(a, dstfafafather, signatureChidlren);
		MyTreeUtil.traverseMethodSignatureChildrenWithoutBlock(a, srcfafafather, signatureChidlren);
		this.mMiningActionBean.mapMethodSignatureAction(srcfafafather, signatureChidlren);
		this.mMiningActionBean.setActionTraversedMap(signatureChidlren);
		return insertCnt;
	}

	/**
	 * main level-I 入口 思路：围绕if/else/else if 和method call来抽 AST 节点：
	 * VariableDeclarationStatement，ExpressionStatement，IfStatement
	 */
	public void find() {
		// this.findMove();
		this.findInsert();
		this.findUpdate();
		this.findDelete();
		this.findMethodSignatureChange();
//		this.findMethodInvocationChange();
	}

	/**
	 * Sub level-II
	 */
	public void findInsert() {
		int insertActionCount = this.mMiningActionBean.mActionGeneratorBean.getInsertActions().size();
		int insertActionIndex = 0;
		int count = 0;// not used
		String resultStr = null;
		while (true) {
			if (insertActionIndex == insertActionCount) {
				break;
			}
			Action a = this.mMiningActionBean.mActionGeneratorBean.getInsertActions().get(insertActionIndex);
			insertActionIndex++;
			if (this.mMiningActionBean.mActionGeneratorBean.getAllActionMap().get(a) == 1) {
				// 标记过的action
				continue;
			}
			Insert ins = (Insert) a;
			ITree insNode = ins.getNode();
			String type = this.mMiningActionBean.mDstTree.getTypeLabel(insNode);
			ITree fafafather = AstRelations.findFafafatherNode(insNode, this.mMiningActionBean.mDstTree);
			String fatherType = this.mMiningActionBean.mDstTree.getTypeLabel(fafafather);
			String nextAction = ConsolePrint.printMyOneActionString(a, 0, this.mMiningActionBean.mDstTree);
			System.out.print(nextAction);
			if (StatementConstants.METHODDECLARATION.equals(type)) {
				// 新增方法
				resultStr = matchNewOrDeleteMethod(a);
				System.out.println(resultStr);
			} else if (StatementConstants.METHODDECLARATION.equals(fatherType)) {
				// 方法签名修改
				if (StatementConstants.BLOCK.equals(type)) {
					System.err.println("Not considered");
				} else {
					count = matchMethodSignatureChange(a, fafafather);
				}
			} else {
				// 方法体
				switch (type) {
				case StatementConstants.IFSTATEMENT:
					// Pattern 1. Match If/else if
					count = matchIf(a, type);
					break;
				case StatementConstants.BLOCK:
					// Pattern 1.2 Match else
					if (AstRelations.isFatherIfStatement(a, this.mMiningActionBean.mDstTree)) {
						count = this.matchElse(a);
					} else {
						System.err.println("Other Condition");
						// TODO剩下的情况
					}
					break;
				case StatementConstants.TRYSTATEMENT:
					count = matchTry(a);
					break;
				case StatementConstants.VARIABLEDECLARATIONSTATEMENT:
					count = this.matchVariableDeclaration(a);
					break;
				case StatementConstants.EXPRESSIONSTATEMENT:
					count = this.matchExpression(a);
					break;
				// 方法参数
				case StatementConstants.SIMPLENAME:
				case StatementConstants.STRINGLITERAL:
				case StatementConstants.NULLLITERAL:
				case StatementConstants.CHARACTERLITERAL:
				case StatementConstants.NUMBERLITERAL:
				case StatementConstants.BOOLEANLITERAL:
				case StatementConstants.INFIXEXPRESSION:
					count = this.matchSimplenameOrLiteral(a, this.mMiningActionBean.mDstTree);
					break;
				default:
					System.err.println("Default1:" + type);
					break;
				}
			}

		}
	}

	/**
	 * Sub level-II
	 */
	public void findDelete() {
		int deleteActionCount = this.mMiningActionBean.mActionGeneratorBean.getDeleteActions().size();
		int index = 0;
		int count = 0;
		String resultStr = null;
		while (true) {
			if (index == deleteActionCount) {
				break;
			}
			Action a = this.mMiningActionBean.mActionGeneratorBean.getDeleteActions().get(index);
			index++;
			if (this.mMiningActionBean.mActionGeneratorBean.getAllActionMap().get(a) == 1) {
				continue;
			}
			Delete del = (Delete) a;
			ITree tmp = a.getNode();
			String type = this.mMiningActionBean.mDstTree.getTypeLabel(tmp);
			String nextAction = ConsolePrint.printMyOneActionString(a, 0, this.mMiningActionBean.mDstTree);
			ITree fafafather = AstRelations.findFafafatherNode(tmp, this.mMiningActionBean.mSrcTree);
			String fatherType = this.mMiningActionBean.mDstTree.getTypeLabel(fafafather);
			System.out.print(nextAction);
			if (StatementConstants.METHODDECLARATION.equals(type)) {
				// 删除方法体
				resultStr = matchNewOrDeleteMethod(a);
				System.out.println(resultStr);
			} else if (StatementConstants.METHODDECLARATION.equals(fatherType)) {
				// 删除方法参数
			} else {
				// 方法体内部
				switch (type) {
				case StatementConstants.IFSTATEMENT:
					count = matchIf(a, type);
					break;
				case StatementConstants.BLOCK:
					// Pattern 1.2 Match else
					if (AstRelations.isFatherIfStatement(a, this.mMiningActionBean.mSrcTree)) {
						count = this.matchElse(a);
					} else {
						System.err.println("Other Condition");
						// TODO剩下的情况
					}
					break;
				case StatementConstants.EXPRESSIONSTATEMENT:
					count = this.matchExpression(a);
					break;
				case StatementConstants.TRYSTATEMENT:
					count = matchTry(a);
					break;
				case StatementConstants.VARIABLEDECLARATIONSTATEMENT:
					count = this.matchVariableDeclaration(a);
					break;

				// 方法参数
				case StatementConstants.SIMPLENAME:
				case StatementConstants.STRINGLITERAL:
				case StatementConstants.NULLLITERAL:
				case StatementConstants.CHARACTERLITERAL:
				case StatementConstants.NUMBERLITERAL:
				case StatementConstants.BOOLEANLITERAL:
				case StatementConstants.INFIXEXPRESSION:
					count = this.matchSimplenameOrLiteral(a, this.mMiningActionBean.mDstTree);
					break;

				default:
					break;
				}
			}
		}
	}

	/**
	 * Sub level-II
	 */
	public void findUpdate() {
		int updateActionCount = this.mMiningActionBean.mActionGeneratorBean.getUpdateActions().size();
		int index = 0;
		int count = 0;
		while (true) {
			if (index == updateActionCount) {
				break;
			}
			Action a = this.mMiningActionBean.mActionGeneratorBean.getUpdateActions().get(index);
			index++;
			if (this.mMiningActionBean.mActionGeneratorBean.getAllActionMap().get(a) == 1) {
				// 标记过的 update action
				continue;
			}
			Update up = (Update) a;
			ITree tmp = a.getNode();
			String type = this.mMiningActionBean.mDstTree.getTypeLabel(tmp);
			String nextAction = ConsolePrint.printMyOneActionString(a, 0, this.mMiningActionBean.mDstTree);
			ITree fafafather = AstRelations.findFafafatherNode(tmp, this.mMiningActionBean.mSrcTree);
			String fatherType = this.mMiningActionBean.mDstTree.getTypeLabel(fafafather);
			System.out.print(nextAction);
			if (StatementConstants.METHODDECLARATION.equals(type)) {
				// 新增方法
				System.err.println("Update 应该不会是method declaration");
			} else if (StatementConstants.METHODDECLARATION.equals(fatherType)) {
				// 方法签名update
				if (StatementConstants.BLOCK.equals(type)) {
					System.err.println("Not considered");
				} else {
					count = matchMethodSignatureChange(a, fafafather);
				}
			} else {
				// 方法体
				switch (type) {
				case StatementConstants.SIMPLENAME:
					count = this.matchSimplenameOrLiteral(a, this.mMiningActionBean.mSrcTree);
					break;
				default:
					break;
				}
			}
		}

	}

	public void findMove() {
		int moveActionCount = this.mMiningActionBean.mActionGeneratorBean.getMoveActions().size();
		int index = 0;
		while (true) {
			if (index == moveActionCount) {
				break;
			}
			Action a = this.mMiningActionBean.mActionGeneratorBean.getMoveActions().get(index);
			index++;
			if (this.mMiningActionBean.mActionGeneratorBean.getAllActionMap().get(a) == 1) {
				// 标记过的 update action
				continue;
			}
			Move up = (Move) a;
			ITree moveNode = a.getNode();
			String type = this.mMiningActionBean.mDstTree.getTypeLabel(moveNode);
			String nextAction = ConsolePrint.printMyOneActionString(a, 0, this.mMiningActionBean.mDstTree);
			ITree fafafather = AstRelations.findFafafatherNode(moveNode, this.mMiningActionBean.mSrcTree);
			String fatherType = this.mMiningActionBean.mDstTree.getTypeLabel(fafafather);
			System.out.print(nextAction);
			ITree dstNode = up.getParent();

		}

	}

	/**
	 * Sub level-II
	 * 
	 */
	public void findMethodInvocationChange() {
		for (Entry<ITree, List<Action>> item : this.mMiningActionBean.fatherToActionMap.entrySet()) {
			Tree srcParentNode = (Tree) item.getKey();
			Tree dstParentNode = (Tree) this.mMiningActionBean.mMapping.getDst(srcParentNode);
			List<Action> mList = item.getValue();
			Set<String> mSet = this.mMiningActionBean.fatherToActionChangeTypeMap.get(item.getKey());
			int lenSrc = srcParentNode.getChildren().size();
			int lenDst = dstParentNode.getChildren().size();
			if (lenSrc > lenDst) {
				if (mSet.contains(ActionConstants.UPDATE)) {
					System.out.println("1.Altering method parameters -Delete&update parameter");
				} else {
					System.out.println("1.Altering method parameters - Delete parameter");
				}
			} else if (lenSrc < lenDst) {
				if (mSet.contains(ActionConstants.UPDATE)) {
					System.out.println("1.Altering method parameters - Add&update parameter");
				} else {
					System.out.println(
							"1.Atering method parameters - Add parameter / 3.Calling another overloaded method with one more parameter");
				}
			} else {
				System.out.println("1.Altering method parameters - Update parameter");
			}
		}
	}

	public void findMethodSignatureChange() {
		for (Entry<ITree, List<Action>> item : this.mMiningActionBean.methodSignatureMap.entrySet()) {
			// Tree srcParentNode = (Tree) item.getKey();
			// Tree dstParentNode = (Tree)
			// this.mMiningActionBean.mMapping.getDst(srcParentNode);
			System.out.println("[PATTREN] Method Signature Change");
		}
	}
//	Tree t = (Tree) a.getNode().getChild(0);
//	MethodInvocation mi = (MethodInvocation) (t.getAstNode());
//	System.out.println(mi.getName());
//
//	System.out.println(mi.getExpression());

}
