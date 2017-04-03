package in.prashanthrao.json2java.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.google.common.base.CaseFormat;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class JSONParser {
	
	IPackageFragment fragment;
	
	public JSONParser(IPackageFragment mypackage) {
		fragment = mypackage;
	}

	public void parse(File file) throws IOException, JavaModelException {

		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(new FileReader(file));
		parse(jsonElement);
	}
	
	private void parse(JsonElement jsonElement) throws JavaModelException {
		
		if (jsonElement.isJsonArray()) {
			for (JsonElement element : (JsonArray)jsonElement) {
				parse(element);
			}
		} else if (jsonElement.isJsonObject()) {
			
			JsonObject jsonObject = (JsonObject) jsonElement;
			Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
			for (Entry<String, JsonElement> entry : entrySet) {
				if (entry.getValue().isJsonObject()) {
					parseJsonObject(entry.getKey(), (JsonObject)entry.getValue());
				}
			}
		} else if (jsonElement.isJsonPrimitive()) {
		}
	}
	
//	private void printAttributes(JsonObject jsonObject, IType type) throws JavaModelException {
//		Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
//		
//		for (Entry<String, JsonElement> entry : entrySet) {
//			JsonElement value = entry.getValue();
//			String className = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, entry.getKey());
//			if (value instanceof JsonArray) {
//				for (JsonElement element : (JsonArray)value) {
//					System.out.println("Multiple");
//					System.out.println(className + " : " + value);
//					parse(element);
//				}
//			} else if (value instanceof JsonObject) {
//				System.out.println("Single");
//				System.out.println(className + " : " + value);
//				//create type here with the name key
//				ICompilationUnit compilationUnit = fragment.createCompilationUnit(className +".java", "package " + fragment.getElementName() + ";",
//						false, null);
//				IType classType = compilationUnit.createType("public class " + className +"{}", null, true, new NullProgressMonitor());
//				printAttributes((JsonObject) value, classType);
//			} else {
//				
//				JsonPrimitive jsonPrimitive = (JsonPrimitive) value;
//				String name = null;
//				if (jsonPrimitive.isString()) {
//					name = "private String " +  entry.getKey() + ";";
//				} else if (jsonPrimitive.isNumber()) {
//					name = "private double " + entry.getKey() + ";";
//				} if (jsonPrimitive.isBoolean()) {
//					name = "private boolean "+ entry.getKey() + ";";
//					
//				}
//				
//				System.out.println("Attribute : " + name);
//				System.out.println(entry.getKey() + " : " + value);
//				type.createField(name, null, false, new NullProgressMonitor());
//			}
//		}
//	}
	
	private IType parseJsonObject(String className, JsonObject jsonObject) {
		
		IType classType = null;
		try {
			
			// Create file and class
			className = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, className);
			ICompilationUnit compilationUnit = fragment.createCompilationUnit(className +".java", "package " + fragment.getElementName() + ";",
					false, null);
			classType = compilationUnit.createType("public class " + className +"{}", null, true, new NullProgressMonitor());
			
			// loop through properties
			Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
			
			for (Entry<String, JsonElement> entry : entrySet) {
				JsonElement value = entry.getValue();
				String key = entry.getKey();
				if (value.isJsonArray()) {
					for (JsonElement element : (JsonArray)value) {
						if(element.isJsonObject()) {
							IType parseJsonObject = parseJsonObject(key, (JsonObject) element);
							classType.getCompilationUnit().createImport("java.util.List", null, new NullProgressMonitor());	
							
							String name = "private List<" + parseJsonObject.getElementName() + "> " + key + ";"; 
							classType.createField(name, null, false, new NullProgressMonitor());
							
							break;
						}
					}
				} else if (value.isJsonObject()) {
					IType parseJsonObject = parseJsonObject(key, (JsonObject) value);
					String name = "private " + parseJsonObject.getElementName() + " " + key + ";"; 
					classType.createField(name, null, false, new NullProgressMonitor());
				} else if (value.isJsonPrimitive()) {
					
					JsonPrimitive jsonPrimitive = (JsonPrimitive) value;
					String name = null;
					if (jsonPrimitive.isString()) {
						name = "private String " +  key + ";";
					} else if (jsonPrimitive.isNumber()) {
						name = "private double " + key + ";";
					} if (jsonPrimitive.isBoolean()) {
						name = "private boolean "+ key + ";";
						
					}
					classType.createField(name, null, false, new NullProgressMonitor());
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		
		return classType;
	}
	
//	public static void main(String[] args) throws JsonIOException, JsonSyntaxException, IOException, JavaModelException {
//		JSONParser jsonParser = new JSONParser();
//		jsonParser.parse(new File("C:\\Users\\Prashanth Rao\\Desktop\\example.json"));
//	}
}
