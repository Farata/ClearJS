package com.farata.dto2extjs.asap;

public interface INameTransformer {
	public String transform(String originalName);
	
	final public static INameTransformer NOP = new INameTransformer() {
		public String transform(final String originalName) {
			return originalName;
		}
	}; 
}
