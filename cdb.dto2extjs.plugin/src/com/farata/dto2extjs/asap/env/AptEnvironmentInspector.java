/**
 * Copyright (c) 2009 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
package com.farata.dto2extjs.asap.env;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;

public class AptEnvironmentInspector implements IEnvironmentInspector {
	public boolean isReconciliation(final AnnotationProcessorEnvironment env) {
		return false;
	}
	
	final public static IEnvironmentInspector INSTANCE = new AptEnvironmentInspector();	
}
