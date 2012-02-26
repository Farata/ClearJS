package com.farata.cleardatabuilder.js.facet.common;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;

import com.farata.cleardatabuilder.js.facet.common.ui.CommonInstallWizardPageUI;

public class CommonInstallWizardPage extends AbstractFacetWizardPage {

	private CommonInstallConfig config;

	public CommonInstallWizardPage() {
		super("");
	}

	public CommonInstallWizardPage(String name) {
		super(name);
	}

	@Override
	public void createControl(Composite composite) {
		Composite c = new Composite(composite, SWT.NONE);
		CommonInstallWizardPageUI impl = new CommonInstallWizardPageUI(this, c);
		impl.createParent();
		setControl(c);
		setTitle("Clear Data Builder JS Facet");
		setDescription("Configure Clear Data Builder JS settings.");
		setPageComplete(true);
	}

	public CommonInstallConfig getConfig() {
		return config;
	}

	@Override
	public void setConfig(Object config) {
		this.config = (CommonInstallConfig) config;
		if (this.config != null) {
			this.config.setWizardContext(context);
		}
	}

	public boolean validateExtJSPath(final File path) {
		if (path != null && path.isDirectory()) {
			File[] files = path.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return dir.equals(path) && name.equalsIgnoreCase("ext.js");
				}
			});
			return files.length > 0;
		}
		return false;
	}
}