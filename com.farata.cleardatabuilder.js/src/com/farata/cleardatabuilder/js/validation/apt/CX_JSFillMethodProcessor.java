package com.farata.cleardatabuilder.js.validation.apt;

import java.util.Collection;

import org.eclipse.jdt.apt.core.util.EclipseMessager;

import clear.cdb.js.annotations.CX_JSFillChildrenMethod;
import clear.cdb.js.annotations.CX_JSFillMethod;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.MethodDeclaration;

public class CX_JSFillMethodProcessor implements AnnotationProcessor {

	private AnnotationProcessorEnvironment _env;

	CX_JSFillMethodProcessor(AnnotationProcessorEnvironment _env) {
		this._env = _env;
	}

	@Override
	public void process() {
		EclipseMessager messager = (EclipseMessager) _env.getMessager();

		// obtain the declaration of the annotation we want to process
		AnnotationTypeDeclaration annoDecl = (AnnotationTypeDeclaration) _env
				.getTypeDeclaration(CX_JSFillMethod.class.getName());

		// get the annotated types
		Collection<Declaration> annotatedTypes = _env
				.getDeclarationsAnnotatedWith(annoDecl);

		for (Declaration decl : annotatedTypes) {
			Collection<AnnotationMirror> mirrors = decl.getAnnotationMirrors();

			// for each annotation found, get a map of element name/value pairs
			for (AnnotationMirror mirror : mirrors) {
				if (!"CX_JSFillMethod".equals(mirror.getAnnotationType()
						.getDeclaration().getSimpleName())) {
					continue;
				}
				MethodDeclaration methodDeclaration = (MethodDeclaration) decl;
				CX_JSJPQLMethodProcessor.checkReturnType(mirror,
						methodDeclaration, messager);
			}
		}
	}
}