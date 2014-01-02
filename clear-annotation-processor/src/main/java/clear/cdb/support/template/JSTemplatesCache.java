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
package clear.cdb.support.template;

import clear.cdb.support.template.xslt.XsltOperation;

import javax.xml.transform.Templates;

public class JSTemplatesCache {
    final private static TemplatesLoader CLASSIC_JS_CLASS
            = new TemplatesLoader("/xslt/js-classic-js-class.xslt");
    final private static TemplatesLoader EXT_JS__CLASS
            = new TemplatesLoader("/xslt/js-ext-js-class.xslt");
    final private static TemplatesLoader INTERFACE
            = new TemplatesLoader("/xslt/js-interface.xslt");
    final private static TemplatesLoader ENUM_OBJECTS
            = new TemplatesLoader("/xslt/js-enum-objects.xslt");
    final private static TemplatesLoader ENUM_STRINGS
            = new TemplatesLoader("/xslt/js-enum-strings.xslt");


    public static Templates jsClassicJSClass() {
        return CLASSIC_JS_CLASS.load();
    }

    public static Templates jsExtJSClass() {
        return EXT_JS__CLASS.load();
    }

    public static Templates jsInterface() {
        return INTERFACE.load();
    }

    public static Templates jsEnumObjects() {
        return ENUM_OBJECTS.load();
    }

    public static Templates jsEnumString() {
        return ENUM_STRINGS.load();
    }

    private static class TemplatesLoader {
        final private String templatesUri;
        private Templates templates;

        TemplatesLoader(final String templatesUri) {
            this.templatesUri = templatesUri;
        }

        synchronized Templates load() {
            if (null == templates)
                templates = XsltOperation.loadTemplates(templatesUri);
            return templates;
        }
    }
}

