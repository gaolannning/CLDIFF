package edu.fdu.se.astdiff.webapi;

import com.github.gumtreediff.actions.model.*;
import com.github.gumtreediff.tree.Tree;
import edu.fdu.se.astdiff.associating.Association;
import edu.fdu.se.astdiff.associating.TotalFileAssociations;
import edu.fdu.se.astdiff.miningactions.bean.MiningActionData;
import edu.fdu.se.astdiff.miningchangeentity.ChangeEntityData;
import edu.fdu.se.astdiff.miningchangeentity.ClusteredActionBean;
import edu.fdu.se.astdiff.miningchangeentity.base.ChangeEntity;
import edu.fdu.se.astdiff.miningchangeentity.base.ChangeEntityDesc;
import edu.fdu.se.astdiff.miningchangeentity.base.StageIIIBean;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IfStatement;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 * Created by huangkaifeng on 2018/4/11.
 */
public class GenerateChangeEntityJson {

    private static void setChangeEntityOpt(MiningActionData miningActionData) {
        List<ChangeEntity> changeEntityList = miningActionData.getChangeEntityList();
        for (int i = 0; i < changeEntityList.size(); i++) {
            ChangeEntity changeEntity = changeEntityList.get(i);
            changeEntity.stageIIIBean.setChangeEntityId(changeEntity.changeEntityId);
            if (changeEntity.stageIIBean.getEntityCreationStage().equals(ChangeEntityDesc.StageIIGenStage.ENTITY_GENERATION_STAGE_PRE_DIFF)) {
                changeEntity.stageIIIBean.setKey("preprocess");
            } else {
                changeEntity.stageIIIBean.setKey("gumtree");
            }
            Tree srcNode;
            switch (changeEntity.stageIIBean.getOpt()) {
                case ChangeEntityDesc.StageIIOpt.OPT_INSERT:
                    changeEntity.stageIIIBean.setFile(ChangeEntityDesc.StageIIIFile.DST);
                    changeEntity.stageIIIBean.setRange(changeEntity.stageIIBean.getLineRange());
                    changeEntity.stageIIIBean.setType1(changeEntity.stageIIBean.getGranularity());
                    changeEntity.stageIIIBean.setType2(changeEntity.stageIIBean.getOpt());
                    break;
                case ChangeEntityDesc.StageIIOpt.OPT_DELETE:
                    changeEntity.stageIIIBean.setFile(ChangeEntityDesc.StageIIIFile.SRC);
                    changeEntity.stageIIIBean.setRange(changeEntity.stageIIBean.getLineRange());
                    changeEntity.stageIIIBean.setType1(changeEntity.stageIIBean.getGranularity());
                    changeEntity.stageIIIBean.setType2(changeEntity.stageIIBean.getOpt());
                    break;
                case ChangeEntityDesc.StageIIOpt.OPT_MOVE:
                    changeEntity.stageIIIBean.setFile(ChangeEntityDesc.StageIIIFile.SRC_DST);
                    if (changeEntity.clusteredActionBean.fafather.getTreeSrcOrDst() == ChangeEntityDesc.StageITreeType.SRC_TREE_NODE) {
                        Tree dstNode = (Tree) miningActionData.getMappedDstOfSrcNode(changeEntity.clusteredActionBean.fafather);
                        srcNode = changeEntity.clusteredActionBean.fafather;
                        String rangeStr = srcNode.getRangeString() + "-" + dstNode.getRangeString();
                        changeEntity.stageIIIBean.setRange(rangeStr);
                        changeEntity.stageIIIBean.setType1(changeEntity.stageIIBean.getGranularity());
                        changeEntity.stageIIIBean.setType2(changeEntity.stageIIBean.getOpt());
                    }
                    break;
                case ChangeEntityDesc.StageIIOpt.OPT_CHANGE_MOVE:
                    changeEntity.stageIIIBean.setFile(ChangeEntityDesc.StageIIIFile.SRC_DST);
                    srcNode = (Tree) changeEntity.clusteredActionBean.curAction.getNode();
                    if (srcNode.getTreeSrcOrDst() == ChangeEntityDesc.StageITreeType.SRC_TREE_NODE) {
                        Tree dstNode = (Tree) miningActionData.getMappedDstOfSrcNode(srcNode);
                        String rangeStr = srcNode.getRangeString() + "-" + dstNode.getRangeString();
                        changeEntity.stageIIIBean.setRange(rangeStr);
                        changeEntity.stageIIIBean.setType1(changeEntity.stageIIBean.getGranularity());
                        changeEntity.stageIIIBean.setType2(changeEntity.stageIIBean.getOpt());
                    }
                    break;
                case ChangeEntityDesc.StageIIOpt.OPT_CHANGE:
                    changeEntity.stageIIIBean.setFile(ChangeEntityDesc.StageIIIFile.SRC_DST);
                    //todo 可能还会变 仅仅获取其change的那几行
                    String rangeStr = null;
                    if (changeEntity.clusteredActionBean.fafather.getTreeSrcOrDst() == ChangeEntityDesc.StageITreeType.SRC_TREE_NODE) {
                        Tree dstNode = (Tree) miningActionData.getMappedDstOfSrcNode(changeEntity.clusteredActionBean.fafather);
                        rangeStr = changeEntity.clusteredActionBean.fafather.getRangeString() + "-" + dstNode.getRangeString();
                    } else {
                        srcNode = (Tree) miningActionData.getMappedSrcOfDstNode(changeEntity.clusteredActionBean.fafather);
                        rangeStr = srcNode.getRangeString() + "-" + changeEntity.clusteredActionBean.fafather.getRangeString();
                    }
                    changeEntity.stageIIIBean.setType1(changeEntity.stageIIBean.getGranularity());
                    changeEntity.stageIIIBean.setType2(changeEntity.stageIIBean.getOpt());
                    changeEntity.stageIIIBean.setRange(rangeStr);
                    if(changeEntity.stageIIBean.getOpt2List()!=null){
                        JSONArray jsonArray = changeEntity.stageIIBean.opt2ExpListToJSONArray();
                        changeEntity.stageIIIBean.setOpt2Exp2(jsonArray);
                    }
                    break;
            }
            changeEntity.stageIIIBean.setDisplayDesc(changeEntity.stageIIBean.toString2());

        }
    }


