package edu.fdu.se.base.miningchangeentity.base;

import edu.fdu.se.base.links.MyRange;
import edu.fdu.se.base.miningchangeentity.ClusteredActionBean;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPairC;

/**
 * Created by huangkaifeng on 2018/2/8.
 *
 */
public class MemberPlusChangeEntityC extends ChangeEntity {

    public BodyDeclarationPairC bodyDeclarationPair;

    public MemberPlusChangeEntityC(ClusteredActionBean bean){
        super(bean);
    }

    public MemberPlusChangeEntityC(String location,String changeType,MyRange myRange){
        super(location,changeType,myRange);

    }




}
