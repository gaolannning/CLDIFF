package edu.fdu.se.lang.c.generatingactions;

import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.TreeContext;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntityDesc;
import edu.fdu.se.lang.common.generatingactions.ParserTreeGenerator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

public class CParserTreeGenerator extends ParserTreeGenerator {
    public CParserTreeGenerator(Object prev, Object curr){
        IASTTranslationUnit prev1 = (IASTTranslationUnit) prev;
        IASTTranslationUnit curr1 = (IASTTranslationUnit)curr;
        srcTC = generateFromCompilationUnit(prev1, ChangeEntityDesc.StageITreeType.SRC_TREE_NODE);
        src = srcTC.getRoot();
        dstTC = generateFromCompilationUnit(curr1,ChangeEntityDesc.StageITreeType.DST_TREE_NODE);
        dst = dstTC.getRoot();
        Matcher m = Matchers.getInstance().getMatcher(src, dst);
        m.match();
        mapping = m.getMappings();
    }
    private TreeContext generateFromCompilationUnit(IASTTranslationUnit cu, int srcOrDst) {
        CParserVisitor visitor = new CParserVisitor(srcOrDst);
        visitor.getTreeContext().setTu(cu);
        IASTNode astNode = cu;
        setShouldVisit(visitor);
        astNode.accept(visitor);
        TreeContext ctx = visitor.getTreeContext();
        ctx.validate();
        return ctx;
    }
    private void setShouldVisit(CParserVisitor visitorC){
        visitorC.shouldVisitTranslationUnit = true;
        visitorC.shouldVisitArrayModifiers  = true;
        visitorC.shouldVisitAttributes  = true;
        visitorC.shouldVisitBaseSpecifiers   = true;
        visitorC.shouldVisitCaptures  = true;
        visitorC.shouldVisitDeclarations   = true;
        visitorC.shouldVisitDeclarators  = true;
        visitorC.shouldVisitDeclSpecifiers   = true;
        visitorC.shouldVisitDesignators  = true;
        visitorC.shouldVisitEnumerators  = true;
        visitorC.shouldVisitExpressions = true;
        visitorC.shouldVisitImplicitNameAlternates  = true;
        visitorC.shouldVisitImplicitNames = true;
        visitorC.shouldVisitInitializers  = true;
        visitorC.shouldVisitNames = true;
        visitorC.shouldVisitNamespaces  = true;
        visitorC.shouldVisitParameterDeclarations = true;
        visitorC.shouldVisitPointerOperators   = true;
        visitorC.shouldVisitProblems = true;
        visitorC.shouldVisitStatements  = true;
        visitorC.shouldVisitTemplateParameters = true;
        visitorC.shouldVisitTokens  = true;
        visitorC.shouldVisitTranslationUnit = true;
        visitorC.shouldVisitTypeIds  = true;
    }
}
