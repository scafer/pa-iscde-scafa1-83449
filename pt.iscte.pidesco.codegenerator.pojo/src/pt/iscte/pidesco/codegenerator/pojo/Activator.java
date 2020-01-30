package pt.iscte.pidesco.codegenerator.pojo;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
	private static BundleContext context;
	private static Activator instance;

	public static BundleContext getContext() {
		return context;
	}
	
	public static Activator getInstance() {
		return instance;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		instance = this;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		instance = null;
	}
}