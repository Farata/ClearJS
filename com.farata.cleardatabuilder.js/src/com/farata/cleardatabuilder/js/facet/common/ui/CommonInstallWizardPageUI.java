package com.farata.cleardatabuilder.js.facet.common.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.farata.cleardatabuilder.js.facet.common.CommonInstallWizardPage;


public class CommonInstallWizardPageUI {

	private Composite			parent			= null; // @jve:decl-index=0:visual-constraint="46,23"
	private Button				springCheckbox	= null;
	private CommonInstallWizardPage	installWizardPage;

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
		springCheckbox = new Button(parent, SWT.CHECK);
		springCheckbox.setText("Add Spring support");
		springCheckbox.setSelection(true);
		springCheckbox.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				installWizardPage.getConfig().setAddSpringSupport(springCheckbox.getSelection());
			}
		});
		
	}

	public CommonInstallWizardPageUI(CommonInstallWizardPage installWizardPage,
			Composite parent) {
		this.parent = parent;
		this.installWizardPage = installWizardPage;
	}
}