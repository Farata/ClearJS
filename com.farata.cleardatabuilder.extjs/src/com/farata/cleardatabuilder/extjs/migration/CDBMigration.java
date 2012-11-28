package com.farata.cleardatabuilder.extjs.migration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Version;

public class CDBMigration {

	/**
	 * If necessary shows modal dialog.
	 * 
	 * @throws Exception
	 */
	public static boolean checkProjectVersion(final IProject project,
			final IProgressMonitor monitor, final boolean isIncrementalBuild)
			throws Exception {
		final CDBConfigurator conf = new CDBConfigurator(project);
		final Version projectCdbVersion = conf.getProjectCdbVersion(monitor);
		final Version pluginVersion = Resources.getPluginVersion();

		if (pluginVersion.equals(projectCdbVersion)) {
			if (conf.isIncrementalBuildSkipped(monitor))
				conf.setIncrementalBuildSkipped(false, monitor);
			return true; // EARLY EXIT: migration not needed!
		}

		if (isIncrementalBuild && conf.isIncrementalBuildSkipped(monitor))
			return false; // EARLY EXIT: migration once cancelled!
		// => incremental build skipped;

		if (showNeedsMigrationDialog(project.getName(),
				pluginVersion.toString())) {
//TODO
//			final MigrationResult migrationResult = conf.migrate(monitor,
//					projectCdbVersion);
//			renameXSLFiles(project, migrationResult, monitor);
//TODO
//			conf.setProjectCdbVersion(pluginVersion, monitor);
			conf.setIncrementalBuildSkipped(false, monitor);
//			showMigrationResults(migrationResult, project.getName(),
//					pluginVersion.toString());
			return true;
		}

		if (isIncrementalBuild)
			conf.setIncrementalBuildSkipped(true, monitor);

		return false;
	}

//	private static void renameXSLFiles(IProject project,
//			MigrationResult migrationResult, IProgressMonitor monitor)
//			throws CoreException {
//		for (String file : migrationResult.backedFiles) {
//			File f = project.getFile(migrationResult.backupFolder + "/" + file)
//					.getLocation().toFile();
//			if (file.endsWith(".xsl")) {
//				File toFile = new File(f.getParentFile(), f.getName() + ".bak");
//				f.renameTo(toFile);
//			}
//		}
//		project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
//	}

	private static boolean showNeedsMigrationDialog(final String projectName,
			final String projectVersion) {
		final boolean[] result = new boolean[] { false };

		final String msg = errorMessage(projectName, projectVersion);
		Display.getDefault().syncExec(new Runnable() {

			public void run() {

				result[0] = MessageDialog.openConfirm(Display.getDefault()
						.getActiveShell(), errorTitle(projectName), msg);
			}
		});
		return result[0];
	}

//	private static void showMigrationResults(
//			final MigrationResult migrationResult, final String projectName,
//			final String projectVersion) {
//
//		Display.getDefault().asyncExec(new Runnable() {
//
//			public void run() {
//				MessageDialog.openInformation(
//						Display.getDefault().getActiveShell(),
//						resultsTitle(projectName),
//						resultsMessage(projectName, projectVersion,
//								migrationResult));
//			}
//		});
//	}

	private static String errorTitle(final String projectName) {
		return Messages.getString("CDB.Migration.NeedMigrationError.Title",
				new Object[] { projectName });
	}

	private static String errorMessage(final String projectName,
			final String projectVersion) {
		return Messages.getString("CDB.Migration.NeedMigrationError.Message",
				new Object[] { projectName, projectVersion });
	}

	private static String resultsTitle(final String projectName) {
		return Messages.getString(
				"CDB.Migration.MigrationResultsMessage.Title",
				new Object[] { projectName });
	}

//	private static String resultsMessage(final String projectName,
//			final String projectVersion, final MigrationResult migrationResult) {
//		final String mainMessage = Messages.getString(
//				"CDB.Migration.MigrationResultsMessage.MainMessage",
//				new Object[] { projectName, projectVersion });
//
//		final String backupMessage;
//		if (migrationResult.backedFiles.size() > 0) {
//			backupMessage = Messages.getString(
//					"CDB.Migration.MigrationResultsMessage.BackupMessage",
//					new String[] { migrationResult.backupFolder })
//					+ "\n" + formatFileNames(migrationResult.backedFiles);
//		} else {
//			backupMessage = Messages
//					.getString("CDB.Migration.MigrationResultsMessage.NoBackupMessage");
//		}
//
//		return mainMessage + "\n" + backupMessage;
//	}

	private static String formatFileNames(List<String> files) {
		final StringBuilder builder = new StringBuilder();
		int counter = 0;
		final int limit = 10;
		for (final String file : files) {
			if (counter++ == limit) {
				builder.append("  ")
						.append(Messages
								.getString(
										"CDB.Migration.MigrationResultsMessage.Overflow",
										new Object[] { files.size() }));
				break;// EARLY EXIT on limit's overflow
			}
			builder.append("  ").append(file).append("\n");
		}
		return builder.toString();
	}
}