    public static void setStageIIIBean(ChangeEntityData changeEntityData) {
        setChangeEntityOpt(changeEntityData.mad);
        setChangeEntitySubRange(changeEntityData.mad);

    }

    public static void setChangeEntitySubRange(MiningActionData mad) {
        List<ChangeEntity> mList = mad.getChangeEntityList();
        for (ChangeEntity tmp : mList) {
            if (tmp.stageIIBean.getOpt().equals(ChangeEntityDesc.StageIIOpt.OPT_CHANGE)) {
                // 设置sub
                setStageIIIBeanSubRangeDetail(tmp.stageIIIBean, tmp.clusteredActionBean.actions, mad);
            } else if (tmp.stageIIBean.getOpt().equals(ChangeEntityDesc.StageIIOpt.OPT_CHANGE_MOVE)) {
                // 设置move
                setStageIIIBeanSubRangeDetailMove(tmp.stageIIIBean, tmp.clusteredActionBean, mad);
            }
        }
    }

    public static void setStageIIIBeanSubRangeDetailMove(StageIIIBean stageIIIBean, ClusteredActionBean bean, MiningActionData mad) {
        Action a = bean.curAction;
        if (!(a instanceof Move)) {
            return;
        }
        CompilationUnit src = mad.preprocessedData.srcCu;
        CompilationUnit dst = mad.preprocessedData.dstCu;
        Move mv = (Move) a;

        Tree moveNode = (Tree)mv.getNode();
        Tree movedDstNode = (Tree) mad.getMappedDstOfSrcNode(moveNode);
        stageIIIBean.setRange(moveNode.getRangeString() + "-" + movedDstNode.getRangeString());
        Integer[] m = {moveNode.getPos(),moveNode.getPos()+moveNode.getLength()};
        Integer[] n = {movedDstNode.getPos(),movedDstNode.getPos()+movedDstNode.getLength()};
        stageIIIBean.addMoveListSrc(m, src);
        stageIIIBean.addMoveListDst(n, dst);
    }

