package com.farata.cleardatabuilder.extjs.facet.sample;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;
import org.eclipse.wst.common.project.facet.ui.ModifyFacetedProjectWizard;

import com.farata.cleardatabuilder.extjs.facet.sample.ui.SampleInstallWizardPageUI;


public class SampleInstallWizardPage extends AbstractFacetWizardPage {

	private SampleInstallConfig	config;

	public SampleInstallWizardPage() {
		super("");
	}

	public SampleInstallWizardPage(String name) {
		super(name);
	}

	@Override
	public void createControl(Composite composite) {
		Composite c = new Composite(composite, SWT.NONE);
		SampleInstallWizardPageUI impl = new SampleInstallWizardPageUI(this, c);
		impl.createParent();
		setControl(c);
		setTitle("Clear Data Builder JS Facet");
		setDescription("Configure Clear Data Builder JS settings.");
	}

	public SampleInstallConfig getConfig() {
		return config;
	}

	@Override
	public void setConfig(Object config) {
		this.config = (SampleInstallConfig) config;
		if (this.config != null) {
			this.config.setWizardContext(context);
		}
	}

	public boolean validateInstallationFolder(File path) {
		return path.isAbsolute();
	}
}