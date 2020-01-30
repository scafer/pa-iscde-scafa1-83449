package pt.iscte.pidesco.codegenerator.service;

public interface CodeGeneratorServices {
	
	void addListener(CodeGeneratorListener listener);
	
	void removeListener(CodeGeneratorListener listener);
}