package edu.fdu.se.base.links.linkbean;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.tree.Tree;
import edu.fdu.se.base.generatingactions.JavaParserVisitorC;
import edu.fdu.se.base.miningactions.bean.MiningActionData;
import edu.fdu.se.base.miningactions.util.BasicTreeTraversal;
import edu.fdu.se.base.miningactions.util.MyList;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntityDesc;
import edu.fdu.se.base.miningchangeentity.member.MethodChangeEntity;
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
            IASTFunctionDefinition md = (IASTFunctionDefinition) ce.bodyDeclarationPair.getBodyDeclaration();
            setValue(md);
        }else{
            Tree tree = (Tree)ce.clusteredActionBean.curAction.getNode();
            if(ce.clusteredActionBean.curAction instanceof Move){
                if(JavaParserVisitorC.getNodeTypeId(tree.getAstNodeC()) == JavaParserVisitorC.METHOD_DECLARATION){
                    IASTFunctionDefinition md = (IASTFunctionDefinition) tree.getAstNodeC();
                    setValue(md);
                }
            }else {
                parseNonMove(ce,fp);
            }
        }

    }

    public void setValue(IASTFunctionDefinition md){
        methodName.add(md.getDeclarator().getName().toString());
        List<IASTNode> params = Arrays.asList(((IASTFunctionDeclarator) md.getDeclarator()).getChildren());
//        params = params.subList(1,params.size());
        for(IASTNode svd :params){
            if(svd instanceof IASTParameterDeclaration) {
                IASTParameterDeclaration pd = (IASTParameterDeclaration) svd;
                ((IASTParameterDeclaration) svd).getDeclarator().getName();
                parameterName.add(pd.getDeclarator().getName().toString());
                parameterType.add(pd.getDeclSpecifier().toString());
            }
        }
        if(md.getDeclSpecifier()!=null){
            returnType = md.getDeclSpecifier().toString();
        }
    }


    public void parseNonMove(MethodChangeEntity ce,MiningActionData fp){
        Tree tree = (Tree)ce.clusteredActionBean.curAction.getNode();
//        List<String> tempMethodName = new MyList<>();
        List<String> tempParameterType = new MyList<>();
        List<String> tempParameterName = new MyList<>();
        String tempReturn = null;
        if(JavaParserVisitorC.getNodeTypeId(tree.getAstNodeC()) != JavaParserVisitorC.METHOD_DECLARATION) {
            tree = BasicTreeTraversal.findFafatherNode(ce.clusteredActionBean.curAction.getNode());
        }
        //*** start of retrieving methodName
        if(tree.getTreeSrcOrDst() == ChangeEntityDesc.StageITreeType.SRC_TREE_NODE){
            Tree dstTree = (Tree) fp.getMappedDstOfSrcNode(tree);
            if(dstTree!=null){
                IASTFunctionDefinition mdDst = (IASTFunctionDefinition) dstTree.getAstNodeC();
                methodName.add(mdDst.getDeclarator().getName().toString());
            }

        }
        IASTFunctionDefinition md = (IASTFunctionDefinition) tree.getAstNodeC();
        List<IASTNode> params = Arrays.asList(((IASTFunctionDeclarator) md.getDeclarator()).getChildren());
        params = params.subList(1,params.size());
        for(IASTNode svd :params){
            IASTParameterDeclaration pd = (IASTParameterDeclaration) svd;
            ((IASTParameterDeclaration) svd).getDeclarator().getName();
            parameterName.add(pd.getDeclarator().getName().toString());
            parameterType.add(pd.getDeclSpecifier().toString());
        }
        if(md.getDeclSpecifier()!=null){
            returnType = md.getDeclSpecifier().toString();
        }
        //***End

        for(Action a:ce.clusteredActionBean.actions){
            Tree t = (Tree) a.getNode();
            if (JavaParserVisitorC.getNodeTypeId(t.getAstNodeC()) == JavaParserVisitorC.NAME
                    || t.getAstNodeC().getClass().getSimpleName().endsWith("Literal")) {
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
