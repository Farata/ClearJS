package com.farata.cleardatabuilder.js.validation.apt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import clear.cdb.annotations.CX_FillChildrenMethod;
import clear.cdb.annotations.CX_FillMethod;
import clear.cdb.annotations.CX_GenerateDataCollection;
import clear.cdb.annotations.CX_GenerateMXMLSample;
import clear.cdb.annotations.CX_GetMethod;
import clear.cdb.annotations.CX_JPQLMethod;
import clear.cdb.annotations.CX_Service;
import clear.cdb.annotations.CX_TransferInfo;
import clear.cdb.annotations.CX_UpdateInfo;

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
		annotations.add(CX_FillChildrenMethod.class.getName());
		annotations.add(CX_FillMethod.class.getName());
		annotations.add(CX_GenerateDataCollection.class.getName());
		annotations.add(CX_GenerateMXMLSample.class.getName());
		annotations.add(CX_GetMethod.class.getName());
		annotations.add(CX_JPQLMethod.class.getName());
		annotations.add(CX_Service.class.getName());
		annotations.add(CX_TransferInfo.class.getName());
		annotations.add(CX_UpdateInfo.class.getName());
	}
}