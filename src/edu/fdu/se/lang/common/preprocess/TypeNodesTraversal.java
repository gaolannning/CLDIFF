package edu.fdu.se.lang.common.preprocess;

import edu.fdu.se.base.preprocessingfile.data.PreprocessedTempData;

public interface TypeNodesTraversal {
    void traverseTypeDeclarationSetVisited(PreprocessedTempData compareCache, Object cod, String prefixClassName);
}
