package com.farata.cleardatabuilder.extjs.builder;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import clear.cdb.extjs.annotations.CX_JSService;

public class ClearResourceDeltaVisitor implements IResourceDeltaVisitor {

	public boolean serviceFound = false;
	private IProject project;
	private ClassLoader contextClassLoader;
	private URLClassLoader urlClazzLoader;

	public ClearResourceDeltaVisitor(IProject project) {
		this.project = project;
	}

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		final IResource resource = delta.getResource();
		if (delta.getKind() == IResourceDelta.ADDED
				|| delta.getKind() == IResourceDelta.CHANGED) {
			if ("class".equals(resource.getFileExtension())) {
				String className = resource.getFullPath().toString();
				String classes = project.getFolder("build/classes")
						.getFullPath().toString();
				className = className.replace(classes + "/", "");
				className = className.replace("/", ".");
				className = className.replace(".class", "");
				try {
					loadClasses();
					if (typeAnnotated(className)) {
						serviceFound = true;
						return false;
					}
				} catch (Throwable e) {
					//e.printStackTrace();
				} finally {
					//unloadClasses();
				}
			}
		}
		return true;
	}

	private boolean typeAnnotated(String className)
			throws ClassNotFoundException {
		Class<?> clazz = urlClazzLoader.loadClass(className);
		Annotation[] ann = clazz.getAnnotations();
		for (Annotation a: ann) {
			if (a.annotationType() != null && "clear.cdb.extjs.annotations.CX_JSService".equals(a.annotationType().getName())) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	private void loadClasses() throws MalformedURLException {
		ArrayList<URL> urls = new ArrayList<URL>();
		String classPath = project.getFolder("build/classes").getLocation()
				.toOSString();
		if (classPath != null) {
			urls.add(new File(classPath).toURL());
		}
		File classPaths = project.getFolder("WebContent/WEB-INF/lib").getLocation()
				.toFile();
		for (File file: classPaths.listFiles()) {
			urls.add(file.toURL());
		}
		urlClazzLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]));
	}
}
