package pt.iscte.pidesco.codegenerator;

import java.io.File;
import java.util.Map;

import javax.swing.JOptionPane;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import pt.iscte.pidesco.codegenerator.extensibility.CodeGeneratorAction;
import pt.iscte.pidesco.codegenerator.internal.FileEditorListener;
import pt.iscte.pidesco.codegenerator.internal.FileHandler;
import pt.iscte.pidesco.extensibility.PidescoView;
import pt.iscte.pidesco.javaeditor.service.JavaEditorServices;

public class MainView implements PidescoView {
	Text textInput;
	Boolean isFromFile;
	static MainView instance;
	FileEditorListener fileEditorListener;

	public static MainView getInstance() { return instance; }
	
	@Override
	public void createContents(Composite viewArea, Map<String, Image> imageMap) {
		
		GridLayout layout = new GridLayout(1, true);
		viewArea.setLayout(layout);
		
		fileEditorListener = new FileEditorListener();
		JavaEditorServices javaEditorServices = Activator.getInstance().getJavaEditorServices();
		javaEditorServices.addListener(fileEditorListener);
	
		new Label(viewArea, SWT.NONE).setText("Code Generator:");
		
		//Options Group		
		Group optionsGroup = new Group(viewArea, SWT.NONE);
		optionsGroup.setLayout(new RowLayout(SWT.VERTICAL));
		
		Button buttonContentFromText = new Button(optionsGroup, SWT.RADIO);
		buttonContentFromText.setText("Get code from text box");

		buttonContentFromText.addSelectionListener(new SelectionAdapter()  {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button source = (Button) e.getSource();
                if(source.getSelection())  {
                	isFromFile = false;
                }
            }
        });
		
		Button buttonContentFromFile = new Button(optionsGroup, SWT.RADIO);
		buttonContentFromFile.setText("Get code from opened file");
		
		buttonContentFromFile.addSelectionListener(new SelectionAdapter()  {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button source = (Button) e.getSource();
                if(source.getSelection())  {
                	isFromFile = true;
                }
            }
        });
		
		//Code Block GUI
		new Label(viewArea, SWT.PUSH).setText("Code Block:");
		GridData codeBlockData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		textInput = new Text(viewArea, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
		textInput.setLayoutData(codeBlockData);
				
		ExtensionRegistry(viewArea);
	}
	
	private void ExtensionRegistry(Composite viewArea) {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = reg.getConfigurationElementsFor("pt.iscte.pidesco.codegenerator.actions");
		for(IConfigurationElement e : elements) {
			String name = e.getAttribute("name");
			System.out.println(name);
			
			new Label(viewArea, SWT.TOP).setText(name);
			try {
				CodeGeneratorAction action = (CodeGeneratorAction) e.createExecutableExtension("class");
				action.run(viewArea);
				viewArea.layout();
				
				Button generateCode = new Button(viewArea, SWT.PUSH);
				generateCode.setText("Generate Code");
				
				generateCode.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						String resultContent = action.generateFileContent(getContent());
						if(resultContent != null) {
							if(fileEditorListener.getFile().getAbsolutePath() == null) {
								JOptionPane.showMessageDialog(null, "File path is empty. Open a new file.");
							}
							else {
								FileHandler fileHandler = new FileHandler();
								fileHandler.saveOrUpdateFile(new File(fileEditorListener.getFile().getAbsolutePath()), resultContent);
								JOptionPane.showMessageDialog(null, "Successfully generated, refresh the document.");
							}
						}
						else
							JOptionPane.showMessageDialog(null, "Error while parsing code!");
					}
				});
			} catch (CoreException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, ex.getMessage());
			}
		}
	}
	
	private String getContent() {
		String result = null;
		
		if(isFromFile) {
			FileHandler fileHandler = new FileHandler();
        	result = fileHandler.readFileLines(fileEditorListener.getFile().getAbsolutePath());
		}
		else {
			result = textInput.getText();
		}

		return result;
	}
}