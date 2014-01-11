/*
 * Copyright (c) 2014 Farata Systems  http://www.faratasystems.com
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

package clear.cdb.support.test;


import junit.framework.AssertionFailedError;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.List;

/**
 * Set of assert methods used on the {@link Diagnostic}s emitted by the Java {@link Compiler}.
 *
 * @author Michael Pellaton
 */
public final class DiagnosticsAssert {

    /**
     * Avoid instantiation.
     */
    private DiagnosticsAssert() {
        throw new AssertionError("Not instantiable.");
    }


    /**
     * Asserts that the list of {@link Diagnostic}s passed is null or empty (meaning the compiler has not emitted any
     * messages).
     *
     * @param diagnostics the diagnostics to assert
     */
    public static void assertNoCompilerMessage(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        if (diagnostics != null && !diagnostics.isEmpty()) {
            throw new AssertionFailedError();
        }
    }

    /**
     * Asserts that the diagnostics passed contain exactly the single expected message on the expected line.
     *
     * @param expectedKind       the expected message
     * @param expectedLineNumber the expected line number
     * @param diagnostics        the diagnostics to assert
     */
    public static void assertContainsSingleMessage(Diagnostic.Kind expectedKind, long expectedLineNumber,
                                                   List<Diagnostic<? extends JavaFileObject>> diagnostics) {

        if (diagnostics.size() != 1) {
            throw new AssertionFailedError("Number of diagnostic messages expected <1> but was <" + diagnostics.size() + ">");
        }

        Diagnostic<? extends JavaFileObject> diagnostic = diagnostics.get(0);
        if (!(expectedKind == diagnostic.getKind() && expectedLineNumber == diagnostic.getLineNumber())) {
            throw new AssertionFailedError("Diagnostic message expected <" + expectedKind + ", on line " + expectedLineNumber
                    + "> but was <" + diagnostic.getKind() + " on line " + diagnostic.getLineNumber() + ">");
        }
    }

    public static void assertNoCompilerErrors(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        for (Diagnostic d : diagnostics)
            if (d.getKind().equals(Diagnostic.Kind.ERROR))
                throw new AssertionFailedError("Diagnostic message expected without errors but was ERROR: " + d.toString());
    }

}
