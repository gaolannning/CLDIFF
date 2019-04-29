package edu.fdu.se.lang.java;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.Tree;
import edu.fdu.se.base.common.Global;
import edu.fdu.se.base.miningactions.Body.*;
import edu.fdu.se.base.miningactions.bean.MiningActionData;
import edu.fdu.se.base.miningactions.statement.*;
import edu.fdu.se.base.miningactions.util.AstRelations;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntity;
import edu.fdu.se.base.preprocessingfile.FilePairPreDiff;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedData;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedTempData;
import edu.fdu.se.lang.common.Util;
import edu.fdu.se.lang.java.preprocess.TypeNodesTraversalJava;
import org.eclipse.jdt.core.dom.*;

import java.util.*;

public class UtilJava implements Util {
    @Override
    public CompilationUnit getSrcCu(PreprocessedData data){ return (CompilationUnit) data.getSrcCu(); }

    @Override
    public CompilationUnit getDstCu(PreprocessedData data){ return (CompilationUnit) data.getDstCu(); }

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
    public int getPositionFromLine(Object o,int line){
        CompilationUnit cu = (CompilationUnit) o;
        String[] s= cu.toString().split("\n");
        int[] lineCnt = new int[s.length];
        for(int i = 0;i<s.length;i++){
            lineCnt[i] = s[i].length()+1;
        }
        if(line>s.length){
            return -1;
        }
        int cnt = 0;
        for(int i = 0;i<line;i++){
            cnt += lineCnt[i];
        }
        return cnt;
    }

