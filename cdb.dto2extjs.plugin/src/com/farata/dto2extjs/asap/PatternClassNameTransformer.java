package com.farata.dto2extjs.asap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternClassNameTransformer implements INameTransformer {
	final private Pattern pattern;
	final private String replacement;
	
	public PatternClassNameTransformer(final String arguments) {
		final int separatorIdx = arguments.indexOf(SEPARATOR);
		if (separatorIdx >= 0) {
			replacement = arguments.substring(0, separatorIdx);
			final String patternText = arguments.substring(separatorIdx + SEPARATOR.length());
			if (patternText.length() > 0) {
				pattern = Pattern.compile(patternText);
			} else {
				pattern = IDENTITY_PATTERN;
			}
		} else {
			replacement = arguments + ".$1";
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
}
