package com.engage.exception;

public class EmailAlreadyExistsException extends EngageException {
	
	public EmailAlreadyExistsException(String msg) {
		super("ERR018", msg);
	}
}
