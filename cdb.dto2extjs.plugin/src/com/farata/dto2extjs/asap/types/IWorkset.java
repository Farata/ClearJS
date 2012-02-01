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

import com.sun.mirror.declaration.TypeDeclaration;

public interface IWorkset {
	public abstract boolean enlist(final String qualifiedTypeName);
	public abstract boolean enlist(final TypeDeclaration decl);

}