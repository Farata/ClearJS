package com.farata.cleardatabuilder.extjs.facet.common.ui;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.farata.cleardatabuilder.extjs.facet.common.CommonInstallWizardPage;

public class CommonInstallWizardPageUI {

	private Composite parent = null; // @jve:decl-index=0:visual-constraint="46,23"
	private Button springCheckbox = null;
	private CommonInstallWizardPage installWizardPage;
	private Text extJSPath = null;
	private Text appNameText;

	/**
	 * This method initializes parent
	 * 
	 */
	public void createParent() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = false;
		if (parent == null) {
			parent = new Shell();
			parent.setSize(new Point(463, 288));
		}
		parent.setLayout(gridLayout);

		Label label = new Label(parent, SWT.NONE);
		label.setText("ExtJS distribution folder:");
		extJSPath = new Text(parent, SWT.BORDER);
		extJSPath.setLayoutData(gridData);
		extJSPath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validateFields();
			}
		});
		Button button = new Button(parent, SWT.NONE);
		button.setText("Browse...");
		button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(parent.getShell());
				if (!"".equals(extJSPath.getText())) {
					String initialDir = extJSPath.getText();
					dialog.setFilterPath(initialDir);
				}
				String result = dialog.open();
				if (result != null) {
					extJSPath.setText(result.toString());
				}
			}
		});

		String projectName = installWizardPage.getConfig().getWizardContext().getProjectName();
		Label appNameLabel = new Label(parent, SWT.NONE);
		appNameLabel.setText("Application name:");
		appNameText = new Text(parent, SWT.BORDER);
		appNameText.setLayoutData(gridData);
		appNameText.setText(projectName.toUpperCase());
		appNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validateFields();
			}
		});
		
		new Label(parent, SWT.NONE);

		springCheckbox = new Button(parent, SWT.CHECK);
		springCheckbox.setText("Add Spring support");
		springCheckbox.setEnabled(false);
		// springCheckbox.setSelection(true);
		springCheckbox.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				installWizardPage.getConfig().setAddSpringSupport(springCheckbox.getSelection());
			}
		});

	}

	private void validateFields() {
		boolean extJSPathValid = installWizardPage.validateExtJSPath(new File(extJSPath.getText()));
		boolean appNameValid = installWizardPage.validateAppName(appNameText.getText());
		boolean valid = extJSPathValid && appNameValid;
		installWizardPage.setPageComplete(valid);
		if (valid) {
			installWizardPage.setErrorMessage(null);
			installWizardPage.getConfig().setExtJSPath(new File(extJSPath.getText()));
			installWizardPage.getConfig().setAppName(appNameText.getText());
		} else if (!extJSPathValid) {
			installWizardPage.setErrorMessage("ExtJS distribution folder is not valid.");
		} else if (!appNameValid) {
			installWizardPage.setErrorMessage("Application name is not valid.");
		}
	}

	public CommonInstallWizardPageUI(CommonInstallWizardPage installWizardPage, Composite parent) {
		this.parent = parent;
		this.installWizardPage = installWizardPage;
	}
}