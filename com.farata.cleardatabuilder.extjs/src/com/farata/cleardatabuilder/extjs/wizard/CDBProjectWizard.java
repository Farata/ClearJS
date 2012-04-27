package com.farata.cleardatabuilder.extjs.wizard;

import java.io.File;
import java.sql.Connection;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jpt.jpa.core.JpaDataSource;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.JptJpaCorePlugin;
import org.eclipse.jpt.jpa.db.ConnectionProfile;
import org.eclipse.jst.common.project.facet.core.JavaFacetInstallConfig;
import org.eclipse.jst.servlet.ui.project.facet.WebProjectWizard;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationPropertiesNew;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action.Type;
import org.eclipse.wst.common.project.facet.ui.IWizardContext;
import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.sqltools.core.DatabaseIdentifier;
import org.eclipse.datatools.sqltools.core.profile.ProfileUtil;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.resolver.DialectFactory;

import com.farata.cleardatabuilder.extjs.facet.common.CommonInstallConfig;
import com.farata.cleardatabuilder.extjs.facet.common.Installer;
import com.farata.cleardatabuilder.extjs.facet.sample.SampleInstallConfig;
import com.farata.cleardatabuilder.extjs.facet.sample.SampleInstallDelegate;
import com.farata.cleardatabuilder.extjs.util.HibernateDialectResolver;

//import org.hibernate.dialect.Dialect;

public class CDBProjectWizard extends WebProjectWizard implements CDBFacetDataModelProperties {
	private IWizardPage[] beginingPages;
	CDBProjectSecondPage secondPage;
	CDBProjectFirstPage firstPage;

	public CDBProjectWizard() {
		super();
	}

	protected IWizardPage[] createBeginingPages() {
		return (new IWizardPage[] { createFirstPage(), createSecondPage() });
	}

	protected IWizardPage createFirstPage() {
		firstPage = new CDBProjectFirstPage(model, "first.page");
		return firstPage;
	}

	private IWizardPage createSecondPage() {
		secondPage = new CDBProjectSecondPage(model, "second.page");
		return secondPage;
	}

	public void addPages() {
		beginingPages = createBeginingPages();
		for (int i = 0; i < beginingPages.length; i++) {
			addPage(beginingPages[i]);
		}
	}

	public IWizardPage[] getPages() {
		return beginingPages;
	}

	@Override
	protected IDataModel createDataModel() {
		try {
			IDataModel m = super.createDataModel();
			m.addNestedModel("jpa", DataModelFactory.createDataModel(new CDBDataModelProvider()));
			return m;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected void performFinish(IProgressMonitor monitor) throws CoreException {
		super.performFinish(monitor);

		String type = model.getStringProperty(CDB_PROJECT_TYPE);
		final boolean isNew = "new".equals(type);
		final boolean isHibernateExample = "hibernateExample".equals(type);
		final boolean isJavaExample = "javaExample".equals(type);
		final boolean isMyBatisExample = "myBatisExample".equals(type);

		if (isHibernateExample || isMyBatisExample) {
			File installationFolder = new File(model.getStringProperty(CDB_SAMPLEDB_FOLDER));
			SampleInstallDelegate.unpackSampleDB(installationFolder, monitor);
			Collection all = model.getAllProperties();
			IProject project = (IProject) model.getProperty(IProjectCreationPropertiesNew.PROJECT);
			try {
				SampleInstallDelegate.createDBConnection(project, installationFolder);
			} catch (ConnectionProfileException e) {
				e.printStackTrace();
			}
		}

		final String prjName = getProjectName();
		Job job = new Job("Installing " + prjName) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				final Properties props = new Properties();
				String v = getJavaVersion();
				if (v != null) {
					props.setProperty("project.java.version", v);
				} else {
					props.setProperty("project.java.version", "1.6");
				}

				props.setProperty("extjs.path", model.getStringProperty(CDB_EXTJS_FOLDER));
				props.setProperty("app.name", prjName);

				if (isHibernateExample) {
					props.setProperty("is.hibernate.sample", "true");
				} else if (isJavaExample) {
					props.setProperty("is.plain.java.sample", "true");
				} else if (isMyBatisExample) {
					props.setProperty("is.mybatis.sample", "true");
				}
				if (model.getBooleanProperty(CDB_SPRING_ITEGRATION)) {
					props.setProperty("add.spring.support", "true");
				} else {
					props.setProperty("dont.add.spring.support", "true");
				}

				fillHibernateProps(props, monitor);

				Installer.install(props, prjName, isNew, null);
				return Status.OK_STATUS;
			}
		};
		job.schedule(5);
	}

