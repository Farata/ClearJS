package com.engage.exception;

public class NoResultsFoundException extends EngageException {
	
	public NoResultsFoundException(String msg) {
		super("ERR011", msg);
	}
}
