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
        String commitId = "74a087a2052ee761001340ac607b1b87a3048c2e";
        String outputDir = "C:/Users/Administrator/Desktop/aaa";
        CLDiffLocal CLDiffLocal = new CLDiffLocal();
        CLDiffLocal.run(commitId,repo,outputDir);
    };
}
