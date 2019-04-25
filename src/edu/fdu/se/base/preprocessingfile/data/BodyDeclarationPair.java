package edu.fdu.se.base.preprocessingfile.data;


import edu.fdu.se.base.common.Global;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * Created by huangkaifeng on 2018/1/22.
 *
 */
public class BodyDeclarationPair {
    private Object bd;

    private String locationClassString;

    public BodyDeclarationPair(Object bd1,String str){
        this.bd = bd1;
        this.locationClassString = str;
        String a = Global.util.BodyDeclarationToString(bd1);
        String hashStr = String.valueOf( Global.util.BodyDeclarationToString(bd1).hashCode())+String.valueOf(str.hashCode());
        this.hashCode = hashStr.hashCode();
    }
    private int hashCode;

    public Object getBodyDeclaration() {
        return bd;
    }

    public String getLocationClassString() {
        return locationClassString;
    }

    @Override
    public boolean equals(Object obj){
        BodyDeclarationPair bdp = (BodyDeclarationPair)obj;
        if(bdp.hashCode() ==this.hashCode) return true;
        return false;
    }

    @Override
    public int hashCode(){
        return hashCode;
    }

    @Override
    public String toString(){
        return Global.util.BodyDeclarationPairToString(this);
//        String result = this.getLocationClassString() +" ";
//        if(this.getBodyDeclaration() instanceof TypeDeclaration){
//            TypeDeclaration td = (TypeDeclaration)this.getBodyDeclaration();
//            result += td.getClass().getSimpleName()+": "+td.getName().toString();
//        }else if(this.getBodyDeclaration() instanceof FieldDeclaration){
//            FieldDeclaration td = (FieldDeclaration)this.getBodyDeclaration();
//            result += td.getClass().getSimpleName()+": "+td.fragments().toString();
//        }else if(this.getBodyDeclaration() instanceof MethodDeclaration) {
//            MethodDeclaration td = (MethodDeclaration) this.getBodyDeclaration();
//            result += td.getClass().getSimpleName() + ": " + td.getName().toString();
//        }
//        return result;
    }
}
