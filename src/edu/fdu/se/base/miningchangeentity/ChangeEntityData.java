package edu.fdu.se.base.miningchangeentity;

import com.github.gumtreediff.actions.model.Delete;
import com.github.gumtreediff.actions.model.Insert;
import edu.fdu.se.base.common.Global;
import edu.fdu.se.base.links.LayeredChangeEntityContainerC;
import edu.fdu.se.base.links.Link;
import edu.fdu.se.base.links.MyRange;
import edu.fdu.se.base.miningactions.bean.MiningActionData;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntity;
import edu.fdu.se.base.links.LayeredChangeEntityContainer;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntityDesc;
import edu.fdu.se.base.miningchangeentity.member.*;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPairC;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.jdt.core.dom.*;

import java.util.List;


/**
 * Created by huangkaifeng on 2018/1/13.
 *
 */
public class ChangeEntityData {

    public String fileName;

    public LayeredChangeEntityContainer entityContainer;
    public MiningActionData mad;

    public List<Link> mLinks;

    public ChangeEntityData(MiningActionData mad) {
        if(mad.preprocessedData!=null) {
            this.entityContainer = mad.preprocessedData.entityContainer;
        }
        this.mad = mad;
    }


    public ChangeEntity addOneBody(BodyDeclarationPair item, String type) {
        ChangeEntity ce = null;
        int s;
        int e;
        MyRange myRange = null;
        Object n = item.getBodyDeclaration();
        if (Insert.class.getSimpleName().equals(type)) {
            s = Global.util.getLineNumber(Global.util.getDstCu(mad.preprocessedData),Global.util.getStartPosition(n)); //n.getFileLocation().getStartingLineNumber();    //getDstCu().getLineNumber(item.getBodyDeclaration().getStartPosition());
            e = Global.util.getLineNumber(Global.util.getDstCu(mad.preprocessedData),Global.util.getStartPosition(n)+Global.util.getNodeLength(n)); //n.getFileLocation().getEndingLineNumber();       //getDstCu().getLineNumber(item.getBodyDeclaration().getStartPosition() + item.getBodyDeclaration().getLength() - 1);
            myRange = new MyRange(s, e, ChangeEntityDesc.StageITreeType.DST_TREE_NODE);
        } else if (Delete.class.getSimpleName().equals(type)) {
            s = Global.util.getLineNumber(Global.util.getSrcCu(mad.preprocessedData),Global.util.getStartPosition(n)); //n.getFileLocation().getStartingLineNumber();             //getSrcCu().getLineNumber(item.getBodyDeclaration().getStartPosition());
            e = Global.util.getLineNumber(Global.util.getSrcCu(mad.preprocessedData),Global.util.getStartPosition(n)+Global.util.getNodeLength(n)); //n.getFileLocation().getEndingLineNumber();              //getSrcCu().getLineNumber(item.getBodyDeclaration().getStartPosition() + item.getBodyDeclaration().getLength() - 1);
            myRange = new MyRange(s, e, ChangeEntityDesc.StageITreeType.SRC_TREE_NODE);
        }
        if (Global.util.isFieldDeclaration(n)) {
            ce = new FieldChangeEntity(item, type, myRange);
        } else if (Global.util.isMethodDeclaration(n)) {
            ce = new MethodChangeEntity(item, type, myRange);
        } else if (item.getBodyDeclaration() instanceof Initializer) {  //Java专用
            ce = new InitializerChangeEntity(item, type, myRange);
        } else if (Global.util.isTypeDeclaration(n)) {
            ce = new ClassChangeEntity(item, type, myRange);
        } else if (Global.util.isEnumDeclaration(n)) {
            ce = new EnumChangeEntity(item, type, myRange);
        }
        return ce;
    }






}
