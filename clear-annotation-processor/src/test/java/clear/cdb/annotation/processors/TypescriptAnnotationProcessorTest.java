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

package clear.cdb.annotation.processors;

import clear.cdb.support.template.FreemarkerUtil;
import clear.cdb.support.test.DiagnosticsAssert;
import clear.cdb.support.test.JSAnnotationProcessorSupport;
import org.junit.Test;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.List;

public class TypescriptAnnotationProcessorTest extends JSAnnotationProcessorSupport {

    public String[] getClassesToCompile() {
        return new String[]{
                TypescriptAnnotationProcessorTest.class.getResource("/dto/CompanyDTO.java").getPath()
        };
    }

    @Override
    protected String getCompilerOptions() {
        return "-AdtoGeneratedPath=/tmp/1";
    }

    /**
     * Tests the processor with a perfectly valid pojo class.
     */
    @Test
    public void validConfigurationClassWithoutErrors() throws IOException {
        final List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(getClassesToCompile());
        System.out.print("Diagnostics: " + diagnostics.toString());
        DiagnosticsAssert.assertNoCompilerErrors(diagnostics);
    }
}
