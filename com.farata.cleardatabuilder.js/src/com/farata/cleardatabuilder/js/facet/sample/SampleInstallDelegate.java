package com.farata.cleardatabuilder.js.facet.sample;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

public class SampleInstallDelegate implements IDelegate{

	@Override
	public void execute(IProject project, IProjectFacetVersion projectFacetVersion, Object context,
			IProgressMonitor monitor) throws CoreException {
	}
}