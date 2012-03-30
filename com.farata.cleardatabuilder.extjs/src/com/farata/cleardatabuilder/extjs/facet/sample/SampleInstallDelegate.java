package com.farata.cleardatabuilder.extjs.facet.sample;

import java.io.File;
import java.util.Properties;
import java.util.Set;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action.Type;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jpt.jpa.core.JptJpaCorePlugin;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.ui.IWizardContext;

import com.farata.cleardatabuilder.extjs.Activator;
import com.farata.cleardatabuilder.extjs.facet.common.CommonInstallConfig;
import com.farata.cleardatabuilder.extjs.util.Commons;
import com.farata.cleardatabuilder.extjs.util.Util;

public class SampleInstallDelegate implements IDelegate{

	private SampleInstallConfig config;
	private CommonInstallConfig commonInstallConfig;

	@Override
	public void execute(IProject project, IProjectFacetVersion projectFacetVersion, Object context,
			IProgressMonitor monitor) throws CoreException {
		extractConfigurations(project, projectFacetVersion,
				(SampleInstallConfig) context, monitor);
		
		try {
			if (config.isHibernateSample() || config.isMybatisSample()) {
				unpackSampleDB(config.getSampleDBInstallFolder(), monitor);
				createDBConnection(project, config.getSampleDBInstallFolder());
			}
			JptJpaCorePlugin.setJpaPlatformId(project, "generic");
			JptJpaCorePlugin.setDiscoverAnnotatedClasses(project, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void extractConfigurations(IProject project,
			IProjectFacetVersion projectFacetVersion,
			SampleInstallConfig config, IProgressMonitor progressMonitor)
			throws CoreException {
		this.config = config;
		IWizardContext wizardContext = config.getWizardContext();
		Set<?> projectFacets = wizardContext.getSelectedProjectFacets();
		for (Object oProjectFacet : projectFacets) {
			if (oProjectFacet instanceof IProjectFacetVersion) {
				Object conf = wizardContext.getConfig((IProjectFacetVersion) oProjectFacet, Type.INSTALL, "");
				if (conf instanceof CommonInstallConfig) {
					commonInstallConfig = (CommonInstallConfig) conf;
					break;
				} 
			}
		}
	}
	
	private static void unpackSampleDB(File installationFolder,
			IProgressMonitor monitor) {
		File clearDBZip;
		try {
			clearDBZip = Commons.getBundleEntry(Activator.getDefault()
					.getBundle(), "resources/SampleDB/cleardb.zip");
			Util.unpack(installationFolder.toString(), clearDBZip.toString(),
					Util.OVERRIDE_ALWAYS);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static void createDBConnection(IProject project, File sampleDBInstallFolder)
			throws ConnectionProfileException, CoreException {
		Properties baseProperties = new Properties();
		baseProperties.setProperty(
				"org.eclipse.datatools.connectivity.db.savePWD", "true");
		baseProperties.setProperty(
				"org.eclipse.datatools.connectivity.drivers.defnType",
				"org.eclipse.datatools.enablement.hsqldb.1_8.driver");
		baseProperties.setProperty("jarList", new File(sampleDBInstallFolder,
				"hsqldb.jar").getAbsolutePath());
		baseProperties.setProperty(
				"org.eclipse.datatools.connectivity.db.username", "sa");
		baseProperties.setProperty(
				"org.eclipse.datatools.connectivity.db.driverClass",
				"org.hsqldb.jdbcDriver");
		baseProperties.setProperty(
				"org.eclipse.datatools.connectivity.db.databaseName",
				"cleardb");
		baseProperties
				.setProperty(
						"org.eclipse.datatools.connectivity.driverDefinitionID",
						"DriverDefn.org.eclipse.datatools.enablement.hsqldb.1_8.driver.HSQLDB JDBC Driver");
		baseProperties.setProperty(
				"org.eclipse.datatools.connectivity.db.version", "1.8");
		baseProperties.setProperty("org.eclipse.datatools.connectivity.db.URL",
				"jdbc:hsqldb:hsql://localhost:9002/cleardb");
		baseProperties.setProperty(
				"org.eclipse.datatools.connectivity.db.vendor", "HSQLDB");

		ProfileManager profileManager = ProfileManager.getInstance();
		IConnectionProfile profile = profileManager
				.getProfileByName("CDB-Sample");
		if (profile != null) {
			//profileManager.deleteProfile(profile);
		}
		try {
			if (profile == null) {
				profileManager.createProfile("CDB-Sample", "",
						"org.eclipse.datatools.enablement.hsqldb.connectionProfile",
						baseProperties, null, false);
		
			}
		}catch(Throwable th) {}
		JptJpaCorePlugin.setConnectionProfileName(project, "CDB-Sample");

//		JpaProject jpaProject = CommonInstallDelegate.waitForJpaProject(project);
//		JpaDataSource ds = jpaProject.getDataSource();
//		ds.setConnectionProfileName("CDB-Sample");
	}

}