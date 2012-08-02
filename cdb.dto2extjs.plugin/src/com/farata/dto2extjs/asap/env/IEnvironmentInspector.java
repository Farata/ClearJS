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

import java.io.File;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;

public interface IEnvironmentInspector {
	abstract public boolean isReconciliation(final AnnotationProcessorEnvironment env);
	abstract public File resolveOutputFolder(final String outputParameter);
	abstract public void refreshFile(final File generatedFile);
}
