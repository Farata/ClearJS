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

public enum TypeDeclarationKind {
	CLASS("class"), INTERFACE("interface"), ENUM("enum");
	
	final private String _id;
	
	private TypeDeclarationKind(final String kind) { 
		_id = kind; 
	}
	
	public String id() { return _id; }
}
