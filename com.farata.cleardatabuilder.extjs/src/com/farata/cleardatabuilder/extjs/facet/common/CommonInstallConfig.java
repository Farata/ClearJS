package com.farata.cleardatabuilder.extjs.facet.common;

import java.io.File;

import org.eclipse.wst.common.project.facet.ui.IWizardContext;

public class CommonInstallConfig {

	private IWizardContext wizardContext;
	private boolean addSpringSupport = false;
	private File extJSPath;
	private String appName;

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

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppName() {
		return appName;
	}
	
}
