package main;

import edu.fdu.se.base.common.Global;
import edu.fdu.se.cldiff.CLDiffLocal;

/**
 * Created by huangkaifeng on 2018/10/11.
 */
public class CLDIFFCmd {

    public static void main(String args[]){
        Global.runningMode = 0;
        String repo = "C:/Users/Administrator/Desktop/gitc/.git";
        String commitId = "03c5c1b993c341fb71e34762a4e363febeb13f4b";
        String outputDir = "C:/Users/Administrator/Desktop/aaa";
        CLDiffLocal CLDiffLocal = new CLDiffLocal();
        CLDiffLocal.run(commitId,repo,outputDir);
    };
}
