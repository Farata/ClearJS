package com.farata.cdb.annotations.processor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;

public class CDBAnnotationProcessor implements AnnotationProcessor,
		AnnotationProcessorFactory {

	public static final String OPTION_CDB_ANNOTATIONS_FILE = "-Acom.faratasystems.cdb.annotations.file";
	private Set<AnnotationTypeDeclaration>	annotationTypeDeclarations;
	private AnnotationProcessorEnvironment	annotationProcessorEnvironment;

	@Override
	public void process() {
		//System.out.println("process");
		HashSet<String> annotated = new HashSet<String>();
		Collection<TypeDeclaration> specifiedTypeDeclarations = annotationProcessorEnvironment.getTypeDeclarations();
		for (AnnotationTypeDeclaration annotationTypeDeclaration : annotationTypeDeclarations) {
			for (TypeDeclaration specifiedTypeDeclaration:specifiedTypeDeclarations) {
				try {
					Class<? extends Annotation> clazz = (Class<? extends Annotation>) getClass().getClassLoader().loadClass(annotationTypeDeclaration.getQualifiedName());
					if (specifiedTypeDeclaration.getAnnotation(clazz)!= null) {
						annotated.add(specifiedTypeDeclaration.toString() + ":" + clazz.getCanonicalName());
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		String sOut = "<annotated-types>\n";
		for (String annotatedType : annotated) {
			String[] annotatedTypes = annotatedType.split(":");
			sOut += "\t<annotated-type name=\"" + annotatedTypes[0] + "\" annotation=\"" + annotatedTypes[1] + "\"/>\n";
		}
		sOut += "</annotated-types>";

		String outputFile = getOption(OPTION_CDB_ANNOTATIONS_FILE);
		outputFile = CDBHQLAnnotationProcessor.resolveFile(outputFile);
		writeContent(sOut, outputFile);
		//writeContent("", outputFile + ".flag");
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
	public AnnotationProcessor getProcessorFor(	Set<AnnotationTypeDeclaration> annotationTypeDeclarations,
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