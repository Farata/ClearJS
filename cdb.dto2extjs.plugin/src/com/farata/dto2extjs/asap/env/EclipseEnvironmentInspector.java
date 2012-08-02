/**
 * Copyright (c) 2009 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
package com.farata.dto2extjs.asap.env;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.apt.core.env.EclipseAnnotationProcessorEnvironment;
import org.eclipse.jdt.apt.core.env.Phase;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;

public class EclipseEnvironmentInspector implements IEnvironmentInspector {
	
	final private static String WORKSPACE_LINK_TOKEN = "${DOCUMENTS}";
	
	public boolean isReconciliation(final AnnotationProcessorEnvironment env) {
		if ( env instanceof EclipseAnnotationProcessorEnvironment) {
			final EclipseAnnotationProcessorEnvironment eenv = (EclipseAnnotationProcessorEnvironment)env;
			final Phase phase = eenv.getPhase();
			return Phase.RECONCILE == phase;
		} else
			return false;
	}
	
	public File resolveOutputFolder(final String outputParameter) {
		File outputFile;
		if (outputParameter.startsWith(WORKSPACE_LINK_TOKEN)) {
			final File root = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
			// ${...}/directory
			final int indexOfCharAfterLinkToken = WORKSPACE_LINK_TOKEN.length() + 1;
			// handle the case if pointed to workspace root
			if (outputParameter.length() > indexOfCharAfterLinkToken) {
				outputFile = new File(root, outputParameter.substring(indexOfCharAfterLinkToken));
			} else {
				outputFile = root;
			}
		} else {
			outputFile = new File(outputParameter);
			if (!outputFile.isAbsolute()) {
				File root = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
				outputFile = new File(root, outputParameter);
			}
		}
		return outputFile;
	}
	
	public void refreshFile(final File generatedFile) {
		try {
			IWorkspace workspace= ResourcesPlugin.getWorkspace();    
			IPath location= Path.fromOSString(generatedFile.getAbsolutePath()); 
			IFile ifile= workspace.getRoot().getFileForLocation(location);
			if (ifile != null)
				ifile.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	final public static IEnvironmentInspector INSTANCE = new EclipseEnvironmentInspector();
}
