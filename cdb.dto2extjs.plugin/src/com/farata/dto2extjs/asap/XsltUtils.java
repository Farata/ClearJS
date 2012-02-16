package com.farata.dto2extjs.asap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	
	public static String resolvePackagePath(final String originalPackagePath) {
		return currentPackagePathResolver().transform(originalPackagePath);
	}
	
	private static String capitalizedName(final String property) {
		if (null == property || property.length() < 1)
			return property;
		
		final char[] chars = property.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}
	
	
	private static INameTransformer currentPackagePathResolver() {
		final List<INameTransformer> stack = PACKAGE_PATH_RESOLVERS_STACK.get();
		if (stack.isEmpty())
			return null;
		else
			return stack.get(stack.size() - 1);
	}
	
	protected static void pushPackagePathResolver(final INameTransformer transformer) {
		final List<INameTransformer> stack = PACKAGE_PATH_RESOLVERS_STACK.get();
		stack.add(transformer);
	}
	
	protected static INameTransformer popPackagePathResolver() {
		final List<INameTransformer> stack = PACKAGE_PATH_RESOLVERS_STACK.get();
		return stack.remove(stack.size() - 1);
	}
	
	final private static ThreadLocal<List<INameTransformer>> PACKAGE_PATH_RESOLVERS_STACK = new ThreadLocal<List<INameTransformer>>() {
		@Override
		public List<INameTransformer> initialValue() {
			return new ArrayList<INameTransformer>();
		}
	};
}
