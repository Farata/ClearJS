package com.farata.cleardatabuilder.extjs.facet.common;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action.Type;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;
import org.eclipse.wst.common.project.facet.ui.IWizardContext;

import com.farata.cleardatabuilder.extjs.facet.common.ui.CommonInstallWizardPageUI;
import com.farata.cleardatabuilder.extjs.facet.sample.SampleInstallConfig;

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
		setTitle("ClearDataBuilder for Ext JS Facet");
		setDescription("Configure ClearDataBuilder for Ext JS settings.");
		setPageComplete(false);
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

	public static SampleInstallConfig extractSampleConfiguration(
			CommonInstallConfig config) throws CoreException {
		IWizardContext wizardContext = config.getWizardContext();
		Set<?> projectFacets = wizardContext.getSelectedProjectFacets();
		for (Object oProjectFacet : projectFacets) {
			if (oProjectFacet instanceof IProjectFacetVersion) {
				Object conf = wizardContext.getConfig(
						(IProjectFacetVersion) oProjectFacet, Type.INSTALL, "");
				if (conf instanceof SampleInstallConfig) {
					return (SampleInstallConfig) conf;
				}
			}
		}
		return null;
	}
}