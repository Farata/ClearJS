package com.farata.cleardatabuilder.js.facet.common;

import org.eclipse.wst.common.project.facet.ui.IWizardContext;

public class CommonInstallConfig {

	private IWizardContext wizardContext;
	private boolean addSpringSupport = true;

	public boolean isAddSpringSupport() {
		return addSpringSupport;
	}

	public void setAddSpringSupport(boolean addSpringSupport) {
		this.addSpringSupport = addSpringSupport;
	}

	public IWizardContext getWizardContext() {
		return wizardContext;
	}

	public void setWizardContext(IWizardContext wizardContext) {
		this.wizardContext = wizardContext;
	}
	
}
