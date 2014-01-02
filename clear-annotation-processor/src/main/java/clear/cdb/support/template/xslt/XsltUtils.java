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

    public static void pushPackagePathResolver(final INameTransformer transformer) {
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
