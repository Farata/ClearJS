package com.farata.cdb.annotations.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternPackageNameTransformer extends PatternClassNameTransformer {
	public PatternPackageNameTransformer(final String arguments) {
		super(normalizeArguments(arguments));
	}
	
	public PatternPackageNameTransformer(final Pattern pattern, final String replacement) {
		super(pattern, replacement);
	}
	
	public static String normalizeArguments(final String arguments) {
		final Matcher matcher = null == arguments || arguments.length() == 0 ? null : SIMPLE_PACKAGE_RENAME_PATTERN.matcher(arguments);
		if (null != matcher && matcher.matches()) {
			return matcher.replaceAll("$4\\$1<<^$1(\\\\.?.*)\\$");
		} else {
			return arguments;
		}
	}
	
	final private static Pattern SIMPLE_PACKAGE_RENAME_PATTERN = Pattern.compile("^((\\w+\\.)*(\\w+))\\:((\\w+\\.)*(\\w+))$");
	
	protected static class Arguments {
		final public Pattern pattern;
		final public String replacement;
		
		public Arguments(final Pattern pattern, final String replacement) {
			this.pattern = pattern;
			this.replacement = replacement;
		}
	}
	
}
