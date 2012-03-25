package com.farata.cleardatabuilder.extjs.jpa.platform;

import org.eclipse.core.resources.IFile;
import org.eclipse.jpt.jpa.core.JpaAnnotationProvider;
import org.eclipse.jpt.jpa.core.JpaFactory;
import org.eclipse.jpt.jpa.core.JpaFile;
import org.eclipse.jpt.jpa.core.JpaPlatformProvider;
import org.eclipse.jpt.jpa.core.JpaPlatformVariation;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.internal.GenericJpaPlatform;

@SuppressWarnings("restriction")
public class CDBGenericJpaPlatform extends GenericJpaPlatform {

	public CDBGenericJpaPlatform(String id, Version jpaVersion,
			JpaFactory jpaFactory, JpaAnnotationProvider jpaAnnotationProvider,
			JpaPlatformProvider platformProvider,
			JpaPlatformVariation jpaVariation) {
		super(id, jpaVersion, jpaFactory, jpaAnnotationProvider,
				platformProvider, jpaVariation);

	}

	public JpaFile buildJpaFile(JpaProject jpaProject, IFile file) {
		if ("jar".equalsIgnoreCase(file.getFileExtension())) {
			return null;
		}
		return super.buildJpaFile(jpaProject, file);
	}
}
