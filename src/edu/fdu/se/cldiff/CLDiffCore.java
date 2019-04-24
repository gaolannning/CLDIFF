package edu.fdu.se.cldiff;

import edu.fdu.se.base.common.Global;
import edu.fdu.se.base.generatingactions.GeneratingActionsData;
import edu.fdu.se.base.generatingactions.MyActionGenerator;
import edu.fdu.se.base.generatingactions.SimpleActionPrinter;
import edu.fdu.se.base.miningactions.ActionAggregationGenerator;
import edu.fdu.se.base.miningactions.bean.MiningActionData;
import edu.fdu.se.base.miningchangeentity.ChangeEntityData;
import edu.fdu.se.base.miningchangeentity.ChangeEntityPreprocess;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntityDesc;
import edu.fdu.se.base.preprocessingfile.FilePairPreDiff;
import edu.fdu.se.base.preprocessingfile.AddOrRemoveFileProcessing;
import edu.fdu.se.base.preprocessingfile.data.FileOutputLog;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedData;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedTempData;
import edu.fdu.se.base.preprocessingfile.data.PreprocessedTempDataC;
import edu.fdu.se.base.webapi.GenerateChangeEntityJson;
import edu.fdu.se.config.ProjectProperties;
import edu.fdu.se.config.PropertyKeys;
import edu.fdu.se.lang.generatingactions.ParserTreeGenerator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by huangkaifeng on 2018/2/27.
 *
 */
public class CLDiffCore {

    public ChangeEntityData changeEntityData;
    public FileOutputLog mFileOutputLog;

    public void dooDiffFile(String filePrev, String fileCurr, String output) {
        int index = filePrev.lastIndexOf('/');
        String fileName = filePrev.substring(index+1,filePrev.length());
        Global.fileName = fileName;
        FilePairPreDiff preDiff = new FilePairPreDiff();
        preDiff.initFilePath(filePrev,fileCurr);
        int result = preDiff.compareTwoFile();
        if(result ==-1){
            return;
        }
        runDiff(preDiff,fileName);
    }

    /**
     * filter out non-java files or test files
     * @param filePathName
     * @return true: non-java files or test files, false:java files
     */
    public static boolean isFilter(String filePathName){
        String name = filePathName.toLowerCase();
        //System.out.println(name);
        if(!name.endsWith(".java")&&!name.endsWith(".cpp")&&!name.endsWith(".c")){
            return true;
        }
        if(name.contains("\\test\\")||name.contains("/test/")){
            return true;
        }
        String[] data = filePathName.split("/");
        String fileName = data[data.length-1];
        if(filePathName.endsWith("Test.java")||fileName.startsWith("Test")||filePathName.endsWith("Tests.java")){
            return true;
        }
        return false;
    }


    public void dooDiffFile(String fileName, byte[] filePrevContent, byte[] fileCurrContent, String output) {
        long start = System.nanoTime();
        // 1.pre
        FilePairPreDiff preDiff = new FilePairPreDiff();
        preDiff.initFileContent(filePrevContent,fileCurrContent);
        int result = preDiff.compareTwoFile();
        long end = System.nanoTime();
        System.out.println("----pre-processing " +(end-start));
        if(result ==-1){
            return;
        }
        runDiff(preDiff,fileName);
    }

    public void dooAddFile(String fileName, byte[] fileCurrContent, String output){
        AddOrRemoveFileProcessing addOrRemoveFileProcessing = new AddOrRemoveFileProcessing(fileCurrContent, ChangeEntityDesc.StageIIIFile.DST);
        //may cause series of issues for types such like enum haven't been handled now.
        if(addOrRemoveFileProcessing.ced == null){
            return;
        }
        changeEntityData = addOrRemoveFileProcessing.ced;
        changeEntityData.fileName = fileName;

    }

    public void dooRemoveFile(String fileName,byte[] fileCurrContent,String output){
        AddOrRemoveFileProcessing addOrRemoveFileProcessing = new AddOrRemoveFileProcessing(fileCurrContent,ChangeEntityDesc.StageIIIFile.SRC);
        //may cause series of issues for types such like enum haven't been handled now.
        if (addOrRemoveFileProcessing.ced == null) {
            return;
        }
        changeEntityData = addOrRemoveFileProcessing.ced;
        changeEntityData.fileName = fileName;
    }


    private void runDiff(FilePairPreDiff preDiff,String fileName){
        long start = System.nanoTime();
        PreprocessedData preData = preDiff.getPreprocessedData();
        PreprocessedTempData preTempDataC = preDiff.getPreprocessedTempData();
        Global.util.preProcess(preTempDataC);;
        ParserTreeGenerator parserTreeGenerator = null;
        try{
            Class clazz= Class.forName("edu.fdu.se.lang.generatingactions."+Global.lang+"ParserTreeGenerator");
            Class[] argClazz = {Object.class,Object.class};
            Object src = Global.util.getSrcCu(preData);
            Object dst = Global.util.getDstCu(preData);
            parserTreeGenerator = (ParserTreeGenerator) clazz.getConstructor(argClazz).newInstance(new Object[]{src,dst});
        }catch (Exception e){
            e.printStackTrace();
            assert(parserTreeGenerator!=null);
        }

//        JavaParserTreeGenerato treeGenerator = new JavaParserTreeGenerato(Global.util.getSrcCu(preData),Global.util.getDstCu(preData));
//        treeGenerator.setFileName(fileName);
//        //gumtree
//        MyActionGenerator actionGenerator = new MyActionGenerator(treeGenerator);
//        GeneratingActionsData actionsData = actionGenerator.generate();
        //print
//        long end = System.nanoTime();
//        System.out.println("----mapping " +(end-start));
//        printActions(actionsData,treeGenerator);
//        long start2 = System.nanoTime();
//        MiningActionData mad = new MiningActionData(preData,actionsData,treeGenerator);
//        ActionAggregationGenerator aag = new ActionAggregationGenerator();
//        aag.doCluster(mad);
////correcting
//        ChangeEntityData ced = new ChangeEntityData(mad);
//        ChangeEntityPreprocess cep = new ChangeEntityPreprocess(ced);
//        cep.preprocessChangeEntity();//1.init 2.merge 3.set 4.sub
//        changeEntityData = ced;
//        changeEntityData.fileName = fileName;
//        long end2 = System.nanoTime();
//        System.out.println("----grouping " +(end2-start2));
//// json
//        GenerateChangeEntityJson.setStageIIIBean(ced);
//        JSONArray json = GenerateChangeEntityJson.generateEntityJson(ced.mad);
//        this.mFileOutputLog.writeEntityJson(json.toString(4));
//        if(Global.runningMode==0){
//            System.out.println(GenerateChangeEntityJson.toConsoleString(json));
//        }else {
//            System.out.println(json.toString(4));
//        }

    }


//    private void printActions(GeneratingActionsData actionsData, JavaParserTreeGenerator treeGenerator){
//        mFileOutputLog.writeTreeFile(treeGenerator.getPrettyOldTreeString(),treeGenerator.getPrettyNewTreeString());
//        SimpleActionPrinter.printMyActions(actionsData.getAllActions());
//    }


}
