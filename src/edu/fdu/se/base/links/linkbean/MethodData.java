package edu.fdu.se.base.links.linkbean;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.tree.Tree;
import edu.fdu.se.base.common.Global;
import edu.fdu.se.base.miningactions.bean.MiningActionData;
import edu.fdu.se.base.miningactions.util.BasicTreeTraversal;
import edu.fdu.se.base.miningactions.util.MyList;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntityDesc;
import edu.fdu.se.base.miningchangeentity.member.MethodChangeEntity;
import edu.fdu.se.lang.generatingactions.CParserVisitor;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.model.FunctionDeclaration;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import java.util.Arrays;
import java.util.List;

/**
 * Created by huangkaifeng on 2018/4/7.
 *
 */
public class MethodData extends LinkBean {

    public MethodData(MethodChangeEntity ce, MiningActionData fp) {
        this.parameterName = new MyList<>();
        this.parameterType = new MyList<>();
        this.methodName = new MyList<>();

        if(ce.stageIIBean.getEntityCreationStage().equals(ChangeEntityDesc.StageIIGenStage.ENTITY_GENERATION_STAGE_PRE_DIFF)){
            Object md =  ce.bodyDeclarationPair.getBodyDeclaration();
            setValue(md);
        }else{
            Tree tree = (Tree)ce.clusteredActionBean.curAction.getNode();
            if(ce.clusteredActionBean.curAction instanceof Move){
                if(Global.util.isMethodDeclaration(tree.getNode())){
                    Object md = tree.getNode();
                    setValue(md);
                }
            }else {
                parseNonMove(ce,fp);
            }
        }

    }

    public void setValue(Object md){
        methodName.add(Global.util.getMethodName(md));
        List<Object> params = Global.util.getSingleVariableDeclarations(md);
//        params = params.subList(1,params.size());
        for(Object svd :params){
            if(Global.util.isSingleVariableDeclaration(svd)) {
//                IASTParameterDeclaration pd = (IASTParameterDeclaration) svd;
//                ((IASTParameterDeclaration) svd).getDeclarator().getName();
                parameterName.add(Global.util.getSingleVariableDeclarationName(svd));
                parameterType.add(Global.util.getSingleVariableDeclarationTypeName(svd));
            }
        }
        if(Global.util.getMethodType(md)!=null){
            returnType = Global.util.getMethodType(md).toString();
        }
    }


    public void parseNonMove(MethodChangeEntity ce,MiningActionData fp){
        Tree tree = (Tree)ce.clusteredActionBean.curAction.getNode();
//        List<String> tempMethodName = new MyList<>();
        List<String> tempParameterType = new MyList<>();
        List<String> tempParameterName = new MyList<>();
        String tempReturn = null;
        if(!Global.util.isMethodDeclaration(tree.getNode())) {
            tree = BasicTreeTraversal.findFafatherNode(ce.clusteredActionBean.curAction.getNode());
        }
        //*** start of retrieving methodName
        if(tree.getTreeSrcOrDst() == ChangeEntityDesc.StageITreeType.SRC_TREE_NODE){
            Tree dstTree = (Tree) fp.getMappedDstOfSrcNode(tree);
            if(dstTree!=null){
                Object mdDst =  dstTree.getNode();
                methodName.add(Global.util.getMethodName(mdDst));
            }

        }
        Object md =  tree.getNode();
        List<Object> params = Global.util.getSingleVariableDeclarations(md);
        params = params.subList(1,params.size());
        for(Object svd :params){
//            IASTParameterDeclaration pd = (IASTParameterDeclaration) svd;
//            ((IASTParameterDeclaration) svd).getDeclarator().getName();
            parameterName.add(Global.util.getSingleVariableDeclarationName(svd));
            parameterType.add(Global.util.getSingleVariableDeclarationTypeName(svd));
        }
        if(Global.util.getMethodType(md)!=null){
            returnType = Global.util.getMethodType(md).toString();
        }
        //***End

        for(Action a:ce.clusteredActionBean.actions){
            Tree t = (Tree) a.getNode();
            if (Global.util.isLiteral(t)) {
//                if(tempMethodName.contains(t.getLabel())){
//                    methodName.add(t.getLabel());
//                }
                if(tempParameterName.contains(t.getLabel())){
                    parameterName.add(t.getLabel());
                }
                if(tempParameterType.contains(t.getLabel())){
                    parameterType.add(t.getLabel());
                }
                if(tempReturn!=null &&tempReturn.equals(t.getLabel())){
                    returnType = t.getLabel();
                }
            }
        }
    }

    public List<String> methodName;

    public List<String> parameterType;

    public List<String> parameterName;

    public String returnType;


}
