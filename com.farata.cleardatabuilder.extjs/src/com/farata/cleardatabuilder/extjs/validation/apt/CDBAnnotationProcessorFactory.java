package com.farata.cleardatabuilder.extjs.validation.apt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import clear.cdb.extjs.annotations.JSFillChildrenMethod;
import clear.cdb.extjs.annotations.JSFillMethod;
import clear.cdb.extjs.annotations.JSGenerateStore;
import clear.cdb.extjs.annotations.JSGenerateSample;
import clear.cdb.extjs.annotations.JSGetMethod;
import clear.cdb.extjs.annotations.JSJPQLMethod;
import clear.cdb.extjs.annotations.JSService;
import clear.cdb.extjs.annotations.JSTransferInfo;
import clear.cdb.extjs.annotations.JSUpdateInfo;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

public class CDBAnnotationProcessorFactory implements
		AnnotationProcessorFactory {

	public Collection<String> supportedOptions() {
		return Collections.emptyList();
	}

	public Collection<String> supportedAnnotationTypes() {
		return annotations;
	}

	public AnnotationProcessor getProcessorFor(
			Set<AnnotationTypeDeclaration> atds,
			AnnotationProcessorEnvironment env) {
		return new CDBAnnotationProcessor(env);
	}

	private static ArrayList<String> annotations = new ArrayList<String>();

	{
		annotations.add(JSFillChildrenMethod.class.getName());
		annotations.add(JSFillMethod.class.getName());
		annotations.add(JSGenerateStore.class.getName());
		annotations.add(JSGenerateSample.class.getName());
		annotations.add(JSGetMethod.class.getName());
		annotations.add(JSJPQLMethod.class.getName());
		annotations.add(JSService.class.getName());
		annotations.add(JSTransferInfo.class.getName());
		annotations.add(JSUpdateInfo.class.getName());
	}
}