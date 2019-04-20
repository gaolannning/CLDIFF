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
        String commitId = "9b32fb061745cfb7d625c7a96c090171d76c272f";
        String outputDir = "C:/Users/Administrator/Desktop/aaa";
        CLDiffLocal CLDiffLocal = new CLDiffLocal();
        CLDiffLocal.run(commitId,repo,outputDir);
    };
}
