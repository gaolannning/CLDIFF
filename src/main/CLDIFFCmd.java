package main;

import edu.fdu.se.base.common.Global;
import edu.fdu.se.cldiff.CLDiffLocal;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.jdt.core.dom.ASTNode;

import java.io.*;

/**
 * Created by huangkaifeng on 2018/10/11.
 */
public class CLDIFFCmd {

    public static void main(String args[]) throws Exception{
//        Global.runningMode = 0;
//        String repo = "C:/Users/Administrator/Desktop/git2/.git";
//        String commitId = "7c3c7f6aa080fa890cf09cec05e06590428a8b98";
//        String outputDir = "C:/Users/Administrator/Desktop/aaa";
//        CLDiffLocal CLDiffLocal = new CLDiffLocal();
//        CLDiffLocal.run(commitId,repo,outputDir);
        IASTTranslationUnit a = getTranslationUnit("C:/Users/Administrator/Desktop/git2/b.cpp");
        int b = 1;
    }

    static String getContentFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        String line;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file)))) {
            while ((line = br.readLine()) != null)
                content.append(line).append('\n');
        }catch (Exception e){

        }

        return content.toString();
    }

    public static IASTTranslationUnit getTranslationUnit(String filePath) throws Exception{
        File source = new File(filePath);
        return getTranslationUnit(getContentFile(source).getBytes(),filePath);
    }

    public static IASTTranslationUnit getTranslationUnit(byte[] fileContent,String name) throws Exception{

        FileContent reader = FileContent.create(
                name,
                new String(fileContent).toCharArray());

        return GPPLanguage.getDefault().getASTTranslationUnit(
                reader,
                new ScannerInfo(),
                IncludeFileContentProvider.getSavedFilesProvider(),
                null,
                ILanguage.OPTION_IS_SOURCE_UNIT,
                new DefaultLogService());
    }
}
