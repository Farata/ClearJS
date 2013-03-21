package com.farata.cdb.annotations.helper;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.hql.ParameterTranslations;
import org.hibernate.hql.ast.QueryTranslatorImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import clear.cdb.extjs.annotations.JSFillChildrenMethod;
import clear.cdb.extjs.annotations.JSGetMethod;
import clear.cdb.extjs.annotations.JSJPQLMethod;
import clear.cdb.extjs.annotations.JSUpdateInfo;
import clear.cdb.extjs.annotations.DEFAULT;

public class AnnotationsHelper {

	private static final String[] PRIMITIVE_TYPES = { "int", "float", "double", "char", "byte", "boolean", "long",
			"short" };
	public static boolean DEBUG;

	public AnnotationsHelper() {
	}

	public static boolean isPrimitiveType(String typeName) {
		for (String s : PRIMITIVE_TYPES) {
			if (s.equals(typeName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean typeAnnotatedWith(String typeName, String annotationName) throws ClassNotFoundException {
		try {
			Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
			Class<?> annotationClazz = AnnotationsHelper.class.getClassLoader().loadClass(annotationName);
			Annotation[] annotations = clazz.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(annotationClazz)) {
					return true;
				}
			}
		} catch (Throwable e) {
		}
		return false;
	}

	public static String getMethodName(String typeName, String methodName) {
		try {
			Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
			Method method = findMethodByName(clazz, methodName);
			return method == null ? null : method.getName();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getMethodNameByMethod(Method method) {
		return method.getName();
	}

	public static boolean methodAnnotatedWith(String typeName, String methodName, String annotationName)
			throws ClassNotFoundException {
		try {
			Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
			Class<?> annotationClazz = AnnotationsHelper.class.getClassLoader().loadClass(annotationName);
			Method method = findMethodByName(clazz, methodName);
			if (method == null) {
				return false;
			}
			Annotation[] annotations = method.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(annotationClazz)) {
					return true;
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}
		return false;
	}

	public static Object getMethodAnnotationValue(String typeName, String methodName, String annotationName,
			String annotationAtrribute) throws ClassNotFoundException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		// System.out.println(methodName + " " + annotationName +
		// " "+annotationAtrribute);
		Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
		Class<?> annotationClazz = AnnotationsHelper.class.getClassLoader().loadClass(annotationName);
		Method method = findMethodByName(clazz, methodName);
		if (method == null) {
			return null;
		}
		Annotation[] annotations = method.getAnnotations();
		for (Annotation annotation : annotations) {
			Class<? extends Annotation> annotationType = annotation.annotationType();
			if (annotationType.equals(annotationClazz)) {
				Method[] methods = annotationType.getDeclaredMethods();
				for (Method annotationMethod : methods) {
					if (annotationMethod.getName().equals(annotationAtrribute)) {
						Object res = annotationMethod.invoke(annotation);
						return res;
					}
				}
			}
		}
		return null;
	}

	public static Node getTypeAnnotation(String typeName, String annotationName) throws ClassNotFoundException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParserConfigurationException,
			SAXException, IOException {
		Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
		Class<?> annotationClazz = AnnotationsHelper.class.getClassLoader().loadClass(annotationName);
		String out = "<annotation>\n";
		Annotation[] annotations = clazz.getAnnotations();
		for (Annotation annotation : annotations) {
			Class<? extends Annotation> annotationType = annotation.annotationType();
			if (annotationType.equals(annotationClazz)) {
				out += "\t<exists/>\n";
				Method[] methods = annotationType.getDeclaredMethods();
				for (Method annotationMethod : methods) {
					Object res = annotationMethod.invoke(annotation);
					out += "\t<method name=\"" + annotationMethod.getName() + "\" value=\"" + res + "\"/>\n";
				}
			}
		}
		out += "</annotation>";

		return buildDocumentElement(out);
	}

	public static Node getMethodAnnotation(String typeName, String methodName, String annotationName)
			throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException,
			ParserConfigurationException, SAXException, IOException {
		Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
		Class<? extends Annotation> annotationClazz = (Class<? extends Annotation>) AnnotationsHelper.class
				.getClassLoader().loadClass(annotationName);
		Method method = findMethodByName(clazz, methodName);
		if (method == null) {
			return buildDocumentElement("<annotation/>");
		}

		Annotation annotation = method.getAnnotation(annotationClazz);
		StringBuffer out = new StringBuffer();
		getAnnotationValues(annotation, out, "");

		return buildDocumentElement(out.toString());
	}

	public static void getAnnotationValues(Annotation annotation, StringBuffer out, String indent)
			throws IllegalAccessException, InvocationTargetException {
		String annotationName = annotation == null ? "" : annotation.annotationType().getCanonicalName();
		out.append(indent + "<annotation name=\"" + annotationName + "\">\n");
		if (annotation != null) {
			out.append(indent + "\t<exists/>\n");
			Class<? extends Annotation> annotationClazz = annotation.annotationType();
			Method[] methods = annotationClazz.getDeclaredMethods();
			for (Method annotationMethod : methods) {
				Object res = annotationMethod.invoke(annotation);
				if (res instanceof Annotation) {
					out.append(indent + "\t<method name=\"" + annotationMethod.getName() + "\">\n" + indent
							+ "\t\t<value>\n");
					Annotation ann = (Annotation) res;
					getAnnotationValues(ann, out, indent + "\t\t\t");
					out.append(indent + "\t\t</value>\n");
					out.append(indent + "\t</method>\n");
				} else {
					out.append(indent + "\t<method name=\"" + annotationMethod.getName() + "\" value=\""
							+ objectToString(res) + "\"/>\n");
				}
			}
		}
		out.append(indent + "</annotation>\n");
	}

	private static String objectToString(Object obj) {
		if (obj == null) {
			return "";
		}
		if (obj instanceof Class<?>) {
			return ((Class<?>) obj).getCanonicalName();
		}
		return obj.toString();
	}

	public static Node getMethodParametersAnnotations(String typeName, String methodName, String annotationName)
			throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException,
			ParserConfigurationException, SAXException, IOException {
		Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
		Class<?> annotationClazz = AnnotationsHelper.class.getClassLoader().loadClass(annotationName);
		Method method = findMethodByName(clazz, methodName);
		if (method == null) {
			return null;
		}
		String out = "<annotations>\n";
		Annotation[][] annotations = method.getParameterAnnotations();
		for (int i = 0; i < annotations.length; i++) {
			for (int j = 0; j < annotations[i].length; j++) {
				Annotation annotation = annotations[i][j];
				Class<? extends Annotation> annotationType = annotation.annotationType();
				if (annotationType.equals(annotationClazz)) {
					out += "\t<exists/>\n";
					out += "\t<annotation parameterCount=\"" + i + "\">\n";
					Method[] methods = annotationType.getDeclaredMethods();
					for (Method annotationMethod : methods) {
						Object res = annotationMethod.invoke(annotation);
						out += "\t\t<method name=\"" + annotationMethod.getName() + "\" value=\"" + res + "\"/>\n";
					}
					out += "\t</annotation>\n";
				}
			}
		}
		out += "</annotations>";

		return buildDocumentElement(out);
	}

	public static String getMethodReturnType(String typeName, String methodName) throws ClassNotFoundException {
		Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
		Method method = findMethodByName(clazz, methodName);
		Type type = method.getGenericReturnType();
		String s = type.toString();
		return s;
	}

	public static String getMethodTransferType(String typeName, String methodName) throws ClassNotFoundException {
		Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
		Method method = findMethodByName(clazz, methodName);

		// Check return type parameter
		Type type = method.getGenericReturnType();
		String s = type.toString();
		String transferType = getTypeParameter(s);
		if (transferType != null && transferType.length() > 0 && !transferType.equals("?")) {
			return transferType;
		}

		// Check transferType annotation
		JSGetMethod getMethod = method.getAnnotation(JSGetMethod.class);
		if (getMethod != null) {
			transferType = getMethod.transferInfo().type();
			if (transferType != null && transferType.length() > 0 && !transferType.equals("?")) {
				return transferType;
			} else {
				return s.replace("class ", "");
			}
		}

		JSFillChildrenMethod fillChildrenMethod = method.getAnnotation(JSFillChildrenMethod.class);
		if (fillChildrenMethod != null) {
			transferType = fillChildrenMethod.transferInfo().type();
			if (transferType != null && transferType.length() > 0 && !transferType.equals("?")) {
				return transferType;
			} else {
				Class<?> parent = fillChildrenMethod.parent();
				String property = fillChildrenMethod.property();
				try {
					String entityType = getBeanPropertyType(parent.getCanonicalName(), property);
					entityType = getTypeParameter(entityType);
					transferType = getDTOByEntity(entityType);
					if (transferType == null) {
						return entityType;
					} else {
						return transferType;
					}
				} catch (Exception e) {
				}
			}
		}

		JSJPQLMethod jpqlMethod = method.getAnnotation(JSJPQLMethod.class);
		if (jpqlMethod != null) {
			transferType = jpqlMethod.transferInfo().type();
			if (transferType != null && transferType.length() > 0 && !transferType.equals("?")) {
				return transferType;
			}

			// transferType = jpqlMethod.transferType();
			// if (transferType != null && transferType.length()>0 &&
			// !transferType.equals("?")) {
			// return transferType;
			// }

			// Check updateEntity annotation
			JSUpdateInfo uiAnnotation = jpqlMethod.updateInfo();
			if (uiAnnotation != null) {
				Class<?> updateEntity = uiAnnotation.updateEntity();
				if (updateEntity != null && !updateEntity.equals(DEFAULT.class)) {
					transferType = updateEntity.getCanonicalName();
					if (transferType != null && transferType.length() > 0 && !transferType.equals("?")) {
						return transferType;
					}
				}
			}
		}

		// Check updateEntity annotation
		JSUpdateInfo uiAnnotation = method.getAnnotation(JSUpdateInfo.class);
		if (uiAnnotation != null) {
			Class<?> updateEntity = uiAnnotation.updateEntity();
			if (updateEntity != null) {
				transferType = updateEntity.getCanonicalName();
				if (transferType != null && transferType.length() > 0 && !transferType.equals("?")) {
					return transferType;
				}
			}
		}
		return null;
	}

	public static String getTypeParameter(String typeName) {
		int start = typeName.indexOf('<');
		int end = typeName.lastIndexOf('>');
		if (start > 0 && end > 0 && end > start) {
			typeName = typeName.substring(start + 1, end).trim();
			return typeName;
		}
		start = typeName.indexOf("&lt;");
		end = typeName.indexOf("&gt;");
		if (start > 0 && end > 0 && end > start) {
			typeName = typeName.substring(start + 4, end).trim();
			return typeName;
		}
		return null;
	}

	public static String getPackageName(String typeName) {
		if (typeName.indexOf('.') > 0) {
			return typeName.substring(0, typeName.lastIndexOf('.'));
		}
		return "";
	}

	public static String getTypeName(String typeName) {
		if (typeName.indexOf('.') > 0) {
			return typeName.substring(typeName.lastIndexOf('.') + 1);
		}
		return typeName;
	}

	private static Annotation getPropertyAnnotation(Class<?> clazz, PropertyDescriptor descriptor,
			Class<? extends Annotation> annotationClazz) {
		Annotation annotation = null;
		try {
			Field field = clazz.getDeclaredField(descriptor.getName());
			annotation = field.getAnnotation(annotationClazz);
		} catch (Exception e) {
		}
		if (annotation == null) {
			Method method = descriptor.getReadMethod();
			if (method != null) {
				annotation = method.getAnnotation(annotationClazz);
			}
		}
		if (annotation == null) {
			Method method = descriptor.getWriteMethod();
			if (method != null) {
				annotation = method.getAnnotation(annotationClazz);
			}
		}
		return annotation;

	}

	public static Node getEntityIdBeanProperty(String typeName) throws Exception {
		try {
			Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
			PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);
			String out = "";
			for (PropertyDescriptor descriptor : descriptors) {
				String propertyType = "";
				if (descriptor.getReadMethod() == null || descriptor.getWriteMethod() == null) {
					continue;
				}
				Annotation idAnnotation = getPropertyAnnotation(clazz, descriptor, javax.persistence.Id.class);
				if (idAnnotation == null) {
					idAnnotation = getPropertyAnnotation(clazz, descriptor, javax.persistence.EmbeddedId.class);
				}
				if (idAnnotation == null) {
					continue;
				}
				Type type = descriptor.getReadMethod().getGenericReturnType();
				if (type instanceof ParameterizedType) {
					ParameterizedType partype = (ParameterizedType) type;
					Type rawType = partype.getRawType();
					propertyType = ((Class<?>) rawType).getName() + "&lt;";
					Type[] aTypeArgs = partype.getActualTypeArguments();
					for (Type aTypeArg : aTypeArgs) {
						propertyType += ((Class<?>) aTypeArg).getName();
					}
					propertyType += "&gt;";
				} else {
					propertyType = getTypeName((Class<?>) type);
				}
				out += "<property name=\"" + descriptor.getName() + "\" " + "type=\"" + propertyType + "\" "
						+ "readMethod=\"" + descriptor.getReadMethod().getName() + "\" " + "writeMethod=\""
						+ descriptor.getWriteMethod().getName() + "\"/>";
				return buildDocumentElement(out);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static Node getVersionProperties(String typeName) throws Exception {
		try {
			Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
			PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);
			String out = "<properties>\n";
			for (PropertyDescriptor descriptor : descriptors) {
				Node versionNode = getBeanPropertyAnnotation(typeName, descriptor.getName(),
						"javax.persistence.Version");
				if (versionNode.getOwnerDocument().getElementsByTagName("exists").getLength() == 0) {
					continue;
				}
				String propertyType = "";

				if (descriptor.getReadMethod() == null || descriptor.getWriteMethod() == null) {
					continue;
				}
				Type type = descriptor.getReadMethod().getGenericReturnType();
				if (type instanceof ParameterizedType) {
					ParameterizedType partype = (ParameterizedType) type;
					Type rawType = partype.getRawType();
					propertyType = ((Class<?>) rawType).getName() + "&lt;";
					Type[] aTypeArgs = partype.getActualTypeArguments();
					for (Type aTypeArg : aTypeArgs) {
						propertyType += ((Class<?>) aTypeArg).getName();
					}
					propertyType += "&gt;";
				} else {
					propertyType = getTypeName((Class<?>) type);
				}
				out += "\t<property name=\"" + descriptor.getName() + "\" " + "type=\"" + propertyType + "\" "
						+ "readMethod=\"" + descriptor.getReadMethod().getName() + "\" " + "writeMethod=\""
						+ descriptor.getWriteMethod().getName() + "\"/>\n";
			}
			out += "</properties>";

			return buildDocumentElement(out);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static Node getBeanProperties(String typeName) throws Exception {
		try {
			Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
			PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);
			String out = "<properties>\n";
			for (PropertyDescriptor descriptor : descriptors) {
				String propertyType = "";

				// Field field = null;
				// try {
				// //descriptor.
				// field = clazz.getDeclaredField(descriptor.getName());
				// } catch (Exception e) {
				// continue;
				// }
				if (descriptor.getReadMethod() == null || descriptor.getWriteMethod() == null) {
					continue;
				}
				Type type = descriptor.getReadMethod().getGenericReturnType();
				// Type type = descriptor.getPropertyType();
				if (type instanceof ParameterizedType) {
					ParameterizedType partype = (ParameterizedType) type;
					Type rawType = partype.getRawType();
					propertyType = ((Class<?>) rawType).getName() + "&lt;";
					Type[] aTypeArgs = partype.getActualTypeArguments();
					for (Type aTypeArg : aTypeArgs) {
						propertyType += ((Class<?>) aTypeArg).getName();
					}
					propertyType += "&gt;";
				} else {
					propertyType = getTypeName((Class<?>) type);
				}
				out += "\t<property name=\"" + descriptor.getName() + "\" " + "type=\"" + propertyType + "\" "
						+ "readMethod=\"" + descriptor.getReadMethod().getName() + "\" " + "writeMethod=\""
						+ descriptor.getWriteMethod().getName() + "\">\n";
				try {
					out += getBeanPropertyAnnotationsString(typeName, descriptor.getName(), "\t\t") + "\n";
				} catch (Throwable th) {
					th.printStackTrace();
				}
				out += "\t</property>\n";
			}
			out += "</properties>";

			return buildDocumentElement(out);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public static Node getBeanPropertyAnnotation(String typeName, String propertyId, String annotationName)
			throws Exception {
		try {
			Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
			Class<? extends Annotation> annotationClazz = (Class<? extends Annotation>) AnnotationsHelper.class
					.getClassLoader().loadClass(annotationName);
			PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);
			String out = "<annotation>\n";
			for (PropertyDescriptor descriptor : descriptors) {
				if (descriptor.getName().equals(propertyId)) {
					Annotation annotation = null;
					try {
						Field field = clazz.getDeclaredField(propertyId);
						annotation = field.getAnnotation(annotationClazz);
					} catch (Exception e) {
					}
					if (annotation == null) {
						Method method = descriptor.getReadMethod();
						if (method != null) {
							annotation = method.getAnnotation(annotationClazz);
						}
					}
					if (annotation == null) {
						Method method = descriptor.getWriteMethod();
						if (method != null) {
							annotation = method.getAnnotation(annotationClazz);
						}
					}
					if (annotation != null) {
						out += "\t<exists/>\n";
						Method[] methods = annotationClazz.getDeclaredMethods();
						for (Method annotationMethod : methods) {
							Object res = annotationMethod.invoke(annotation);
							out += "\t<method name=\"" + annotationMethod.getName() + "\" value=\"" + res + "\"/>\n";
						}
					}
					break;
				}
			}
			out += "</annotation>";

			return buildDocumentElement(out);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static Node getBeanPropertyAnnotations(String typeName, String propertyId) {
		try {
			String out = getBeanPropertyAnnotationsString(typeName, propertyId, "");
			return buildDocumentElement(out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getBeanPropertyAnnotationsString(String typeName, String propertyId, String indent)
			throws Exception {
		Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);
		String out = indent + "<annotations>\n";
		for (PropertyDescriptor descriptor : descriptors) {
			if (descriptor.getName().equals(propertyId)) {
				Annotation[] annotations = null;
				try {
					Field field = clazz.getDeclaredField(propertyId);
					annotations = field.getAnnotations();
				} catch (Exception e) {
				}
				if (annotations == null || annotations.length == 0) {
					Method method = descriptor.getReadMethod();
					if (method != null) {
						annotations = method.getAnnotations();
					}
				}
				if (annotations == null || annotations.length == 0) {
					Method method = descriptor.getWriteMethod();
					if (method != null) {
						annotations = method.getAnnotations();
					}
				}

				if (annotations != null) {
					for (Annotation annotation : annotations) {
						Class<? extends Annotation> annotationClazz = annotation.annotationType();
						out += indent + "\t<annotation name=\"" + annotation.annotationType().getCanonicalName()
								+ "\">\n";
						Method[] methods = annotationClazz.getDeclaredMethods();
						for (Method annotationMethod : methods) {
							try {
								Object res = annotationMethod.invoke(annotation);
								out += indent + "\t\t<method name=\"" + annotationMethod.getName() + "\" value=\""
										+ res + "\"/>\n";
							} catch (Throwable th) {
								continue;
							}
						}
						out += indent + "\t</annotation>\n";
					}
				}
				break;
			}
		}
		out += indent + "</annotations>";

		return out;
	}

	@SuppressWarnings("unchecked")
	public static String getBeanPropertyType(String typeName, String propertyId) throws Exception {
		try {
			Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
			PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);
			String out = "<annotation>\n";
			for (PropertyDescriptor descriptor : descriptors) {
				if (descriptor.getName().equals(propertyId)) {
					String propertyType = "";

					// Field field = null;
					// try {
					// //descriptor.
					// field = clazz.getDeclaredField(descriptor.getName());
					// } catch (Exception e) {
					// continue;
					// }
					if (descriptor.getReadMethod() == null || descriptor.getWriteMethod() == null) {
						continue;
					}
					Type type = descriptor.getReadMethod().getGenericReturnType();
					// Type type = descriptor.getPropertyType();
					if (type instanceof ParameterizedType) {
						ParameterizedType partype = (ParameterizedType) type;
						Type rawType = partype.getRawType();
						propertyType = ((Class<?>) rawType).getName() + "&lt;";
						Type[] aTypeArgs = partype.getActualTypeArguments();
						for (Type aTypeArg : aTypeArgs) {
							propertyType += ((Class<?>) aTypeArg).getName();
						}
						propertyType += "&gt;";
					} else {
						propertyType = getTypeName((Class<?>) type);
					}
					return propertyType;
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static Node buildDocumentElement(String string) throws ParserConfigurationException, SAXException,
			IOException {
		if (DEBUG) {
			System.out.println(string);
		}
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		StringReader reader = new StringReader(string);
		InputSource inputSource = new InputSource(reader);
		return docBuilder.parse(inputSource).getDocumentElement();
	}

	public static String replaceAll(String string, String subject, String replacement) {
		return string.replace(subject, replacement);
	}

	public static Node split(String string, String delimiter) throws ParserConfigurationException, SAXException,
			IOException {
		String[] arr = string.split(delimiter);
		String out = "<array>\n";
		for (String s : arr) {
			s = s.trim();
			if (!isEmptyString(s)) {
				out += "\t<element value=\"" + s + "\"/>\n";
			}
		}
		out += "</array>";
		return buildDocumentElement(out);
	}

	public static Node getMethods(String typeName) throws ParserConfigurationException, SAXException, IOException,
			ClassNotFoundException {
		String out = "<methods>\n";
		Method[] methods = getClassMethods(typeName);
		for (Method method : methods) {
			out += "\t<method id=\"" + method.toString() + "\" to-string=\""
					+ methodToString(method).replaceAll("<", "&lt;").replaceAll(">", "&gt;") + "\" name=\""
					+ method.getName() + "\">\n";
			Class<?>[] params = method.getParameterTypes();
			out += "\t\t<parameters>\n";
			int count = 0;
			for (Class<?> param : params) {
				out += "\t\t\t<parameter type=\"" + getTypeName(param) + "\" order=\"" + count + "\"/>\n";
				count++;
			}
			out += "\t\t</parameters>\n";
			out += "\t</method>\n";
		}
		out += "</methods>";
		return buildDocumentElement(out);
	}

	public static Method[] getClassMethods(String typeName) throws ClassNotFoundException {
		Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
		Method[] methods = clazz.getMethods();
		return methods;
	}

	public static boolean methodHasReturnType(String typeName, String methodName) throws ClassNotFoundException {
		Class<?> clazz = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
		Method method = findMethodByName(clazz, methodName);
		Class<?> type = method.getReturnType();
		return type != null && !type.toString().equals("void");
	}

	@SuppressWarnings("unchecked")
	public static String getHQLReturnTypes(String query, String indent) throws Exception {
		try {
			String[] parsedAliases = getSelectQueryAliases(query);
			QueryTranslatorImpl trans = (QueryTranslatorImpl) HQLHelper.compileQuery(query);
			String out = indent + "<query>\n";
			out += indent + "\t<types>\n";
			org.hibernate.type.Type[] types = trans.getReturnTypes();
			String[] aliases = trans.getReturnAliases();
			for (int i = 0; i < aliases.length; i++) {
				String alias = aliases[i];
				try {
					Integer.valueOf(alias);
					alias = "property" + alias;
					if (Pattern.matches("[a-zA-Z$_][a-zA-Z$_0-9]*", parsedAliases[i])) {
						alias = parsedAliases[i];
					}
				} catch (Throwable e) {
				}
				out += indent + "\t\t<type name=\"" + getTypeName(types[i].getReturnedClass()) + "\" alias=\"" + alias
						+ "\" />\n";
			}
			out += indent + "\t</types>\n";
			out += indent + "\t<parameters>\n";
			ParameterTranslations params = trans.getParameterTranslations();
			String[] paramNames = (String[]) params.getNamedParameterNames().toArray(new String[0]);
			Arrays.sort(paramNames, new HQLParamersComparator(params));
			for (String paramName : paramNames) {
				out += indent + "\t\t<parameter name=\"" + paramName + "\"/>\n";
			}
			out += indent + "\t</parameters>\n";
			out += indent + "</query>\n";
			return out;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static String adjustQuery(String query) {
		String result = query;
		try {
			String[] parsedAliases = getSelectQueryAliases(query);
			if (parsedAliases != null && parsedAliases.length == 1) {
				QueryTranslatorImpl trans = (QueryTranslatorImpl) HQLHelper.compileQuery(query);
				org.hibernate.type.Type[] types = trans.getReturnTypes();
				String[] aliases = trans.getReturnAliases();
				if (types != null && types.length == 1) {
					org.hibernate.type.Type type = types[0];
					String alias = aliases[0];
					try {
						Integer.valueOf(alias);
						alias = "property" + alias;
						if (Pattern.matches("[a-zA-Z$_][a-zA-Z$_0-9]*", parsedAliases[0])) {
							alias = parsedAliases[0];
						} else {
							return result;
						}
					} catch (Throwable e) {
						return result;
					}
					Class<?> clazz = type.getReturnedClass();
					if (clazz.isAnnotationPresent(Entity.class)) {
						NodeList beanProperties = getBeanProperties(clazz.getCanonicalName()).getChildNodes();
						String s = "SELECT ";
						for (int i = 0; i < beanProperties.getLength(); i++) {
							Node item = beanProperties.item(i);
							if ("property".equals(item.getNodeName())) {
								String propertyName = item.getAttributes().getNamedItem("name").getNodeValue();
								String typeName = item.getAttributes().getNamedItem("type").getNodeValue();
								try {
									if (typeName.indexOf('<') != -1) {
										typeName = typeName.substring(0, typeName.indexOf('<'));
									}
									Class<?> clazz1 = AnnotationsHelper.class.getClassLoader().loadClass(typeName);
									if (Collection.class.isAssignableFrom(clazz1)) {
										continue;
									}
									Field propField = clazz.getDeclaredField(propertyName);
									Transient transientAnnotation = propField.getAnnotation(Transient.class);
									if (transientAnnotation == null) {
										String readMethodName = item.getAttributes().getNamedItem("readMethod")
												.getNodeValue();
										String writeMethodName = item.getAttributes().getNamedItem("writeMethod")
												.getNodeValue();

										Method[] methods = clazz.getDeclaredMethods();
										for (Method m : methods) {
											if (m.getName().equals(readMethodName)
													|| m.getName().equals(writeMethodName)) {
												transientAnnotation = methods[i].getAnnotation(Transient.class);
											}
										}
									}
									if (transientAnnotation != null) {
										continue;
									}
								} catch (Throwable e) {
								}
								s += alias + "." + propertyName + " as " + propertyName + " , ";
							}
						}
						s = s.substring(0, s.length() - 2) + " ";
						result = s + result.substring(result.toLowerCase().indexOf("from "));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String decapitalizeString(String string) {
		return String.valueOf(string.charAt(0)).toLowerCase() + string.substring(1);
	}

	public static String capitalizeString(String string) {
		return String.valueOf(string.charAt(0)).toUpperCase() + string.substring(1);
	}

	public static boolean isEmptyString(String string) {
		return string == null || string.length() == 0;
	}

	public static String javaType2FlexType(String string) {
		int beginIndex = string.indexOf("<");
		int endIndex = string.lastIndexOf(">");
		if (beginIndex >= 0 && endIndex >= 0) {
			String toReplace = string.substring(beginIndex, endIndex + 1);
			string = string.replace(toReplace, "");
		}
		String result = string;
		Class<?> clazz = null;
		try {
			clazz = AnnotationsHelper.class.getClassLoader().loadClass(string);
		} catch (ClassNotFoundException e) {
		}
		if (clazz != null) {
			if (Number.class.isAssignableFrom(clazz)) {
				result = "Number";
			} else if (Collection.class.isAssignableFrom(clazz)) {
				result = "mx.collections.ArrayCollection";
			} else if (Document.class.isAssignableFrom(clazz)) {
				result = "XML";
			} else if (Date.class.isAssignableFrom(clazz)) {
				result = "Date";
			}
		}
		if (string.endsWith("[]")) {
			result = "Array";
		} else if ("enum".equals(string)) {
			result = "String";
		} else if ("java.lang.String".equals(string)) {
			result = "String";
		} else if ("java.lang.Boolean".equals(string)) {
			result = "Boolean";
		} else if ("boolean".equals(string)) {
			result = "Boolean";
		} else if ("int".equals(string)) {
			result = "int";
		} else if ("short".equals(string)) {
			result = "int";
		} else if ("java.lang.Short".equals(string)) {
			result = "int";
		} else if ("byte[]".equals(string)) {
			result = "flash.utils.ByteArray";
		} else if ("java.lang.Byte[]".equals(string)) {
			result = "flash.utils.ByteArray";
		} else if ("double".equals(string)) {
			result = "Number";
		} else if ("long".equals(string)) {
			result = "Number";
		} else if ("float".equals(string)) {
			result = "Number";
		} else if ("java.lang.Character".equals(string)) {
			result = "String";
		} else if ("char".equals(string)) {
			result = "String";
		} else if ("java.lang.Character[]".equals(string)) {
			result = "String";
		} else if ("char[]".equals(string)) {
			result = "String";
		} else if ("java.math.BigInteger".equals(string)) {
			result = "String";
		} else if ("java.math.BigDecimal".equals(string)) {
			result = "String";
		} else if ("java.util.Calendar".equals(string)) {
			result = "Date";
		} else if ("java.util.Date".equals(string)) {
			result = "Date";
		} else if ("java.lang.Object".equals(string)) {
			result = "Object";
		}
		return result;
	}

	private static String methodToString(Method method) {
		StringBuilder sb = new StringBuilder();
		Type[] typeparms = method.getTypeParameters();
		if (typeparms.length > 0) {
			boolean first = true;
			sb.append("<");
			for (Type typeparm : typeparms) {
				if (!first)
					sb.append(",");
				if (typeparm instanceof Class<?>)
					sb.append(((Class<?>) typeparm).getName());
				else
					sb.append(typeparm.toString());
				first = false;
			}
			sb.append("> ");
		}

		Type genRetType = method.getGenericReturnType();
		sb.append(((genRetType instanceof Class<?>) ? getTypeName((Class<?>) genRetType) : genRetType.toString()) + " ");

		sb.append(method.getName() + "(");
		Type[] params = method.getGenericParameterTypes();
		for (int j = 0; j < params.length; j++) {
			sb.append((params[j] instanceof Class<?>) ? getTypeName((Class<?>) params[j]) : (params[j].toString()));
			String pName = "arg" + j;
			sb.append(" ").append(pName);
			if (j < (params.length - 1))
				sb.append(",");
		}
		sb.append(")");
		Type[] exceptions = method.getGenericExceptionTypes();
		if (exceptions.length > 0) {
			sb.append(" throws ");
			for (int k = 0; k < exceptions.length; k++) {
				sb.append((exceptions[k] instanceof Class<?>) ? ((Class<?>) exceptions[k]).getName() : exceptions[k]
						.toString());
				if (k < (exceptions.length - 1))
					sb.append(",");
			}
		}
		return sb.toString();

	}

	private static String getTypeName(Class<?> type) {
		if (type.isArray()) {
			try {
				Class<?> cl = type;
				int dimensions = 0;
				while (cl.isArray()) {
					dimensions++;
					cl = cl.getComponentType();
				}
				StringBuffer sb = new StringBuffer();
				sb.append(cl.getName());
				for (int i = 0; i < dimensions; i++) {
					sb.append("[]");
				}
				return sb.toString();
			} catch (Throwable e) { /* FALLTHRU */
			}
		}
		return type.getName();
	}

	private static Method findMethodByName(Class<?> clazz, String methodName) {
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		return null;
	}

	private static String[] extractColumns(String query) {
		query = query.replace('\t', ' ');
		query = query.replace("\n", "");
		int fromPosition = query.toLowerCase().indexOf(" from ");
		int selectPosition = query.toLowerCase().indexOf("select");
		if (selectPosition >= 0) {
			String columns = query.substring(selectPosition + 6, fromPosition);
			StringTokenizer st = new StringTokenizer(columns, ",");
			List<String> columnList = new ArrayList<String>();
			while (st.hasMoreTokens()) {
				columnList.add(st.nextToken().trim());
			}
			return (String[]) columnList.toArray(new String[0]);
		} else {
			return null;
		}
	}

	private static String[] getAttributeFieldNames(final String[] fieldNames) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < fieldNames.length; i++) {
			list.add(getAttributeFieldName(fieldNames[i]));
		}
		return (String[]) list.toArray(new String[0]);
	}

	private static String getAttributeFieldName(final String fieldName) {
		int dot = fieldName.indexOf('.');
		String trimmedFieldName = null;
		String normalisedFieldName = null;
		if (dot >= 0) {
			trimmedFieldName = fieldName.substring(dot + 1);
		} else {
			trimmedFieldName = fieldName;
		}

		int asClause = trimmedFieldName.toLowerCase().indexOf(" as ");
		if (asClause > 0) {
			normalisedFieldName = trimmedFieldName.substring(0, asClause);
		} else {
			normalisedFieldName = trimmedFieldName;
		}
		return normalisedFieldName;
	}

	public static String[] getSelectQueryAliases(String queryString) {
		if (queryString == null) {
			return null;
		}
		String[] cols = extractColumns(queryString);
		return getAttributeFieldNames(cols);
	}

	public static void createSessionFactory(String configurationFile) throws FileNotFoundException,
			ParserConfigurationException, SAXException, IOException {
		HQLHelper.createSessionFactory(configurationFile);
	}

	public static HashMap<String, String> getEntityToDTOMappings() {
		return entityToDTOMappings;
	}

	public static void setEntityToDTOMappings(HashMap<String, String> entityToDTOMappings) {
		AnnotationsHelper.entityToDTOMappings = entityToDTOMappings;
	}

	public static HashMap<String, String> getDtoToEntityMappings() {
		return dtoToEntityMappings;
	}

	public static void setDtoToEntityMappings(HashMap<String, String> dtoToEntityMappings) {
		AnnotationsHelper.dtoToEntityMappings = dtoToEntityMappings;
	}

	private static HashMap<String, String> entityToDTOMappings = new HashMap<String, String>();
	private static HashMap<String, String> dtoToEntityMappings = new HashMap<String, String>();
	private static Properties aptProps;

	public static String getEntityByDTO(String dtoName) {
		return dtoToEntityMappings.get(dtoName);
	}

	public static String getDTOByEntity(String entityName) {
		return entityToDTOMappings.get(entityName);
	}

	public static String entityToGenDTO(String entityName) {
		String dtoName = getDTOByEntity(entityName);
		if (dtoName == null) {
			return entityName;
		} else {
			return dtoToGenDTO(dtoName);
		}
	}

	public static String replaceEntitiesWithGenDTOs(String string) {
		Set<String> keySet = entityToDTOMappings.keySet();
		List<String> ac = new ArrayList<String>(keySet);
		Collections.sort(ac, new EntityNamesComparator());
		for (String key : ac) {
			string = replaceAllEntities(string, key, entityToGenDTO(key));
		}
		return string;
	}

	private static String replaceAllEntities(String string, String key, String replacement) {
		string = "<" + string + ">";
		String result = "";
		while (true) {
			int index = string.indexOf(key);
			if (index != -1) {
				String s = string.substring(0, index + key.length());
				char ch = string.charAt(index + key.length());
				String sch = String.valueOf(ch);
				char ch2 = string.charAt(index - 1);
				String sch2 = String.valueOf(ch2);

				string = string.substring(index + key.length());
				String matchString = "[a-zA-Z0-9_.]";
				if (!sch.matches(matchString) && !sch2.matches(matchString)) {
					s = s.replace(key, replacement);
				}
				result += s;
			} else {
				String resultString = result + string;
				return resultString.substring(1, resultString.length() - 1);
			}
		}
	}

	public static String genDTOtoDTO(String dtoName) {
		dtoName = dtoName.replace(".$", ".");
		return dtoName;
	}

	public static String genDTOtoEntity(String dtoName) {
		dtoName = genDTOtoDTO(dtoName);
		return getEntityByDTO(dtoName);
	}

	public static String dtoToGenDTO(String dtoName) {
		String dtoShortName = dtoName.substring(dtoName.lastIndexOf('.') + 1, dtoName.length());
		return dtoName.replace('.' + dtoShortName, ".$" + dtoShortName);
	}

	public static String removeDuplicateTokens(String in, String delimiter) {
		String out = "";

		StringTokenizer st = new StringTokenizer(in, delimiter);
		HashSet<String> set = new HashSet<String>();
		while (st.hasMoreTokens()) {
			set.add(st.nextToken());
		}

		for (String uniqueToken : set) {
			if (!("").equals(out))
				out = out + delimiter;
			out = out + uniqueToken;
		}
		return out;
	}

	public static String createSubServiceName(String serviceName) {
		String genTypeName = serviceName;
		if (serviceName.indexOf(".") != -1) {
			genTypeName = serviceName.substring(serviceName.lastIndexOf(".") + 1);
		}
		if (genTypeName.substring(0, 1).equals("I")) {
			genTypeName = genTypeName.substring(1);
		} else {
			genTypeName = genTypeName + "Impl";
		}
		if (serviceName.indexOf(".") != -1) {
			genTypeName = serviceName.substring(0, serviceName.lastIndexOf(".") + 1) + genTypeName;
		}
		return genTypeName;
	}

	public static void clearDTOMappings() {
		dtoToEntityMappings.clear();
		entityToDTOMappings.clear();
	}

	public static void addDTOMapping(String dtoName, String entityName) {
		dtoToEntityMappings.put(dtoName, entityName);
		entityToDTOMappings.put(entityName, dtoName);
	}

	public static long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	public static void traceTime() {
		System.out.println(currentTimeMillis());
	}

	public static int compareLastModified(String fileName1, String fileName2) {
		File file1 = new File(fileName1);
		File file2 = new File(fileName2);
		if (!file1.exists() && !file2.exists()) {
			return 0;
		}
		if (file1.exists() && !file2.exists()) {
			return 1;
		}
		if (!file1.exists() && file2.exists()) {
			return -1;
		}
		long lastModified1 = file1.lastModified();
		long lastModified2 = file2.lastModified();
		if (lastModified1 > lastModified2) {
			return 1;
		} else {
			return -1;
		}
	}

	public static boolean fileExists(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	public static String createGenServiceName(String serviceName) {
		String genTypeName = serviceName;
		if (genTypeName.substring(0, 1).equals("I")) {
			genTypeName = "_" + genTypeName.substring(1);
		} else {
			genTypeName = "_" + genTypeName + "Impl";
		}
		return genTypeName;
	}

	public static String getStorePath(String fullDTOName) {
		String fullName = getStoreNameFull(fullDTOName);
		return getStorePathByStoreName(fullName);
	}

	public static String getStorePathByStoreName(String fullName) {
		initAPTProperties();
		String pkg = fullName.substring(0, fullName.lastIndexOf('.'));
		String pkgTrans = aptProps
				.getProperty("org.eclipse.jdt.apt.processorOptions/-Acom.faratasystems.cdbjs.store.package-path-transformer");
		PatternPackageNameTransformer patternPackageNameTransformer = new PatternPackageNameTransformer(pkgTrans);
		pkg = patternPackageNameTransformer.transform(pkg);

		return pkg.replace('.', '/');
	}

	public static String getStorePackage(String fullDTOName) {
		fullDTOName = getStoreNameFull(fullDTOName);
		return fullDTOName.substring(0, fullDTOName.lastIndexOf('.'));
	}

	public static String getStoreNameShort(String fullDTOName) {
		fullDTOName = getStoreNameFull(fullDTOName);
		return fullDTOName.substring(fullDTOName.lastIndexOf('.') + 1);
	}

	public static String getStoreNameFull(String fullDTOName) {
		initAPTProperties();

		String cnTrans = aptProps
				.getProperty("org.eclipse.jdt.apt.processorOptions/-Acom.faratasystems.cdbjs.store.class-name-transformer");
		PatternClassNameTransformer patternClassNameTransformer = new PatternClassNameTransformer(cnTrans);

		String fullName = patternClassNameTransformer.transform(fullDTOName);
		String pkg = fullName.substring(0, fullName.lastIndexOf('.'));

		String name = fullName.substring(fullName.lastIndexOf('.') + 1);

		return pkg + '.' + name;
	}

	public static String getStoreNameFullGen(String fullDTOName) {
		fullDTOName = getStoreNameFull(fullDTOName);
		String name = fullDTOName.substring(fullDTOName.lastIndexOf('.') + 1);
		String pkg = fullDTOName.substring(0, fullDTOName.lastIndexOf('.'));
		return pkg + ".generated._" + name;
	}
	
	public static String getServicePath(String fullDTOName) {
		String fullName = getServiceNameFull(fullDTOName);
		return getServicePathByServiceName(fullName);
	}

	public static String getServicePathByServiceName(String fullName) {
		initAPTProperties();
		String pkg = fullName.substring(0, fullName.lastIndexOf('.'));
		String pkgTrans = aptProps
				.getProperty("org.eclipse.jdt.apt.processorOptions/-Acom.faratasystems.cdbjs.service.package-path-transformer");
		PatternPackageNameTransformer patternPackageNameTransformer = new PatternPackageNameTransformer(pkgTrans);
		pkg = patternPackageNameTransformer.transform(pkg);

		return pkg.replace('.', '/');
	}

	public static String getServicePackage(String fullDTOName) {
		fullDTOName = getServiceNameFull(fullDTOName);
		return fullDTOName.substring(0, fullDTOName.lastIndexOf('.'));
	}

	public static String getServiceNameShort(String fullDTOName) {
		fullDTOName = getServiceNameFull(fullDTOName);
		return fullDTOName.substring(fullDTOName.lastIndexOf('.') + 1);
	}

	public static String getServiceNameFull(String fullDTOName) {
		initAPTProperties();

		String cnTrans = aptProps
				.getProperty("org.eclipse.jdt.apt.processorOptions/-Acom.faratasystems.cdbjs.service.class-name-transformer");
		PatternClassNameTransformer patternClassNameTransformer = new PatternClassNameTransformer(cnTrans);

		String fullName = patternClassNameTransformer.transform(fullDTOName);
		String pkg = fullName.substring(0, fullName.lastIndexOf('.'));

		String name = fullName.substring(fullName.lastIndexOf('.') + 1);

		return pkg + '.' + name;
	}

	public static String getServiceNameFullGen(String fullDTOName) {
		fullDTOName = getServiceNameFull(fullDTOName);
		String name = fullDTOName.substring(fullDTOName.lastIndexOf('.') + 1);
		String pkg = fullDTOName.substring(0, fullDTOName.lastIndexOf('.'));
		return pkg + ".generated._" + name;
	}

	public static String getModelPath(String fullDTOName) {
		String fullName = getModelNameFull(fullDTOName);
		return getModelPathByModelName(fullName);
	}

	public static String getModelPathByModelName(String fullName) {
		initAPTProperties();
		String pkg = fullName.substring(0, fullName.lastIndexOf('.'));
		String pkgTrans = aptProps
				.getProperty("org.eclipse.jdt.apt.processorOptions/-Acom.faratasystems.dto2extjs.package-path-transformer");
		PatternPackageNameTransformer patternPackageNameTransformer = new PatternPackageNameTransformer(pkgTrans);
		pkg = patternPackageNameTransformer.transform(pkg);

		return pkg.replace('.', '/');
	}

	public static String getModelPackage(String fullDTOName) {
		fullDTOName = getModelNameFull(fullDTOName);
		return fullDTOName.substring(0, fullDTOName.lastIndexOf('.'));
	}

	public static String getModelNameShort(String fullDTOName) {
		fullDTOName = getModelNameFull(fullDTOName);
		return fullDTOName.substring(fullDTOName.lastIndexOf('.') + 1);
	}

	public static String getModelNameFull(String fullDTOName) {
		initAPTProperties();

		String cnTrans = aptProps
				.getProperty("org.eclipse.jdt.apt.processorOptions/-Acom.faratasystems.dto2extjs.class-name-transformer");
		PatternClassNameTransformer patternClassNameTransformer = new PatternClassNameTransformer(cnTrans);

		String fullName = patternClassNameTransformer.transform(fullDTOName);
		String pkg = fullName.substring(0, fullName.lastIndexOf('.'));

		String name = fullName.substring(fullName.lastIndexOf('.') + 1);

		return pkg + '.' + name;
	}

	public static String getModelNameFullGen(String fullDTOName) {
		fullDTOName = getModelNameFull(fullDTOName);
		String name = fullDTOName.substring(fullDTOName.lastIndexOf('.') + 1);
		String pkg = fullDTOName.substring(0, fullDTOName.lastIndexOf('.'));
		return pkg + ".generated._" + name;
	}

	private static void initAPTProperties() {
		if (aptProps == null) {
			aptProps = new Properties();
			try {
				aptProps.load(new FileInputStream("../.settings/org.eclipse.jdt.apt.core.prefs"));
			} catch (Throwable th) {
			}
			// System.err.println(aptProps);
		}
	}
}