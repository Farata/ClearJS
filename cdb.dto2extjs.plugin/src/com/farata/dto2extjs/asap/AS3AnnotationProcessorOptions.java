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

import org.eclipse.core.resources.ResourcesPlugin;

import com.farata.dto2extjs.annotations.FXClassKind;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Messager;

public class AS3AnnotationProcessorOptions {
	
	private static final String FLEX_WORKSPACE_LINK_TOKEN = "${DOCUMENTS}";
	
	final private AnnotationProcessorEnvironment _env;
	
	private File        _output;  
	private boolean     _reconcile = false;
	private boolean     _dumpMetadata = false;
	private boolean     _numberAsString = false;
	private FXClassKind _defaultClassKind = FXClassKind.DEFAULT;
	private FXClassKind _defaultEnumKind  = FXClassKind.DEFAULT;
	
	public AS3AnnotationProcessorOptions(final AnnotationProcessorEnvironment env) {
		_env = env;
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
			if (output.startsWith(FLEX_WORKSPACE_LINK_TOKEN)) {
				final File root = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
				// ${...}/directory
				final int indexOfCharAfterLinkToken = FLEX_WORKSPACE_LINK_TOKEN.length() + 1;
				// handle the case if pointed to workspace root
				if (output.length() > indexOfCharAfterLinkToken) {
					_output = new File(root, output.substring(indexOfCharAfterLinkToken));
				} else {
					_output = root;
				}
			} else {
				_output = new File(output);
				if (!_output.isAbsolute()) {
					File root = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
					_output = new File(root, output);
				}
			}
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
			if ( "remote".equalsIgnoreCase(defaultClassKind) )
				_defaultClassKind = FXClassKind.REMOTE;
			else if ( "managed".equalsIgnoreCase(defaultClassKind) )
				_defaultClassKind = FXClassKind.MANAGED;
			else {
				messager.printError(
					"Invalid value of default class kind option. " +
					"Please use remote/managed as " + DEFAULT_CLASS_KIND_PARAM + " value."
				);
				isValid = false;
			}
		}
		
		final String defaultEnumKind = options.get(DEFAULT_ENUM_KIND_PARAM);
		if (null != defaultEnumKind && defaultEnumKind.length() > 0) {
			if ( "remote".equalsIgnoreCase(defaultEnumKind) )
				_defaultEnumKind = FXClassKind.REMOTE;
			else if ( "string-constants".equalsIgnoreCase(defaultEnumKind) )
				_defaultEnumKind = FXClassKind.STRING_CONSTANTS;
			else {
				messager.printError(
					"Invalid value of default enum kind option. " +
					"Please use remote/constants as " + DEFAULT_ENUM_KIND_PARAM + " value."
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

		return isValid;
	}
	
	public File output() { return _output; }
	public boolean reconcile() { return _reconcile; }
	public boolean dumpMetadata() { return _dumpMetadata; }
	public boolean numberAsString() { return _numberAsString; }
	public FXClassKind defaultClassKind() { return _defaultClassKind; }
	public FXClassKind defaultEnumKind() { return _defaultEnumKind; }
	
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

	final public static Collection<String> SUPPORTED_OPTIONS 
		= Arrays.asList(
			OUTPUT_DIR_PARAM, 
			RECONCILE_PARAM, 
			DEFAULT_CLASS_KIND_PARAM, DEFAULT_ENUM_KIND_PARAM, 
			NUMBER_AS_STRING,
			MDDUMP_PARAM
		);	
}
