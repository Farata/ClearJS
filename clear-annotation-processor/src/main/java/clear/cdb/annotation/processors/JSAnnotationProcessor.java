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

import clear.cdb.support.logger.LogLocation;
import clear.cdb.support.logger.Logger;
import clear.cdb.support.logger.ProcessorLogger;
import clear.cdb.support.template.JSTemplatesCache;
import clear.cdb.support.template.xslt.XsltOperation;
import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSClassKind;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import java.io.ByteArrayInputStream;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO
 *
 * @author Viktor Gamov (viktor.gamov@faratasystems.com)
 * @since 12/30/13
 */
@SupportedAnnotationTypes("com.farata.dto2extjs.annotations.*")
//@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class JSAnnotationProcessor extends AbstractProcessor {
    public static final String JS_CLASS_ANNOTATION_TYPE = "com.farata.dto2extjs.annotations.JSClass";

    protected static final Pattern RELEASE_PATTERN = Pattern.compile("^RELEASE_(\\d+)$");
    private static final int MAX_SUPPORTED_VERSION = 8;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        SourceVersion[] svs = SourceVersion.values();
        for (int i = svs.length - 1; i >= 0; i--) {
            String name = svs[i].name();
            Matcher m = RELEASE_PATTERN.matcher(name);
            if (m.matches()) {
                int release = Integer.parseInt(m.group(1));
                if (release <= MAX_SUPPORTED_VERSION) return svs[i];
            }
        }

        return SourceVersion.RELEASE_6;
    }

    Logger logger;

    /**
     * Initializes the processor with the processing environment by
     * setting the {@code processingEnv} field to the value of the
     * {@code processingEnv} argument.  An {@code
     * IllegalStateException} will be thrown if this method is called
     * more than once on the same object.
     *
     * @param environment environment to access facilities the tool framework
     *                    provides to the processor
     * @throws IllegalStateException if this method is called more than once.
     */
    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        super.init(environment);

        try {
            initialize();
        } catch (Exception e) {
            environment.getMessager().printMessage(Diagnostic.Kind.ERROR, ProcessorLogger.exceptionToString(e));
        }
    }

    protected void initialize() {
            /*options = new Options(ServiceProviderProcessor.NAME, processingEnv.getOptions());
            if (options.disabled()) {
                return;
            }*/
        //logger = new ProcessorLogger(processingEnv.getMessager(), options);
        logger = new ProcessorLogger(processingEnv.getMessager());

        //checkCompatibility();
    }

    /**
     * {@inheritDoc}
     *
     * @param annotations
     * @param roundEnv
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Messager messager = processingEnv.getMessager();


        for (TypeElement annotaion : annotations) {
            // process only JSCLass annotations
            if (JS_CLASS_ANNOTATION_TYPE.equals(annotaion.asType().toString())) {
                final Set<? extends Element> annotatedTypes = roundEnv.getElementsAnnotatedWith(JSClass.class);

                for (Element elem : annotatedTypes) {
                    Templates templates;
                    final JSClass jsClassAnnotation = elem.getAnnotation(JSClass.class);

                    //TODO check edge cases
                    /*if (jsClassAnnotation == null){
                        logger.warning(LogLocation.MESSAGER, "Bad annotate field: System error - annotation is null" );
                    }*/

                    final JSClassKind jsClassKind = jsClassAnnotation.kind();
                    final ElementKind elementKind = elem.getKind();

                    String message = "annotation found in " + elem.getSimpleName();

                    logger.note(LogLocation.MESSAGER, message);


                    switch (elementKind) {
                        case ENUM:
                            switch (jsClassKind) {
                                case EXT_JS:
                                    templates = JSTemplatesCache.jsEnumObjects();
                                    break;
                                case STRING_CONSTANTS:
                                    templates = JSTemplatesCache.jsEnumString();
                                    break;
                                default:
                            }
                        case CLASS:
                            switch (jsClassKind) {
                                case EXT_JS:
                                    templates = JSTemplatesCache.jsExtJSClass();
                                    break;
                                case CLASSIC:
                                    templates = JSTemplatesCache.jsClassicJSClass();
                                    break;
                                default:
                            }
                        case INTERFACE:
                            templates = JSTemplatesCache.jsInterface();
                            break;
                        default:
                            continue;
                    }
                    try {
                        // TODO custom parser for XSLT templates
                        final Source source = new SAXSource(new XMLFilterImpl(), noInput());
                        final Result result = noResult();
                        final Templates finalTemplates = templates;
                        XsltOperation.withCurrentClassLoader(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    final Transformer serializer = finalTemplates.newTransformer();
                                    serializer.setParameter("base", "/Users/apple/tmp");
                                    serializer.setParameter("metadata-dump", "yes");
                                    //serializer.transform(source, result);
                                } catch (TransformerConfigurationException e) {
                                    e.printStackTrace();
                                } catch (TransformerException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } catch (Exception e) {
                        logger.warning(LogLocation.MESSAGER, ProcessorLogger.exceptionToString(e));
                    }
                }
            }
        }

        return true; // no further processing of this annotation type
    }

    final private static byte[] VOID_BYTES = {};

    private static InputSource noInput() {
        return new InputSource(new ByteArrayInputStream(VOID_BYTES));
    }

    private static Result noResult() {
        return new SAXResult(new XMLFilterImpl());
    }
}
