package com.farata.cleardatabuilder.extjs.validation.apt;

import java.util.Collection;

import org.eclipse.jdt.apt.core.util.EclipseMessager;

import clear.cdb.extjs.annotations.CX_JSGetMethod;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.MethodDeclaration;

public class CX_JSGetMethodProcessor implements AnnotationProcessor {

	private AnnotationProcessorEnvironment _env;

	CX_JSGetMethodProcessor(AnnotationProcessorEnvironment _env) {
		this._env = _env;
	}

	@Override
	public void process() {
		EclipseMessager messager = (EclipseMessager) _env.getMessager();

		// obtain the declaration of the annotation we want to process
		AnnotationTypeDeclaration annoDecl = (AnnotationTypeDeclaration) _env
				.getTypeDeclaration(CX_JSGetMethod.class.getName());

		// get the annotated types
		Collection<Declaration> annotatedTypes = _env
				.getDeclarationsAnnotatedWith(annoDecl);

		for (Declaration decl : annotatedTypes) {
			Collection<AnnotationMirror> mirrors = decl.getAnnotationMirrors();

			// for each annotation found, get a map of element name/value pairs
			for (AnnotationMirror mirror : mirrors) {
				if (!"CX_JSGetMethod".equals(mirror.getAnnotationType()
						.getDeclaration().getSimpleName())) {
					continue;
				}
				MethodDeclaration methodDeclaration = (MethodDeclaration) decl;
				CX_JSJPQLMethodProcessor.checkTransferInfo(mirror,
						methodDeclaration, false, messager);
			}
		}
	}
}
