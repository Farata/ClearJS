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

public interface IAS3Type {
	abstract public String id();
	abstract public FXClassKind classKind();
	abstract public boolean isContainer();
	abstract public boolean isEnum();
	abstract public IAS3Type contentType();
}
