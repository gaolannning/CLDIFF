package edu.fdu.se.base.links.linkbean;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.tree.Tree;
import edu.fdu.se.base.common.Global;
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
            Object td =  ce.bodyDeclarationPair.getBodyDeclaration();
            this.clazzName = Global.util.getTypeName(td);
            List<String> aa  = Global.util.getBaseTypeName(td);
            interfacesAndSuperClazz.addAll(aa);
            //MethodDeclaration[] mehtodss = td.getMethods();
            List<Object> funcs = Global.util.getFunctionFromType(td);
//            IASTNode[] children = td.getChildren();
            for(Object n :funcs){
                methods.add(Global.util.getMethodName(n));
//                if(n instanceof IASTFunctionDefinition) {
//                    IASTFunctionDefinition fd = (IASTFunctionDefinition) n;
//                    methods.add(((IASTFunctionDefinition) n).getDeclarator().getName().toString());
//                }
            }
            //FieldDeclaration[] fielddd = td.getFields();
            List<Object> fields = Global.util.getFieldFromType(td);
            for(Object n:fields){
                fields.addAll(Global.util.getFieldDeclaratorNames(n));
                fieldType.add(Global.util.getFieldType(n));
//                if(n instanceof IASTSimpleDeclaration && (((IASTSimpleDeclaration)n).getDeclSpecifier() instanceof IASTSimpleDeclSpecifier ||((IASTSimpleDeclaration)n).getDeclSpecifier() instanceof IASTNamedTypeSpecifier)){
//                    IASTSimpleDeclaration fd = (IASTSimpleDeclaration) n;
//                    List<IASTDeclarator> mmList = Arrays.asList(fd.getDeclarators());
//                    for (IASTDeclarator vd : mmList) {
//                        fields.add(vd.getName().toString());
//                    }
//                    fieldType.add(fd.getDeclSpecifier().toString());
//                }
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
        if(Global.util.isTypeDeclaration(tree.getNode())) {
            Object td =  ce.bodyDeclarationPair.getBodyDeclaration();
            tempClassName = Global.util.getTypeName(td);
            List<String> aa  = Global.util.getBaseTypeName(td);
            interfacesAndSuperClazz.addAll(aa);

        }
        for(Action a:ce.clusteredActionBean.actions){
            Tree t = (Tree) a.getNode();
            if (Global.util.isLiteral(t)) {
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
