package com.farata.cleardatabuilder.extjs.wizard;

import org.eclipse.jpt.jpa.ui.internal.wizards.JpaFacetInstallPage;
import org.eclipse.swt.widgets.Composite; 
import  org.eclipse.wst.web.ui.internal.wizards.DataModelFacetInstallPage;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;

public class CDBJpaFacetInstallPage extends JpaFacetInstallPage {
	public static ConnectionGroup getConnectionGroup(Composite composite, CDBProjectWizard w) {
		CDBJpaFacetInstallPage instance = new CDBJpaFacetInstallPage();
		instance.model.addNestedModel("cdb", w.getDataModel());
		ConnectionGroup result = instance.new ConnectionGroup(composite);
		return result;
	}
}
