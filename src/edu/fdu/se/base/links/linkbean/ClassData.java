package edu.fdu.se.base.links.linkbean;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.tree.Tree;
import edu.fdu.se.base.generatingactions.JavaParserVisitorC;
import edu.fdu.se.base.miningactions.util.MyList;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntityDesc;
import edu.fdu.se.base.miningchangeentity.member.ClassChangeEntity;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.jdt.core.dom.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by huangkaifeng on 2018/4/7.
 *
 */
public class ClassData extends LinkBean {


    public ClassData(ClassChangeEntity ce) {
        interfacesAndSuperClazz = new MyList<>();
        methods = new MyList<>();
        fields = new MyList<>();
        fieldType = new MyList<>();
        if(ce.stageIIBean==null
            || ce.stageIIBean.getEntityCreationStage().equals(ChangeEntityDesc.StageIIGenStage.ENTITY_GENERATION_STAGE_PRE_DIFF)){
            IASTSimpleDeclaration td = (IASTSimpleDeclaration) ce.bodyDeclarationPair.getBodyDeclaration();
            this.clazzName = ((IASTCompositeTypeSpecifier)td.getDeclSpecifier()).getName().toString();
            List<IASTNode> aa  = Arrays.asList(((ICPPASTCompositeTypeSpecifier) td.getDeclSpecifier()).getBaseSpecifiers());
            for(IASTNode aaa:aa) {
                interfacesAndSuperClazz.add(aaa.toString());
            }
            //MethodDeclaration[] mehtodss = td.getMethods();
            IASTNode[] children = td.getChildren();
            for(IASTNode n :children){
                if(n instanceof IASTFunctionDefinition) {
                    IASTFunctionDefinition fd = (IASTFunctionDefinition) n;
                    methods.add(((IASTFunctionDefinition) n).getDeclarator().getName().toString());
                }
            }
            //FieldDeclaration[] fielddd = td.getFields();
            for(IASTNode n:children){
                if(n instanceof IASTSimpleDeclaration && (((IASTSimpleDeclaration)n).getDeclSpecifier() instanceof IASTSimpleDeclSpecifier ||((IASTSimpleDeclaration)n).getDeclSpecifier() instanceof IASTNamedTypeSpecifier)){
                    IASTSimpleDeclaration fd = (IASTSimpleDeclaration) n;
                    List<IASTDeclarator> mmList = Arrays.asList(fd.getDeclarators());
                    for (IASTDeclarator vd : mmList) {
                        fields.add(vd.getName().toString());
                    }
                    fieldType.add(fd.getDeclSpecifier().toString());
                }
            }
//            for(FieldDeclaration fdd:fielddd){
//                List<VariableDeclarationFragment> list = fdd.fragments();
//                for(VariableDeclarationFragment vd:list){
//                    fields.add(vd.getName().toString());
//                }
//                fieldType.add(fdd.getType().toString());
//            }

        }else{
            if(ce.clusteredActionBean.curAction instanceof Move){

            }else{
                parseNonMove(ce);
            }
        }
    }
    private List<String> interfacesAndSuperClazz;
    public String clazzName;
    public List<String> methods;
    public List<String> fields;
    public List<String> fieldType;


    public void parseNonMove(ClassChangeEntity ce){
        Tree tree = (Tree)ce.clusteredActionBean.curAction.getNode();
        List<String> tempinterfacesAndSuperClazz = new MyList<>();
        String tempClassName = null;
        if(JavaParserVisitorC.getNodeTypeId(tree.getAstNodeC()) == JavaParserVisitorC.TYPE_DECLARATION) {
            IASTSimpleDeclaration td = (IASTSimpleDeclaration) ce.bodyDeclarationPair.getBodyDeclaration();
            tempClassName = ((IASTCompositeTypeSpecifier)td.getDeclSpecifier()).getName().toString();
            List<IASTNode> aa  = Arrays.asList(((ICPPASTCompositeTypeSpecifier) td.getDeclSpecifier()).getBaseSpecifiers());
            for(IASTNode aaa:aa) {
                interfacesAndSuperClazz.add(aaa.toString());
            }

        }
        for(Action a:ce.clusteredActionBean.actions){
            Tree t = (Tree) a.getNode();
            if (JavaParserVisitorC.getNodeTypeId(t.getAstNodeC()) == JavaParserVisitorC.NAME
                    || t.getAstNodeC().getClass().getSimpleName().endsWith("Literal")) {
                if(tempinterfacesAndSuperClazz.contains(t.getLabel())){
                    interfacesAndSuperClazz.add(t.getLabel());
                }
                if(tempClassName!=null &&tempClassName.equals(t.getLabel())){
                    clazzName = t.getLabel();
                }
            }
        }
    }



}
