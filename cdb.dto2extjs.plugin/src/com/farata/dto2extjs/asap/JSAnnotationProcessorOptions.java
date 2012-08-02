/**
 * Copyright (c) 2009 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
package com.farata.dto2extjs.asap;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.farata.dto2extjs.annotations.JSClassKind;
import com.farata.dto2extjs.asap.env.IEnvironmentInspector;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Messager;

public class JSAnnotationProcessorOptions {
	
	final private AnnotationProcessorEnvironment _env;
	final private IEnvironmentInspector _inspector;
	
	private File             _output;  
	private boolean          _reconcile = false;
	private boolean          _dumpMetadata = false;
	private boolean          _numberAsString = false;
	private JSClassKind      _defaultClassKind = JSClassKind.DEFAULT;
	private JSClassKind      _defaultEnumKind  = JSClassKind.STRING_CONSTANTS;
	private INameTransformer _classNameTransformer = INameTransformer.NOP;
	private INameTransformer _packagePathTransformer = INameTransformer.NOP;	
	
	public JSAnnotationProcessorOptions(final AnnotationProcessorEnvironment env, final IEnvironmentInspector inspector) {
		_env = env;
		_inspector = inspector;
	}
	
	public boolean parse() {
		boolean isValid = true;
		
		final Map<String, String> options = antFix( _env.getOptions() );
		final Messager messager = _env.getMessager();
		
		final String reconsiliationOption = options.get(RECONCILE_PARAM);
		if (null != reconsiliationOption) {
			if ("yes".equalsIgnoreCase(reconsiliationOption) || "true".equalsIgnoreCase(reconsiliationOption) )
				_reconcile = true;
			else if ("no".equalsIgnoreCase(reconsiliationOption) || "false".equalsIgnoreCase(reconsiliationOption))
				_reconcile = false;
			else {
				messager.printError(
					"Invalid value of reconsilation option. " +
					"Please use true/false/yes/no as " + RECONCILE_PARAM + " value."
				);
				isValid = false;
			}
		}
		
		final String output = options.get(OUTPUT_DIR_PARAM);
		if (null == output) {
			messager.printError(
				"Output directory is not specified. " +
				"Please use " + OUTPUT_DIR_PARAM + " option of APT " +
				"define full or workspace relative path to output directory."
			);
			isValid = false;
		} else {
			_output = _inspector.resolveOutputFolder(output);
			try {
				if (_output.exists() ) {
					if ( !_output.isDirectory() ) {
						messager.printError(
							"Path specified via " + OUTPUT_DIR_PARAM + " option " +
							"is a file rather then directory (" + _output.getCanonicalPath() + ")."
						);
						isValid = false;
					}
				} else {
					isValid = _output.mkdirs();
					if (!isValid)
						messager.printError(
								"Unable to create directory defined via " + OUTPUT_DIR_PARAM + " option " +
								"(" + _output.getCanonicalPath() + ")."
							);
				}
			} catch (final IOException ex) {
				messager.printError( ex.getLocalizedMessage() );
			}
		}
		
		final String defaultClassKind = options.get(DEFAULT_CLASS_KIND_PARAM);
		if (null != defaultClassKind && defaultClassKind.length() > 0) {
			if ( "ext-js".equalsIgnoreCase(defaultClassKind) )
				_defaultClassKind = JSClassKind.EXT_JS;
			else if ( "classic".equalsIgnoreCase(defaultClassKind) )
				_defaultClassKind = JSClassKind.CLASSIC;
			else {
				messager.printError(
					"Invalid value of default class kind option. " +
					"Please use ext-js/classic as " + DEFAULT_CLASS_KIND_PARAM + " value."
				);
				isValid = false;
			}
		}
		
		final String defaultEnumKind = options.get(DEFAULT_ENUM_KIND_PARAM);
		if (null != defaultEnumKind && defaultEnumKind.length() > 0) {
			if ( "ext-js".equalsIgnoreCase(defaultEnumKind) )
				_defaultEnumKind = JSClassKind.EXT_JS;
			else if ( "string-constants".equalsIgnoreCase(defaultEnumKind) )
				_defaultEnumKind = JSClassKind.STRING_CONSTANTS;
			else {
				messager.printError(
					"Invalid value of default enum kind option. " +
					"Please use ext-js/string-constants as " + DEFAULT_ENUM_KIND_PARAM + " value."
				);
				isValid = false;
			}
		}		
		
		final String dumpMetadata = options.get(MDDUMP_PARAM);
		if (null != dumpMetadata) {
			if ("yes".equalsIgnoreCase(dumpMetadata) || "true".equalsIgnoreCase(dumpMetadata))
				_dumpMetadata = true;
			else if ("no".equalsIgnoreCase(dumpMetadata) || "false".equalsIgnoreCase(dumpMetadata))
				_dumpMetadata = false;
			else {
				messager.printError(
					"Invalid value of metadata dump option. " +
					"Please use true/false/yes/no as " + MDDUMP_PARAM + " value."
				);
				isValid = false;
			}			
		}
		
		final String numberAsString = options.get(NUMBER_AS_STRING);
		if (null != numberAsString) {
			if ("yes".equalsIgnoreCase(numberAsString) || "true".equalsIgnoreCase(numberAsString))
				_numberAsString = true;
			else if ("no".equalsIgnoreCase(numberAsString) || "false".equalsIgnoreCase(numberAsString))
				_numberAsString = false;
			else {
				messager.printError(
					"Invalid value of number as string option. " +
					"Please use true/false/yes/no as " + NUMBER_AS_STRING + " value."
				);
				isValid = false;
			}			
		}
		
		final String classNameTransformer = options.get(CLASS_NAME_TRANSFORMER);
		if (null != classNameTransformer && classNameTransformer.trim().length() > 0) {
			_classNameTransformer = new PatternClassNameTransformer(classNameTransformer);
		}
		
		final String packagePathTransformer = options.get(PACKAGE_PATH_TRANSFORMER);
		if (null != packagePathTransformer && packagePathTransformer.trim().length() > 0) {
			_packagePathTransformer = new PatternPackageNameTransformer(packagePathTransformer);
		}		

		return isValid;
	}
	
	public File output() { return _output; }
	public boolean reconcile() { return _reconcile; }
	public boolean dumpMetadata() { return _dumpMetadata; }
	public boolean numberAsString() { return _numberAsString; }
	public JSClassKind defaultClassKind() { return _defaultClassKind; }
	public JSClassKind defaultEnumKind() { return _defaultEnumKind; }
	public INameTransformer classNameTransformer() { return _classNameTransformer; }
	public INameTransformer packagePathTransformer() { return _packagePathTransformer; }
	
	private static Map<String,String> antFix(final Map<String,String> options) {
		final Map<String, String> result = new HashMap<String, String>( options.size() );
		for (final Map.Entry<String, String> e : options.entrySet()) {
			final String key = e.getKey();
			final String value = e.getValue();
			if ( null == value ) {
				final int pos = key.indexOf('=');
				if (pos > 0 && pos < key.length() - 1) {
					final String realKey = key.substring(0, pos);
					final String realValue = key.substring(pos + 1);
					result.put(realKey, realValue);
				}
				else
					result.put(key, value);
			} else
				result.put( key, value );
		}
		return result;
	}
	
	final public static String OUTPUT_DIR_PARAM   
		= "-Acom.faratasystems.dto2extjs.output";
	
	final public static String RECONCILE_PARAM       
		= "-Acom.faratasystems.dto2extjs.reconcile";
	
	final public static String DEFAULT_CLASS_KIND_PARAM 
		= "-Acom.faratasystems.dto2extjs.default-class-kind";
	
	final public static String DEFAULT_ENUM_KIND_PARAM 
		= "-Acom.faratasystems.dto2extjs.default-enum-kind";
	
	final public static String MDDUMP_PARAM
		= "-Acom.faratasystems.dto2extjs.md_dump";

	final public static String NUMBER_AS_STRING
		= "-Acom.faratasystems.dto2extjs.number-as-string";
	
	final public static String CLASS_NAME_TRANSFORMER
		= "-Acom.faratasystems.dto2extjs.class-name-transformer";
	
	final public static String PACKAGE_PATH_TRANSFORMER
		= "-Acom.faratasystems.dto2extjs.package-path-transformer";	

	final public static Collection<String> SUPPORTED_OPTIONS 
		= Arrays.asList(
			OUTPUT_DIR_PARAM, 
			RECONCILE_PARAM, 
			DEFAULT_CLASS_KIND_PARAM, DEFAULT_ENUM_KIND_PARAM, 
			NUMBER_AS_STRING,
			CLASS_NAME_TRANSFORMER,
			PACKAGE_PATH_TRANSFORMER,
			MDDUMP_PARAM
		);	
}
