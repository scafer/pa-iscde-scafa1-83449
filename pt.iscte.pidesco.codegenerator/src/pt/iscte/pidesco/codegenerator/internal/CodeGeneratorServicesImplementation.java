package pt.iscte.pidesco.codegenerator.internal;

import pt.iscte.pidesco.codegenerator.Activator;
import pt.iscte.pidesco.codegenerator.service.CodeGeneratorListener;
import pt.iscte.pidesco.codegenerator.service.CodeGeneratorServices;

public class CodeGeneratorServicesImplementation implements CodeGeneratorServices {

	@Override
	public void addListener(CodeGeneratorListener listener) {
		Activator.getInstance().addListener(listener);
	}

	@Override
	public void removeListener(CodeGeneratorListener listener) {
		Activator.getInstance().removeListener(listener);
	}
}