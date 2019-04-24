package edu.fdu.se.lang.generatingactions;

import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;
import edu.fdu.se.base.generatingactions.SimpleActionPrinter;

public abstract class ParserTreeGenerator {
    public TreeContext srcTC;
    public TreeContext dstTC;
    public ITree src;
    public ITree dst;
    public MappingStore mapping;

    String fileName;
    public void setFileName(String fileName){
        String[] s = fileName.split("\\.");
        StringBuilder sb = new StringBuilder();
        for(int i = 0;i<s.length-1;i++){
            sb.append(s[i]);
        }
        this.fileName = sb.toString();
    }

    public String getPrettyOldTreeString() {
        return SimpleActionPrinter.getPrettyTreeString(src);
    }

    public String getPrettyNewTreeString() {
        return SimpleActionPrinter.getPrettyTreeString(dst);
    }
}
