package edu.fdu.se.base.miningchangeentity;

import com.github.gumtreediff.actions.model.Delete;
import com.github.gumtreediff.actions.model.Insert;
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

    public LayeredChangeEntityContainerC entityContainer;
    public MiningActionData mad;

    public List<Link> mLinks;

    public ChangeEntityData(MiningActionData mad) {
        if(mad.preprocessedData!=null) {
            this.entityContainer = mad.preprocessedData.entityContainer;
        }
        this.mad = mad;
    }


    public ChangeEntity addOneBody(BodyDeclarationPairC item, String type) {
        ChangeEntity ce = null;
        int s;
        int e;
        MyRange myRange = null;
        IASTNode n = item.getBodyDeclaration();
        if (Insert.class.getSimpleName().equals(type)) {
            s = n.getFileLocation().getStartingLineNumber();    //getDstCu().getLineNumber(item.getBodyDeclaration().getStartPosition());
            e = n.getFileLocation().getEndingLineNumber();       //getDstCu().getLineNumber(item.getBodyDeclaration().getStartPosition() + item.getBodyDeclaration().getLength() - 1);
            myRange = new MyRange(s, e, ChangeEntityDesc.StageITreeType.DST_TREE_NODE);
        } else if (Delete.class.getSimpleName().equals(type)) {
            s = n.getFileLocation().getStartingLineNumber();             //getSrcCu().getLineNumber(item.getBodyDeclaration().getStartPosition());
            e = n.getFileLocation().getEndingLineNumber();              //getSrcCu().getLineNumber(item.getBodyDeclaration().getStartPosition() + item.getBodyDeclaration().getLength() - 1);
            myRange = new MyRange(s, e, ChangeEntityDesc.StageITreeType.SRC_TREE_NODE);
        }
        if (n instanceof IASTSimpleDeclaration && (((IASTSimpleDeclaration)n).getDeclSpecifier() instanceof IASTSimpleDeclSpecifier ||((IASTSimpleDeclaration)n).getDeclSpecifier() instanceof IASTNamedTypeSpecifier)) {
            ce = new FieldChangeEntity(item, type, myRange);
        } else if (n instanceof IASTFunctionDefinition) {
            ce = new MethodChangeEntity(item, type, myRange);
        } else if (item.getBodyDeclaration() instanceof Initializer) {
            ce = new InitializerChangeEntity(item, type, myRange);
        } else if (n instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration)n).getDeclSpecifier() instanceof IASTCompositeTypeSpecifier) {
            ce = new ClassChangeEntity(item, type, myRange);
        } else if (n instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration)n).getDeclSpecifier() instanceof IASTEnumerationSpecifier) {
            ce = new EnumChangeEntity(item, type, myRange);
        }
        return ce;
    }






}
