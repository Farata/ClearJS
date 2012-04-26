package com.farata.cleardatabuilder.extjs.wizard;

import org.eclipse.jst.j2ee.internal.plugin.J2EEUIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;

public class CDBProjectFirstPage extends DataModelWizardPage {

	Button newProject;
	Button myBatisExampleProjct;
	Button hibernateExampleProject;
	Button javaExampleProject;

	protected CDBProjectFirstPage(IDataModel model, String pageName) {
		super(model, pageName);
		setTitle("Clear Data Builder Project Type");
		setDescription("Select Clear Data Builder project type");
		setImageDescriptor(J2EEUIPlugin.getDefault().getImageDescriptor("war_wiz"));
		//setInfopopID("org.eclipse.jst.servlet.ui.webw1000");
	}

	@Override
	protected String[] getValidationPropertyNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite top = new Composite(parent, 0);
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(top, getInfopopID());
		top.setLayout(new GridLayout());
		top.setLayoutData(new GridData(1808));
		Label label = new Label(top, SWT.NONE);
		label.setText("Select Project Type:");
		
		CDBProjectTypeSelectionListener selectionListener = new CDBProjectTypeSelectionListener(this, model);
	    
		newProject = new Button(top, SWT.RADIO);
	    newProject.setText("New Clear Data Builder Project");
	    newProject.setSelection(true);
	    newProject.addSelectionListener(selectionListener);
	    
	    myBatisExampleProjct = new Button(top, SWT.RADIO);
	    myBatisExampleProjct.setText("MyBatis/Spring Example Project");
	    myBatisExampleProjct.addSelectionListener(selectionListener);
	    
	    hibernateExampleProject = new Button(top, SWT.RADIO);
	    hibernateExampleProject.setText("Hibernate Example Project");
	    hibernateExampleProject.addSelectionListener(selectionListener);
	    
	    javaExampleProject = new Button(top, SWT.RADIO);
	    javaExampleProject.setText("Java Example Project");
	    javaExampleProject.addSelectionListener(selectionListener);
	    
		return top;
	}
}