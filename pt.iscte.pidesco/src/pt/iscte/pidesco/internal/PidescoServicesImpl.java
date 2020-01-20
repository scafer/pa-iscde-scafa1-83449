package pt.iscte.pidesco.internal;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import pt.iscte.pidesco.extensibility.PidescoExtensionPoint;
import pt.iscte.pidesco.extensibility.PidescoServices;
import pt.iscte.pidesco.extensibility.PidescoTool;
import pt.iscte.pidesco.extensibility.ViewLocation;

public class PidescoServicesImpl implements PidescoServices {

	private Map<String, PidescoTool> tools;
	
	private void loadTools() {
		tools = new HashMap<String, PidescoTool>();
		for(IExtension ext : PidescoExtensionPoint.TOOL.getExtensions()) {
			try {
				PidescoTool tool = (PidescoTool) ext.getConfigurationElements()[0].createExecutableExtension("class");
				tools.put(ext.getUniqueIdentifier(), tool);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void openView(String viewId) {
		Assert.isNotNull(viewId, "view id cannot be null");
		PidescoExtensionPoint.VIEW.checkId(viewId);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			page.showView(PidescoServices.VIEW_ID, viewId, IWorkbenchPage.VIEW_ACTIVATE);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getActiveView() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkbenchPart activePart = page.getActivePart();
		if(activePart != null) {
			if(activePart instanceof IViewPart)
				return ((IViewPart) activePart).getViewSite().getSecondaryId();
			else if(activePart instanceof IEditorPart)
				return ((IEditorPart) activePart).getEditorSite().getId();
		}
		return null;
	}
	
	@Override
	public Image getImageFromPlugin(String pluginId, String fileName) {
		Assert.isNotNull(pluginId, "plugin id cannot be null");
		Assert.isNotNull(fileName, "file name cannot be null");
		
		ImageDescriptor imgDesc = PidescoActivator.getImageDescriptor(pluginId, IMAGES_FOLDER + "/" + fileName);
		return imgDesc == null ? null : imgDesc.createImage();
	}
	

	@Override
	public void runTool(String toolId, boolean activate) {
		Assert.isNotNull(toolId, "tool id cannot be null");
		PidescoExtensionPoint.TOOL.checkId(toolId);

		if(tools == null)
			loadTools();
		
		if(!tools.containsKey(toolId))
			throw new IllegalArgumentException("toolId '" + toolId + "' does not exist.");

		tools.get(toolId).run(activate);
		
	}

	@Override
	public void layout(List<ViewLocation> viewLocations) {
		Assert.isNotNull(viewLocations, "list cannot be null");
		PidescoActivator.getInstance().setLayout(viewLocations);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().resetPerspective();
	}
	

	@Override
	public File getWorkspaceRoot() {
		return new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
	}


	
}
