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

import clear.cdb.annotation.processors.JSAnnotationProcessor;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.List;

/**
 * TODO
 *
 * @author Viktor Gamov (viktor.gamov@faratasystems.com)
 * @since 1/2/14
 */
@SuppressWarnings("unused")
public class JSAnnotationProcessorSupport {

    public static final String PACKAGE = "clear/examle/dto/";

    protected void compileAndAssert(String javaFile, Diagnostic.Kind expectedKind, long expectedLineNumber) throws IOException {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClass(javaFile);
        DiagnosticsAssert.assertContainsSingleMessage(expectedKind, expectedLineNumber, diagnostics);
    }

    protected void compileAndAssertNoMessage(String configurationClass) throws IOException {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClass(configurationClass);
        DiagnosticsAssert.assertNoCompilerMessage(diagnostics);
    }

    protected List<Diagnostic<? extends JavaFileObject>> compileClass(String javaFile) throws IOException {
        return AnnotationProcessorTestCompiler.compileClass(PACKAGE + javaFile,
                new JSAnnotationProcessor());
    }

    protected List<Diagnostic<? extends JavaFileObject>> compileClasses(String[] javaFiles) throws IOException {
        return AnnotationProcessorTestCompiler.compileClasses(javaFiles,
                new JSAnnotationProcessor());
    }
}
