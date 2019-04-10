package edu.fdu.se.base.preprocessingfile.data;


import java.util.*;

import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTUsingDirective;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.text.edits.TextEditGroup;

/**
 * Created by huangkaifeng on 2018/1/18.
 *
 */
public class PreprocessedTempDataC {

    /**
     * 已经设置为same remove有可能被 overload遍历到，设置为retain，需要加check
     */
    final static public int BODY_SAME_REMOVE = 10;
    /**
     * 已经为retain ，如果遍历到发现是same remove 则可以re- set 为remove
     */
    final static public int BODY_DIFFERENT_RETAIN = 11;
    final static public int BODY_INITIALIZED_VALUE = 12;
    /**
     * 已经设置为remove的，表示curr中cod已经被删除，所以不会再revisit到
     */
    final static public int BODY_FATHERNODE_REMOVE = 13;


    public PreprocessedTempDataC(){
        srcNodeBodyNameMap = new HashMap<>();
        srcNodeVisitingMap = new HashMap<>();
        srcNodeHashCodeMap = new HashMap<>();
        srcRemovalNodes = new ArrayList<>();
        dstRemovalNodes = new ArrayList<>();
    }

    public Map<String, List<BodyDeclarationPairC>> srcNodeBodyNameMap;
    /**
     * 0 初始化之后的值  1 遍历到了之后 需要保留的different  2 遍历到了之后 需要删除的same   3 prev中有，curr没有，change：deleted
     */
    public Map<BodyDeclarationPairC,Integer> srcNodeVisitingMap;

    public Map<BodyDeclarationPairC,Integer> srcNodeHashCodeMap;

    /**
     * list of comments to be removed
     */
    public List<IASTNode> srcRemovalNodes;
    public List<IASTNode> dstRemovalNodes;


    /**
     * method name
     *
     */
    public void addToMapBodyName(BodyDeclarationPairC bd,String name) {
        if (this.srcNodeBodyNameMap.containsKey(name)) {
            List<BodyDeclarationPairC> mList = this.srcNodeBodyNameMap.get(name);
            mList.add(bd);
        } else {
            List<BodyDeclarationPairC> mList = new ArrayList<>();
            mList.add(bd);
            this.srcNodeBodyNameMap.put(name, mList);
        }
    }


    public void addToSrcRemoveList(IASTNode bd) {
        this.srcRemovalNodes.add(bd);
    }

    private void setLinesFlag(List<Integer> lineFlags,int start,int end){
        for(int i =start ;i<=end;i++){
            if(lineFlags.get(i-1)>0){
                lineFlags.set(i-1, -lineFlags.get(i-1));
            }
        }
    }

    public void removeSrcRemovalList(IASTTranslationUnit cu, List<Integer> lineList) {
        for (IASTNode item : this.srcRemovalNodes) {
//            if(item instanceof MethodDeclaration){
//                MethodDeclaration md = (MethodDeclaration) item;
//                if(md.getName().toString().startsWith("create")){
//                    System.out.println(md.getName().toString());
//
//                }
//            }

//        	System.out.println(item.toString());
//            System.out.println(cu.getLineNumber(item.getStartPosition()) +"  "+cu.getLineNumber(item.getStartPosition()+item.getLength()-1));
            //如果不是IASTFileLocation则终止程序
            assert(item.getNodeLocations()[0] instanceof IASTFileLocation);
            setLinesFlag(lineList, ((IASTFileLocation) item.getNodeLocations()[0]).getStartingLineNumber(),
                        ((IASTFileLocation) item.getNodeLocations()[0]).getStartingLineNumber());
//            ASTRewrite rewriter = ASTRewrite.create(cu);
//            rewriter.remove(item,null);
//            item.setParent(null);


        }
        //this.srcRemovalNodes.clear();
    }

    public void addToDstRemoveList(IASTNode bd) {
        dstRemovalNodes.add(bd);
    }

    public void removeDstRemovalList(IASTTranslationUnit cu, List<Integer> lineList) {
        for (IASTNode item : this.dstRemovalNodes) {
            assert(item.getNodeLocations()[0] instanceof IASTFileLocation);
            setLinesFlag(lineList, ((IASTFileLocation) item.getNodeLocations()[0]).getStartingLineNumber(),
                    ((IASTFileLocation) item.getNodeLocations()[0]).getStartingLineNumber());
            ASTRewrite rewriter = ASTRewrite.create(cu);
//            rewriter.remove(item,null);
        }
//        dstRemovalNodes.clear();
    }

    public void initBodySrcNodeMap(BodyDeclarationPairC bodyDeclarationPair){
        this.srcNodeVisitingMap.put(bodyDeclarationPair,BODY_INITIALIZED_VALUE);
    }

    /**
     *
     * @param v
     */
    public void setBodySrcNodeMap(BodyDeclarationPairC bdp, int v){
        this.srcNodeVisitingMap.put(bdp,v);
    }

    public int getNodeMapValue(BodyDeclarationPairC bodyDeclarationPair){
        return this.srcNodeVisitingMap.get(bodyDeclarationPair);
    }


    public void removeAllSrcComments(IASTTranslationUnit cu, List<Integer> lineList) {
//        PackageDeclaration packageDeclaration = cu.getPackage();  C++没有包声明
//        if (packageDeclaration != null)
//            addToSrcRemoveList(packageDeclaration);
        for(IASTNode item:cu.getChildren()){
            if(item instanceof CPPASTUsingDirective){
                addToSrcRemoveList(item);
            }
        }

        List<IASTNode> commentList = Arrays.asList(cu.getComments());
        for (int i = commentList.size() - 1; i >= 0; i--) {
                addToSrcRemoveList(commentList.get(i));
        }
        List<IASTNode> includes = Arrays.asList(cu.getIncludeDirectives());
        for (int i = includes.size() - 1; i >= 0; i--) {
            addToSrcRemoveList(includes.get(i));
        }
        removeSrcRemovalList(cu,lineList);
    }

    public void removeAllDstComments(IASTTranslationUnit cu,List<Integer> lineList) {
        for(IASTNode item:cu.getChildren()){
            if(item instanceof CPPASTUsingDirective){
                addToDstRemoveList(item);
            }
        }

        List<IASTNode> commentList = Arrays.asList(cu.getComments());
        for (int i = commentList.size() - 1; i >= 0; i--) {
            addToDstRemoveList(commentList.get(i));
        }
        List<IASTNode> includes = Arrays.asList(cu.getIncludeDirectives());
        for (int i = includes.size() - 1; i >= 0; i--) {
            addToDstRemoveList(includes.get(i));
        }
        removeDstRemovalList(cu,lineList);
    }

}
