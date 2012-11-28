/**
 * Copyright (c) 2009 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
package com.farata.cleardatabuilder.extjs.migration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

import com.farata.cleardatabuilder.extjs.Activator;
import com.farata.cleardatabuilder.extjs.util.Commons;

public final class Resources {
	private Resources() {
	}

	public static File clearProjectTemplateFolder;
	public static File clearExampleTemplateFolder;
	public static File databaseDriversFolder;
	public static File jotmWar;
	public static File daoflexWar;
	public static File serverArtifactsFolder;
	public static File clearBuildFilesFolder;
	public static File blazeDsFolder;
	public static File clearTemplateFolder;
	public static File clearJavaDTOTemplateFolder;

	public static File getEntry(final IPath relativePath) throws Exception {
		return Commons.getBundleEntry(Activator.getDefault().getBundle(),
				relativePath.toString());
	}

	public static Version getPluginVersion() {
		return Version.parseVersion(Activator.getDefault().getBundle()
				.getHeaders().get(Constants.BUNDLE_VERSION).toString());
	}

	public static File getDaoflexBuildFile() {
		return new File(clearBuildFilesFolder, "daoflex-build.xml");
	}

	public static File getServerDataSourceContextTemplate(
			final String serverType) {
		return new File(new File(clearBuildFilesFolder, serverType), "jdbc.xsl");
	}

	public static File getServerArtifactsFolder(final String serverType) {
		return new File(serverArtifactsFolder, serverType);
	}

	public static List<File> getDatabaseJdbcJars(final String driverId) {
		final File[] files = new File(databaseDriversFolder, driverId)
				.listFiles(new FilenameFilter() {
					public boolean accept(final File dir, final String name) {
						return name.endsWith(".jar");
					}
				});
		if (files != null) {
			return Arrays.asList(files);
		} else {
			return Collections.emptyList();
		}
	}

	public static List<File> getDatabaseDriverFiles() throws Exception {
			return Commons.getBundleEntries(Activator.getDefault().getBundle(),
					"resources/DatabaseDrivers", "*.properties", true);
	}

	public static File getDatabaseDriverFile(final String driverId) throws Exception {
		final List<File> foundEntries = Commons.getBundleEntries(Activator
					.getDefault().getBundle(), "resources/DatabaseDrivers",
					driverId + ".properties", true);
			
			return foundEntries.get(0);
		
	}

	static {
		try {
			initializeResources(Activator
					.getDefault().getBundle(), "resources.properties",
					Resources.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public static void initializeResources(final Bundle bundle,
            final String resourcesProperties, final Class<?> resourcesClass) throws Exception {
        final Field[] fields = resourcesClass.getDeclaredFields();
        final InputStream propertiesStream = resourcesClass
                .getResourceAsStream(resourcesProperties);
        final Properties properties = new Properties();
        properties.load(propertiesStream);
        String resourcePath = null;
        Field field = null;
            for (int i = 0; i < fields.length; i++) {
                field = fields[i];
                if (!Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                resourcePath = properties.getProperty(field.getName());
                File bundleEntry;

                bundleEntry = Commons.getBundleEntry(bundle, resourcePath);

                field.set(null, bundleEntry);
            }
    }

}
