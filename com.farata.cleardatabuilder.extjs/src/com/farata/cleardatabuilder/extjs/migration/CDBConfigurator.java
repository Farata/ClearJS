package com.farata.cleardatabuilder.extjs.migration;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.framework.Version;

import com.farata.cleardatabuilder.extjs.util.Commons;


/**
 * Performs configuration of folder "daoflex.conf" within a particular CDB
 * project.
 */
public class CDBConfigurator {

	private final IProject project;
	private final IFolder confFolder;

	public CDBConfigurator(final IProject project) {
		super();
		this.project = project;
		this.confFolder = project.getFolder("cdb_build");
	}

	public IProject getProject() {
		return project;
	}

	public Version getProjectCdbVersion(final IProgressMonitor monitor)
			throws IOException, CoreException {
		final IFile versionFile = confFolder.getFile(".version");
		versionFile.refreshLocal(IContainer.DEPTH_ZERO, monitor);
		if (!versionFile.exists()) {
			return Version.emptyVersion;
		}
		return Version.parseVersion(Commons.readFirstLine(versionFile));
	}
	
	public boolean isIncrementalBuildSkipped(final IProgressMonitor monitor)
			throws CoreException {
		final IFile file = confFolder.getFile(".skipIncrementalBuilder");
		file.refreshLocal(IContainer.DEPTH_ZERO, monitor);
		return file.exists();
	}

	public void setIncrementalBuildSkipped(final boolean skip,
			final IProgressMonitor monitor) throws CoreException {
		final IFile file = confFolder.getFile(".skipIncrementalBuilder");
		file.refreshLocal(IContainer.DEPTH_ZERO, monitor);
		if (skip == isIncrementalBuildSkipped(monitor))
			return;// EARLY EXIT!!!
		if (skip)
			file.create(new ByteArrayInputStream(new byte[] {}), true, monitor);
		else
			file.delete(true, monitor);
	}
}
