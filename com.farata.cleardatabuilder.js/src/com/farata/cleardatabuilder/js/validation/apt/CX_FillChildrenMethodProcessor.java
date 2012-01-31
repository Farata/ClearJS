package com.farata.cleardatabuilder.js.validation.apt;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.apt.core.util.EclipseMessager;

import clear.cdb.annotations.CX_FillChildrenMethod;
import clear.cdb.annotations.CX_JPQLMethod;

import com.farata.cleardatabuilder.js.validation.IValidationConstants;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.type.TypeMirror;
import com.sun.mirror.util.SourcePosition;

public class CX_FillChildrenMethodProcessor implements AnnotationProcessor,
		IValidationConstants {

	private AnnotationProcessorEnvironment _env;

	CX_FillChildrenMethodProcessor(AnnotationProcessorEnvironment _env) {
		this._env = _env;
	}

	@Override
	public void process() {
		EclipseMessager messager = (EclipseMessager) _env.getMessager();

		// obtain the declaration of the annotation we want to process
		AnnotationTypeDeclaration annoDecl = (AnnotationTypeDeclaration) _env
				.getTypeDeclaration(CX_FillChildrenMethod.class.getName());

		// get the annotated types
		Collection<Declaration> annotatedTypes = _env
				.getDeclarationsAnnotatedWith(annoDecl);

		for (Declaration decl : annotatedTypes) {
			Collection<AnnotationMirror> mirrors = decl.getAnnotationMirrors();

			// for each annotation found, get a map of element name/value pairs
			for (AnnotationMirror mirror : mirrors) {
				if (!"CX_FillChildrenMethod".equals(
						mirror.getAnnotationType().getDeclaration()
								.getSimpleName())) {
					continue;
				}
				MethodDeclaration methodDeclaration = (MethodDeclaration) decl;
				CX_JPQLMethodProcessor.checkReturnType(mirror, methodDeclaration, messager);
				CX_JPQLMethodProcessor.checkTransferInfo(mirror, methodDeclaration, true, messager);
				CX_JPQLMethodProcessor.checkIfAnnotationValueAttributeIsEntity(mirror, "parent", messager);
			}
		}
	}
}
