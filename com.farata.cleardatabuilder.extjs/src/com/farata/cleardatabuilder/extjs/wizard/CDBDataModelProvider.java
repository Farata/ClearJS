package com.farata.cleardatabuilder.extjs.wizard;

import java.io.File;
import java.util.Set;

import org.eclipse.core.internal.runtime.LocalizationUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jpt.jpa.core.internal.facet.JpaFacetInstallDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;

import com.farata.cleardatabuilder.extjs.facet.common.CommonInstallWizardPage;
import com.farata.cleardatabuilder.extjs.facet.sample.SampleInstallWizardPage;

public class CDBDataModelProvider extends JpaFacetInstallDataModelProvider implements CDBFacetDataModelProperties {
	public Set getPropertyNames() {
		Set<String> names = super.getPropertyNames();
		names.add(CDB_PROJECT_TYPE);
		names.add(CDB_EXTJS_FOLDER);
		names.add(CDB_PERSISTANCE_PLATFORM);
		names.add(CDB_SPRING_ITEGRATION);
		names.add(CDB_SAMPLEDB_FOLDER);
		return names;
	}

	public IStatus validate(String s) {
		if (CDB_EXTJS_FOLDER.equals(s)) {
			boolean valid = false;
			String sPath = model.getStringProperty(s);
			if (sPath != null && sPath.trim().length() > 0) {
				File path = new File(sPath.trim());
				if (path.exists()) {
					valid = CommonInstallWizardPage.validateExtJSPath(path);
				}
			}
			if (valid) {
				return Status.OK_STATUS;
			} else {
				return new Status(4, "unknown", "Ext JS folder is not valid.");
			}
		} else if (CDB_SAMPLEDB_FOLDER.equals(s)) {
			boolean valid = false;
			String sPath = model.getStringProperty(s);
			if (sPath != null && sPath.trim().length() > 0) {
				File path = new File(sPath.trim());
				if (path.exists()) {
					valid = SampleInstallWizardPage.validateInstallationFolder(path);
				}
			}
			if (valid) {
				return Status.OK_STATUS;
			} else {
				return new Status(4, "unknown", "cleardb installation folder is not valid.");
			}
		}
		return super.validate(s);
	}
}