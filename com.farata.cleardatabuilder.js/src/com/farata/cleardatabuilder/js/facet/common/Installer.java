package com.farata.cleardatabuilder.js.facet.common;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.farata.cleardatabuilder.js.Activator;
import com.farata.cleardatabuilder.js.util.Commons;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class Installer {

	public static boolean install(final String projectName, boolean isNew,
			IProgressMonitor monitor) {
		final AntRunner ant = new AntRunner();
		File buildFile;
		File buildPropsFile;
		try {
			buildFile = Commons.getBundleEntry(Activator.getDefault()
					.getBundle(), "install/build.xml");
			buildPropsFile = Commons.getBundleEntry(Activator.getDefault()
					.getBundle(), "install/build.properties");
			ant.setBuildFileLocation(buildFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}

		
		Properties properties = new Properties();
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		String workspacePath = root.getLocation().toOSString();
		properties.setProperty("workspace.path", workspacePath);
		properties.setProperty("app.name", projectName.trim());
		properties.setProperty("project.name", projectName.trim());
		
		if (isNew) {
			properties.setProperty("is.new", "true");
		} else {
			properties.setProperty("is.sample", "true");
		}

		try {
			properties.store(new FileOutputStream(buildPropsFile), "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		ant.addUserProperties(properties);
//		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
//
//			@Override
//			protected void execute(IProgressMonitor monitor)
//					throws CoreException, InvocationTargetException,
//					InterruptedException {
//
//				ant.run();
//				IProject prj = root.getProject(projectName.trim());
//				//prj.create(null);
//				prj.open(null);
//			}
//		};

		try {
			ant.run();
			IProject prj = root.getProject(projectName.trim());
			//prj.create(null);
			prj.open(null);
			prj.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// runAsyncOperation(op);

		return true;

	}

}
