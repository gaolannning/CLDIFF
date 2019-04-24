package edu.fdu.se.lang;

import edu.fdu.se.base.preprocessingfile.FilePairPreDiff;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedData;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedTempData;
import edu.fdu.se.lang.parser.CDTParserFactory;
import edu.fdu.se.lang.parser.JDTParserFactory;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.jdt.core.dom.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class UtilJava implements Util{
    @Override
    public CompilationUnit getSrcCu(PreprocessedData data){ return (CompilationUnit) data.getSrcCu(); }

    @Override
    public CompilationUnit getDstCu(PreprocessedData data){ return (CompilationUnit) data.getSrcCu(); }

    @Override
    public Object parseCu(String path){
        return JDTParserFactory.getCompilationUnit(path);
    }

    @Override
    public Object parseCu(byte[] raw){
        Object o = null;
        try {
            o = JDTParserFactory.getCompilationUnit(raw);
        }catch (Exception e){

        }
        assert (o!=null);
        return o;
    }

    @Override
    public int getLineNumber(Object o, int num) {
        assert(o instanceof CompilationUnit);
        CompilationUnit cu = (CompilationUnit) o;
        return cu.getLineNumber(num);
    }

    @Override
    public int getStartPosition(Object o){
        ASTNode node =  (ASTNode)o;
        return node.getStartPosition();
    }

    @Override
    public int getNodeLength(Object o){
        ASTNode node =  (ASTNode)o;
        return node.getLength();
    }

    @Override
    public void removeAllSrcComments(PreprocessedTempData tempData, Object o, List<Integer> lineList) {
        CompilationUnit cu = (CompilationUnit) o;
        PackageDeclaration packageDeclaration = cu.getPackage();
        if (packageDeclaration != null)
            tempData.addToSrcRemoveList(packageDeclaration);
        List<ASTNode> commentList = cu.getCommentList();
        for (int i = commentList.size() - 1; i >= 0; i--) {
            if(commentList.get(i) instanceof Javadoc){
                tempData.addToSrcRemoveList(commentList.get(i));
            }
        }
        List<ImportDeclaration> imprortss = cu.imports();
        for (int i = imprortss.size() - 1; i >= 0; i--) {
            tempData.addToSrcRemoveList(imprortss.get(i));
        }
        removeSrcRemovalList(tempData,cu,lineList);
    }
    @Override
    public void removeAllDstComments(PreprocessedTempData tempData, Object o, List<Integer> lineList) {
        CompilationUnit cu = (CompilationUnit) o;
        List<ASTNode> commentList = cu.getCommentList();
        PackageDeclaration packageDeclaration = cu.getPackage();
        if (packageDeclaration != null)
            tempData.addToDstRemoveList(packageDeclaration);
        List<ImportDeclaration> imprortss = cu.imports();
        for (int i = commentList.size() - 1; i >= 0; i--) {
            if(commentList.get(i) instanceof Javadoc) {
                tempData.addToDstRemoveList(commentList.get(i));
            }
        }
        for (int i = imprortss.size() - 1; i >= 0; i--) {
            tempData.addToDstRemoveList(imprortss.get(i));
        }
        removeDstRemovalList(tempData,cu,lineList);
    }
    private void removeSrcRemovalList(PreprocessedTempData tempData,CompilationUnit cu, List<Integer> lineList) {
        for (Object o : tempData.srcRemovalNodes) {
             ASTNode item = (ASTNode)o;
//            if(item instanceof MethodDeclaration){
//                MethodDeclaration md = (MethodDeclaration) item;
//                if(md.getName().toString().startsWith("create")){
//                    System.out.println(md.getName().toString());
//
//                }
//            }

//        	System.out.println(item.toString());
//            System.out.println(cu.getLineNumber(item.getStartPosition()) +"  "+cu.getLineNumber(item.getStartPosition()+item.getLength()-1));
            tempData.setLinesFlag(lineList, cu.getLineNumber(item.getStartPosition()),
                    cu.getLineNumber(item.getStartPosition() + item.getLength() - 1));

            item.delete();
        }
        tempData.srcRemovalNodes.clear();
    }
    private void removeDstRemovalList(PreprocessedTempData tempData,CompilationUnit cu, List<Integer> lineList) {
        for (Object o : tempData.dstRemovalNodes) {
            ASTNode item = (ASTNode)o;
//            if(item instanceof MethodDeclaration){
//                MethodDeclaration md = (MethodDeclaration) item;
//                if(md.getName().toString().startsWith("create")){
//                    System.out.println(md.getName().toString());
//
//                }
//            }

//        	System.out.println(item.toString());
//            System.out.println(cu.getLineNumber(item.getStartPosition()) +"  "+cu.getLineNumber(item.getStartPosition()+item.getLength()-1));
            tempData.setLinesFlag(lineList, cu.getLineNumber(item.getStartPosition()),
                    cu.getLineNumber(item.getStartPosition() + item.getLength() - 1));

            item.delete();
        }
        tempData.srcRemovalNodes.clear();
    }

    @Override
    public List<Object> getChildrenFromCu(Object o) {
        CompilationUnit cu = (CompilationUnit) o;
        return cu.types();
    }

    class SrcDstPair{
        TypeDeclaration tpSrc;
        TypeDeclaration tpDst;
    }
    @Override
    public int compareTwoFile(FilePairPreDiff preDiff,PreprocessedTempData tempData, PreprocessedData data){
        CompilationUnit cuSrc = (CompilationUnit) data.getSrcCu();
        CompilationUnit cuDst = (CompilationUnit) data.getDstCu();
        Queue<SrcDstPair> queue = new LinkedList<>();
        if(cuSrc.types().size() != cuDst.types().size()){
            return -1;
        }
//        if(Global.util.getChildrenFromCu(Global.util))
        for(int i = 0;i<cuSrc.types().size();i++){
            BodyDeclaration bodyDeclarationSrc = (BodyDeclaration) cuSrc.types().get(i);
            BodyDeclaration bodyDeclarationDst = (BodyDeclaration) cuDst.types().get(i);
            if ((bodyDeclarationSrc instanceof TypeDeclaration) && (bodyDeclarationDst instanceof TypeDeclaration)) {
                SrcDstPair srcDstPair = new SrcDstPair();
                srcDstPair.tpSrc = (TypeDeclaration) bodyDeclarationSrc;
                srcDstPair.tpDst = (TypeDeclaration) bodyDeclarationDst;
                queue.offer(srcDstPair);
            }else{
                return -1;
            }
        }
        while(queue.size()!=0){
            SrcDstPair tmp = queue.poll();
            compare(cuSrc,cuDst,tmp.tpSrc,tmp.tpDst,data,tempData,preDiff);
        }
        return 0;
    }
    private void compare(CompilationUnit cuSrc,CompilationUnit cuDst,TypeDeclaration tdSrc,TypeDeclaration tdDst,PreprocessedData data,PreprocessedTempData tempData,FilePairPreDiff preDiff){
        TypeNodesTraversalJava astTraversal = new TypeNodesTraversalJava();
        addSuperClass(tdSrc,data.getInterfacesAndFathers());
        addSuperClass(tdDst,data.getInterfacesAndFathers());
        astTraversal.traverseSrcTypeDeclarationInit(data, tempData, tdSrc, tdSrc.getName().toString() + ".");
        astTraversal.traverseDstTypeDeclarationCompareSrc(data, tempData, tdDst, tdDst.getName().toString() + ".");
        preDiff.iterateVisitingMap();
        preDiff.undeleteSignatureChange();
        removeSrcRemovalList(tempData,cuSrc,data.srcLines);
        removeDstRemovalList(tempData,cuDst, data.dstLines);
        preDiff.iterateVisitingMap2LoadContainerMap();
    }
    private void addSuperClass(TypeDeclaration type,List<String> list){
        List<Type> aa  = type.superInterfaceTypes();
        List<ASTNode> modifiers = type.modifiers();
        for(ASTNode node:modifiers){
            if(node instanceof Modifier){
                Modifier modifier = (Modifier)node;
                if(modifier.toString().equals("abstract")){
                    list.add("abstract---"+type.getName().toString());
                }
            }
        }
        if(aa!=null) {
            for (Type aaa : aa) {
                list.add("interface---"+aaa.toString());
            }
        }

        if(type.getSuperclassType()!=null) {
            list.add("superclass---"+type.getSuperclassType().toString());
        }
    }

    @Override
    public String BodyDeclarationPairToString(BodyDeclarationPair pair) {
        String result = pair.getLocationClassString() +" ";
        if(pair.getBodyDeclaration() instanceof TypeDeclaration){
            TypeDeclaration td = (TypeDeclaration)pair.getBodyDeclaration();
            result += td.getClass().getSimpleName()+": "+td.getName().toString();
        }else if(pair.getBodyDeclaration() instanceof FieldDeclaration){
            FieldDeclaration td = (FieldDeclaration)pair.getBodyDeclaration();
            result += td.getClass().getSimpleName()+": "+td.fragments().toString();
        }else if(pair.getBodyDeclaration() instanceof MethodDeclaration) {
            MethodDeclaration td = (MethodDeclaration) pair.getBodyDeclaration();
            result += td.getClass().getSimpleName() + ": " + td.getName().toString();
        }
        return result;
    }

    @Override
    public boolean isTypeDeclaration(Object o){
        if(o instanceof TypeDeclaration){
            return true;
        }
        return false;
    }

    public boolean isMethodDeclaration(Object o){
        if(o instanceof MethodDeclaration){
            return true;
        }
        return false;
    }
    @Override
    public String getMethodName(Object o){
        MethodDeclaration md = (MethodDeclaration) o;
        return md.getName().toString();
    }

    @Override
    public void preProcess(PreprocessedTempData tempData){

    }


}
