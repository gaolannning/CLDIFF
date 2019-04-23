package edu.fdu.se.cldiff;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;

public class CUtil {
    public static boolean isTypeDeclaration(IASTNode node){
        if(node instanceof IASTSimpleDeclaration &&
                (
                        ((IASTSimpleDeclaration)node).getDeclSpecifier() instanceof IASTCompositeTypeSpecifier
//                        ||((IASTSimpleDeclaration)node).getDeclSpecifier() instanceof IASTElaboratedTypeSpecifier
//                        ||((IASTSimpleDeclaration)node).getDeclSpecifier() instanceof IASTNamedTypeSpecifier
                )
        )
            return true;
        return false;
    }

    public static String getTypeKind(IASTNode node){
        int kind = -1;
        if(((IASTSimpleDeclaration)node).getDeclSpecifier() instanceof ICPPASTCompositeTypeSpecifier){
            kind = ((ICPPASTCompositeTypeSpecifier) ((IASTSimpleDeclaration)node).getDeclSpecifier()).getKey();
        }
//        if(((IASTSimpleDeclaration)node).getDeclSpecifier() instanceof ICPPASTElaboratedTypeSpecifier){
//            kind = ((ICPPASTElaboratedTypeSpecifier)((IASTSimpleDeclaration)node).getDeclSpecifier()).getKind();
//        }
        assert(kind != -1);
        switch (kind) {
            case 0:
                return "enum:";
            case 1:
                return "struct:";
            case 2:
                return "union:";
            case 3:
                return "class";
        }
        assert(false);
        return null;
    }

    public static String getTypeName(IASTNode node){
        IASTSimpleDeclaration n = (IASTSimpleDeclaration)node;
        if(n.getDeclSpecifier() instanceof IASTCompositeTypeSpecifier){
            return ((IASTCompositeTypeSpecifier)n.getDeclSpecifier()).getName().toString();
        }
//        if(n.getDeclSpecifier() instanceof IASTElaboratedTypeSpecifier){
//            return ((IASTElaboratedTypeSpecifier)n.getDeclSpecifier()).getName().toString();
//        }
        assert(false);
        return null;
    }

    public static boolean isFieldDeclaration(IASTNode node){
       if(
               node instanceof IASTSimpleDeclaration &&
               (
                       ((IASTSimpleDeclaration)node).getDeclSpecifier() instanceof IASTSimpleDeclSpecifier
                       ||((IASTSimpleDeclaration)node).getDeclSpecifier() instanceof IASTNamedTypeSpecifier
                       ||((IASTSimpleDeclaration)node).getDeclSpecifier() instanceof IASTElaboratedTypeSpecifier
               )
       ){
            return true;
       }
        return false;
    }

}
