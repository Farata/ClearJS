package com.farata.dto2extjs.asap;

import java.io.File;

public class XsltUtils {

	public static String fileExists(final String fileName) {
		return new File(fileName).exists() ? "yes" : "no";
	}
	
	public static String getterFor(final String property) {
		return "get" + capitalizedName(property);
	}
	
	public static String setterFor(final String property) {
		return "set" + capitalizedName(property);
	}
	
	private static String capitalizedName(final String property) {
		if (null == property || property.length() < 1)
			return property;
		
		final char[] chars = property.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}
	
}