    @Override
    public int getNodeTypeId(Object o){
        ASTNode node = (ASTNode)o;
        return node.getNodeType();
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
    public String BodyDeclarationToString(Object o) {
        return o.toString();
    }

    @Override
    public boolean isIf(Object o){
        if(getNodeTypeId(o)==ASTNode.IF_STATEMENT){
            return true;
        }
        return false;
    }

    @Override
    public boolean isTypeDeclaration(Object o){
        if(o instanceof TypeDeclaration){
            return true;
        }
        return false;
    }
    @Override
    public boolean isMethodDeclaration(Object o){
        if(o instanceof MethodDeclaration){
            return true;
        }
        return false;
    }

    @Override
    public boolean isFieldDeclaration(Object o){
        ASTNode node = (ASTNode)o;
        if(node instanceof FieldDeclaration){
            return true;
        }
        return false;
    }

    @Override
    public boolean isEnumDeclaration(Object o){
        ASTNode n = (ASTNode)o;
        if(n instanceof EnumDeclaration){
            return true;
        }
        return false;
    }

    @Override
    public boolean isMethodInvocation(Object o){
        ASTNode n = (ASTNode)o;
        if(n instanceof MethodInvocation){
            return true;
        }
        return false;
    }

    @Override
    public boolean isClassInstanceCreation(Object o){
        if(o instanceof ClassInstanceCreation){
            return true;
        }
        return false;
    }

    @Override
    public boolean isSingleVariableDeclaration(Object o){
        ASTNode n = (ASTNode)o;
        if(n instanceof SingleVariableDeclaration){
            return true;
        }
        return false;
    }

    @Override
    public boolean isLiteral(Tree tree){
        return getNodeTypeId(tree.getNode()) == ASTNode.SIMPLE_NAME
                || tree.getNode().getClass().getSimpleName().endsWith("Literal");
    }

    @Override
    public boolean isCompilationUnit(Object o){
        ASTNode n = (ASTNode)o;
        if(n instanceof CompilationUnit){
            return true;
        }
        return false;
    }

    @Override
    public boolean isBlock(Object o){
        ASTNode n = (ASTNode)o;
        if(n instanceof Block){
            return true;
        }
        return false;
    }

    @Override
    public boolean isSwitchCase(Object o){
        ASTNode n = (ASTNode)o;
        if(n instanceof SwitchCase){
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
    public String getMethodInvocationName(Object o){
        MethodInvocation mi = (MethodInvocation)o;
        return mi.getName().toString();
    }

    @Override
    public String getClassCreationName(Object o){
        ClassInstanceCreation n = (ClassInstanceCreation) o;
        return n.getType().toString();
    }

    @Override
    public String getFieldType(Object o){
        FieldDeclaration fd = (FieldDeclaration) o;
        return fd.getType().toString();
    }

    @Override
    public List<Object> getSingleVariableDeclarations(Object o){
        MethodDeclaration md = (MethodDeclaration) o;
        return md.parameters();
    }

    @Override
    public String getSingleVariableDeclarationName(Object o){
        SingleVariableDeclaration n = (SingleVariableDeclaration) o;
        return n.getName().toString();
    }

    @Override
    public String getSingleVariableDeclarationTypeName(Object o){
        SingleVariableDeclaration n = (SingleVariableDeclaration) o;
        return n.getType().toString();
    }

    @Override
    public Object getMethodType(Object o){
        MethodDeclaration md = (MethodDeclaration)o;
        return md.getReturnType2();
    }

    @Override
    public String getTypeName(Object o){
        TypeDeclaration td = (TypeDeclaration) o;
        return td.getName().toString();
    }

    @Override
    public List<String> getBaseTypeName(Object o){
        TypeDeclaration td = (TypeDeclaration) o;
        List<String> s = new ArrayList<String>();
        List<Type> aa  = td.superInterfaceTypes();
        for(Type aaa:aa) {
            s.add(aaa.toString());
        }
        if(td.getSuperclassType()!=null) {
            s.add(td.getSuperclassType().toString());
        }
        return s;
    }

//    @Override
    public List<Object> getChildren(Object o){
        return null;
    }

    @Override
    public List<Object> getFunctionFromType(Object o){
        TypeDeclaration n = (TypeDeclaration) o;
        return Arrays.asList(n.getMethods());
    }

    @Override
    public List<Object> getFieldFromType(Object o){
        TypeDeclaration n = (TypeDeclaration) o;
        return Arrays.asList(n.getFields());
    }


    @Override
    public void preProcess(PreprocessedTempData tempData){

    }

    @Override
    public Tree findFafatherNode(ITree node){
        int type;
        Tree curNode = (Tree)node;
        while (true) {
            type = getNodeTypeId(curNode.getNode());
            boolean isEnd = false;
            switch (type) {
                case ASTNode.TYPE_DECLARATION:
                case ASTNode.METHOD_DECLARATION:
                case ASTNode.FIELD_DECLARATION:
                case ASTNode.ENUM_DECLARATION:
//                case ASTNode.BLOCK:
                case ASTNode.ASSERT_STATEMENT:
                case ASTNode.THROW_STATEMENT:
                case ASTNode.RETURN_STATEMENT:
                case ASTNode.DO_STATEMENT:
                case ASTNode.IF_STATEMENT:
                case ASTNode.WHILE_STATEMENT:
                case ASTNode.ENHANCED_FOR_STATEMENT:
                case ASTNode.FOR_STATEMENT:
                case ASTNode.TRY_STATEMENT:
                case ASTNode.SWITCH_STATEMENT:
                case ASTNode.SWITCH_CASE:
                case ASTNode.CATCH_CLAUSE:
                case ASTNode.EXPRESSION_STATEMENT:
                case ASTNode.VARIABLE_DECLARATION_STATEMENT:
                case ASTNode.SYNCHRONIZED_STATEMENT:
                case ASTNode.CONSTRUCTOR_INVOCATION:
                case ASTNode.SUPER_CONSTRUCTOR_INVOCATION:
                case ASTNode.LABELED_STATEMENT:
                    isEnd = true;
                default:break;
            }

            if(isEnd){
                break;
            }
//            try {
            curNode = (Tree) curNode.getParent();
//            }catch(Exception e){
//                System.out.println("a");
//            }
        }
        return curNode;
    }

    @Override
    public void matchNodeNewEntity(MiningActionData fp, Action a, Tree queryFather, int treeType, Tree traverseFather){
        int nodeType = Global.util.getNodeTypeId(traverseFather.getNode());
        switch (nodeType) {
            case ASTNode.TYPE_DECLARATION:
                MatchClass.matchClassSignatureNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case ASTNode.FIELD_DECLARATION:
                MatchFieldDeclaration.matchFieldDeclarationChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case ASTNode.INITIALIZER:
                break;
            case ASTNode.METHOD_DECLARATION:
                if (getNodeTypeId(((Tree) a.getNode()).getNode()) != ASTNode.BLOCK) {
                    MatchMethod.matchMethodSignatureChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                }
                break;
            case ASTNode.ENUM_DECLARATION:
            case ASTNode.ENUM_CONSTANT_DECLARATION:
                MatchEnum.matchEnumDeclarationNewEntity(fp,a,queryFather,treeType,traverseFather);
                break;
            case ASTNode.IF_STATEMENT:
                MatchIfElse.matchIfPredicateChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case ASTNode.FOR_STATEMENT:
                MatchForStatement.matchForConditionChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case ASTNode.WHILE_STATEMENT:
                MatchWhileStatement.matchWhileConditionChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case ASTNode.DO_STATEMENT:
                MatchWhileStatement.matchDoConditionChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case ASTNode.ENHANCED_FOR_STATEMENT:
                MatchForStatement.matchEnhancedForConditionChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case ASTNode.VARIABLE_DECLARATION_STATEMENT:
                MatchVariableDeclarationExpression.matchVariableDeclarationNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case ASTNode.EXPRESSION_STATEMENT:
                MatchExpressionStatement.matchExpressionChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case ASTNode.RETURN_STATEMENT:
                MatchReturnStatement.matchReturnChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case ASTNode.ASSERT_STATEMENT:
                MatchAssert.matchAssertChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case ASTNode.CATCH_CLAUSE:
                MatchTry.matchCatchChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case ASTNode.SYNCHRONIZED_STATEMENT:
                MatchSynchronized.matchSynchronizedChangeNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case ASTNode.SWITCH_STATEMENT:
                MatchSwitch.matchSwitchNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case ASTNode.SWITCH_CASE:
                MatchSwitch.matchSwitchCaseNewEntity(fp, a, queryFather,treeType, traverseFather);
                break;
            case ASTNode.SUPER_CONSTRUCTOR_INVOCATION:
//                System.err.println("aaa-----");
                MatchConstructorInvocation.matchSuperConstructorInvocationNewEntity(fp,a,queryFather,treeType,traverseFather);
                break;
            case ASTNode.CONSTRUCTOR_INVOCATION:
                MatchConstructorInvocation.matchConstructorInvocationNewEntity(fp,a,queryFather,treeType,traverseFather);
                break;
            case ASTNode.LABELED_STATEMENT:
                MatchLabeledStatement.matchLabeledStatementNewEntity(fp,a,queryFather,treeType,traverseFather);
                break;
            case ASTNode.THROW_STATEMENT:
                MatchTry.matchThrowStatementNewEntity(fp, a, queryFather, treeType, traverseFather);
                break;
            default:
                break;
        }
    }

    @Override
    public String getLocationString(Object o) {
        String result="";
        ASTNode node = (ASTNode)o;
        while(!(node instanceof CompilationUnit)){
            if(isTypeDeclaration(node)){
                TypeDeclaration tp  = (TypeDeclaration) node;
                result = tp.getName().toString()+"."+ result;
            }
            node = node.getParent();
        }
        return result;
    }

    @Override
    public List<String> getFieldDeclaratorNames(Object o){
        FieldDeclaration fd = (FieldDeclaration) o;
        List<VariableDeclarationFragment> list = fd.fragments();
        List<String> s = new ArrayList<String>();
        for(VariableDeclarationFragment vd:list){
            s.add(vd.toString());
        }
        return s;
    }

    public Object findExpression(Tree tree){
        int flag = 0;
        while (!tree.getNode().getClass().getSimpleName().endsWith("Declaration")) {
            tree = (Tree) tree.getParent();
            switch (Global.util.getNodeTypeId(tree.getNode())) {
                case ASTNode.NORMAL_ANNOTATION:
                case ASTNode.MARKER_ANNOTATION:
                case ASTNode.SINGLE_MEMBER_ANNOTATION:
                case ASTNode.ARRAY_CREATION:
                case ASTNode.ARRAY_INITIALIZER:
                case ASTNode.ASSIGNMENT:
                case ASTNode.BOOLEAN_LITERAL:
                case ASTNode.CAST_EXPRESSION:
                case ASTNode.CHARACTER_LITERAL:
                case ASTNode.CLASS_INSTANCE_CREATION:
                case ASTNode.CONDITIONAL_EXPRESSION:
                case ASTNode.CREATION_REFERENCE:
                case ASTNode.EXPRESSION_METHOD_REFERENCE:
                case ASTNode.FIELD_ACCESS:
                case ASTNode.INFIX_EXPRESSION:
                case ASTNode.INSTANCEOF_EXPRESSION:
                case ASTNode.LAMBDA_EXPRESSION:
                case ASTNode.METHOD_INVOCATION:
                case ASTNode.SIMPLE_NAME:
                case ASTNode.QUALIFIED_NAME:
                case ASTNode.NULL_LITERAL:
                case ASTNode.NUMBER_LITERAL:
                case ASTNode.PARENTHESIZED_EXPRESSION:
                case ASTNode.POSTFIX_EXPRESSION:
                case ASTNode.PREFIX_EXPRESSION:
                case ASTNode.STRING_LITERAL:
                case ASTNode.SUPER_FIELD_ACCESS:
                case ASTNode.SUPER_METHOD_INVOCATION:
                case ASTNode.SUPER_METHOD_REFERENCE:
                case ASTNode.THIS_EXPRESSION:
                case ASTNode.TYPE_LITERAL:
                case ASTNode.TYPE_METHOD_REFERENCE:
                case ASTNode.VARIABLE_DECLARATION_EXPRESSION:
                    flag = 1;
                    break;
            }
            if (flag == 1) {
                return tree.getNode();
            }
        }
        return null;
    }

    @Override
    public int processBigAction(MiningActionData fp,Action a,int type){
        int res = 0;
        switch (type) {
            // 外面
            case ASTNode.TYPE_DECLARATION:
                MatchClass.matchClassDeclaration(fp, a);
                break;
            case ASTNode.FIELD_DECLARATION:
                MatchFieldDeclaration.matchFieldDeclaration(fp, a);
                break;
            case ASTNode.INITIALIZER:
                MatchInitializerBlock.matchInitializerBlock(fp, a);
                break;
            case ASTNode.METHOD_DECLARATION:
                MatchMethod.matchMethdDeclaration(fp, a);
                break;
            case ASTNode.ENUM_DECLARATION:
            case ASTNode.ENUM_CONSTANT_DECLARATION:
                MatchEnum.matchEnum(fp,a);
                break;

            // 里面
            case ASTNode.ASSERT_STATEMENT:
                MatchAssert.matchAssert(fp,a);
                break;
            case ASTNode.IF_STATEMENT:
                MatchIfElse.matchIf(fp, a);
                break;
            case ASTNode.BLOCK:
                MatchBlock.matchBlock(fp, a);
                break;
            case ASTNode.BREAK_STATEMENT:
                MatchControlStatements.matchBreakStatements(fp,a);
                break;
            case ASTNode.CONTINUE_STATEMENT:
                MatchControlStatements.matchContinueStatements(fp,a);
            case ASTNode.RETURN_STATEMENT:
                MatchReturnStatement.matchReturnStatement(fp, a);
                break;
            case ASTNode.FOR_STATEMENT:
                //增加for语句
                MatchForStatement.matchForStatement(fp, a);
                break;
            case ASTNode.ENHANCED_FOR_STATEMENT:
                //增加for语句
                MatchForStatement.matchEnhancedForStatement(fp, a);
                break;
            case ASTNode.WHILE_STATEMENT:
                //增加while语句
                MatchWhileStatement.matchWhileStatement(fp, a);
                break;
            case ASTNode.DO_STATEMENT:
                //增加do while语句
                MatchWhileStatement.matchDoStatement(fp, a);
                break;
            case ASTNode.TRY_STATEMENT:
                MatchTry.matchTry(fp, a);
                break;
            case ASTNode.THROW_STATEMENT:
                MatchTry.matchThrowStatement(fp, a);
                break;
            case ASTNode.CATCH_CLAUSE:
                MatchTry.matchCatchClause(fp,a);
                break;
            case ASTNode.VARIABLE_DECLARATION_STATEMENT:
                MatchVariableDeclarationExpression.matchVariableDeclaration(fp, a);
                break;
            case ASTNode.EXPRESSION_STATEMENT:
                if (AstRelations.isFatherXXXStatement(a, ASTNode.IF_STATEMENT) && a.getNode().getParent().getChildPosition(a.getNode()) == 2) {
                    MatchIfElse.matchElse(fp, a);
                } else {
                    MatchExpressionStatement.matchExpression(fp, a);
                }
                break;
            case ASTNode.SYNCHRONIZED_STATEMENT:
                MatchSynchronized.matchSynchronized(fp, a);
                break;
            case ASTNode.SWITCH_STATEMENT:
                MatchSwitch.matchSwitch(fp, a);
                break;
            case ASTNode.SWITCH_CASE:
                MatchSwitch.matchSwitchCase(fp, a);
                break;
            case ASTNode.EMPTY_STATEMENT:
                break;
            case ASTNode.TYPE_DECLARATION_STATEMENT:
                break;
            case ASTNode.CONSTRUCTOR_INVOCATION:
                MatchConstructorInvocation.matchConstructorInvocation(fp,a);
                break;
            case ASTNode.SUPER_CONSTRUCTOR_INVOCATION:
                MatchConstructorInvocation.matchSuperConstructorInvocation(fp,a);
                break;
            case ASTNode.LABELED_STATEMENT:
                MatchLabeledStatement.matchLabeledStatement(fp,a);
                break;
            default:
                res =1;
                break;
        }
        return  res;
    }

    @Override
    public void matchXXXChangeCurEntity(MiningActionData fp, Action a, ChangeEntity changeEntity, int nodeType, Tree traverseFather){
        switch (nodeType) {
            case ASTNode.TYPE_DECLARATION:
                MatchClass.matchClassSignatureCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case ASTNode.FIELD_DECLARATION:
                MatchFieldDeclaration.matchFieldDeclarationChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case ASTNode.INITIALIZER:
                break;
            case ASTNode.METHOD_DECLARATION:
                if (!isBlock(((Tree) a.getNode()).getNode())) {
                    MatchMethod.matchMethodSignatureChangeCurrEntity(fp, a, changeEntity, traverseFather);
                }
                break;
            case ASTNode.ENUM_DECLARATION:
            case ASTNode.ENUM_CONSTANT_DECLARATION:
                MatchEnum.matchEnumDeclarationCurrEntity(fp,a,changeEntity,traverseFather);
                break;
            case ASTNode.IF_STATEMENT:
                MatchIfElse.matchIfPredicateChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case ASTNode.FOR_STATEMENT:
                MatchForStatement.matchForConditionChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case ASTNode.WHILE_STATEMENT:
                MatchWhileStatement.matchWhileConditionChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case ASTNode.DO_STATEMENT:
                MatchWhileStatement.matchDoConditionChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case ASTNode.ENHANCED_FOR_STATEMENT:
                MatchForStatement.matchEnhancedForConditionChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case ASTNode.VARIABLE_DECLARATION_STATEMENT:
                MatchVariableDeclarationExpression.matchVariableDeclarationCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case ASTNode.EXPRESSION_STATEMENT:
                MatchExpressionStatement.matchExpressionChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;

            case ASTNode.RETURN_STATEMENT:
                MatchReturnStatement.matchReturnChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case ASTNode.ASSERT_STATEMENT:
                MatchAssert.matchAssertChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case ASTNode.CATCH_CLAUSE:
                MatchTry.matchCatchChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case ASTNode.SYNCHRONIZED_STATEMENT:
                MatchSynchronized.matchSynchronizedChangeCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case ASTNode.SWITCH_STATEMENT:
                MatchSwitch.matchSwitchCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case ASTNode.SWITCH_CASE:
                MatchSwitch.matchSwitchCaseCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            case ASTNode.SUPER_CONSTRUCTOR_INVOCATION:
                MatchConstructorInvocation.matchSuperConstructorInvocationCurrEntity(fp,a,changeEntity,traverseFather);
                break;
            case ASTNode.CONSTRUCTOR_INVOCATION:
                MatchConstructorInvocation.matchConstructorInvocationCurrEntity(fp,a,changeEntity,traverseFather);
                break;
            case ASTNode.LABELED_STATEMENT:
                MatchLabeledStatement.matchLabeledStatementCurrEntity(fp,a,changeEntity,traverseFather);
                break;
            case ASTNode.THROW_STATEMENT:
                MatchTry.matchThrowStatementCurrEntity(fp, a, changeEntity, traverseFather);
                break;
            default:
                break;
        }
    }


    @Override
    public int getGeneratingExpressionsType(int type){
        int flag = 0;
        switch(type){

            case ASTNode.ASSIGNMENT:
            case ASTNode.CLASS_INSTANCE_CREATION:
            case ASTNode.CONDITIONAL_EXPRESSION:
            case ASTNode.CREATION_REFERENCE:
            case ASTNode.EXPRESSION_METHOD_REFERENCE:
            case ASTNode.FIELD_ACCESS:
            case ASTNode.INFIX_EXPRESSION:
            case ASTNode.INSTANCEOF_EXPRESSION:
            case ASTNode.LAMBDA_EXPRESSION:
            case ASTNode.METHOD_INVOCATION:
            case ASTNode.PARENTHESIZED_EXPRESSION:
            case ASTNode.POSTFIX_EXPRESSION:
            case ASTNode.PREFIX_EXPRESSION:
            case ASTNode.SUPER_FIELD_ACCESS:
            case ASTNode.SUPER_METHOD_INVOCATION:
            case ASTNode.SUPER_METHOD_REFERENCE:
            case ASTNode.THIS_EXPRESSION:
            case ASTNode.TYPE_METHOD_REFERENCE:
            case ASTNode.VARIABLE_DECLARATION_EXPRESSION:
            case ASTNode.TYPE_LITERAL:
                flag = 2; break;
            case ASTNode.CHARACTER_LITERAL:
            case ASTNode.BOOLEAN_LITERAL:

            case ASTNode.SIMPLE_NAME:
            case ASTNode.STRING_LITERAL:
            case ASTNode.NULL_LITERAL:
            case ASTNode.NUMBER_LITERAL:
            case ASTNode.QUALIFIED_NAME:
                flag = 1;break;
            default:break;
        }
        return flag;
    }

    @Override
    public void matchBlock(MiningActionData fp, Action a,int type,Tree fatherNode){
        switch (type) {
            case ASTNode.SWITCH_STATEMENT:
//                MatchSwitch.matchSwitchCaseNewEntity(fp,a);
                fp.setActionTraversedMap(a);
                break;
            case ASTNode.IF_STATEMENT:
                //Pattern 1.2 Match else
                if (fatherNode.getChildPosition(a.getNode()) == 2) {
                    MatchIfElse.matchElse(fp, a);
                }
                fp.setActionTraversedMap(a);
                break;
            case ASTNode.TRY_STATEMENT:
                ////Finally块
                if (fatherNode.getChildPosition(a.getNode()) == fatherNode.getChildren().size() - 1) {
                    MatchTry.matchFinally(fp, a);
                }
                fp.setActionTraversedMap(a);
                break;
            default:
                fp.setActionTraversedMap(a);
                break;
        }

    }

}
