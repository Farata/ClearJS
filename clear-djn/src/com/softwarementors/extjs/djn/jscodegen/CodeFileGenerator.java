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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.softwarementors.extjs.djn.StringUtils;
import com.softwarementors.extjs.djn.api.RegisteredApi;
import com.softwarementors.extjs.djn.api.RegisteredCode;
import com.softwarementors.extjs.djn.api.Registry;
import com.softwarementors.extjs.djn.config.GlobalConfiguration;

import edu.umd.cs.findbugs.annotations.NonNull;

public class CodeFileGenerator {
  @NonNull private Registry registry;

  private GlobalConfiguration getGlobalConfiguration() {
    return this.registry.getGlobalConfiguration();
  }
  
  private CodeFileGenerator( Registry registry) {
    assert registry != null;
    
    this.registry = registry;
  }
  
  @NonNull
  private static final Logger logger = Logger.getLogger( CodeFileGenerator.class);
  
  public static void updateSource( Registry registry, boolean saveApiFiles ) throws IOException {
    assert registry != null;
    
    CodeFileGenerator generator = new CodeFileGenerator(registry);
    generator.generateCode(saveApiFiles);
  }

  private void generateCode(boolean saveApiFiles) throws IOException {
    boolean minify = getGlobalConfiguration().getMinify();
    boolean debug = getGlobalConfiguration().getDebug();
    
    // Generate code
    Map<String, RegisteredCode> registeredCodes = new HashMap<String,RegisteredCode>();
    for( RegisteredApi api : this.registry.getApis() ) {
      String apiFile = api.getApiFile();
      String fullApiFileName = api.getFullApiFileName();
      RegisteredCode code = registeredCodes.get( apiFile );
      if( code == null ) {
        code = new RegisteredCode( apiFile, fullApiFileName, minify, debug );
        registeredCodes.put( apiFile, code);
      }
      ApiCodeGenerator generator = new ApiCodeGenerator( getGlobalConfiguration(), api );
      generator.appendCode(code.getDebugCodeBuilder(), false);
      generator.appendCode(code.getNonCommentsCodeBuilder(), true);
    }

    // Return source-code pairs!
    for( Map.Entry<String, RegisteredCode> entry : registeredCodes.entrySet()) {
      String sourceName = entry.getKey();
      RegisteredCode code = entry.getValue();

      // Return source name-code pairs!
      String debugSourceName = getDebugFileName(sourceName);
      this.registry.addSource(sourceName, code.getCode());
      this.registry.addSource(debugSourceName, code.getDebugCode());
      if( minify ) {
        String minifiedSourceName = Minifier.getMinifiedFileName(sourceName);
        this.registry.addSource( minifiedSourceName, code.getMinifiedCode());
      }
      
      // Save files, if needed
      if( saveApiFiles ) {
        if( logger.isInfoEnabled()) {
          logger.info( "Creating source files for APIs...");
        }
        String defaultFileName = code.getFullApiFileName();  
        String debugFileName = getDebugFileName(defaultFileName);
        String minifiedFileName = Minifier.getMinifiedFileName(defaultFileName);
        updateFile( debugFileName, code.getDebugCode() );
        updateFile( defaultFileName, code.getCode() );
        if( minify ) {
          updateFile( minifiedFileName, code.getMinifiedCode() );
        }
        else {
          deleteFile( minifiedFileName );
        }
      }
      else {
        if( logger.isInfoEnabled()) {
          logger.info( "Source files for APIs have not been created: you will need to reference them dynamically");
        }
      }
    }
  }


  private static void deleteFile( String fullFileName ) throws IOException {
    assert !StringUtils.isEmpty(fullFileName);

    File file = new File( fullFileName );
    if( file.exists()) {
      if( !file.delete() ) {
        throw new IOException( "Unable to delete " + fullFileName);
      }
      if( logger.isDebugEnabled() ) {
        logger.debug( "Api file deleted: '" + file.getAbsolutePath() + "'");
      }
    }
  }
  
  private static void updateFile( String  fullFileName, String code ) throws IOException {
    assert !StringUtils.isEmpty(fullFileName);
    assert code != null;
    
    File file = new File( fullFileName );    
    if( !fileContentEquals(file, code) ) {
      FileUtils.writeStringToFile(file, code);
      if( logger.isDebugEnabled() ) {
        logger.debug( "Api file updated: '" + file.getAbsolutePath() + "'");
      }
    }
    else {
      if( logger.isDebugEnabled() ) {
        logger.debug( "Api file '" + file.getAbsolutePath() + '\'' + " is up to date: it was not rewritten.");
      }
    }
  }

  private static boolean fileContentEquals(File file, String code) throws IOException {
    assert file != null;
    assert code != null;
    
    if( file.exists()) {
      String contents = FileUtils.readFileToString(file);
      boolean result = contents.equals( code );
      return result;
    }
    return false;
  }
  
  private static String getDebugFileName( String file ) {
    String result = file.replace( ".js", "-debug.js");
    return result;
  }
}
