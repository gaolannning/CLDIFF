package edu.fdu.se.base.miningchangeentity.member;

import edu.fdu.se.base.links.MyRange;
import edu.fdu.se.base.miningchangeentity.ClusteredActionBean;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntityDesc;
import edu.fdu.se.base.miningchangeentity.base.MemberPlusChangeEntity;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPairC;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * Created by huangkaifeng on 2018/1/23.
 *
 */
public class ClassChangeEntity extends MemberPlusChangeEntity {



    /**
     * 预处理 识别的
     */
    public ClassChangeEntity(BodyDeclarationPairC bodyDeclarationPair, String changeType, MyRange myRange){
        super(bodyDeclarationPair.getLocationClassString(),changeType,myRange);
        IASTSimpleDeclaration cod = (IASTSimpleDeclaration) bodyDeclarationPair.getBodyDeclaration();
        this.stageIIBean.setLocation(bodyDeclarationPair.getLocationClassString());
        if(((IASTCompositeTypeSpecifier)cod.getDeclSpecifier()).getKey() != 3){
            this.stageIIBean.setChangeEntity(ChangeEntityDesc.StageIIENTITY.ENTITY_INTERFACE);
        }else{
            this.stageIIBean.setChangeEntity(ChangeEntityDesc.StageIIENTITY.ENTITY_INNER_CLASS);
        }
        this.stageIIBean.setThumbnail(((IASTCompositeTypeSpecifier) cod.getDeclSpecifier()).getName().toString());
        this.bodyDeclarationPair = bodyDeclarationPair;
    }

    /**
     * gumtree 识别的 add/remove/modify
     * @param bean
     */
    public ClassChangeEntity(ClusteredActionBean bean){
        super(bean);
    }



    public String toString2(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.changeEntityId);
        sb.append(". ");
        sb.append(this.stageIIBean.toString2());
        return sb.toString();

    }


}
