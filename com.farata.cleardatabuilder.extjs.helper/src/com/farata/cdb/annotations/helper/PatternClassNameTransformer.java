package com.farata.cdb.annotations.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternClassNameTransformer {
	final private Pattern pattern;
	final private String replacement;
	
	public PatternClassNameTransformer(final String arguments) {
		int separatorIdx = -1;
		if (arguments != null) {
			separatorIdx = arguments.indexOf(SEPARATOR);
		}
		if (separatorIdx >= 0) {
			replacement = arguments.substring(0, separatorIdx);
			final String patternText = arguments.substring(separatorIdx + SEPARATOR.length());
			if (patternText.length() > 0) {
				pattern = Pattern.compile(patternText);
			} else {
				pattern = IDENTITY_PATTERN;
			}
		} else {
			replacement = arguments == null ? "$1" : arguments + ".$1";
			pattern = IDENTITY_PATTERN;
		}
	}
	
	public PatternClassNameTransformer(final Pattern pattern, final String replacement) {
		this.pattern = pattern;
		this.replacement = replacement;
	}
	
	public String transform(final String originalName) {
		final Matcher matcher = pattern.matcher(originalName);
		if (matcher.matches()) {
			return matcher.replaceAll(replacement);
		} else {
			return originalName;
		}
	}
	
	
	final public static String SEPARATOR = "<<";
	final public static Pattern IDENTITY_PATTERN = Pattern.compile("^(.*)$");
	
	
	public static void main(String[] args) {
//		PatternClassNameTransformer tr = new PatternClassNameTransformer("uuii.model.$2_Model<<^com.farata.example.((\\w+\\.)*)dto.(\\S+)DTO$");
//		String res = tr.transform("com.farata.example.yyy.eee.dto.$AssociateDTO");
		PatternClassNameTransformer tr = new PatternClassNameTransformer("uuii.service.$1$3<<^com.farata.((\\w+\\.)*)(\\S+)$");
		String res = tr.transform("com.farata.example.yyy.eee.serv.AssociateService");

		System.out.println(res);
	}
}
