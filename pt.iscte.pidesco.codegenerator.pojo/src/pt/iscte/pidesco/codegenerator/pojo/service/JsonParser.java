package pt.iscte.pidesco.codegenerator.pojo.service;

import java.util.List;
import java.util.ArrayList;

public class JsonParser {
	private String className = "";
	private String packageName = "";
	private String serializationImports = "";
	private String serializationMode = "";
	
	List<JsonAtribute> jsonAtributes = new ArrayList<JsonAtribute>();
	
	public JsonParser(String className, String packageName, String serializationMode) {
		this.className = className;
		this.packageName = packageName;
		setSerializationMode(serializationMode);
	}
		
	public String GeneratePojo(String jsonString) {	
		String jsonBody = "";
		String result ="JSON Parsing Error.";
		
		if(!jsonString.isEmpty()) {
			System.out.println("#### JSON IMPUT ####");
			System.out.println(jsonString);
			
			try {
				jsonBody = jsonPreProcessor(jsonString);
				if(isJsonObject(jsonBody)) {
					String[] lines = jsonBody.split("\n");
					
					for(String line : lines) {
						if(line.startsWith("\"")) {
							String[] keyPair = line.split(":");
							jsonAtributes.add(new JsonAtribute(removeQuotationMark(keyPair[0]), getKeyDataType(keyPair[1]), true));
						}
					}
					
					result = getClassBody();
				}
				else if(isJsonArray(jsonBody)) {
					String[] lines = jsonBody.split("\n");
					
					for(String line : lines) {
						System.out.println(line);
					}
				}
				else {
					throw new Exception();
				}
			}
			catch(Exception ex) {
				System.out.println(ex.toString());
				result = ex.getMessage();
			}
		}
		else {
			System.out.println("JSON STRING IS EMPTY!");
		}
		return result;
	}
	
	private String getClassBody() {
		StringBuilder bodyResult = new StringBuilder();

		String packageHeader = "package " + packageName + ";\n";
		
		String classHeader = "\npublic class " + className + " {" + "\n";
		String toStr = "\n\t@Override\n" + "\tpublic String toString() {\n" + "\treturn \"" + className + " " + "{\" ";
		
		bodyResult.append(packageHeader);
		bodyResult.append(serializationImports);
		bodyResult.append(classHeader);		
		
		for(JsonAtribute atribute : jsonAtributes) {		
			bodyResult.append(getJsonAnnotation(atribute));
			bodyResult.append(getJsonProperty(atribute));
			bodyResult.append(getJsonPropertyGetter(atribute));
			bodyResult.append(getJsonPropertySetter(atribute));
			
			toStr += getPropertyToString(atribute);
		}
		
		toStr += "\n\t" + " + " + "\"}\";";
		bodyResult.append(toStr);
		bodyResult.append("\n}");
		
		return bodyResult.toString();
	}
	
	private String jsonPreProcessor(String str) {
		String removeLines = removeBreakLines(removeWhiteSpace(removeTabs(str)));		
		String result = insertBreakLines(removeLines);
		return result;
	}
	
	private String getKeyDataType(String str) {
		String value = removeWhiteSpace(str);
		value = value.replaceAll(",", "");
		String result = "";
		
		if(value.startsWith("\"")) {
			result = "String";
		}
		else if(value.equalsIgnoreCase("false") || value.equalsIgnoreCase("true")) {
			result = "Boolean";
		}
		else {
			try {
				Integer.parseInt(value);
				result = "int";
			}
			catch(Exception ex) {
				result = "Object";
			}
		}	
		return result;
	}
	
	private Boolean isJsonArray(String str) {
		Boolean isArray = false;
		String value = removeWhiteSpace(str);
		
		if(value.startsWith("[") && value.endsWith("]")) {
			isArray = true;
		}
		
		System.out.println("isJsonArray: " + isArray.toString());
		return isArray;
	}
	
	private Boolean isJsonObject(String str) {
		Boolean isObject = false;
		String value = removeWhiteSpace(str);
		
		if(value.startsWith("{") && value.endsWith("}")) {
			isObject = true;
		}
		
		System.out.println("isJsonObject: " + isObject.toString());
		return isObject;
	}
	
	private void setSerializationMode(String mode) {
		if(mode == "gson") {
			this.serializationMode = "SerializedName";
			this.serializationImports = "import com.google.gson.*;\n";
		}
		else {
			this.serializationMode = "JsonProperty";
			this.serializationImports = "import com.fasterxml.jackson.annotation.JsonProperty;\n";
		}
	}
	
	private String getJsonAnnotation(JsonAtribute atribute) {
		return "\n\t@" + serializationMode + "(\"" + atribute.getName() + "\")\n";
	}
	
	private String getJsonProperty(JsonAtribute atribute) {
		if(!atribute.getIsObject())
			return "\nprivate " + "List<" + atribute.getDataType() + "> " + atribute.getName() + ";" + "\n";
		else
			return "\tprivate " + atribute.getDataType() + " " + atribute.getName() + ";" + "\n";
	}
	
	private String getJsonPropertyGetter(JsonAtribute atribute) {
		if(!atribute.getIsObject())
			return "\npublic " + "List<" + atribute.getDataType() + "> " + "get" + atribute.getName() +"()" + " {" + "\n" + "\t\treturn this." + atribute.getName() + ";" + "\n" + "}\n";
		else
			return "\n\tpublic " + atribute.getDataType() + " " + "get" + atribute.getName() +"()" + " {" + "\n" + "\t\treturn this." + atribute.getName() + ";" + "\n" + "\t}\n";
	}
	
	private String getJsonPropertySetter(JsonAtribute atribute) {
		if(!atribute.getIsObject())
			return "\npublic " + "void" + " " + "set" + atribute.getName() +"(List<" + atribute.getDataType() + "> " + atribute.getName() + ")" + " {" + "\n" + "this." + atribute.getName() + "=" + atribute.getName() + ";" + "\n" + "}\n";
		else
			return "\n\tpublic " + "void" + " " + "set" + atribute.getName() +"(" + atribute.getDataType() + " " + atribute.getName() + ")" + " {" + "\n\t\t" + "this." + atribute.getName() + " = " + atribute.getName() + ";" + "\n" + "\t}\n";
	}
	
	private String getPropertyToString(JsonAtribute atribute) {
		return  "+" + "\n" + "\t\"" + atribute.getName() + " = \'\"" + "+" + atribute.getName() + "+ " + "'" + "\\'" + "'" + ",";
	}
	
	private String insertBreakLines(String str) {
		str = str.replaceAll("\\{", "{\n");
		str = str.replaceAll("\\}", "\n}");
		str = str.replaceAll("\\[", "[\n");
		str = str.replaceAll("\\]", "\n]");
		str = str.replaceAll(",", ",\n");
		return str;
	}
	
	private String removeBreakLines(String str) {
		return str.replaceAll("\n", "");
	}
	
	private String removeTabs(String str) {
		return str.replaceAll("\t", "");
	}
	
	private String removeQuotationMark(String str) {
		return str.replaceAll("\"", "");
	}
	
	private String removeWhiteSpace(String str) {
		return str.replaceAll("\\s", "");
	}
}