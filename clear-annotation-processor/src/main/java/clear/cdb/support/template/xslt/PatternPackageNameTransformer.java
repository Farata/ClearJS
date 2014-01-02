/*
 * Copyright (c) 2013 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 */

package clear.cdb.support.template.xslt;

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
