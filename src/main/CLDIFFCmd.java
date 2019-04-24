package main;

import edu.fdu.se.base.common.Global;
import edu.fdu.se.cldiff.CLDiffLocal;
import org.eclipse.jdt.core.dom.ASTNode;

/**
 * Created by huangkaifeng on 2018/10/11.
 */
public class CLDIFFCmd {

    public static void main(String args[]){
        Global.runningMode = 0;
        String repo = "C:/Users/Administrator/Desktop/git2/.git";
        String commitId = "c0f25d24577cfe9694f96ed61cf5210b7ec82292";
        String outputDir = "C:/Users/Administrator/Desktop/aaa";
        CLDiffLocal CLDiffLocal = new CLDiffLocal();
        CLDiffLocal.run(commitId,repo,outputDir);
    }
}
