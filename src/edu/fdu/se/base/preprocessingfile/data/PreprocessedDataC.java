package edu.fdu.se.base.preprocessingfile.data;


import edu.fdu.se.base.links.LayeredChangeEntityContainer;
import edu.fdu.se.base.miningactions.util.MyList;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntity;
import edu.fdu.se.javaparser.CDTParserFactory;
import edu.fdu.se.javaparser.JDTParserFactory;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.*;

/**
 * Created by huangkaifeng on 2018/1/16.
 *
 */
public class PreprocessedDataC {

    public String fullStringSrc;
    public String fullStringDst;

    public List<String> dstLineList;
    public List<String> srcLineList;
    public List<Integer> dstLines;
    public List<Integer> srcLines;


    //编译单元
    public CompilationUnit dstCu;
    public CompilationUnit srcCu;
    public IASTTranslationUnit dstTu;
    public IASTTranslationUnit srcTu;

    private List<String> interfacesAndFathers;

    public Set<String> prevFieldNames;
    public Set<String> currFieldNames;
    public Set<String> prevCurrFieldNames;

    public CompilationUnit getDstCu() {
        return dstCu;
    }
    public CompilationUnit getSrcCu() {
        return srcCu;
    }

    /**
     * curr 删除的added的body
     */
    private List<BodyDeclarationPairC> mBodiesAdded;
    /**
     * prev 删除的removed body
     */
    private List<BodyDeclarationPairC> mBodiesDeleted;

    private List<ChangeEntity> preprocessChangeEntity;

    public List<ChangeEntity> getPreprocessChangeEntity() {
        return preprocessChangeEntity;
    }

    public void setPreprocessChangeEntity(List<ChangeEntity> preprocessChangeEntity) {
        this.preprocessChangeEntity = preprocessChangeEntity;
    }


    private Map<String,List<IASTNode>> classOrInterfaceOrEnum;

    public PreprocessedDataC(){
        mBodiesAdded = new ArrayList<>();
        mBodiesDeleted = new ArrayList<>();
        classOrInterfaceOrEnum = new HashMap<>();
        entityContainer = new LayeredChangeEntityContainer();
        prevFieldNames = new HashSet<>();
        currFieldNames = new HashSet<>();
        prevCurrFieldNames = new HashSet<>();
        interfacesAndFathers = new MyList<>();


    }
    public LayeredChangeEntityContainer entityContainer;


    public void addTypeDeclaration(String prefix, IASTNode a, String name){
        String key = prefix + "." + name;
        if(this.classOrInterfaceOrEnum.containsKey(key)){
            classOrInterfaceOrEnum.get(key).add(a);
        }else{
            List<IASTNode> mList = new ArrayList<>();
            mList.add(a);
            this.classOrInterfaceOrEnum.put(key,mList);
        }
    }

    public void loadTwoTranslationUnits(IASTTranslationUnit src,IASTTranslationUnit dst,String srcPath,String dstPath){
        this.srcTu = src;
        this.srcLineList = new ArrayList<>();
        this.fullStringSrc = CDTParserFactory.getLinesOfFile(srcPath,this.srcLineList);
        this.srcLines = CDTParserFactory.getLinesList(srcLineList.size());

        this.dstTu = dst;
        this.dstLineList = new ArrayList<>();
        this.fullStringDst = JDTParserFactory.getLinesOfFile(dstPath,this.dstLineList);
        this.dstLines = JDTParserFactory.getLinesList(dstLineList.size());
    }

    public void loadTwoTranslationUnits(IASTTranslationUnit src,IASTTranslationUnit dst,byte[] srcContent,byte[] dstContent){
        this.srcTu = src;
        this.srcLineList = new ArrayList<>();
        this.fullStringSrc = CDTParserFactory.getLinesOfFile(srcContent,this.srcLineList);
        this.srcLines = CDTParserFactory.getLinesList(srcLineList.size());

        this.dstTu = dst;
        this.dstLineList = new ArrayList<>();
        this.fullStringDst = CDTParserFactory.getLinesOfFile(dstContent,this.dstLineList);
        this.dstLines = CDTParserFactory.getLinesList(dstLineList.size());
    }



    public void addBodiesAdded(IASTNode bodyDeclaration,String classPrefix){
        this.mBodiesAdded.add(new BodyDeclarationPairC(bodyDeclaration,classPrefix));
    }


    public void addBodiesDeleted(BodyDeclarationPairC bodyDeclarationPair){
        this.mBodiesDeleted.add(bodyDeclarationPair);
    }


    public void printAddedRemovedBodies(){
        for(BodyDeclarationPairC item:this.mBodiesAdded){
//            System.out.println(item.getBodyDeclaration().toString()+"  "+item.getLocationClassString());
            System.out.println(item.getBodyDeclaration().toString());
        }
        System.out.print("-----------------------------\n");
        for(BodyDeclarationPairC item:this.mBodiesDeleted){
//            System.out.println(item.getBodyDeclaration().toString()+"  "+item.getLocationClassString());
            System.out.println(item.getBodyDeclaration().toString());
        }
    }

    public List<BodyDeclarationPairC> getmBodiesAdded() {
        return mBodiesAdded;
    }

    public List<BodyDeclarationPairC> getmBodiesDeleted() {
        return mBodiesDeleted;
    }


    public List<String> getInterfacesAndFathers() {
        return interfacesAndFathers;
    }
}
