package pt.iscte.pidesco.codegenerator;

import java.util.Set;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import pt.iscte.pidesco.codegenerator.service.CodeGeneratorListener;
import pt.iscte.pidesco.javaeditor.service.JavaEditorServices;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static Activator instance;
	private JavaEditorServices javaEditorServices;
	private Set<CodeGeneratorListener> listeners;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		instance = this;
				
		ServiceReference<JavaEditorServices> ref = bundleContext.getServiceReference(JavaEditorServices.class);
		javaEditorServices = bundleContext.getService(ref);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		instance = null;
	}
	
	public static Activator getInstance() {
		return instance;
	}

	public JavaEditorServices getJavaEditorServices() {
		return javaEditorServices;
	}
	
	public void addListener(CodeGeneratorListener l) {
		listeners.add(l);
	}

	public void removeListener(CodeGeneratorListener l) {
		listeners.remove(l);
	}
}