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

public class JSPropertyDefinition implements IJSPropertyDefinition {
	final private Declaration              _origin;
	private String                   _label;
	private String                   _resource;
	private String                   _formatString;
	final private String                   _name;
	final private TypeMirror               _type;
	final private JSMethodDeclarationKind _declareGetter;
	final private JSMethodDeclarationKind _declareSetter;
	final private boolean                  _isAbstract;
	

	public JSPropertyDefinition(
			final Declaration origin,
			final String      name, 
			final TypeMirror  type
	) {
		this(origin, name,  type, JSMethodDeclarationKind.DECLARE, JSMethodDeclarationKind.DECLARE, false);
	}

	public JSPropertyDefinition(
			final Declaration              origin,
			final String                   name, 
			final TypeMirror               type,
			final JSMethodDeclarationKind declareGetter,
			final JSMethodDeclarationKind declareSetter,
			final boolean                  isAbstract) {
		_origin        = origin;
		_name          = name;
		_type          = type;
		_declareGetter = declareGetter;
		_declareSetter = declareSetter;
		_isAbstract    = isAbstract;
	}
	public void setMetadata(			final String      label, 
			final String      resource, 
			final String      formatString	) {
		_label = label;
		_resource = resource;
		_formatString = formatString;
	}
	public Declaration origin()                     { return _origin; }
	public String name()                            { return _name; }
	public String label()                           { return _label; }
	public String resource()                        { return _resource; }
	public String formatString()                    { return _formatString; }
	public TypeMirror type()                        { return _type; }
	public JSMethodDeclarationKind declareGetter() { return _declareGetter; }
	public JSMethodDeclarationKind declareSetter() { return _declareSetter; }
	public boolean isAbstract()                     { return _isAbstract; }
}
