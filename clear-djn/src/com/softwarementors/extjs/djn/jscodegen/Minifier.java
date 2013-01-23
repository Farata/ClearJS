/*
 * Copyright © 2008, 2012 Pedro Agulló Soliveres.
 * 
 * This file is part of DirectJNgine.
 *
 * DirectJNgine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 * 
 * Commercial use is permitted to the extent that the code/component(s)
 * do NOT become part of another Open Source or Commercially developed
 * licensed development library or toolkit without explicit permission.
 *
 * DirectJNgine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DirectJNgine.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * This software uses the ExtJs library (http://extjs.com), which is 
 * distributed under the GPL v3 license (see http://extjs.com/license).
 */

package com.softwarementors.extjs.djn.jscodegen;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.softwarementors.extjs.djn.StringUtils;
import com.softwarementors.extjs.djn.Timer;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

public class Minifier {

  @NonNull
  private static final Logger logger = Logger.getLogger( Minifier.class);

  private Minifier() {
    // Avoid instantation
  }

  public static String getMinifiedFileName(String file) {
    assert !StringUtils.isEmpty(file);
    
    String result = file.replace( ".js", "-min.js" );
    return result;
  }

  @CheckForNull public static final String minify( String input, String inputFilename, int debugCodeLength ) {
    assert input != null;
    assert !StringUtils.isEmpty(inputFilename);
    assert debugCodeLength > 0;

    try {
      Timer timer = new Timer();      
      // logger.debug( "Starting minification for '" + inputFilename + "'...");
      Reader in = new StringReader( input );
      JavaScriptCompressor compressor = new JavaScriptCompressor(in, new ErrorReporter() {
        public void warning(String message, String sourceName,
            int line, String lineSource, int lineOffset) {
          if (line < 0) {
            logger.warn("Minifier Warning: " + message);
          } else {
            logger.warn("Minifier Warning, " + line + ':' + lineOffset + ':' + message);
          }
        }

        public void error(String message, String sourceName,
            int line, String lineSource, int lineOffset) {
          if (line < 0) {
            logger.warn("Minifier Error: " + message);
          } else {
            logger.warn("Minifier Error, " + line + ':' + lineOffset + ':' + message);
          }
        }

        public EvaluatorException runtimeError(String message, String sourceName,
            int line, String lineSource, int lineOffset) {
          error(message, sourceName, line, lineSource, lineOffset);
          return new EvaluatorException(message);
        }
      });

      // Close the input stream first, and then open the output stream,
      // in case the output file should override the input file.
      in.close();

      try {
        Writer out = new StringWriter();
        boolean munge = true;
        boolean preserveAllSemiColons = false;
        boolean disableOptimizations = false;
        boolean verbose = false;
        int linebreakpos = 0;

        compressor.compress(out, linebreakpos, munge, verbose,
            preserveAllSemiColons, disableOptimizations);
        out.close();
        String result = out.toString();
        if( logger.isDebugEnabled() ) {
          timer.stop();
          int compressionPercentage = 100 - (result.length() * 100 / debugCodeLength);
          timer.logDebugTimeInMilliseconds( "Finished minification for '" + inputFilename + "'. Debug code length: " + debugCodeLength + ", Minified length: " + result.length() + ", Compression: " + compressionPercentage + "%. Time");
        }
        return result;
      }
      catch (UnsupportedEncodingException e) {
        logger.warn( "Unable to minify '" + inputFilename + "'.", e );
        return null;
      }
    }
    catch( IOException e) {
      logger.warn( "Unable to minify '" + inputFilename + "' due to an IOException.", e );
      return null;
    }
    catch (EvaluatorException e) {
      logger.warn( "Unable to minify '" + inputFilename + "' due to a problem with the Javascript evaluator.", e );
      return null;
    }
  }

}
