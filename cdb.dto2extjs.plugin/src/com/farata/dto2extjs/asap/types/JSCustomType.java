/**
 * Copyright (c) 2009 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
package com.farata.dto2extjs.asap.types;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSClassKind;
import com.farata.dto2extjs.asap.IClassNameTransformer;

import com.sun.mirror.declaration.EnumDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;

public class JSCustomType implements IJSType {
	final private String      _id;
	final private JSClassKind _classKind;
	final private boolean     _isEnum;
	final private IJSType    _contentType;
	
	public JSCustomType(final String id) {
		this(id, null);
	}
	
	public JSCustomType(final String id, final IJSType contentType) {
		_id          = id;
		_classKind   = null;
		_isEnum      = false;
		_contentType = contentType;
	}
	
	public JSCustomType(final TypeDeclaration declaration, final IClassNameTransformer classNameTransformer, final JSClassKind resolvedClassKind) {
		this(declaration, classNameTransformer, resolvedClassKind, null);
	}

	public JSCustomType(final TypeDeclaration declaration, final IClassNameTransformer classNameTransformer, final JSClassKind resolvedClassKind, final IJSType contentType) {
		final JSClass def = declaration.getAnnotation(JSClass.class);
		String qname = def.value();
		if (null == qname || qname.length() == 0) {
			qname = classNameTransformer.transform(declaration.getQualifiedName());
		}
		
		_id          = qname;
		_classKind   = resolvedClassKind;
		_isEnum      = declaration instanceof EnumDeclaration;
		_contentType = contentType;
	}
	
	public String id() { return _id; }
	public JSClassKind classKind() { return _classKind; }
	public boolean isContainer() { return null != _contentType; }
	public boolean isEnum() { return _isEnum; }
	public IJSType contentType() {
		if (null == _contentType) 
			throw new UnsupportedOperationException();
		return _contentType; 
	}
}
