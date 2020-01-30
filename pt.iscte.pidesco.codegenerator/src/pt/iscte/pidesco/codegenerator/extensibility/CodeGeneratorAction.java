package pt.iscte.pidesco.codegenerator.extensibility;

import org.eclipse.swt.widgets.Composite;

public interface CodeGeneratorAction {
	
	void run(Composite area);

	String generateFileContent(String content);
}