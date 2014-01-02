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

import org.springframework.core.io.ClassPathResource;

import javax.annotation.processing.Processor;
import javax.tools.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Utility class that compiles a Java class using the {@link Compiler} and an annotation {@link Processor}. This class
 * is intended to be used for tests of annotation processors.
 *
 * @author Michael Pellaton
 */
public class AnnotationProcessorTestCompiler {

    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();
    private static final Iterable<String> COMPILER_OPTIONS = Collections.singletonList("-proc:only");

    /**
     * Avoid instantiation.
     */
    private AnnotationProcessorTestCompiler() {
        throw new AssertionError("Not instantiable.");
    }

    /**
     * Processes the java class specified. This implementation only parses and processes the java classes and does not
     * fully compile them - i.e. it does not write class files back to the disk. Basically, {@code javac} is called with
     * {@code -proc:only}.
     *
     * @param processor        the annotation {@link javax.annotation.processing.Processor} to use during compilation
     * @param fileManager
     * @param compilationUnits
     * @return a list of {@link Diagnostic} messages emitted during the compilation
     */
    private static List<Diagnostic<? extends JavaFileObject>> compileClassInternal(Processor processor, StandardJavaFileManager fileManager, Iterable<? extends JavaFileObject> compilationUnits)
            throws IOException {

        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<JavaFileObject>();

        try {
            JavaCompiler.CompilationTask task = COMPILER.getTask(null, fileManager, collector, COMPILER_OPTIONS, null, compilationUnits);
            task.setProcessors(Arrays.asList(processor));
            task.call();

            return collector.getDiagnostics();
        } finally {
            if (fileManager != null) {
                fileManager.close();
            }
        }
    }

    public static List<Diagnostic<? extends JavaFileObject>> compileClass(String classToCompile, Processor processor)
            throws IOException {
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<JavaFileObject>();
        final StandardJavaFileManager fileManager = getFileManager(collector);
        return compileClassInternal(processor, fileManager, getCompilationUnitOfClass(fileManager, classToCompile));
    }

    public static List<Diagnostic<? extends JavaFileObject>> compileClasses(String[] classesToCompile, Processor processor) throws IOException {
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<JavaFileObject>();
        final StandardJavaFileManager fileManager = getFileManager(collector);
        return compileClassInternal(processor, fileManager, getCompilationUnitsOfClasses(fileManager, classesToCompile));
    }

    private static StandardJavaFileManager getFileManager(DiagnosticCollector<JavaFileObject> diagnosticCollector) {
        return COMPILER.getStandardFileManager(diagnosticCollector, Locale.getDefault(), null);
    }

    private static Iterable<? extends JavaFileObject> getCompilationUnitOfClass(StandardJavaFileManager fileManager,
                                                                                String classToCompile) throws IOException {
        ClassPathResource resource = new ClassPathResource(classToCompile + ".java");
        return fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(resource.getFile()));
    }

    private static Iterable<? extends JavaFileObject> getCompilationUnitsOfClasses(StandardJavaFileManager fileManager,
                                                                                   String[] classesToCompile) throws IOException {
        return fileManager.getJavaFileObjects(classesToCompile);
    }

}
