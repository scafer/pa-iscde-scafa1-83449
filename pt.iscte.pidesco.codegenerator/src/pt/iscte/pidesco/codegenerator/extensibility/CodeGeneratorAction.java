package pt.iscte.pidesco.codegenerator.extensibility;

import org.eclipse.swt.widgets.Composite;

public interface CodeGeneratorAction {
	//Create view elements
	void run(Composite viewArea);

	//Method that return the generated code
	String generateFileContent(String content);
}