package main;

import com.github.javaparser.ast.comments.CommentsCollection;
import edu.fdu.se.base.common.Global;
import edu.fdu.se.base.preprocessingfile.data.FileOutputLog;
import edu.fdu.se.cldiff.CLDiffCore;
import edu.fdu.se.cldiff.CLDiffLocal;
import org.eclipse.cdt.core.dom.ICodeReaderFactory;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.*;
import org.eclipse.core.runtime.CoreException;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangkaifeng on 2018/10/11.
 */
public class CLDIFFCmdC {

    public static void main(String args[]) throws  IOException, CoreException {
        Global.runningMode = 0;
        String filePath = "C:/Users/Administrator/Desktop/a.cpp";
        /*
        String[] includeDirectories = {"."};
        IScannerInfo info = new ScannerInfo(new HashMap<String, String>(), includeDirectories);
        FileContent fileContent = FileContent.create(filePath, writeInFile(filePath).toCharArray());
        IncludeFileContentProvider includeProvider = new IncludeFileContentProvider(){

        };
        IParserLogService log = ParserFactory.createDefaultLogService();
        IASTTranslationUnit translationUnit = GPPLanguage.getDefault().getASTTranslationUnit(fileContent, info, includeProvider, null, 0, log);
        */
        try {
            IASTTranslationUnit tu = getTranslationUnit(new File(filePath));
            ICPPASTTranslationUnit ctu = (ICPPASTTranslationUnit)tu;
            IASTNode[] node = ctu.getChildren();
            IASTNode[] node1 = ctu.getMacroDefinitions();
            IASTNode[] node2 = ctu.getDeclarations();
            IASTNode[] node3 = ctu.getIncludeDirectives();
            for(IASTNode nd:node){
                if(nd instanceof IASTSimpleDeclaration){
                    IASTDeclarator[] declarators = ((IASTSimpleDeclaration) nd).getDeclarators();           //int a的a
                    IASTDeclSpecifier declSpecifiers = ((IASTSimpleDeclaration) nd).getDeclSpecifier();     //int a的int
                    System.out.println(declarators.length);
                }
            }
            int i = 1;
        }catch(Exception e){

        }
    }
    /*
    public static IASTTranslationUnit getAst(String code) {
        IParserLogService log = new DefaultLogService();
        CodeReader reader = new CodeReader(code.toCharArray());
        Map definedSymbols = new HashMap();
        String[] includePaths = new String[0];
        IScannerInfo info = new ScannerInfo(definedSymbols, includePaths);
        ICodeReaderFactory readerFactory = FileCodeReaderFactory.getInstance();
        IASTTranslationUnit ast = null;
        try {
            ast = GPPLanguage.getDefault().getASTTranslationUnit(reader, info, FilesProvider.getInstance(), null, 0,log);
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            System.out.println("getAST fail");
            e.printStackTrace();
        }
        return ast;
    }
    */
    static String getContentFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        String line;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file)))) {
            while ((line = br.readLine()) != null)
                content.append(line).append('\n');
        }

        return content.toString();
    }

    static IASTTranslationUnit getTranslationUnit(File source) throws Exception{
        FileContent reader = FileContent.create(
                source.getAbsolutePath(),
                getContentFile(source).toCharArray());

        return GPPLanguage.getDefault().getASTTranslationUnit(
                reader,
                new ScannerInfo(),
                IncludeFileContentProvider.getSavedFilesProvider(),
                null,
                ILanguage.OPTION_IS_SOURCE_UNIT,
                new DefaultLogService());
    }

    public static String writeInFile(String path) throws IOException{
        String str = "";
        String count = "";
        try {
            // 使用字符流对文件进行读取
            BufferedReader bf = new BufferedReader(new FileReader(path));
            while (true) {
                //读取每一行数据并将其赋值给str
                if ((count = bf.readLine()) != null) {
                    str += count;
                } else {
                    break;
                }
            }
            // 关闭流
            bf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return str;
    }

}
