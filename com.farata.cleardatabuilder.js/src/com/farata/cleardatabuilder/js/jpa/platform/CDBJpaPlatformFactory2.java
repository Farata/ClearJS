package com.farata.cleardatabuilder.js.jpa.platform;

import org.eclipse.jpt.jpa.core.JpaAnnotationDefinitionProvider;
import org.eclipse.jpt.jpa.core.JpaFacet;
import org.eclipse.jpt.jpa.core.JpaPlatform;
import org.eclipse.jpt.jpa.core.internal.GenericJpaAnnotationProvider;
import org.eclipse.jpt.jpa.core.internal.jpa2.Generic2_0JpaAnnotationDefinitionProvider;
import org.eclipse.jpt.jpa.core.internal.jpa2.Generic2_0JpaPlatformFactory;
import org.eclipse.jpt.jpa.core.internal.jpa2.Generic2_0JpaPlatformProvider;
import org.eclipse.jpt.jpa.core.internal.jpa2.GenericJpaFactory2_0;

@SuppressWarnings("restriction")
public class CDBJpaPlatformFactory2 extends Generic2_0JpaPlatformFactory {

	public JpaPlatform buildJpaPlatform(String id) {
		return new CDBGenericJpaPlatform(
				id,
				buildJpaVersion(),
				new GenericJpaFactory2_0(),
				new GenericJpaAnnotationProvider(
						new JpaAnnotationDefinitionProvider[] { Generic2_0JpaAnnotationDefinitionProvider
								.instance() }),
				Generic2_0JpaPlatformProvider.instance(),
				buildJpaPlatformVariation());
	}

	private org.eclipse.jpt.jpa.core.JpaPlatform.Version buildJpaVersion() {
		return new org.eclipse.jpt.jpa.core.internal.GenericJpaPlatformFactory.SimpleVersion(
				JpaFacet.VERSION_2_0.getVersionString());
	}
}