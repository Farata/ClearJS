package com.farata.cleardatabuilder.extjs.wizard;

import java.util.Collection;
import java.util.Properties;

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
import org.eclipse.jst.servlet.ui.project.facet.WebProjectWizard;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationPropertiesNew;
import org.eclipse.datatools.sqltools.core.DatabaseIdentifier;
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
		final String prjName = getProjectName();
		Job job = new Job("Installing " + prjName) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				final Properties props = new Properties();
				// try {
				// String v = getProjectFacetVersion()
				// .getVersionString();
				// props.setProperty("project.java.version", v);
				// } catch (Throwable e) {
				// props.setProperty("project.java.version", "1.6");
				// }

				props.setProperty("extjs.path", model.getStringProperty(CDB_EXTJS_FOLDER));
				props.setProperty("app.name", prjName);

				String type = model.getStringProperty(CDB_PROJECT_TYPE);
				boolean isNew = "new".equals(type);
				boolean isHibernateExample = "hibernateExample".equals(type);
				boolean isJavaExample = "javaExample".equals(type);
				boolean isMyBatisExample = "myBatisExample".equals(type);

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

				Collection pp = model.getAllProperties();
				
				IProject project = (IProject) model.getProperty(IProjectCreationPropertiesNew.PROJECT);
				
				fillHibernateProps(props, project, monitor);

				//Installer.install(props, prjName, sampleInstallConfig == null ? true : false, null);
				return Status.OK_STATUS;
			}
		};
		job.schedule(5);
	}
	
	private void fillHibernateProps(Properties props, IProject project, IProgressMonitor monitor) {
		try {
			props.setProperty("persistence.unit", project.getName());
			setDefaultProps(props);

			Properties properties = new Properties();
			JpaProject jpaProject = waitForJpaProject(project, 1000);
			if (jpaProject == null) {
				return;
			}
			JpaDataSource ds = jpaProject.getDataSource();
			ConnectionProfile profile = ds.getConnectionProfile();
			DatabaseIdentifier databaseIdentifier = null;
			//Dialect dialect = null;
			if (profile != null) {
				props.setProperty(PARAM_DS_DRIVER_CLASS_NAME, profile.getDriverClassName());
				props.setProperty(PARAM_DS_NAME, profile.getDatabaseName());
				props.setProperty(PARAM_DS_PASSWORD, profile.getUserPassword() == null ? "" : profile.getUserPassword());
				props.setProperty(PARAM_DS_URL, profile.getURL());
				props.setProperty(PARAM_DS_USER, profile.getUserName() == null ? "" : profile.getUserName());
				databaseIdentifier = new DatabaseIdentifier(profile.getName(), profile.getDatabaseName());
//				try {
//					Connection conn = ProfileUtil.getOrCreateReusableConnection(databaseIdentifier);
//					dialect = DialectFactory.buildDialect(properties, conn);
//					ProfileUtil.closeConnection(profile.getName(), profile.getDatabaseName(), conn);
//				} catch (Throwable e) {
//					try {
//						IConnectionProfile iprofile = ProfileUtil.getProfile(profile.getName());
//						Properties dbProps = iprofile.getProperties(ProfileUtil.PROFILE_DB_VERSION_TYPE);
//						String dbVersion = dbProps.getProperty(ProfileUtil.PROFILE_DB_VERSION);
//						String dbType = dbProps.getProperty(ProfileUtil.PROFILE_DB_VENDOR_NAME);
//						dialect = HibernateDialectResolver.resolveDialect(dbType, dbVersion);
//					} catch (Throwable e1) {
//					}
//				}
			}

//			if (dialect != null) {
//				props.setProperty("hibernate.dialect", dialect.toString());
//				String dsName = "java:/comp/env/jdbc/" + profile.getDatabaseName();
//				props.setProperty("jta.data.source", dsName);
//			} else {
//				props.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
//				String dsName = "java:/comp/env/jdbc/cleardb";
//				props.setProperty("jta.data.source", dsName);
//			}

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
	
	public static JpaProject waitForJpaProject(IProject project, long timeout) {
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		JpaProject jpaProject = JptJpaCorePlugin.getJpaProject(project);
		long time = 0;
		while (jpaProject == null) {
			if (time > timeout) {
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			time += 100;
			// JptCorePlugin.rebuildJpaProject(project);
			jpaProject = JptJpaCorePlugin.getJpaProject(project);
		}
		return jpaProject;
	}
}