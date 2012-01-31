package com.farata.cleardatabuilder.js.validation.apt;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;

import org.eclipse.jdt.apt.core.util.EclipseMessager;

import clear.cdb.annotations.CX_JPQLMethod;

import com.farata.cleardatabuilder.js.validation.IValidationConstants;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.type.TypeMirror;
import com.sun.mirror.util.SourcePosition;

public class CX_JPQLMethodProcessor implements AnnotationProcessor,
		IValidationConstants {

	private static final String CLASS_OF_THE_COLLECTION_ELEMENTS_IS_NOT_SPECIFIED_AND_TRANSFER_INFO_PARAMETER_IS_MISSING = "Class of the collection elements is not specified and transferInfo parameter is missing";
	private static final String TYPE_ATTRIBUTE_SHOULD_BE_NOT_EMPTY = "'type' attribute should be not empty";
	private AnnotationProcessorEnvironment _env;

	CX_JPQLMethodProcessor(AnnotationProcessorEnvironment _env) {
		this._env = _env;
	}

	@Override
	public void process() {
		EclipseMessager messager = (EclipseMessager) _env.getMessager();

		// obtain the declaration of the annotation we want to process
		AnnotationTypeDeclaration annoDecl = (AnnotationTypeDeclaration) _env
				.getTypeDeclaration(CX_JPQLMethod.class.getName());

		// get the annotated types
		Collection<Declaration> annotatedTypes = _env
				.getDeclarationsAnnotatedWith(annoDecl);

		for (Declaration decl : annotatedTypes) {
			Collection<AnnotationMirror> mirrors = decl.getAnnotationMirrors();

			// for each annotation found, get a map of element name/value pairs
			for (AnnotationMirror mirror : mirrors) {
				if (!"CX_JPQLMethod".equals(mirror.getAnnotationType()
						.getDeclaration().getSimpleName())) {
					continue;
				}
				MethodDeclaration methodDeclaration = (MethodDeclaration) decl;
				CX_JPQLMethodProcessor.checkReturnType(mirror,
						methodDeclaration, messager);
				CX_JPQLMethodProcessor.checkTransferInfo(mirror,
						methodDeclaration, true, messager);
				CX_JPQLMethodProcessor.checkUpdateInfo(mirror,
						methodDeclaration, messager);

			}
		}
	}

	public static boolean checkTransferInfo(AnnotationMirror mirror,
			MethodDeclaration methodDeclaration, boolean checkReturnType, EclipseMessager messager) {
		Map<AnnotationTypeElementDeclaration, AnnotationValue> valueMap = mirror
				.getElementValues();
		Set<Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue>> valueSet = valueMap
				.entrySet();

		boolean found = false;
		for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> annoKeyValue : valueSet) {
			if (annoKeyValue.getKey().getSimpleName().equals("transferInfo")) {
				found = true;
				checkTransferInfoType(annoKeyValue.getValue(), messager);
				checkIfAnnotationValueAttributeIsEntity(
						(AnnotationMirror) annoKeyValue.getValue().getValue(),
						"mappedBy", messager);
				break;
			}
		}
		if (!found && checkReturnType) {
			TypeMirror returnType = methodDeclaration.getReturnType();
			String returnTypeName = returnType.toString();
			if (returnTypeName.equals("java.util.List<?>")
					|| returnTypeName.equals("java.util.List")
					|| returnTypeName.equals("java.util.Collection<?>")
					|| returnTypeName.equals("java.util.Collection")) {
				// System.out.println(returnType);
				SourcePosition pos = mirror.getPosition();
				messager
						.printFixableError(
								pos,
								CLASS_OF_THE_COLLECTION_ELEMENTS_IS_NOT_SPECIFIED_AND_TRANSFER_INFO_PARAMETER_IS_MISSING,
								PLUGIN_ID, ERROR_transferInfo_is_missing);
				return false;
			}
		}
		return true;

	}

	private static boolean checkUpdateInfo(AnnotationMirror mirror,
			MethodDeclaration methodDeclaration, EclipseMessager messager) {
		Map<AnnotationTypeElementDeclaration, AnnotationValue> valueMap = mirror
				.getElementValues();
		Set<Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue>> valueSet = valueMap
				.entrySet();

		for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> annoKeyValue : valueSet) {
			if (annoKeyValue.getKey().getSimpleName().equals("updateInfo")) {
				checkIfAnnotationValueAttributeIsEntity(
						(AnnotationMirror) annoKeyValue.getValue().getValue(),
						"updateEntity", messager);
			}
		}
		return true;
	}

	private static boolean checkTransferInfoType(
			AnnotationValue annotationValue, EclipseMessager messager) {
		AnnotationMirror mirror = (AnnotationMirror) annotationValue.getValue();
		Map<AnnotationTypeElementDeclaration, AnnotationValue> valueMap = mirror
				.getElementValues();
		Set<Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue>> valueSet = valueMap
				.entrySet();

		for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> annoKeyValue : valueSet) {
			if (annoKeyValue.getKey().getSimpleName().equals("type")) {
				String annoValue = (String) annoKeyValue.getValue().getValue();
				if (annoValue != null && annoValue.trim().length() == 0) {
					SourcePosition pos = annoKeyValue.getValue().getPosition();
					messager
							.printError(pos, TYPE_ATTRIBUTE_SHOULD_BE_NOT_EMPTY);
				}
				return false;
			}
		}
		return true;
	}

	public static boolean checkIfAnnotationValueAttributeIsEntity(
			AnnotationMirror mirror, String attributeName,
			EclipseMessager messager) {
		Map<AnnotationTypeElementDeclaration, AnnotationValue> valueMap = mirror
				.getElementValues();
		Set<Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue>> valueSet = valueMap
				.entrySet();

		for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> annoKeyValue : valueSet) {
			if (annoKeyValue.getKey().getSimpleName().equals(attributeName)) {
				ClassDeclaration annoValue = (ClassDeclaration) annoKeyValue
						.getValue().getValue();
				Entity entityAnnotation = annoValue.getAnnotation(Entity.class);
				if (entityAnnotation == null) {
					SourcePosition pos = annoKeyValue.getValue().getPosition();
					messager
							.printError(
									pos,
									"'"
											+ attributeName
											+ "' attribute value class should be annotated with 'javax.persistence.Entity'");
					return false;
				}
			}
		}
		return true;
	}

	public static boolean checkReturnType(AnnotationMirror mirror,
			MethodDeclaration methodDeclaration, EclipseMessager messager) {
		TypeMirror returnType = methodDeclaration.getReturnType();
		String returnTypeName = returnType.toString();
		if (!(returnTypeName.startsWith("java.util.List") || returnTypeName
				.startsWith("java.util.Collection"))) {
			messager
					.printFixableError(
							methodDeclaration.getPosition(),
							"Method should return java.util.List or java.util.Collection",
							PLUGIN_ID, ERROR_wrong_return_type);
			return false;
		}
		return true;
	}
}