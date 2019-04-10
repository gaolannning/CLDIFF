package edu.fdu.se.base.preprocessingfile;

import com.github.gumtreediff.actions.model.Insert;
import edu.fdu.se.base.links.MyRange;
import edu.fdu.se.base.miningactions.bean.MiningActionData;
import edu.fdu.se.base.miningchangeentity.ChangeEntityData;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntity;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntityDesc;
import edu.fdu.se.base.miningchangeentity.member.ClassChangeEntity;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPairC;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedData;
import edu.fdu.se.javaparser.CDTParserFactory;
import edu.fdu.se.javaparser.JDTParserFactory;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangkaifeng on 2018/4/19.
 *
 */
public class AddOrRemoveFileProcessing {

    public ChangeEntityData ced;

    public IASTTranslationUnit cu;

    public List<String> linesList;

    public String FILE_TYPE;


    public AddOrRemoveFileProcessing(byte[] content, String fileType){
        try {
            FILE_TYPE = fileType;
            cu = CDTParserFactory.getTranslationUnit(content);
            this.linesList = new ArrayList<>();
            if(cu instanceof IASTTranslationUnit){
                init((IASTTranslationUnit)cu);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void init(IASTNode typeDeclaration){
        int treeType = 0;
        if(FILE_TYPE.equals(ChangeEntityDesc.StageIIIFile.DST)){
            treeType = ChangeEntityDesc.StageITreeType.DST_TREE_NODE;
        }else{
            treeType = ChangeEntityDesc.StageITreeType.SRC_TREE_NODE;
        }
        List<ChangeEntity> mList = new ArrayList<>();
        for(IASTNode node:typeDeclaration.getChildren()) {
            if(node instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration)node).getDeclSpecifier() instanceof IASTCompositeTypeSpecifier){
                ClassChangeEntity classChangeEntity = new ClassChangeEntity(new BodyDeclarationPairC(typeDeclaration, ((IASTCompositeTypeSpecifier) ((IASTSimpleDeclaration) typeDeclaration).getDeclSpecifier()).getName().toString() + "."),
                        Insert.class.getSimpleName(),
                        new MyRange(node.getFileLocation().getStartingLineNumber(), node.getFileLocation().getEndingLineNumber(),
                                treeType));
                mList.add(classChangeEntity);
                ced = new ChangeEntityData(new MiningActionData(mList));
            }

        }
        //FilePairPreDiffC diff = new FilePairPreDiffC();
        //ced.mad.preprocessedData = diff.getPreprocessedData();
        //diff.addSuperClass(typeDeclaration,ced.mad.preprocessedData.getInterfacesAndFathers());
    }
}
