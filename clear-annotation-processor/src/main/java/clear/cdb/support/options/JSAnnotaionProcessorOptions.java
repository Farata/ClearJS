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

package clear.cdb.support.options;

import java.util.Arrays;
import java.util.Collection;

/**
 * TODO
 *
 * @author Viktor Gamov (viktor.gamov@faratasystems.com)
 * @since 12/31/13
 */
public class JSAnnotaionProcessorOptions {
    final public static String OUTPUT_DIR_PARAM
            = "-Aclear.dto2extjs.output";

    final public static String RECONCILE_PARAM
            = "-Aclear.dto2extjs.reconcile";

    final public static String DEFAULT_CLASS_KIND_PARAM
            = "-Aclear.dto2extjs.default-class-kind";

    final public static String DEFAULT_ENUM_KIND_PARAM
            = "-Aclear.dto2extjs.default-enum-kind";

    final public static String MDDUMP_PARAM
            = "-Aclear.dto2extjs.md_dump";

    final public static String NUMBER_AS_STRING
            = "-Aclear.dto2extjs.number-as-string";

    final public static String CLASS_NAME_TRANSFORMER
            = "-Aclear.dto2extjs.class-name-transformer";

    final public static String PACKAGE_PATH_TRANSFORMER
            = "-Aclear.dto2extjs.package-path-transformer";

    final public static Collection<String> SUPPORTED_OPTIONS
            = Arrays.asList(
            OUTPUT_DIR_PARAM,
            RECONCILE_PARAM,
            DEFAULT_CLASS_KIND_PARAM, DEFAULT_ENUM_KIND_PARAM,
            NUMBER_AS_STRING,
            CLASS_NAME_TRANSFORMER,
            PACKAGE_PATH_TRANSFORMER,
            MDDUMP_PARAM
    );
}
