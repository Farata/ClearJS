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

import com.farata.dto2extjs.annotations.FXClassKind;

public enum AS3BuiltinType implements IAS3Type {
	Void {
		@Override public String id() { return "void"; }
	},
	Int {
		@Override public String id() { return "int"; }
	}, 
	UInt {
		@Override public String id() { return "uint"; }
	},
	XML, Object, Number, String, Boolean, Date;
	public String id() { return name(); }
	public FXClassKind classKind() { return null; }
	public boolean isContainer() { return false; }
	public boolean isEnum() { return false; }
	public IAS3Type contentType() { throw new UnsupportedOperationException(); }
}
