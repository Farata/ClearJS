package com.farata.cleardatabuilder.extjs.facet.common;

import java.sql.Connection;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.sqltools.core.DatabaseIdentifier;
import org.eclipse.datatools.sqltools.core.profile.ProfileUtil;
import org.eclipse.jpt.jpa.core.JpaDataSource;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.JptJpaCorePlugin;
import org.eclipse.jpt.jpa.db.ConnectionProfile;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action.Type;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetActionEvent;
import org.eclipse.wst.common.project.facet.ui.IWizardContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.resolver.DialectFactory;

import com.farata.cleardatabuilder.extjs.facet.sample.SampleInstallConfig;
import com.farata.cleardatabuilder.extjs.util.HibernateDialectResolver;

public class CommonInstallDelegate implements IDelegate {

	private static final String PARAM_DS_DRIVER_CLASS_NAME = "dataSourceDriverClassName";
	private static final String PARAM_DS_NAME = "dataSourceName";
	private static final String PARAM_DS_URL = "dataSourceUrl";
	private static final String PARAM_DS_USER = "dataSourceUserName";
	private static final String PARAM_DS_PASSWORD = "dataSourcePassword";
	private CommonInstallConfig config;
	private SampleInstallConfig sampleInstallConfig;

	@Override
	public void execute(final IProject project,
			IProjectFacetVersion projectFacetVersion, Object context,
			final IProgressMonitor monitor) throws CoreException {
		extractConfigurations(project, projectFacetVersion,
				(CommonInstallConfig) context, monitor);

		final IFacetedProjectListener listener = new IFacetedProjectListener() {

			@Override
			public void handleEvent(IFacetedProjectEvent event) {
				IProjectFacetActionEvent evt = (IProjectFacetActionEvent) event;
				if ("com.farata.cleardatabuilder.extjs.facet.common".equals(evt
						.getProjectFacet().getId())) {
					try {
						FacetedProjectFramework.removeListener(this);
						final String prjName = project.getName();
						Job job = new Job("Installing " + prjName) {

							@Override
							protected IStatus run(
									IProgressMonitor iprogressmonitor) {
								final Properties props = new Properties();
								props.setProperty("extjs.path", config
										.getExtJSPath().getAbsolutePath());
								if (sampleInstallConfig != null) {
									if (sampleInstallConfig.isHibernateSample()) {
										props.setProperty(
												"is.hibernate.sample", "true");
									} else if (sampleInstallConfig.isPlainJavaSample()) {
										props.setProperty(
												"is.plain.java.sample", "true");
									} else if (sampleInstallConfig.isMybatisSample()) {
										props.setProperty(
												"is.mybatis.sample", "true");
									}
								}
								if (config != null) {
									if (config.isAddSpringSupport()) {
										props.setProperty(
												"add.spring.support", "true");
									} else {
										props.setProperty(
												"dont.add.spring.support", "true");
									}
								}


								fillHibernateProps(props, project, monitor);

								Installer.install(props, prjName,
										sampleInstallConfig == null ? true
												: false, null);
								return Status.OK_STATUS;
							}
						};
						job.schedule(5);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		IFacetedProjectEvent.Type types = IFacetedProjectEvent.Type.POST_INSTALL;
		FacetedProjectFramework.addListener(listener, types);
	}

	private void fillHibernateProps(Properties props, IProject project,
			IProgressMonitor monitor) {
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
			Dialect dialect = null;
			if (profile != null) {
				props.setProperty(PARAM_DS_DRIVER_CLASS_NAME,
						profile.getDriverClassName());
				props.setProperty(PARAM_DS_NAME, profile.getDatabaseName());
				props.setProperty(
						PARAM_DS_PASSWORD,
						profile.getUserPassword() == null ? "" : profile
								.getUserPassword());
				props.setProperty(PARAM_DS_URL, profile.getURL());
				props.setProperty(
						PARAM_DS_USER,
						profile.getUserName() == null ? "" : profile
								.getUserName());
				databaseIdentifier = new DatabaseIdentifier(profile.getName(),
						profile.getDatabaseName());
				try {
					Connection conn = ProfileUtil
							.getOrCreateReusableConnection(databaseIdentifier);
					dialect = DialectFactory.buildDialect(properties, conn);
					ProfileUtil.closeConnection(profile.getName(),
							profile.getDatabaseName(), conn);
				} catch (Throwable e) {
					try {
						IConnectionProfile iprofile = ProfileUtil
								.getProfile(profile.getName());
						Properties dbProps = iprofile
								.getProperties(ProfileUtil.PROFILE_DB_VERSION_TYPE);
						String dbVersion = dbProps
								.getProperty(ProfileUtil.PROFILE_DB_VERSION);
						String dbType = dbProps
								.getProperty(ProfileUtil.PROFILE_DB_VENDOR_NAME);
						dialect = HibernateDialectResolver.resolveDialect(
								dbType, dbVersion);
					} catch (Throwable e1) {
					}
				}
			}

			if (dialect != null) {
				props.setProperty("hibernate.dialect", dialect.toString());
				String dsName = "java:/comp/env/jdbc/"
						+ profile.getDatabaseName();
				props.setProperty("jta.data.source", dsName);
			} else {
				props.setProperty("hibernate.dialect",
						"org.hibernate.dialect.HSQLDialect");
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
		props.setProperty(PARAM_DS_URL,
				"jdbc:hsqldb:hsql://localhost:9002/cleardb");
		props.setProperty(PARAM_DS_USER, "sa");
	}

	private void extractConfigurations(IProject project,
			IProjectFacetVersion projectFacetVersion,
			CommonInstallConfig config, IProgressMonitor progressMonitor)
			throws CoreException {
		this.config = config;
		sampleInstallConfig = null;
		IWizardContext wizardContext = config.getWizardContext();
		Set<?> projectFacets = wizardContext.getSelectedProjectFacets();
		for (Object oProjectFacet : projectFacets) {
			if (oProjectFacet instanceof IProjectFacetVersion) {
				Object conf = wizardContext.getConfig(
						(IProjectFacetVersion) oProjectFacet, Type.INSTALL, "");
				if (conf instanceof SampleInstallConfig) {
					sampleInstallConfig = (SampleInstallConfig) conf;
				}
			}
		}
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