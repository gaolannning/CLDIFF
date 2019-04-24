package edu.fdu.se.base.miningchangeentity;

import edu.fdu.se.base.common.Global;
import edu.fdu.se.base.links.LayeredChangeEntityContainer;
import edu.fdu.se.base.links.LayeredChangeEntityContainerC;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntity;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPairC;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.List;

/**
 * Created by huangkaifeng on 2018/4/7.
 *
 */
public class ChangeEntityPrinter {

    public static void printContainerEntity(LayeredChangeEntityContainer container, Object o) {
        CompilationUnit cu = (CompilationUnit)o;
        System.out.println("\nMember Key Size:" + container.getLayerMap().size());
        List<BodyDeclarationPair> keyList = container.getKeyIndex();
        for(BodyDeclarationPair bodyDeclarationPair : keyList){
            List<ChangeEntity> mList = container.getLayerMap().get(bodyDeclarationPair);
            if (mList == null || mList.size() == 0) {
                continue;
            }
            IASTNode node = (IASTNode) bodyDeclarationPair.getBodyDeclaration();
            int startL = cu.getLineNumber(node.getFileLocation().getNodeOffset());
            int endL = cu.getLineNumber(node.getFileLocation().getNodeLength() + node.getFileLocation().getNodeOffset() - 1);
            System.out.println(bodyDeclarationPair.toString() + " (" + startL + "," + endL + ")"+ " listSize:"+mList.size());
            for (ChangeEntity ce : mList) {
                System.out.println(ce.toString());
            }
            System.out.println("");
        }
    }

    public static void printContainerEntityNatural(LayeredChangeEntityContainer container, Object cu) {
        System.out.println("\nMember key size:" + container.getLayerMap().size());
        System.out.println("Change entity size:" + container.getChangeEntitySize());
        List<BodyDeclarationPair> keyList = container.getKeyIndex();
        for(BodyDeclarationPair bodyDeclarationPair : keyList){
            List<ChangeEntity> mList = container.getLayerMap().get(bodyDeclarationPair);
            if (mList == null || mList.size() == 0) {
                continue;
            }
            Object node = bodyDeclarationPair.getBodyDeclaration();
            int startL = Global.util.getLineNumber(cu,Global.util.getStartPosition(node));
            int endL = Global.util.getLineNumber(cu,Global.util.getStartPosition(node)+Global.util.getNodeLength(node));
            System.out.println(bodyDeclarationPair.toString() + " (" + startL + "," + endL + ")" + " listSize:"+mList.size());
            for (ChangeEntity ce : mList) {
                System.out.println(ce.toString2() +" "+ ce.getLineRange());
            }
            System.out.println("");
        }
    }



    @Deprecated
    public static void printContainerEntityNatural(LayeredChangeEntityContainerC container,CompilationUnit cu) {
        System.out.println("\nMember key size:" + container.getLayerMap().size());
        System.out.println("Change entity size:" + container.getChangeEntitySize());
        List<BodyDeclarationPairC> keyList = container.getKeyIndex();
        for(BodyDeclarationPairC bodyDeclarationPair : keyList){
            List<ChangeEntity> mList = container.getLayerMap().get(bodyDeclarationPair);
            if (mList == null || mList.size() == 0) {
                continue;
            }
            int startL = cu.getLineNumber(bodyDeclarationPair.getBodyDeclaration().getFileLocation().getNodeOffset());
            int endL = cu.getLineNumber(bodyDeclarationPair.getBodyDeclaration().getFileLocation().getNodeLength() + bodyDeclarationPair.getBodyDeclaration().getFileLocation().getNodeOffset() - 1);
            System.out.println(bodyDeclarationPair.toString() + " (" + startL + "," + endL + ")" + " listSize:"+mList.size());
            for (ChangeEntity ce : mList) {
                System.out.println(ce.toString2() +" "+ ce.getLineRange());
            }
            System.out.println("");
        }
    }

}
