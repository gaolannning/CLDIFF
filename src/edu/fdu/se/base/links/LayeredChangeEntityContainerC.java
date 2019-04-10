package edu.fdu.se.base.links;

import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.Tree;
import edu.fdu.se.base.miningactions.bean.MiningActionData;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntity;
import edu.fdu.se.base.miningchangeentity.base.ChangeEntityDesc;
import edu.fdu.se.base.miningchangeentity.member.*;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPair;
import edu.fdu.se.base.preprocessingfile.data.BodyDeclarationPairC;
import org.eclipse.jdt.core.dom.*;

import java.util.*;
import java.util.Map.Entry;

/**
 * Created by huangkaifeng on 3/24/18.
 *
 */
public class LayeredChangeEntityContainerC {

    protected Map<BodyDeclarationPairC, List<ChangeEntity>> layerMap;



    protected List<BodyDeclarationPairC> keyIndex;


    public Map<BodyDeclarationPairC, List<ChangeEntity>> getLayerMap() {
        return layerMap;
    }

    public List<BodyDeclarationPairC> getKeyIndex() {
        return keyIndex;
    }

    public LayeredChangeEntityContainerC() {
        this.layerMap = new HashMap<>();
        this.keyIndex = new ArrayList<>();
    }

    public void sortKeys() {
        List<Entry<BodyDeclarationPairC, List<ChangeEntity>>> mList = new ArrayList<>(layerMap.entrySet());
        mList.sort(new Comparator<Entry<BodyDeclarationPairC, List<ChangeEntity>>>() {
            @Override
            public int compare(Entry<BodyDeclarationPairC, List<ChangeEntity>> a, Entry<BodyDeclarationPairC, List<ChangeEntity>> b) {
                return a.getKey().getBodyDeclaration().getFileLocation().getNodeOffset() - b.getKey().getBodyDeclaration().getFileLocation().getNodeOffset();
            }
        });
        mList.forEach(a -> keyIndex.add(a.getKey()));
    }

    public void addKey(BodyDeclarationPairC bodyDeclarationPair) {
        if (layerMap.containsKey(bodyDeclarationPair)) {
            return;
        }
        List<ChangeEntity> mList = new ArrayList<>();
        layerMap.put(bodyDeclarationPair, mList);
    }

    public void addPreDiffChangeEntity(ChangeEntity changeEntity) {
        if(changeEntity==null) return;
        BodyDeclarationPairC mKey = null;
        for (BodyDeclarationPairC key : this.layerMap.keySet()) {
            if (key.getBodyDeclaration() instanceof TypeDeclaration) {
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
        BodyDeclarationPairC mKey = null;
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
            startPos = tree.getAstNode().getStartPosition();

        } else if (changeEntity.clusteredActionBean.traverseType == ChangeEntityDesc.StageITraverseType.TRAVERSE_DOWN_UP) {
            // father节点的range
            tree = (Tree) node;
            startPos = tree.getAstNode().getStartPosition();
        }
        mKey = getEnclosedBodyDeclaration(changeEntity, startPos);
        if (mKey != null && this.layerMap.containsKey(mKey)) {
            this.layerMap.get(mKey).add(changeEntity);
        } else {
            System.err.println("[ERR]Not In BodyMap keys:" + changeEntity.toString());

        }

    }

    private BodyDeclarationPairC getEnclosedBodyDeclaration(ChangeEntity changeEntity, int start) {
        for (BodyDeclarationPairC key : this.layerMap.keySet()) {
            if (key.getBodyDeclaration() instanceof TypeDeclaration) {
                if (changeEntity instanceof ClassChangeEntity
                        || changeEntity instanceof EnumChangeEntity
                        || changeEntity instanceof FieldChangeEntity
                        || changeEntity instanceof InitializerChangeEntity
                        || changeEntity instanceof MethodChangeEntity) {
                    if (start >= key.getBodyDeclaration().getFileLocation().getNodeOffset() && start <= (key.getBodyDeclaration().getFileLocation().getNodeOffset() + key.getBodyDeclaration().getFileLocation().getNodeLength())) {
                        return key;
                    }
                }
            } else {
                if (start >= key.getBodyDeclaration().getFileLocation().getNodeOffset() && start <= (key.getBodyDeclaration().getFileLocation().getNodeOffset() + key.getBodyDeclaration().getFileLocation().getNodeLength())) {
                    return key;
                }
            }
        }
        return null;
    }




    public void sortEntityList() {
        for (Entry<BodyDeclarationPairC, List<ChangeEntity>> entry : this.layerMap.entrySet()) {
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
        for(Entry<BodyDeclarationPairC,List<ChangeEntity>> entry:this.layerMap.entrySet()){
            size += entry.getValue().size();
        }
        return size;
    }




}
