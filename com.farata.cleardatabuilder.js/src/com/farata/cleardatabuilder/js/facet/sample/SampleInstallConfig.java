package com.farata.cleardatabuilder.js.facet.sample;

import java.io.File;

import org.eclipse.wst.common.project.facet.ui.IWizardContext;

public class SampleInstallConfig {
	private IWizardContext wizardContext;
	private File sampleDBInstallFolder;
	private boolean hibernateSample = true;

	public boolean isHibernateSample() {
		return hibernateSample;
	}

	public File getSampleDBInstallFolder() {
		return sampleDBInstallFolder;
	}

	public void setSampleDBInstallFolder(File sampleDBInstallFolder) {
		this.sampleDBInstallFolder = sampleDBInstallFolder;
	}

	public IWizardContext getWizardContext() {
		return wizardContext;
	}

	public void setWizardContext(IWizardContext wizardContext) {
		this.wizardContext = wizardContext;
	}

	public void setHibernateSample(boolean hibernateSample) {
		this.hibernateSample = hibernateSample;	
	}
}
