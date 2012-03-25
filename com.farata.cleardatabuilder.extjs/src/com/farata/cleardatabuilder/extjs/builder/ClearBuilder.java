package com.farata.cleardatabuilder.extjs.builder;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class ClearBuilder extends IncrementalProjectBuilder {

	@SuppressWarnings("unchecked")
	@Override
	protected IProject[] build(final int kind, final Map args,
			final IProgressMonitor monitor) throws CoreException {
		
//		try {
//			if (!CDBMigration.checkProjectVersion(getProject(), monitor, true)) {
//				return new IProject[0];
//			}
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		IResourceDelta delta = getDelta(getProject());
		if (delta != null) {
			ClearResourceDeltaVisitor visitor = new ClearResourceDeltaVisitor(
					getProject());
			delta.accept(visitor);
			if (visitor.serviceFound) {
				IFile flag = getProject().getFile("cdb_build/.flag");
				if (flag.exists()) {
					flag.touch(monitor);
				} else {
					File file = flag.getLocation().toFile();
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				flag = getProject().getFile("cdb_build/.flag");
				flag.refreshLocal(IResource.DEPTH_INFINITE, monitor);
				//getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
			}
		}
		return new IProject[0];
	}
}