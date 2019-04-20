package edu.fdu.se.base.miningchangeentity.member;

import edu.fdu.se.base.links.MyRange;
import edu.fdu.se.base.miningchangeentity.ClusteredActionBean;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntityDesc;
import edu.fdu.se.base.miningchangeentity.base.MemberPlusChangeEntity;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPairC;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by huangkaifeng on 2018/1/16.
 *
 */
public class FieldChangeEntity extends MemberPlusChangeEntity {



    /**
     * 预处理识别的
     */
    public FieldChangeEntity(BodyDeclarationPairC fieldDeclarationPair, String changeType, MyRange myRange){
        super(fieldDeclarationPair.getLocationClassString(),changeType,myRange);
        IASTSimpleDeclaration fd = (IASTSimpleDeclaration) fieldDeclarationPair.getBodyDeclaration();
        this.stageIIBean.setLocation(fieldDeclarationPair.getLocationClassString());
        this.stageIIBean.setChangeEntity(ChangeEntityDesc.StageIIENTITY.ENTITY_FIELD);
        this.bodyDeclarationPair = fieldDeclarationPair;
        List<IASTDeclarator> list = Arrays.asList(fd.getDeclarators());
        String res = "";
        for(IASTDeclarator vd:list){
            res += vd+",";
        }
        this.stageIIBean.setThumbnail(fieldDeclarationPair.getLocationClassString() + res);

    }

    /**
     * gumtree 识别的
     * @param bean
     */
    public FieldChangeEntity(ClusteredActionBean bean){
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
