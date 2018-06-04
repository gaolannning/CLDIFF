package cn.edu.fudan.se.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;

import cn.edu.fudan.se.ast.AstParser;
import cn.edu.fudan.se.db.DB;
import cn.edu.fudan.se.git.JgitRepository;

public class ApiCallValidator {
	private List libLocalPaths;
	private List importList;
	private List contextList;
//	private List libList;
	private Map allClasses = new HashMap<>();
	private String proPath;
	private int projectId;
	private String LIB_PATH = "F:/GP/lib/";
	private CombinedTypeSolver typeSolver;
	private Map apiCallCount = new HashMap<>();
	
	public ApiCallValidator(int id,String path) {
		this.projectId = id;
		this.proPath = path;
		getLibsUsedByProj();
//		getAllLibPaths();
		this.contextList = new ArrayList<>();
		getContext(this.proPath);
//		for(String context:this.contextList) 
//			System.out.println(context);
		this.typeSolver = new CombinedTypeSolver(ProjectAnalyzer.createJarTypeSolverList(this.libLocalPaths,this.contextList));		
	}
	
	
	private void getLibsUsedByProj() {
//		this.libList = new ArrayList<>();
//		ResultSet rs = DB.query("SELECT * FROM `project_lib_usage` where `project_id`=" + this.projectId);
//		try {
//			while (rs.next()) {
//				int versionTypeId = rs.getInt("version_type_id");
//				this.libList.add(versionTypeId);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		this.libLocalPaths = new ArrayList<>();
		ResultSet rs = DB.query("SELECT * FROM `project_lib_usage` where `project_id`=" + this.projectId);
		try {
			while (rs.next()) {
				int versionTypeId = rs.getInt("version_type_id");
				ResultSet trs = DB.query("SELECT * FROM `version_types` where `type_id`=" + versionTypeId);
				try {
					while (trs.next()) {
						String packageUrl = trs.getString("jar_package_url");
						if(packageUrl.endsWith(".jar"))
							this.libLocalPaths.add(LIB_PATH+packageUrl);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ResultSet crs = DB.query("SELECT * FROM `api_classes` where `version_type_id`=" + versionTypeId);
				try {
					while (crs.next()) {
						String className = crs.getString("class_name");
						int classId = crs.getInt("id");		
						this.allClasses.put(className, classId);		
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	private void getAllLibPaths() {
//		this.libLocalPaths = new ArrayList<>();
//		for(int libId:this.libList) {
//			ResultSet trs = DB.query("SELECT * FROM `version_types` where `type_id`=" + libId);
//			try {
//				while (trs.next()) {
//					String packageUrl = trs.getString("jar_package_url");
//					if(packageUrl.endsWith(".jar"))
//						this.libLocalPaths.add(LIB_PATH+packageUrl);
//				}
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
	
	private void getContext(String dir) {
		File or = new File(dir);
		File[] files = or.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					String path = file.getAbsolutePath();
					if(path.endsWith("\\src\\test\\java")||path.endsWith("\\src\\main\\java")) {
						if(!this.contextList.contains(path))
							this.contextList.add(path);						
					}
					getContext(file.getAbsolutePath());
				}
			}
		}
	}	
	
	private List getContextForOneFile(String path) {
		List result = new ArrayList<>();
		for (String context: this.contextList) {
			if(path.startsWith(context)) 
				result.add(context);
		}
		return result;
	}
	
	public Map getAllApisOfLibUsedByOneFile() {
		Map apis = new HashMap<>();
		for (Map.Entry clazz : this.allClasses.entrySet()) {
			String className = clazz.getKey();
			if(isImported(className)) {
				int classId = clazz.getValue();
				ResultSet irs = DB.query("SELECT * FROM `api_interface` where `class_id`=" + classId);
				try {
					while (irs.next()) {
						String apiName = irs.getString("name");
						int apiId = irs.getInt("id");
						apis.put(apiName, apiId);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
		return apis;
	}	
	
//	private Map getApisOfOneLib(int typeId) {
//		Map apis = new HashMap<>();
//		ResultSet rs = DB.query("SELECT * FROM `api_classes` where `version_type_id`=" + typeId);
//		try {
//			while (rs.next()) {
//				String className = rs.getString("class_name");
//				if(isImported(className)) {
////					System.out.println(className);
//					int classId = rs.getInt("id");
//					ResultSet irs = DB.query("SELECT * FROM `api_interface` where `class_id`=" + classId);
//					while (irs.next()) {
//						String apiName = irs.getString("name");
//						int apiId = irs.getInt("id");
//						apis.put(apiName, apiId);
//					}
//				}				
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return apis;
//	}
	
//	public Map getAllApisOfLibUsedByOneFile() {
//		Map apis = new HashMap<>();
//		for(int libId:this.libList) {
//			apis.putAll(getApisOfOneLib(libId));
//		}
//		return apis;
//	}	
	
	private void getImportListOfFile(String filePath) {
		this.importList = new ArrayList();
		CompilationUnit cu = AstParser.getCompilationUnit(filePath);
		List imports = Navigator.findAllNodesOfGivenClass(cu, ImportDeclaration.class);
		imports.forEach(im -> { //包含构造方法调用
			String type = im.getNameAsString();
			this.importList.add(type);
		}); 
	}
	
	private boolean isImported(String apiClass) {
		boolean imported = false;
		for(String im:this.importList) {
			if(apiClass.startsWith(im) || im.startsWith(apiClass)) {
				imported = true;
				break;
			}
		}
		return imported;
	}	
	
	
	private void printApiList(Map apis) {
		for (Map.Entry api : apis.entrySet()) {
			System.out.println(api.getKey());
		}
	}
	
//	public void validateApiCall(int projectId) {		
//		getImportListOfFile("C:/Users/yw/Desktop/projects/ActionBarSherlock/actionbarsherlock/src/com/actionbarsherlock/ActionBarSherlock.java");
//		Map apis = getAllApisOfLibUsedByOneFile(projectId);
//		System.out.println(apis.size());
//		ProjectAnalyzer pa = new ProjectAnalyzer();
//		List call = pa.analyseOneFile("C:/Users/yw/Desktop/projects/ActionBarSherlock/actionbarsherlock/src/com/actionbarsherlock/ActionBarSherlock.java", projectId);
////		for(int i = 0; i < call.size();i++) {
////			if(apis.containsKey(call.get(i))) {
////				System.out.println(call.get(i));
////			}
////		}
//	}
	private void findApiCall(Map apis,List call) {
		for(int i = 0; i < call.size();i++) {
//			String callStr = call.get(i);
			if(apis.containsKey(call.get(i))) {
				int apiId = apis.get(call.get(i));
				if(this.apiCa                llCount.containsKey(apiId)) {
					int num = this.apiCallCount.get(apiId);
					this.apiCallCount.replace(apiId,num+1);
				}
				else
					this.apiCallCount.put(apiId, 1);
				System.out.println(call.get(i)+" "+apiId);
			}
		}
	}
	
    public void apiCallPersistence() {
    	for (Map.Entry api : this.apiCallCount.entrySet()) {
    		String sql = "INSERT INTO api_call(project_id,api_id,count) VALUES ("+ this.projectId + "," + api.getKey() + ", "+api.getValue()+")";
			DB.update(sql);		
		}
	}
	
	public void validateFile(String dir) {
		File or = new File(dir);
		File[] files = or.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile() && file.getName().endsWith(".java")
					&& file.getAbsolutePath().equals("F:\\GP\\high_quality_repos\\netty\\netty\\codec-dns\\src\\test\\java\\io\\netty\\handler\\codec\\dns\\DnsResponseTest.java")) {
//						&& file.getAbsolutePath().equals("F:\\GP\\high_quality_repos\\JakeWharton\\ActionBarSherlock\\actionbarsherlock\\src\\com\\actionbarsherlock\\internal\\ActionBarSherlockCompat.java")) {
					String absolutePath = file.getAbsolutePath();
					System.out.println("----------------------"+absolutePath);
					getImportListOfFile(absolutePath);
					Map apis = getAllApisOfLibUsedByOneFile();
					System.out.println(apis.size());
					List ctx = getContextForOneFile(absolutePath);
					for(String context:ctx) 
						System.out.println(context);
					CombinedTypeSolver solver = new CombinedTypeSolver(ProjectAnalyzer.createJarTypeSolverList(this.libLocalPaths,ctx));						
					ProjectAnalyzer pa = new ProjectAnalyzer();
					List call = pa.analyseOneFile(absolutePath, solver);
//					List call = pa.analyseOneFile(absolutePath, this.typeSolver);
					findApiCall(apis,call);
				}		
				else if (file.isDirectory()) {
					validateFile(file.getAbsolutePath());
				}
			}
		}
	}	
	
	public static void readFile(String path) {
		String proLocalPath = "F:/GP/high_quality_repos/";
//		String proLocalPath = "E:/high_quality_repos/";
		int index = 0;
		Map projects = new HashMap<>();
		try {
			Scanner in = new Scanner(new File(path));
			while (in.hasNextLine()) {
				String str = in.nextLine();
				String absolutePath = null;
				if(str.startsWith("../data/prior_repository/")) {
					absolutePath = proLocalPath +str.substring(25);
					File file = new File(absolutePath);
					if(file.exists()) {
						projects.put(index, absolutePath);
					}
				}					
				else 
					index = Integer.parseInt(str);				
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		for(int i = 4;i <= 4;i++) {
			if(projects.containsKey(i)) {
				System.out.println("-------------------projectId:"+i);
				ApiCallValidator apiCallValidator = new ApiCallValidator(i,projects.get(i));
				apiCallValidator.validateFile(projects.get(i));
//				apiCallValidator.apiCallPersistence();
			}			
		}
	}
	
	public static void main(String[] args) {
		readFile("C:/Users/yw/Desktop/pro.txt");
//		ApiCallValidator apiCallValidator = new ApiCallValidator();
//		apiCallValidator.validateApiCall(1);
	}
}
