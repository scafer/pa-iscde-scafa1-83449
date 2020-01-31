package pt.iscte.pidesco.codegenerator.pojo;

import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import pt.iscte.pidesco.codegenerator.extensibility.CodeGeneratorAction;
import pt.iscte.pidesco.codegenerator.pojo.service.JsonParser;

public class PojoAction implements CodeGeneratorAction {
	
	Text className, packageName;
	String serializationMode;

	@Override
	public void run(Composite viewArea) {
		//Options Group		
		Group optionsGroup = new Group(viewArea, SWT.NONE);
		optionsGroup.setLayout(new RowLayout(SWT.VERTICAL));
		
		new Label(optionsGroup, SWT.NONE).setText("JSON libraries:");
		
		Button buttonJackson = new Button(optionsGroup, SWT.RADIO);
		buttonJackson.setText("Jackson");
		
		Button buttonGson = new Button(optionsGroup, SWT.RADIO);
		buttonGson.setText("Gson");
		
		buttonJackson.addSelectionListener(new SelectionAdapter()  {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button source = (Button) e.getSource();
                if(source.getSelection())  {
                	serializationMode = "jackson";
                }
            }
        });
		
		buttonGson.addSelectionListener(new SelectionAdapter()  {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button source = (Button) e.getSource();
                if(source.getSelection())  {
                	serializationMode = "gson";
                }
            }
        });
		
		//Properties Group
		Group propertiesGroup = new Group(viewArea, SWT.NONE);
		propertiesGroup.setLayout(new RowLayout(SWT.VERTICAL));
		
		new Label(propertiesGroup, SWT.NONE).setText("Class Name:");
		className = new Text(propertiesGroup, SWT.NONE);	
		new Label(propertiesGroup, SWT.NONE).setText("Package Name:");
		packageName = new Text(propertiesGroup, SWT.NONE);
	}
	
	@Override //Run when generate button is clicked
	public String generateFileContent(String content) {
		String result = null;		
		if(isNullOrEmpty(content)) {
			JOptionPane.showMessageDialog(null, "Content is null or empty!");
		}
		else if(isNullOrEmpty(className.getText()) || isNullOrEmpty(packageName.getText())) {
			JOptionPane.showMessageDialog(null, "Class or package name is empty!");
		}
		else {
			JsonParser jsonParser = new JsonParser(className.getText(), packageName.getText(), serializationMode);
			result = jsonParser.GeneratePojo(content);
		}
		return result;
	}
	
	private Boolean isNullOrEmpty(String value) {
		if(value.isEmpty() || value == null)
			return true;
		else
			return false;
	}
}