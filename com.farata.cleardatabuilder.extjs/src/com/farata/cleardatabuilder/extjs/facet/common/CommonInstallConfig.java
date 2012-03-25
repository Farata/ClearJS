package com.farata.cleardatabuilder.extjs.facet.common;

import java.io.File;

import org.eclipse.wst.common.project.facet.ui.IWizardContext;

public class CommonInstallConfig {

	private IWizardContext wizardContext;
	private boolean addSpringSupport = true;
	private File extJSPath;

	public File getExtJSPath() {
		return extJSPath;
	}

	public void setExtJSPath(File extJSPath) {
		this.extJSPath = extJSPath;
	}

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
