package edu.fdu.se.astdiff.preprocessingfile;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.sun.xml.internal.bind.annotation.OverrideAnnotationOf;

/**
 * Created by huangkaifeng on 2018/1/22.
 */
public class BodyDeclarationPair {
    private BodyDeclaration bd;

    private String locationClassString;

    public BodyDeclarationPair(BodyDeclaration bd1,String str){
        this.bd = bd1;
        this.locationClassString = str;
        String hashStr = String.valueOf(bd1)+String.valueOf(str.hashCode());
        this.hashCode = hashStr.hashCode();
    }
    private int hashCode;

    public BodyDeclaration getBodyDeclaration() {
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
}
