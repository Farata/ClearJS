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

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

public abstract class XsltOperation {

    final protected Templates templates;

    protected XsltOperation(final String templatesUri) {
        this(loadTemplates(templatesUri));
    }

    protected XsltOperation(final Templates templates) {
        this.templates = templates;
    }

    public static Templates loadTemplates(final Class<?> clazz, final String uri) {
        final Package pkg = clazz.getPackage();
        final String path = pkg == null || pkg.getName().length() == 0 ? uri : pkg.getName()
                .replace(".", "/")
                + "/" + uri;

        return loadTemplates(path);
    }

    public static Templates loadTemplates(final String uri) {
        final URL templateUrl = XsltOperation.class.getResource(uri);
        return withCurrentClassLoader(new Callable<Templates>() {
            public Templates call() throws Exception {
                BASE_URL.set(templateUrl);
                try {
                    return TRANSFORMER_FACTORY.newTemplates(new StreamSource(templateUrl.openStream(), templateUrl.toExternalForm()));
                } finally {
                    BASE_URL.set(null);
                }
            }
        });
    }

    public static <T> T withCurrentClassLoader(final Callable<T> block) {
        return withCurrentClassLoader(block, XsltOperation.class.getClassLoader());
    }

    public static void withCurrentClassLoader(final Runnable block) {
        withCurrentClassLoader(block, XsltOperation.class.getClassLoader());
    }


    public static void withCurrentClassLoader(final Runnable block, final ClassLoader classLoader) {
        withCurrentClassLoader(new Callable<Void>() {
            public Void call() {
                block.run();
                return null;
            }
        }, classLoader);
    }

    public static <T> T withCurrentClassLoader(final Callable<T> block, final ClassLoader classLoader) {
        //Try to execute without calling of setContextClassLoader
        try {
            return block.call();
        } catch (Exception e) {
        }
        final Thread currentThread = Thread.currentThread();
        final ClassLoader currentContextClassLoader = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(classLoader);
        try {
            return block.call();
        } catch (final Error ex) {
            throw ex;
        } catch (final RuntimeException ex) {
            throw ex;
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            currentThread.setContextClassLoader(currentContextClassLoader);
        }
    }

    static String composeClassName(final String mainClassName, final String targetClassName) {
        final int lastDot = mainClassName.lastIndexOf('.');
        if (lastDot > 0)
            return mainClassName.substring(0, lastDot + 1) + targetClassName;
        else
            return targetClassName;
    }

    final private static ThreadLocal<URL> BASE_URL = new ThreadLocal<URL>();
    final protected static TransformerFactory TRANSFORMER_FACTORY;

    static {
//		TRANSFORMER_FACTORY = TransformerFactory.newInstance("org.apache.xalan.xsltc.trax.TransformerFactoryImpl", XsltOperation.class.getClassLoader());
//		TRANSFORMER_FACTORY = TransformerFactory.newInstance("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", XsltOperation.class.getClassLoader());
        TRANSFORMER_FACTORY = TransformerFactory.newInstance();
        TRANSFORMER_FACTORY.setURIResolver(new URIResolver() {
            public Source resolve(final String href, final String base) throws TransformerException {
                try {
                    final URL realBase = BASE_URL.get();
                    if (null == realBase) {
                        final String message = "Coding error, thread local for base url not set";
                        System.err.println(message);
                        throw new IllegalStateException(message);
                    }
                    final URL related = new URL(realBase, href);
                    return new StreamSource(related.openStream(), related.toExternalForm());
                } catch (final IOException ex) {
                    throw new TransformerException(ex);
                }
            }
        });
    }
}

