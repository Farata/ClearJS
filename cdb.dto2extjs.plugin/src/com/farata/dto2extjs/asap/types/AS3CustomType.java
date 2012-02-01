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

import com.farata.dto2extjs.annotations.FXClass;
import com.farata.dto2extjs.annotations.FXClassKind;

import com.sun.mirror.declaration.EnumDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;

public class AS3CustomType implements IAS3Type {
	final private String      _id;
	final private FXClassKind _classKind;
	final private boolean     _isEnum;
	final private IAS3Type    _contentType;
	
	public AS3CustomType(final String id) {
		this(id, null);
	}
	
	public AS3CustomType(final String id, final IAS3Type contentType) {
		_id          = id;
		_classKind   = null;
		_isEnum      = false;
		_contentType = contentType;
	}
	
	public AS3CustomType(final TypeDeclaration declaration, final FXClassKind resolvedClassKind) {
		this(declaration, resolvedClassKind, null);
	}

	public AS3CustomType(final TypeDeclaration declaration, final FXClassKind resolvedClassKind, final IAS3Type contentType) {
		final FXClass def = declaration.getAnnotation(FXClass.class);
		String qname = def.value();
		if (null == qname || qname.length() == 0)
			qname = declaration.getQualifiedName();
		
		_id          = qname;
		_classKind   = resolvedClassKind;
		_isEnum      = declaration instanceof EnumDeclaration;
		_contentType = contentType;
	}
	
	public String id() { return _id; }
	public FXClassKind classKind() { return _classKind; }
	public boolean isContainer() { return null != _contentType; }
	public boolean isEnum() { return _isEnum; }
	public IAS3Type contentType() {
		if (null == _contentType) 
			throw new UnsupportedOperationException();
		return _contentType; 
	}
}
