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

public class InvalidJavaTypeException extends IllegalArgumentException {

	public InvalidJavaTypeException() {}
	public InvalidJavaTypeException(final String message) { super(message); }
	public InvalidJavaTypeException(final Throwable cause) { super(cause); }
	public InvalidJavaTypeException(final String message, final Throwable cause) { super(message, cause); }

	final private static long serialVersionUID = 8299003665246087060L;
	
}
