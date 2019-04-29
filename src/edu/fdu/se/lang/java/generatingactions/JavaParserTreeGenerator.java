package edu.fdu.se.lang.java.generatingactions;

import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.TreeContext;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntityDesc;
import edu.fdu.se.lang.common.generatingactions.ParserTreeGenerator;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class JavaParserTreeGenerator extends ParserTreeGenerator {
    public JavaParserTreeGenerator(Object prev, Object curr){
        CompilationUnit prev1 = (CompilationUnit)prev;
        CompilationUnit curr1 = (CompilationUnit)curr;
        srcTC = generateFromCompilationUnit(prev1, ChangeEntityDesc.StageITreeType.SRC_TREE_NODE);
        src = srcTC.getRoot();
        dstTC = generateFromCompilationUnit(curr1,ChangeEntityDesc.StageITreeType.DST_TREE_NODE);
        dst = dstTC.getRoot();
        Matcher m = Matchers.getInstance().getMatcher(src, dst);
        m.match();
        mapping = m.getMappings();
    }
    private TreeContext generateFromCompilationUnit(CompilationUnit cu, int srcOrDst) {
        JavaParserVisitor visitor = new JavaParserVisitor(srcOrDst);
        visitor.getTreeContext().setCu(cu);
        ASTNode astNode = cu;
        astNode.accept(visitor);
        TreeContext ctx = visitor.getTreeContext();
        ctx.validate();
        return ctx;
    }
}