    public static void setStageIIIBeanSubRangeDetail(StageIIIBean stageIIIBean, List<Action> actions, MiningActionData mad) {
        CompilationUnit src = mad.preprocessedData.srcCu;
        CompilationUnit dst = mad.preprocessedData.dstCu;
        List<Integer[]> rangeList = new ArrayList<>();
        MergeIntervals mi = new MergeIntervals();
        actions.forEach(a -> {
            if (a instanceof Insert) {
                Tree temp = (Tree)a.getNode();
                Integer[] tempArr = {temp.getPos(),temp.getPos()+temp.getLength()};
                rangeList.add(tempArr);
            }
        });
        List<Integer[]> insertResult = mi.merge(rangeList);
//        int[] insertRange = maxminLineNumber(insertResult, dst);
        if(insertResult != null && insertResult.size()!=0)
            stageIIIBean.addInsertList(insertResult, dst);
//        String dstRangeStr = "(" + insertRange[0] + "," + insertRange[1] + ")";
        rangeList.clear();
        actions.forEach(a -> {
            if (a instanceof Delete) {
                Tree temp = (Tree)a.getNode();
                Integer[] tempArr = {temp.getPos(),temp.getPos()+temp.getLength()};
                rangeList.add(tempArr);
            }

        });
        List<Integer[]> deleteResult = mi.merge(rangeList);
        if(deleteResult != null && deleteResult.size()!=0)
            stageIIIBean.addDeleteList(deleteResult, src);
//        int[] deleteRange = maxminLineNumber(deleteResult, src);
        rangeList.clear();
        actions.forEach(a -> {
            if (a instanceof Update) {
                Tree temp = (Tree)a.getNode();
                Integer[] tempArr = {temp.getPos(),temp.getPos()+temp.getLength()};
                rangeList.add(tempArr);
            }
        });
        List<Integer[]> updateResult = mi.merge(rangeList);
        if(updateResult != null && updateResult.size()!=0)
            stageIIIBean.addUpdateList(updateResult, src);
//        int[] updateRange = maxminLineNumber(updateResult, src);
//        int max, min;
//        if (deleteRange[0] < updateRange[0]) {
//            min = deleteRange[0];
//        } else {
//            min = updateRange[0];
//        }
//        if (deleteRange[1] > updateRange[1]) {
//            max = deleteRange[1];
//        } else {
//            max = updateRange[1];
//        }
//        String srcRangeStr = "(" + min + "," + max + ")";
//        stageIIIBean.setRange(srcRangeStr + "-" + dstRangeStr);

    }

    public static int[] maxminLineNumber(List<Integer[]> mList, CompilationUnit cu) {
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (Integer[] tmp : mList) {
            if (tmp[0] < min) {
                min = tmp[0];
            }
            if (tmp[1] > max) {
                max = tmp[1];
            }
        }
        int a = cu.getLineNumber(min);
        int b = cu.getLineNumber(max);
        int[] res = {a, b};
        return res;
    }


    public static String generateEntityJson(MiningActionData miningActionData) {
        List<ChangeEntity> changeEntityList = miningActionData.getChangeEntityList();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < changeEntityList.size(); i++) {
            ChangeEntity changeEntity = changeEntityList.get(i);
            JSONObject jsonObject = changeEntity.stageIIIBean.genJSonObject();
            jsonArray.put(jsonObject);
        }

        return jsonArray.toString();
    }

    public static String generateLinkJson(Map<String,ChangeEntityData> linkData,TotalFileAssociations totalAssos){
        JSONObject jsonObject = new JSONObject();
        JSONArray ja1 = new JSONArray();
        for(Entry<String,ChangeEntityData> entry:linkData.entrySet()){
            JSONObject jo1 = new JSONObject();
            jo1.put("file-name",entry.getKey());
            List<ChangeEntity> mList = entry.getValue().mad.getChangeEntityList();
            JSONArray entityArr = new JSONArray();
            for(int i=0;i<mList.size();i++){
                entityArr.put(mList.get(i).getChangeEntityId());
            }
            jo1.put("change-entity-id-list",entityArr);
            ja1.put(jo1);
        }
        jsonObject.put("file-change-entity-list",ja1);
        JSONArray ja2 =new JSONArray();
        for(Entry<String,ChangeEntityData> entry:linkData.entrySet()){
            JSONObject jo2 = new JSONObject();
            jo2.put("link-type","one-file-link");
            jo2.put("file-name",entry.getKey());
            List<Association> assos = entry.getValue().mAssociations;
            JSONArray linkArr = new JSONArray();
            for(Association as :assos){
                linkArr.put(as.linkJsonString());
            }
            jo2.put("links",linkArr);
            ja2.put(jo2);
        }
        for(Entry<String,List<Association>> entry: totalAssos.file2fileAssos.entrySet()){
            String[] data = entry.getKey().split("----");
            List<Association> mList = entry.getValue();
            JSONObject jo3 = new JSONObject();
            jo3.put("link-type","two-file-link");
            jo3.put("file-name",data[0]);
            jo3.put("file-name2",data[1]);
            JSONArray linkArr = new JSONArray();
            for(Association as :mList){
                linkArr.put(as.linkJsonString());
            }
            jo3.put("links",linkArr);
            ja2.put(jo3);
        }
        jsonObject.put("links",ja2);


        return jsonObject.toString();
    }


    public static String generateAssociationJson(List<Association> associationList) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < associationList.size(); i++) {
            Association association = associationList.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", i);
            jsonObject.put("a", association.getChangeEntity1().getChangeEntityId());
            jsonObject.put("b", association.getChangeEntity2().getChangeEntityId());
            jsonObject.put("type", association.getType());
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }
}
