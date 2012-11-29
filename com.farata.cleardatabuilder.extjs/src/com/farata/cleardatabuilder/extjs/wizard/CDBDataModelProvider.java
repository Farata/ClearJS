package com.farata.cleardatabuilder.extjs.wizard;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.eclipse.core.internal.runtime.LocalizationUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jpt.jpa.core.internal.facet.JpaFacetDataModelProperties;
import org.eclipse.jpt.jpa.core.internal.facet.JpaFacetInstallDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;

import com.farata.cleardatabuilder.extjs.facet.common.CommonInstallWizardPage;
import com.farata.cleardatabuilder.extjs.facet.sample.SampleInstallWizardPage;

public class CDBDataModelProvider extends JpaFacetInstallDataModelProvider implements CDBFacetDataModelProperties {
	public Set getPropertyNames() {
		Set<String> names = super.getPropertyNames();
		names.add(CDB_PROJECT_TYPE);
		names.add(CDB_APPLICATION_NAME);
		names.add(CDB_EXTJS_FOLDER);
		names.add(CDB_EXTJS_URL);
		names.add(CDB_EXTJS_CDN);
		names.add(CDB_PERSISTANCE_PLATFORM);
		names.add(CDB_SPRING_INTEGRATION);
		names.add(CDB_SAMPLEDB_FOLDER);
		names.add(CDB_EXTJS_LOCATION_TYPE);
		return names;
	}

	public IStatus validate(String s) {
		if (CDB_APPLICATION_NAME.equals(s)) {
			boolean valid = false;
			String app = model.getStringProperty(s);
			valid = CommonInstallWizardPage.validateAppName(app);
			if (valid) {
				return Status.OK_STATUS;
			} else {
				return new Status(4, "unknown", "Application name is not valid..");
			}
		} else if (CDB_EXTJS_LOCATION_TYPE.equals(s) || CDB_EXTJS_CDN.equals(s) || CDB_EXTJS_FOLDER.equals(s) || CDB_EXTJS_URL.equals(s)) {
			String locationType = model.getStringProperty(CDB_EXTJS_LOCATION_TYPE);
			if (TYPE_LOCAL_FOLDER.equals(locationType)) {
				String sPath = model.getStringProperty(CDB_EXTJS_FOLDER);
				if (sPath != null && sPath.trim().length() > 0) {
					File path = new File(sPath.trim());
					if (path.exists()) {
						boolean valid = CommonInstallWizardPage.validateExtJSPath(path);
						if (valid) {
							return Status.OK_STATUS;
						} else {
							return new Status(4, "unknown", "Ext JS folder is not valid.");
						}
					}
				}
			} else if (TYPE_CDN.equals(locationType)) {
				String sPath = model.getStringProperty(CDB_EXTJS_CDN);
				return validateURL(sPath);
			} else if (TYPE_LOCAL_URL.equals(locationType)) {
				String sPath = model.getStringProperty(CDB_EXTJS_URL);
				return validateURL(sPath);
			}
			
		} else if (CDB_SAMPLEDB_FOLDER.equals(s)) {
			boolean valid = false;
			String sPath = model.getStringProperty(s);
			if (sPath != null && sPath.trim().length() > 0) {
				File path = new File(sPath.trim());
				valid = SampleInstallWizardPage.validateInstallationFolder(path);
			}
			if (valid) {
				return Status.OK_STATUS;
			} else {
				return new Status(4, "unknown", "cleardb installation folder is not valid.");
			}
		} else if (CDB_PERSISTANCE_PLATFORM.equals(s)) {
			String platform = model.getStringProperty(s);
			boolean isNew = "new".equals(model.getStringProperty(CDB_PROJECT_TYPE));
			if (isNew && ("myBatis".equals(platform) || "hibernate".equals(platform))) {
				boolean connectionActive = model.getBooleanProperty(JpaFacetDataModelProperties.CONNECTION_ACTIVE);
				// boolean connectionActive =
				// model.getProperty(JpaFacetDataModelProperties.CONNECTION_ACTIVE);
				if (!connectionActive) {
					return new Status(4, "unknown", "Connection should be selected and active.");
				}
			}
			return Status.OK_STATUS;
		}
		return super.validate(s);
	}

	private IStatus validateURL(String sPath) {
		IStatus status = Status.OK_STATUS;
		boolean valid = sPath != null && sPath.trim().length() > 0;
		if (!valid) {
			return new Status(4, "unknown", "Ext JS location is not valid."); 
		}
//		if (isExternalURL(sPath)) {
//			if (sPath.endsWith("/")) {
//				sPath = sPath.substring(0, sPath.length() - 1);
//			}
//			try {
//				URL url = new URL(sPath+"/ext-all.js");
//				url.openStream();
//			} catch (Exception e) {
//				//e.printStackTrace();
//				return new Status(4, "unknown", "Ext JS location is not valid. " + e.getMessage()); 
//			} 
//			
//		}
		return status;
	}

	private boolean isExternalURL(String sPath) {
		return sPath.startsWith("http");
	}
}