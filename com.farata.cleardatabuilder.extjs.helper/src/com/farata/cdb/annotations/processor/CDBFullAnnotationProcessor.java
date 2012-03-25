package com.farata.cdb.annotations.processor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import clear.cdb.extjs.annotations.CX_JSFillChildrenMethod;
import clear.cdb.extjs.annotations.CX_JSGetMethod;
import clear.cdb.extjs.annotations.CX_JSJPQLMethod;
import clear.cdb.extjs.annotations.CX_JSService;
import clear.cdb.extjs.annotations.CX_TransferInfo;
import clear.cdb.extjs.annotations.CX_UpdateInfo;
import clear.cdb.extjs.annotations.DEFAULT;

import com.farata.cdb.annotations.helper.AnnotationsHelper;
import com.farata.cdb.annotations.helper.HQLHelper;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.type.MirroredTypeException;
import com.sun.mirror.type.ReferenceType;
import com.sun.mirror.type.TypeMirror;

public class CDBFullAnnotationProcessor implements AnnotationProcessor,
		AnnotationProcessorFactory {

	public static final String OPTION_CDB_ANNOTATIONS_FILE = "-Acom.faratasystems.cdb.annotations.file";
	private static final String BR = "\n";
	private Set<AnnotationTypeDeclaration> annotationTypeDeclarations;
	private AnnotationProcessorEnvironment annotationProcessorEnvironment;

	@Override
	public void process() {
		StringBuffer processResult = new StringBuffer();
		Collection<TypeDeclaration> specifiedTypeDeclarations = annotationProcessorEnvironment
				.getTypeDeclarations();
		processResult.append("<annotated-types>\n");
		HashMap<String, String> dtoToEntityMappings = new HashMap<String, String>();
		for (TypeDeclaration specifiedTypeDeclaration : specifiedTypeDeclarations) {
			CX_JSService serv = specifiedTypeDeclaration
					.getAnnotation(CX_JSService.class);
			try {
				if (serv != null) {
					processTypeDeclaration(specifiedTypeDeclaration,
							processResult, "\t");
					createEntityToDTOMappings(specifiedTypeDeclaration,
							dtoToEntityMappings);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		processResult.append("\t<dto-mappings>" + BR);
		for (String dtoName : dtoToEntityMappings.keySet()) {
			processResult.append("\t\t<dto-mapping dto-name=\"" + dtoName
					+ "\" entity-name=\"" + dtoToEntityMappings.get(dtoName)
					+ "\"/>" + BR);
		}
		processResult.append("\t</dto-mappings>" + BR);

		processResult.append("</annotated-types>\n");
		String outputFile = getOption(OPTION_CDB_ANNOTATIONS_FILE);
		outputFile = CDBHQLAnnotationProcessor.resolveFile(outputFile);
		writeContent(processResult.toString(), outputFile);
	}

	private void processTypeDeclaration(TypeDeclaration typeDeclaration,
			StringBuffer processResult, String indent) throws Exception {
		processResult.append(indent + "<annotated-type name=\""
				+ typeDeclaration.getQualifiedName() + "\">" + BR);
		Collection<AnnotationMirror> annotationsCollection = typeDeclaration
				.getAnnotationMirrors();
		processResult.append(indent + "\t<annotations>" + BR);
		for (AnnotationMirror annotationMirror : annotationsCollection) {
			processAnnotation(annotationMirror, processResult, indent + "\t\t");
		}
		processResult.append(indent + "\t</annotations>" + BR);
		Collection<? extends MethodDeclaration> methodsCollection = typeDeclaration
				.getMethods();
		processResult.append(indent + "\t<methods>" + BR);
		for (MethodDeclaration methodDeclaration : methodsCollection) {
			processMethodDeclaration(methodDeclaration, processResult, indent
					+ "\t\t");
		}
		processResult.append(indent + "\t</methods>" + BR);
		processResult.append(indent + "</annotated-type>" + BR);
	}

	private HashMap<String, String> createEntityToDTOMappings(
			TypeDeclaration typeDeclaration,
			HashMap<String, String> dtoToEntityMappings) {
		Collection<? extends MethodDeclaration> methods = typeDeclaration
				.getMethods();
		for (MethodDeclaration method : methods) {
			CX_JSJPQLMethod jpqlMethod = method
					.getAnnotation(CX_JSJPQLMethod.class);
			Class<?> entity = null;
			if (jpqlMethod != null) {
				// Get from transferInfo
				CX_TransferInfo transferInfo = jpqlMethod.transferInfo();
				try {
					transferInfo.mappedBy();
				} catch (MirroredTypeException e) {
					Class<?> mappedBy = extractClass(e);
					if (!mappedBy.equals(DEFAULT.class)) {
						entity = mappedBy;
					}
				}
			} else {
				CX_JSGetMethod getMethod = method
						.getAnnotation(CX_JSGetMethod.class);
				if (getMethod != null) {
					CX_TransferInfo transferInfo = getMethod.transferInfo();
					try {
						transferInfo.mappedBy();
					} catch (MirroredTypeException e) {
						Class<?> mappedBy = extractClass(e);
						if (!mappedBy.equals(DEFAULT.class)) {
							entity = mappedBy;
						}
					}
				} else {
					CX_JSFillChildrenMethod fillChildrenMethod = method
							.getAnnotation(CX_JSFillChildrenMethod.class);
					if (fillChildrenMethod != null) {
						CX_TransferInfo transferInfo = fillChildrenMethod
								.transferInfo();
						try {
							transferInfo.mappedBy();
						} catch (MirroredTypeException e) {
							Class<?> mappedBy = extractClass(e);
							if (!mappedBy.equals(DEFAULT.class)) {
								entity = mappedBy;
							}
						}
					}
				}
			}
			if (entity != null) {
				String transferType = getMethodTransferType(method);
				if (transferType == null || transferType.equals("")) {
					continue;
				}
				String canonicalName = entity.getCanonicalName();
				if (!transferType.equals(canonicalName)) {
					String cn = dtoToEntityMappings.get(transferType);
					if (cn != null && !cn.equals(canonicalName)) {
						System.err.println("WARNING: '" + transferType
								+ "' is already mapped by '" + cn + "' in '"
								+ typeDeclaration.getQualifiedName() + "'");
						continue;
					}
					String tf = findDtoByEntity(dtoToEntityMappings,
							canonicalName);
					if (tf != null && !tf.equals(transferType)) {
						System.err.println("WARNING: '" + tf
								+ "' is already mapped by '" + canonicalName
								+ "' in '" + typeDeclaration.getQualifiedName()
								+ "'");
						continue;
					}
					dtoToEntityMappings.put(transferType, canonicalName);
				}
			}

		}
		return dtoToEntityMappings;
	}

	private String findDtoByEntity(HashMap<String, String> dtoToEntityMappings,
			String name) {
		for (String key : dtoToEntityMappings.keySet()) {
			if (dtoToEntityMappings.get(key).equals(name)) {
				return key;
			}
		}
		return null;
	}

	private Class<?> extractClass(MirroredTypeException e) {
		String type = e.toString();
		type = type.substring(type.lastIndexOf(" ") + 1, type.length()).trim();
		Class<?> mappedBy = null;
		try {
			mappedBy = getClass().getClassLoader().loadClass(type);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return mappedBy;
	}

	private String getMethodTransferType(MethodDeclaration methodDeclaration) {

		// Check return type parameter
		TypeMirror type = methodDeclaration.getReturnType();
		String s = type.toString();
		String transferType = AnnotationsHelper.getTypeParameter(s);
		if (transferType != null && transferType.length() > 0
				&& !transferType.equals("?")) {
			return transferType;
		}

		// Check transferType annotation
		CX_JSJPQLMethod jpqlMethod = methodDeclaration
				.getAnnotation(CX_JSJPQLMethod.class);
		if (jpqlMethod != null) {
			transferType = jpqlMethod.transferInfo().type();
			if (transferType != null && transferType.length() > 0
					&& !transferType.equals("?")) {
				return transferType;
			}

//			transferType = jpqlMethod.transferType();
//			if (transferType != null && transferType.length() > 0
//					&& !transferType.equals("?")) {
//				return transferType;
//			}
		}

		CX_JSGetMethod getMethod = methodDeclaration
				.getAnnotation(CX_JSGetMethod.class);
		if (getMethod != null) {
			transferType = getMethod.transferInfo().type();
			if (transferType != null && transferType.length() > 0
					&& !transferType.equals("?")) {
				return transferType;
			}
		}

		CX_JSFillChildrenMethod fillChildrenMethod = methodDeclaration
				.getAnnotation(CX_JSFillChildrenMethod.class);
		if (fillChildrenMethod != null) {
			transferType = fillChildrenMethod.transferInfo().type();
			if (transferType != null && transferType.length() > 0
					&& !transferType.equals("?")) {
				return transferType;
			}
		}

		// Check updateEntity annotation
		CX_UpdateInfo uiAnnotation = methodDeclaration
				.getAnnotation(CX_UpdateInfo.class);
		if (uiAnnotation != null) {
			Class<?> updateEntity = null;
			try {
				updateEntity = uiAnnotation.updateEntity();
			} catch (MirroredTypeException e) {
				updateEntity = extractClass(e);
			}
			if (updateEntity != null) {
				transferType = updateEntity.getCanonicalName();
				if (transferType != null && transferType.length() > 0
						&& !transferType.equals("?")) {
					return transferType;
				}
			}
		}
		return null;
	}

	private void processAnnotation(AnnotationMirror annotation,
			StringBuffer processResult, String indent) throws Exception {
		getAnnotationValues(annotation, processResult, indent);
	}

	private void processMethodDeclaration(MethodDeclaration methodDeclaration,
			StringBuffer processResult, String indent) throws Exception {
		processResult.append(indent
				+ "<method to-string=\""
				+ methodToString(methodDeclaration).replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;") + "\" name=\""
				+ methodDeclaration.getSimpleName() + "\">\n");
		Collection<ParameterDeclaration> params = methodDeclaration
				.getParameters();
		processResult.append(indent + "\t<parameters>\n");
		int count = 0;
		for (ParameterDeclaration param : params) {
			processResult.append(indent + "\t\t<parameter name=\""
					+ param.getSimpleName() + "\" type=\"" + getTypeName(param)
					+ "\" order=\"" + count + "\"/>\n");
			count++;
		}
		processResult.append(indent + "\t</parameters>\n");
		Collection<AnnotationMirror> annotationMirrors = methodDeclaration
				.getAnnotationMirrors();
		processResult.append(indent + "\t<annotations>\n");
		for (AnnotationMirror annotationMirror : annotationMirrors) {
			processAnnotation(annotationMirror, processResult, indent + "\t\t");
		}
		processResult.append(indent + "\t</annotations>\n");
		processResult.append(indent + "</method>\n");
	}

	private String getTypeName(ParameterDeclaration param) {
		String paramType = param.getType().toString();
		paramType = paramType.replace("<", "&lt;");
		paramType = paramType.replace(">", "&gt;");
		return paramType;
	}

	private static String methodToString(MethodDeclaration method) {
		StringBuilder sb = new StringBuilder();
		TypeMirror genRetType = method.getReturnType();
		sb.append((genRetType.toString()) + " ");

		sb.append(method.getSimpleName() + "(");
		Collection<ParameterDeclaration> params = method.getParameters();
		boolean first = true;
		for (ParameterDeclaration param : params) {
			if (first) {
				sb.append(param.toString());
				first = false;
			} else {
				sb.append(", ");
				sb.append(param.toString());
			}
		}
		sb.append(")");
		ReferenceType[] exceptions = method.getThrownTypes().toArray(
				new ReferenceType[0]);
		if (exceptions.length > 0) {
			sb.append(" throws ");
			for (int k = 0; k < exceptions.length; k++) {
				sb.append(exceptions[k].toString());
				if (k < (exceptions.length - 1))
					sb.append(", ");
			}
		}
		return sb.toString();

	}

	public void getAnnotationValues(AnnotationMirror annotation,
			StringBuffer out, String indent) throws Exception {
		String annotationName = annotation == null ? "" : annotation
				.getAnnotationType().getDeclaration().getQualifiedName();
		out.append(indent + "<annotation name=\"" + annotationName + "\">\n");
		if (annotation != null) {
			out.append(indent + "\t<exists/>\n");
			Map<AnnotationTypeElementDeclaration, AnnotationValue> elementValues = annotation
					.getElementValues();
			for (AnnotationTypeElementDeclaration annotationMethod : elementValues
					.keySet()) {
				Object res = elementValues.get(annotationMethod).getValue();
				if (res instanceof AnnotationMirror) {
					out.append(indent + "\t<method name=\""
							+ annotationMethod.getSimpleName() + "\">\n"
							+ indent + "\t\t<value>\n");
					AnnotationMirror ann = (AnnotationMirror) res;
					getAnnotationValues(ann, out, indent + "\t\t\t");
					out.append(indent + "\t\t</value>\n");
					out.append(indent + "\t</method>\n");
				} else {
					if ("query".equals(annotationMethod.getSimpleName())) {
						if (HQLHelper.factory == null) {
							String configurationFile = CDBHQLAnnotationProcessor.resolveFile(getOption(CDBHQLAnnotationProcessor.OPTION_CDB_CONFIGURATION_FILE));
							HQLHelper.createSessionFactory(configurationFile);
						}
						String query = AnnotationsHelper.adjustQuery(res
								.toString());
						String hqlReturnTypes = AnnotationsHelper
								.getHQLReturnTypes(query, indent + "\t\t");
						out.append(indent + "\t<method name=\""
								+ annotationMethod.getSimpleName()
								+ "\" value=\"" + query + "\">\n");
						out.append(hqlReturnTypes);
						out.append(indent + "\t</method>\n");
					} else {
						out.append(indent + "\t<method name=\""
								+ annotationMethod.getSimpleName()
								+ "\" value=\"" + res.toString() + "\"/>\n");
					}
				}
			}
		}
		out.append(indent + "</annotation>\n");
	}

	private void writeContent(String sOut, String outputFile) {
		try {
			FileWriter fw = new FileWriter(outputFile);
			fw.write(sOut);
			fw.close();
		} catch (IOException e) {
			System.out.println("WARNING: " + e);
		}
	}

	private static String readContent(File file) {
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			char[] b = new char[1024];
			int off = 0;
			int len = b.length;
			StringBuffer sb = new StringBuffer();
			do {
				len = fr.read(b, off, len);
				if (len != -1) {
					sb.append(new String(b, off, len));
				}
			} while (len != -1);
			return sb.toString();
		} catch (Throwable e) {
			System.out.println("WARNING: " + e);
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	protected String getOption(String name) {
		for (String option : this.annotationProcessorEnvironment.getOptions()
				.keySet()) {
			if (option.contains(name)) {
				String[] ret = option.split("=");
				if (ret.length > 1) {
					return ret[1];
				} else {
					return this.annotationProcessorEnvironment.getOptions()
							.get(option);
				}
			}
		}
		return null;
	}

	@Override
	public AnnotationProcessor getProcessorFor(
			Set<AnnotationTypeDeclaration> annotationTypeDeclarations,
			AnnotationProcessorEnvironment annotationProcessorEnvironment) {
		this.annotationTypeDeclarations = annotationTypeDeclarations;
		this.annotationProcessorEnvironment = annotationProcessorEnvironment;
		return this;
	}

	@Override
	public Collection<String> supportedAnnotationTypes() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("*");
		return result;
	}

	@Override
	public Collection<String> supportedOptions() {
		return null;
	}
}