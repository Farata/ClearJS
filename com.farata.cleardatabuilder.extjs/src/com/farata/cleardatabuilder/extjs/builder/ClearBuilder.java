package com.farata.cleardatabuilder.extjs.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaProject;

import com.farata.cleardatabuilder.extjs.migration.CDBMigration;

public class ClearBuilder extends IncrementalProjectBuilder {

	@SuppressWarnings("unchecked")
	@Override
	protected IProject[] build(final int kind, final Map args, final IProgressMonitor monitor) throws CoreException {

		createBuildProperties(monitor);

		try {
			if (!CDBMigration.checkProjectVersion(getProject(), monitor, true)) {
				return new IProject[0];
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		IResourceDelta delta = getDelta(getProject());
		if (delta != null) {
			ClearResourceDeltaVisitor visitor = new ClearResourceDeltaVisitor(getProject());
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
				// getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
			}
		}
		return new IProject[0];
	}

	private void createBuildProperties(final IProgressMonitor monitor) {
		IProject project = getProject();
		List<IJavaElement> folders = getSourceFolders();
		Properties props = new Properties();
		try {

			if (folders.size() > 0) {
				IFile ifile = project.getFile("cdb_build/build.properties");
				if (!ifile.exists()) {
					return;
				}
				props.load(new FileReader(new File(ifile.getLocation().toOSString())));
				String javaSrcProp = props.getProperty("java-src");
				String javaSrcPropNew = "";
				String javaSrcGeneratedPropNew = "";
				for (IJavaElement e : folders) {
					IPath path = e.getPath();
					path = path.removeFirstSegments(1).makeRelative();
					String s = "${project-root}" + path.toString();
					javaSrcPropNew = javaSrcPropNew + s + ':';
					if (javaSrcGeneratedPropNew.length() == 0) {
						javaSrcGeneratedPropNew = s;
					}
				}
				if (javaSrcPropNew.length() > 0) {
					javaSrcPropNew = javaSrcPropNew.substring(0, javaSrcPropNew.length() - 1);
					if (!javaSrcPropNew.equals(javaSrcProp)) {
						props.setProperty("java-src", javaSrcPropNew);
						props.setProperty("java-src-generated", javaSrcGeneratedPropNew);
						props.store(new FileWriter(ifile.getLocation().toOSString()), null);
						ifile.refreshLocal(IResource.DEPTH_INFINITE, monitor);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<IJavaElement> getSourceFolders() {
		IProject project = getProject();
		if (project == null)
			return null;
		List<IJavaElement> ret = new ArrayList<IJavaElement>();
		IJavaProject javaProject = JavaCore.create(project);
		try {
			IPackageFragmentRoot[] packageFragmentRoot = javaProject.getAllPackageFragmentRoots();
			for (int i = 0; i < packageFragmentRoot.length; i++) {
				if (packageFragmentRoot[i].getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT && !packageFragmentRoot[i].isArchive())
					ret.add(packageFragmentRoot[i]);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
		return ret;
	}
}