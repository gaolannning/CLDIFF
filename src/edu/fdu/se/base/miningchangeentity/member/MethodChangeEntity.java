package edu.fdu.se.base.miningchangeentity.member;

import edu.fdu.se.base.links.MyRange;
import edu.fdu.se.base.miningchangeentity.ClusteredActionBean;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntityDesc;
import edu.fdu.se.base.miningchangeentity.base.MemberPlusChangeEntity;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPairC;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * Created by huangkaifeng on 2018/1/22.
 *
 */
public class MethodChangeEntity extends MemberPlusChangeEntity {

    public MethodChangeEntity(ClusteredActionBean bean){
        super(bean);
    }

    public MethodChangeEntity(BodyDeclarationPairC bodyDeclarationPair, String changeType, MyRange myRange){
        super(bodyDeclarationPair.getLocationClassString(),changeType,myRange);
        IASTFunctionDefinition md =(IASTFunctionDefinition) bodyDeclarationPair.getBodyDeclaration();
        this.stageIIBean.setLocation(bodyDeclarationPair.getLocationClassString());
        this.stageIIBean.setChangeEntity(ChangeEntityDesc.StageIIENTITY.ENTITY_METHOD);
        this.stageIIBean.setThumbnail(md.getDeclarator().getName().toString());
        this.bodyDeclarationPair = bodyDeclarationPair;
    }

    @Override
    public String toString2(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.changeEntityId);
        sb.append(". ");
        sb.append(this.stageIIBean.toString2());
        return sb.toString();
    }



}
