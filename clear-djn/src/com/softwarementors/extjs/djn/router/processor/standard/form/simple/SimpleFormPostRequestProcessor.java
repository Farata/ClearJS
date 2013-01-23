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

package com.softwarementors.extjs.djn.router.processor.standard.form.simple;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.softwarementors.extjs.djn.api.Registry;
import com.softwarementors.extjs.djn.config.GlobalConfiguration;
import com.softwarementors.extjs.djn.router.dispatcher.Dispatcher;
import com.softwarementors.extjs.djn.router.processor.RequestProcessorUtils;
import com.softwarementors.extjs.djn.router.processor.standard.form.FormPostRequestProcessorBase;

import edu.umd.cs.findbugs.annotations.NonNull;

public class SimpleFormPostRequestProcessor extends FormPostRequestProcessorBase {

  @NonNull
  private static final Logger logger = Logger.getLogger(SimpleFormPostRequestProcessor.class);
  
  public SimpleFormPostRequestProcessor(Registry registry, Dispatcher dispatcher, GlobalConfiguration globalConfiguration) {
    super( registry, dispatcher, globalConfiguration);
  }

  public void process(Reader reader, Writer writer) throws IOException {
    String requestString = IOUtils.toString(reader);
    if( logger.isDebugEnabled() ) {
      logger.debug( "Request data (SIMPLE FORM)=>" + requestString );
    }
    Map<String, String> formParameters;
    formParameters = RequestProcessorUtils.getDecodedRequestParameters(requestString);    
    String result = process(formParameters, new HashMap<String,FileItem>());
    writer.write( result );
    if( logger.isDebugEnabled() ) {
      logger.debug( "ResponseData data (SIMPLE FORM)=>" + result );
    }
  }

}
