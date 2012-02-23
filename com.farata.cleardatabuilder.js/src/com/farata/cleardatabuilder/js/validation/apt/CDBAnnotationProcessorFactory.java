package com.farata.cleardatabuilder.js.validation.apt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import clear.cdb.js.annotations.CX_JSFillChildrenMethod;
import clear.cdb.js.annotations.CX_JSFillMethod;
import clear.cdb.js.annotations.CX_JSGenerateStore;
import clear.cdb.js.annotations.CX_JSGenerateSample;
import clear.cdb.js.annotations.CX_JSGetMethod;
import clear.cdb.js.annotations.CX_JSJPQLMethod;
import clear.cdb.js.annotations.CX_JSService;
import clear.cdb.js.annotations.CX_TransferInfo;
import clear.cdb.js.annotations.CX_UpdateInfo;

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
		annotations.add(CX_JSFillChildrenMethod.class.getName());
		annotations.add(CX_JSFillMethod.class.getName());
		annotations.add(CX_JSGenerateStore.class.getName());
		annotations.add(CX_JSGenerateSample.class.getName());
		annotations.add(CX_JSGetMethod.class.getName());
		annotations.add(CX_JSJPQLMethod.class.getName());
		annotations.add(CX_JSService.class.getName());
		annotations.add(CX_TransferInfo.class.getName());
		annotations.add(CX_UpdateInfo.class.getName());
	}
}