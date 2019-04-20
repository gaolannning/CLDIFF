package edu.fdu.se.base.links.linkbean;

import com.github.gumtreediff.tree.Tree;
import edu.fdu.se.base.generatingactions.JavaParserVisitorC;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntityDesc;
import edu.fdu.se.base.miningchangeentity.member.FieldChangeEntity;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by huangkaifeng on 2018/4/7.
 */
public class FieldData extends LinkBean {

    public FieldData(FieldChangeEntity ce){
        if(fieldName==null){
            fieldName = new ArrayList<>();
        }
        IASTSimpleDeclaration fd = null;
        switch(ce.stageIIBean.getOpt()) {
            case ChangeEntityDesc.StageIIOpt.OPT_CHANGE:
                Tree t = ce.clusteredActionBean.fafather;
                if (JavaParserVisitorC.getNodeTypeId(t.getAstNodeC()) == JavaParserVisitorC.FIELD_DECLARATION) {
                    fd = (IASTSimpleDeclaration) t.getAstNodeC();
                }
                break;
            case ChangeEntityDesc.StageIIOpt.OPT_DELETE:
            case ChangeEntityDesc.StageIIOpt.OPT_INSERT:
                if (ce.stageIIBean.getEntityCreationStage().equals(ChangeEntityDesc.StageIIGenStage.ENTITY_GENERATION_STAGE_PRE_DIFF)) {
                    fd = (IASTSimpleDeclaration) ce.bodyDeclarationPair.getBodyDeclaration();

                }
                break;

        }
        if(fd!=null){
            List<IASTDeclarator> list = Arrays.asList(fd.getDeclarators());
            for(IASTDeclarator vd:list){
                fieldName.add(vd.getName().toString());
            }
            fieldType = fd.getDeclSpecifier().toString();
        }


    }

    public List<String> fieldName;

    public String fieldType;
}
