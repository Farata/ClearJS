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

package clear.cdb.annotation.processors;

/**
 * TODO
 *
 * @author Viktor Gamov (viktor.gamov@faratasystems.com)
 * @since 12/30/13
 */

import clear.cdb.support.test.CompilerTestCase;
import clear.cdb.support.test.SimpleVerifierCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Test case for the compile time verifier.
 *
 * @author Juan Alberto López Cavallotti
 */
@RunWith(Parameterized.class)
public class JSAnnotationProcessorTest {

    public static final String BUILD_CLASSES_OUT = "build/classes/test";

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        LinkedList<Object[]> ret = new LinkedList<Object[]>();

        ret.add(new Object[]{new SimpleVerifierCase()});

        return ret;
    }

    private static JavaCompiler compiler;
    private StandardJavaFileManager fileManager;
    private DiagnosticCollector<JavaFileObject> collector;
    private CompilerTestCase currentTestCase;

    public JSAnnotationProcessorTest(CompilerTestCase currentTestCase) {
        this.currentTestCase = currentTestCase;
    }

    @BeforeClass
    public static void initClass() throws Exception {

        //get the java compiler.
        compiler = ToolProvider.getSystemJavaCompiler();
    }

    @Before
    public void initTest() throws Exception {

        //configure the diagnostics collector.
        collector = new DiagnosticCollector<JavaFileObject>();
        fileManager = compiler.getStandardFileManager(collector, Locale.US, Charset.forName("UTF-8"));
        // use gradle folder to output classes
        File outDir = new File(BUILD_CLASSES_OUT);
        if (!outDir.exists())
            outDir.mkdirs();
        fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(outDir));
    }

    @Test
    public void testCompilation() throws Exception {
        //the files to be compiled.
        String[] files = currentTestCase.getClassesToCompile();
        try {
            //streams.
            ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
            OutputStreamWriter stdout = new OutputStreamWriter(stdoutStream);

            JavaCompiler.CompilationTask task = compiler.getTask(stdout, fileManager, collector, null, null, fileManager.getJavaFileObjects(files));

            Boolean result = task.call();

            String stdoutS = new String(stdoutStream.toByteArray());


            //perform the verifications.
            currentTestCase.test(collector.getDiagnostics(), stdoutS, result);
        } finally {
            //CompilerTestUtils.cleanClassFiles(0, files);
        }
    }
}


