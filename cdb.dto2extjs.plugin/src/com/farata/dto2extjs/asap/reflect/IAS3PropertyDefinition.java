/**
 * Copyright (c) 2009 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
package com.farata.dto2extjs.asap.reflect;

import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.type.TypeMirror;

public interface IAS3PropertyDefinition {
	abstract public Declaration origin();	
	abstract public String name();
	abstract public String label();
	abstract public String resource();
	abstract public String formatString();
	abstract public TypeMirror type();
	abstract public AS3MethodDeclarationKind declareGetter();
	abstract public AS3MethodDeclarationKind declareSetter();
	abstract public boolean isAbstract();
	abstract public void setMetadata(	final String      label, 	final String      resource, 	final String      formatString	);
}
