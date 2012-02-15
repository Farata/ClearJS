package com.farata.dto2extjs.asap;

public interface IClassNameTransformer {
	public String transform(String originalName);
	
	final public static IClassNameTransformer NOP = new IClassNameTransformer() {
		public String transform(final String originalName) {
			return originalName;
		}
	}; 
}