	private void fillHibernateProps(Properties props, IProgressMonitor monitor) {
		try {
			props.setProperty("persistence.unit", getProjectName());
			setDefaultProps(props);

			Properties properties = new Properties();
			String profileName = model.getStringProperty(CONNECTION);
			if (profileName == null || profileName.length() == 0) {
				return;
			}
			IConnectionProfile profile = ProfileUtil.getProfile(profileName);
			DatabaseIdentifier databaseIdentifier = null;
			Dialect dialect = null;
			if (profile != null) {
				props.setProperty(PARAM_DS_DRIVER_CLASS_NAME,
						profile.getBaseProperties().getProperty(ProfileUtil.DRIVERCLASS));
				props.setProperty(PARAM_DS_NAME, ProfileUtil.getProfileDatabaseName(profileName));
				props.setProperty(PARAM_DS_PASSWORD,
						ProfileUtil.getPassword(profile) == null ? "" : ProfileUtil.getPassword(profile));
				props.setProperty(PARAM_DS_URL, profile.getBaseProperties().getProperty(ProfileUtil.URL));
				props.setProperty(PARAM_DS_USER, ProfileUtil.getUserName(profile));
				databaseIdentifier = new DatabaseIdentifier(profile.getName(),
						ProfileUtil.getProfileDatabaseName(profileName));
				try {
					Connection conn = ProfileUtil.getOrCreateReusableConnection(databaseIdentifier);
					dialect = DialectFactory.buildDialect(properties, conn);
					ProfileUtil.closeConnection(profile.getName(), ProfileUtil.getProfileDatabaseName(profileName),
							conn);
				} catch (Throwable e) {
					try {
						IConnectionProfile iprofile = ProfileUtil.getProfile(profile.getName());
						Properties dbProps = iprofile.getProperties(ProfileUtil.PROFILE_DB_VERSION_TYPE);
						String dbVersion = dbProps.getProperty(ProfileUtil.PROFILE_DB_VERSION);
						String dbType = dbProps.getProperty(ProfileUtil.PROFILE_DB_VENDOR_NAME);
						dialect = HibernateDialectResolver.resolveDialect(dbType, dbVersion);
					} catch (Throwable e1) {
					}
				}
			}

			if (dialect != null) {
				props.setProperty("hibernate.dialect", dialect.toString());
				String dsName = "java:/comp/env/jdbc/" + ProfileUtil.getProfileDatabaseName(profileName);
				props.setProperty("jta.data.source", dsName);
			} else {
				props.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
				String dsName = "java:/comp/env/jdbc/cleardb";
				props.setProperty("jta.data.source", dsName);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setDefaultProps(Properties props) {
		props.setProperty(PARAM_DS_DRIVER_CLASS_NAME, "org.hsqldb.jdbcDriver");
		props.setProperty(PARAM_DS_NAME, "cleardb");
		props.setProperty(PARAM_DS_PASSWORD, "");
		props.setProperty(PARAM_DS_URL, "jdbc:hsqldb:hsql://localhost:9002/cleardb");
		props.setProperty(PARAM_DS_USER, "sa");
	}

	private String getJavaVersion() {
		Set<?> projectFacets = getFacetedProject().getProjectFacets();
		for (Object oProjectFacet : projectFacets) {
			if (oProjectFacet instanceof IProjectFacetVersion) {
				IProjectFacetVersion v = (IProjectFacetVersion) oProjectFacet;
				String id = v.getProjectFacet().getId();
				if ("java".equalsIgnoreCase(id)) {
					return v.getVersionString();
				}
			}
		}
		return null;
	}
}