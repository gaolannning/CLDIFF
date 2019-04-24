package edu.fdu.se.base.common;

import edu.fdu.se.base.miningchangeentity.ChangeEntityData;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedDataC;
import edu.fdu.se.lang.Util;
import edu.fdu.se.server.Meta;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import java.util.List;
import java.util.Map;

/**
 * Created by huangkaifeng on 2018/4/7.
 *
 *
 */
public class Global {
    //add by yxy
    public static Util util;
    public static String lang;

    public static String formatLang(String s){
        if(s.toLowerCase().equals("c")||s.toLowerCase().equals("cpp")){
            return "C";
        }
        if(s.toLowerCase().equals("java")){
           return "Java";
        }
        assert(false);
        return null;
    }

    public static Meta mmeta;

    public static int changeEntityId = 0;

    public static int RQ2 = 0;

    public static String fileName;


    public static String parentCommit;

    public static List<Object> removal;

//    public static List<String> outputFilePathList;


    public static Map<Integer,String> changeEntityFileNameMap;
    /**
     * running mode
     * 0 command mode
     * 1 offline mode
     * 2 online mode
     */
    public static int runningMode;
    /**
     * input configs
     */
    public static String outputDir;
    public static String repoPath; // null in online mode
    public static String projectName;

    /**
     * running vars
     */
    public static ChangeEntityData ced;

}
