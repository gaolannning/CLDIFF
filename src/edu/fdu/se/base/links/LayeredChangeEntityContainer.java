package edu.fdu.se.base.links;

import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.Tree;
import edu.fdu.se.base.common.Global;
import edu.fdu.se.base.miningactions.bean.MiningActionData;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntity;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntityDesc;
import edu.fdu.se.base.miningchangeentity.member.*;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import org.eclipse.jdt.core.dom.*;

import java.util.*;
import java.util.Map.Entry;

/**
 * Created by huangkaifeng on 3/24/18.
 *
 */
public class LayeredChangeEntityContainer {

    protected Map<BodyDeclarationPair, List<ChangeEntity>> layerMap;



    protected List<BodyDeclarationPair> keyIndex;


    public Map<BodyDeclarationPair, List<ChangeEntity>> getLayerMap() {
        return layerMap;
    }

    public List<BodyDeclarationPair> getKeyIndex() {
        return keyIndex;
    }

    public LayeredChangeEntityContainer() {
        this.layerMap = new HashMap<>();
        this.keyIndex = new ArrayList<>();
    }

    public void sortKeys() {
        List<Entry<BodyDeclarationPair, List<ChangeEntity>>> mList = new ArrayList<>(layerMap.entrySet());
        mList.sort(new Comparator<Entry<BodyDeclarationPair, List<ChangeEntity>>>() {
            @Override
            public int compare(Entry<BodyDeclarationPair, List<ChangeEntity>> a, Entry<BodyDeclarationPair, List<ChangeEntity>> b) {
                return Global.util.getStartPosition(a.getKey().getBodyDeclaration()) - Global.util.getStartPosition(b.getKey().getBodyDeclaration());
            }
        });
        mList.forEach(a -> keyIndex.add(a.getKey()));
    }

    public void addKey(BodyDeclarationPair bodyDeclarationPair) {
        if (layerMap.containsKey(bodyDeclarationPair)) {
            return;
        }
        List<ChangeEntity> mList = new ArrayList<>();
        layerMap.put(bodyDeclarationPair, mList);
    }

    public void addPreDiffChangeEntity(ChangeEntity changeEntity) {
        if(changeEntity==null) return;
        BodyDeclarationPair mKey = null;
        for (BodyDeclarationPair key : this.layerMap.keySet()) {
            if (Global.util.isTypeDeclaration(key.getBodyDeclaration())) {
                if (changeEntity instanceof ClassChangeEntity) {
                    String location = changeEntity.stageIIBean.getLocation();
                    location = location.substring(0, location.length() - 1);
                    int index = location.lastIndexOf(".");
                    location = location.substring(0, index + 1);
                    if (location.equals(key.getLocationClassString())) {
                        mKey = key;
                        break;
                    }
                } else {
                    if (changeEntity.stageIIBean.getLocation().equals(key.getLocationClassString())) {
                        mKey = key;
                        break;
                    }
                }
            }
        }
        if (mKey != null && this.layerMap.containsKey(mKey)) {
            this.layerMap.get(mKey).add(changeEntity);
        } else {
            System.err.println("[ERR]Put to LayerMap error: " + changeEntity.stageIIBean.getLocation() + " " + changeEntity.getClass().getSimpleName());
        }
    }

    public void addGumTreePlus(ChangeEntity changeEntity, MiningActionData mad) {
        ITree node = changeEntity.clusteredActionBean.fafather;
        Tree tree = null;
        BodyDeclarationPair mKey = null;
        int startPos = -1;
        if (changeEntity.clusteredActionBean.traverseType == ChangeEntityDesc.StageITraverseType.TRAVERSE_UP_DOWN) {
            if (changeEntity.clusteredActionBean.curAction instanceof Insert) {
                // insert上一个节点mapping的节点
                while (tree == null) {
                    node = node.getParent();
                    tree = (Tree) mad.getMappedSrcOfDstNode(node);
                }
            } else {
                tree = (Tree) node;
            }
            if (tree == null) {
                System.out.println("a");
            }
            startPos = Global.util.getStartPosition(tree.getNode());

        } else if (changeEntity.clusteredActionBean.traverseType == ChangeEntityDesc.StageITraverseType.TRAVERSE_DOWN_UP) {
            // father节点的range
            while (tree == null) {
                tree = (Tree) mad.getMappedSrcOfDstNode(node);
                if(node == null){
                    break;
                }
                node = node.getParent();
            }
            if(tree == null)
                tree = (Tree) changeEntity.clusteredActionBean.fafather;
//            tree = (Tree) node;
            startPos = Global.util.getStartPosition(tree.getNode());
        }
        mKey = getEnclosedBodyDeclaration(changeEntity, startPos);
        if (mKey != null && this.layerMap.containsKey(mKey)) {
            this.layerMap.get(mKey).add(changeEntity);
        } else {
            System.err.println("[ERR]Not In BodyMap keys:" + changeEntity.toString());

        }

    }

    private BodyDeclarationPair getEnclosedBodyDeclaration(ChangeEntity changeEntity, int start) {
        for (BodyDeclarationPair key : this.layerMap.keySet()) {
            Object o = key.getBodyDeclaration();
            if (Global.util.isTypeDeclaration(o)) {
                if (changeEntity instanceof ClassChangeEntity
                        || changeEntity instanceof EnumChangeEntity
                        || changeEntity instanceof FieldChangeEntity
                        || changeEntity instanceof InitializerChangeEntity
                        || changeEntity instanceof MethodChangeEntity) {
                    if (start >= Global.util.getStartPosition(o) && start <= Global.util.getStartPosition(o) + Global.util.getNodeLength(o)) {
                        return key;
                    }
                }
            } else {
                if (start >= Global.util.getStartPosition(o) && start <= Global.util.getStartPosition(o) + Global.util.getNodeLength(o)) {
                    return key;
                }
            }
        }
        return null;
    }




    public void sortEntityList() {
        for (Entry<BodyDeclarationPair, List<ChangeEntity>> entry : this.layerMap.entrySet()) {
            List<ChangeEntity> mList = entry.getValue();
            mList.sort(new Comparator<ChangeEntity>() {
                @Override
                public int compare(ChangeEntity a, ChangeEntity b) {
                    return a.lineRange.startLineNo - b.lineRange.startLineNo;
                }
            });
        }
    }


    public int getChangeEntitySize(){
        int size =0;
        for(Entry<BodyDeclarationPair,List<ChangeEntity>> entry:this.layerMap.entrySet()){
            size += entry.getValue().size();
        }
        return size;
    }




}
