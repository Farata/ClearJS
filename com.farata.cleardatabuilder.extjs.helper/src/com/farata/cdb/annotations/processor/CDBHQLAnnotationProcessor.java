package com.farata.cdb.annotations.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import clear.cdb.extjs.annotations.JSJPQLMethod;

import com.farata.cdb.annotations.helper.HQLCompiler;
import com.farata.cdb.annotations.helper.HQLHelper;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.Messager;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.MethodDeclaration;

public class CDBHQLAnnotationProcessor implements AnnotationProcessor,
		AnnotationProcessorFactory {

	public static final String OPTION_CDB_CONFIGURATION_FILE = "-Acom.faratasystems.cdb.configuration.file";
	public static final String OPTION_CDB_ENTITIES_CLASSPATH = "-Acom.faratasystems.cdb.entities.classpath";
	private AnnotationProcessorEnvironment _env;
	private HQLCompiler hqlCompiler;

	@Override
	public void process() {
		Messager messager = _env.getMessager();

		// obtain the declaration of the annotation we want to process
		AnnotationTypeDeclaration annoDecl = (AnnotationTypeDeclaration) _env
				.getTypeDeclaration(JSJPQLMethod.class.getName());

		// get the annotated types
		Collection<Declaration> annotatedTypes = _env
				.getDeclarationsAnnotatedWith(annoDecl);

		for (Declaration decl : annotatedTypes) {
			Collection<AnnotationMirror> mirrors = decl.getAnnotationMirrors();

			// for each annotation found, get a map of element name/value pairs
			for (AnnotationMirror mirror : mirrors) {
				if (!"JSJPQLMethod".equals(mirror.getAnnotationType()
						.getDeclaration().getSimpleName())) {
					continue;
				}
				Map<AnnotationTypeElementDeclaration, AnnotationValue> valueMap = mirror
						.getElementValues();
				Set<Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue>> valueSet = valueMap
						.entrySet();

				for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> annoKeyValue : valueSet) {
					if (annoKeyValue.getKey().getSimpleName().equals(
							"query")) {
						String query = (String) annoKeyValue.getValue().getValue();
						try {
							if (hqlCompiler == null) {
								Document doc = parseConfiguration();
								loadClasses();
								hqlCompiler = new HQLCompiler(doc);
							}
							hqlCompiler.compileQuery(query);
						} catch (Throwable e) {
							messager.printError(annoKeyValue.getValue().getPosition(), e.getMessage());
						}
						break;
					}
				}
			}
		}
	}

	private void loadClasses() throws MalformedURLException {
		String classpathOption = getOption(OPTION_CDB_ENTITIES_CLASSPATH);
		String[] cps = {};
		if (classpathOption != null) {
			cps = classpathOption.split(";");
		}
		ArrayList<String> cpEntries = new ArrayList<String>();
		for (String cp : cps) {
			if (cp != null) {
				String resolved = resolveFile(cp.trim());
				if (resolved != null) {
					cpEntries.add(resolved);
				}
				File f = new File(resolved);
				if (f.isDirectory()) {
					for (File ch : f.listFiles()) {
						if (ch.getName().endsWith(".jar")) {
							cpEntries.add(ch.getAbsolutePath());
						}
					}
				}
			}
		}
		loadClasspaths(cpEntries.toArray(new String[cpEntries.size()]));
	}

	@SuppressWarnings("deprecation")
	private void loadClasspaths(String cps[]) throws MalformedURLException {
		ArrayList<URL> urls = new ArrayList<URL>();
		for (String cp : cps) {
			URL url = new File(cp).toURL();
			urls.add(url);
		}
		if (urls.size() > 0) {
			URLClassLoader clazzLoader = new URLClassLoader(urls
					.toArray(new URL[urls.size()]), getClass().getClassLoader());
			Thread.currentThread().setContextClassLoader(clazzLoader);
		}
	}

	private Document parseConfiguration() throws FileNotFoundException,
			SAXException, IOException, ParserConfigurationException {
		String configurationFile = resolveFile(getOption(OPTION_CDB_CONFIGURATION_FILE));
		return HQLHelper.parseConfiguration(configurationFile);
	}

	public static String resolveFile(String option) {
		File _output = new File(option);
		if (!_output.isAbsolute()) {
			try {
				File root = org.eclipse.core.resources.ResourcesPlugin
						.getWorkspace().getRoot().getLocation().toFile();
				_output = new File(root, option);
			} catch (Throwable th) {
				// System.out.println("WARNING: " + th);
			}
		}
		return _output.getAbsolutePath();
	}

	protected String getOption(String name) {
		for (String option : _env.getOptions().keySet()) {
			if (option.contains(name)) {
				String[] ret = option.split("=");
				if (ret.length > 1) {
					return ret[1];
				} else {
					return _env.getOptions().get(option);
				}
			}
		}

		return null;
	}

	@Override
	public AnnotationProcessor getProcessorFor(
			Set<AnnotationTypeDeclaration> annotationTypeDeclarations,
			AnnotationProcessorEnvironment annotationProcessorEnvironment) {
		if (_env != annotationProcessorEnvironment) {
			_env = annotationProcessorEnvironment;
			hqlCompiler = null;
		}
		return this;
	}

	@Override
	public Collection<String> supportedAnnotationTypes() {
		ArrayList<String> result = new ArrayList<String>();
		result.add(JSJPQLMethod.class.getCanonicalName());
		return result;
	}

	@Override
	public Collection<String> supportedOptions() {
		return null;
	}

}
