package pt.iscte.pidesco.codegenerator;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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
	String saveTo = null;

	public static MainView getInstance() { return instance; }
	
	@Override
	public void createContents(Composite viewArea, Map<String, Image> imageMap) {
		
		GridLayout layout = new GridLayout(1, true);
		viewArea.setLayout(layout);
		
		fileEditorListener = new FileEditorListener();
		JavaEditorServices javaEditorServices = Activator.getInstance().getJavaEditorServices();
		javaEditorServices.addListener(fileEditorListener);
	
		Label title = new Label(viewArea, SWT.NONE);
		title.setText("Code Generator");
		title.setFont(new Font(title.getDisplay(), new FontData("Arial", 12, SWT.BOLD)));
		
		//Options Group		
		Group optionsGroup = new Group(viewArea, SWT.NONE);
		optionsGroup.setLayout(new RowLayout(SWT.VERTICAL));
		
		Button buttonContentFromText = new Button(optionsGroup, SWT.RADIO);
		buttonContentFromText.setText("Get code from code block");

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
		
		//Save Options Group
		Group saveOptionsGroup = new Group(viewArea, SWT.NONE);
		saveOptionsGroup.setLayout(new RowLayout(SWT.VERTICAL));
				
		Button buttonSaveToFile = new Button(saveOptionsGroup, SWT.RADIO);
		buttonSaveToFile.setText("Save to opened file");
		
		Button buttonSaveToClipboard = new Button(saveOptionsGroup, SWT.RADIO);
		buttonSaveToClipboard.setText("Copy to clipboard");
		
		buttonSaveToFile.addSelectionListener(new SelectionAdapter()  {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button source = (Button) e.getSource();
                if(source.getSelection())  {
                	saveTo = "file";
                }
            }
        });
		
		buttonSaveToClipboard.addSelectionListener(new SelectionAdapter()  {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button source = (Button) e.getSource();
                if(source.getSelection())  {
                	saveTo = "clipboard";
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
			String extensionName = e.getAttribute("name");
			System.out.println(extensionName);
			
			Label name = new Label(viewArea, SWT.TOP);
			name.setText(extensionName);
			name.setFont(new Font(name.getDisplay(), new FontData("Arial", 10, SWT.BOLD)));
			
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
								if(saveTo == "file") {
									FileHandler fileHandler = new FileHandler();
									fileHandler.saveOrUpdateFile(new File(fileEditorListener.getFile().getAbsolutePath()), resultContent);
									JOptionPane.showMessageDialog(null, "Successfully generated, refresh the document.");
								}
								
								else if(saveTo == "clipboard") {
									Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
									Transferable transferable = new StringSelection(resultContent);
									clipboard.setContents(transferable, null);
									JOptionPane.showMessageDialog(null, "Saved to Clipboard");
								}
								else {
									JOptionPane.showMessageDialog(null, "Select one of the save options.");
								}
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
        	System.out.println(result);
		}
		else {
			result = textInput.getText();
		}
		return result;
	}
}