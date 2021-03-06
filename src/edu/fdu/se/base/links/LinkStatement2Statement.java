package edu.fdu.se.base.links;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.tree.Tree;
import edu.fdu.se.base.links.linkbean.StmtData;
import edu.fdu.se.base.miningchangeentity.ChangeEntityData;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntity;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntityDesc;
import edu.fdu.se.base.miningchangeentity.statement.VariableChangeEntity;

/**
 * Created by huangkaifeng on 2018/4/7.
 *
 */
public class LinkStatement2Statement {


    public static void checkStmtAssociation(ChangeEntityData changeEntityData, ChangeEntity ce1, ChangeEntity ce2){

        StmtData linkBean1 = (StmtData) ce1.linkBean;
        StmtData linkBean2 = (StmtData) ce2.linkBean;
//        for(String tmp:linkBean1.variableField){
//            if(linkBean2.variableField.contains(tmp)){
//                Link association = new Link(ce1,ce2,ChangeEntityDesc.StageIIIAssociationType.TYPE_SHARE_FIELD,tmp);
//                changeEntityData.mLinks.add(association);
//                break;
//            }
//        }
        for(String tmp:linkBean1.variableLocal) {
            if(linkBean2.variableLocal.contains(tmp)){
                if("".equals(tmp)){
                    continue;
                }
                if(ce1 instanceof VariableChangeEntity || ce2 instanceof VariableChangeEntity) {
                    Action curAction = ce1.clusteredActionBean.curAction;
                    Tree node = (Tree) curAction.getNode();
                    String methodName = LinkUtil.findResidingMethodName(node);
                    String desc = String.format(ChangeEntityDesc.StageIIIAssociationType.DEF_USE,tmp,methodName);
                    Link link = new Link(changeEntityData.fileName,ce1,ce2, desc,tmp);
                    changeEntityData.mLinks.add(link);
                }
                break;
            }
        }


    }

    public static void checkStmtShareField(ChangeEntityData changeEntityData,ChangeEntity ce1,ChangeEntity ce2){
        StmtData linkBean1 = (StmtData) ce1.linkBean;
        StmtData linkBean2 = (StmtData) ce2.linkBean;
        //pass
//        for(String l1:linkBean1.variableField){
//            for(String l2:linkBean2.variableField){
//                if(l1.equals(l2)){
//                    Link association = new Link(ce1,ce2,ChangeEntityDesc.StageIIIAssociationType.TYPE_SHARE_FIELD,l2);
//                    changeEntityData.mLinks.add(association);
//                }
//            }
//        }
    }



}
