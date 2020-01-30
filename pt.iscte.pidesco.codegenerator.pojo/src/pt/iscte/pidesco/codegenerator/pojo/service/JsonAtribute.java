package pt.iscte.pidesco.codegenerator.pojo.service;

public class JsonAtribute {
	
	private String name;
	private String dataType;
	private Boolean isObject;
	
	public JsonAtribute(String name, String dataType, Boolean isObject) {
		this.name = name;
		this.dataType = dataType;
		this.isObject = isObject;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDataType() {
		return this.dataType;
	}
	
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public Boolean getIsObject() {
		return this.isObject;
	}
	
	public void setIsObject(Boolean isObject) {
		this.isObject = isObject;
	}
	
	public String toString() {
		return "\n" + "Property Name: " + this.name + "\n" + "Property DataType: " + this.dataType + "\n" + "isObject: " + this.isObject.toString() + "\n";
	}
}