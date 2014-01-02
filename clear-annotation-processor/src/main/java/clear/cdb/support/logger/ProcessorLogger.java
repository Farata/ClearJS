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

package clear.cdb.support.logger;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * TODO
 *
 * @author Viktor Gamov (viktor.gamov@faratasystems.com)
 * @since 12/31/13
 */
public class ProcessorLogger implements Logger {

    private final Messager messager;

    private final StringBuffer logContent = new StringBuffer();

    public ProcessorLogger(Messager messager) {
        if (messager == null) {
            throw new NullPointerException("messager");
        }
        this.messager = messager;

        // TODO print initial report
        //note(LogLocation.MESSAGER, options.report());
        /*for (String warning : options.getWarnings()) {
            warning(LogLocation.BOTH, warning);
        }*/
    }

    @Override
    public void note(LogLocation location, String message) {
        if (location == null) {
            throw new NullPointerException("location");
        }
        if (message == null) {
            throw new NullPointerException("message");
        }
        // TODO implement options
        if (/*options.verbose() && */location.toMessager()) {
            messager.printMessage(Diagnostic.Kind.NOTE, message);
        }
        // TODO implement file logger
        if (/*options.logging() &&*/ location.toLogFile()) {
            logContent.append(message).append("\n");
        }
    }

    @Override
    public void warning(LogLocation location, String message) {
        if (location == null) {
            throw new NullPointerException("location");
        }
        if (message == null) {
            throw new NullPointerException("message");
        }

        if (location.toMessager()) {
            messager.printMessage(Diagnostic.Kind.WARNING, message);
        }

        if (/*options.logging() && */location.toLogFile()) {
            logContent.append("warning: ").append(message).append("\n");
        }
    }

    @Override
    public String getFileContent() {
        return logContent.toString();
    }

    public static String exceptionToString(Exception exception) {
        if (exception == null) {
            throw new NullPointerException("exception");
        }

        StringWriter out = new StringWriter();
        exception.printStackTrace(new PrintWriter(out));
        return out.toString();
    }
}
