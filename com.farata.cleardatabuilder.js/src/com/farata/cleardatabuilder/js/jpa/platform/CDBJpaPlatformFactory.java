package com.farata.cleardatabuilder.js.jpa.platform;

import org.eclipse.jpt.jpa.core.JpaAnnotationDefinitionProvider;
import org.eclipse.jpt.jpa.core.JpaFacet;
import org.eclipse.jpt.jpa.core.JpaPlatform;
import org.eclipse.jpt.jpa.core.internal.GenericJpaAnnotationDefinitionProvider;
import org.eclipse.jpt.jpa.core.internal.GenericJpaAnnotationProvider;
import org.eclipse.jpt.jpa.core.internal.GenericJpaPlatform;
import org.eclipse.jpt.jpa.core.internal.GenericJpaPlatformFactory;
import org.eclipse.jpt.jpa.core.internal.GenericJpaPlatformProvider;
import org.eclipse.jpt.jpa.core.internal.jpa1.GenericJpaFactory;

@SuppressWarnings("restriction")
public class CDBJpaPlatformFactory extends GenericJpaPlatformFactory {
	public JpaPlatform buildJpaPlatform(String id) {
		return new GenericJpaPlatform(
				id,
				buildJpaVersion(),
				new GenericJpaFactory(),
				new GenericJpaAnnotationProvider(
						new JpaAnnotationDefinitionProvider[] { GenericJpaAnnotationDefinitionProvider
								.instance() }),
				GenericJpaPlatformProvider.instance(),
				buildJpaPlatformVariation());
	}

	private org.eclipse.jpt.jpa.core.JpaPlatform.Version buildJpaVersion() {
		return new SimpleVersion(JpaFacet.VERSION_1_0.getVersionString());
	}
}
