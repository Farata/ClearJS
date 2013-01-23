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

package com.softwarementors.extjs.djn.router.processor.poll;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.softwarementors.extjs.djn.StringUtils;
import com.softwarementors.extjs.djn.api.RegisteredMethod;
import com.softwarementors.extjs.djn.api.RegisteredPollMethod;
import com.softwarementors.extjs.djn.api.Registry;
import com.softwarementors.extjs.djn.config.GlobalConfiguration;
import com.softwarementors.extjs.djn.router.dispatcher.Dispatcher;
import com.softwarementors.extjs.djn.router.processor.RequestException;
import com.softwarementors.extjs.djn.router.processor.RequestProcessorBase;
import com.softwarementors.extjs.djn.router.processor.RequestProcessorUtils;
import com.softwarementors.extjs.djn.router.processor.ResponseData;

import edu.umd.cs.findbugs.annotations.NonNull;

public class PollRequestProcessor extends RequestProcessorBase {
  @NonNull
  private static Logger logger = Logger.getLogger(PollRequestProcessor.class);
  public static final String PATHINFO_POLL_PREFIX = "/poll/"; 
  
  private String eventName;
  private String requestString;
  private String resultString;
  
  public PollRequestProcessor(Registry registry, Dispatcher dispatcher, GlobalConfiguration globalConfiguration) {
    super(registry, dispatcher, globalConfiguration);
  }

  private static String getEventName(String pathInfo) {
    assert !StringUtils.isEmpty(pathInfo);
    
    return pathInfo.replace( PATHINFO_POLL_PREFIX, "");
  }
 
  /* ***************************************************
   * Customization support
   * ***************************************************/
  // TODO: move to base class as abstract method!
  protected Logger getLogger() {
    return logger;
  }

  // TODO: move to base class as abstract method!
  protected void logRequestEnterInfo() {
    getLogger().debug( "Request data (POLL)=>" + this.requestString + " Event name='" + this.eventName + "'");
  }

  // TODO: move to base class as abstract method!
  protected void logRequestExitInfo(Logger logger) {
    logger.debug( "ResponseData data (POLL)=>" + this.resultString );
  }

  // TODO: move to base class as abstract method!
  protected RegisteredMethod getMethod() {
    RegisteredPollMethod method = getRegistry().getPollMethod( this.eventName );
    if( method == null ) {
      RequestException ex = RequestException.forPollEventNotFound(this.eventName);
      logger.error( ex.getMessage(), ex );
      throw ex;
    }
    return method;
  }

  // TODO: move to base class as abstract method!
  protected Object[] getParameters() {
    return new Object[] {RequestProcessorUtils.getDecodedRequestParameters(this.requestString) };    
  }
  
  // TODO: move to base class as abstract method!
  protected ResponseData createSuccessResponse( Object result ) {
    PollSuccessResponseData r = new PollSuccessResponseData( this.eventName);
    r.setResult(result);
    return r;
  }
  
  // TODO: move to base class as abstract method!
  protected ResponseData createErrorResponse( Throwable exception, boolean debugOn ) {
    PollErrorResponseData result = new PollErrorResponseData(exception, debugOn);
    return result;
  }

  protected void logErrorResponse( Exception t) {
    assert t != null;
    
    getLogger().error( "(Controlled) server error: " + t.getMessage() + " for Poll Event '" + this.eventName + "'", t); 
  }
  
  
  private void logEnterInfo() {
    if( getLogger().isDebugEnabled()) {
      logRequestEnterInfo();
    }
  }
  
  private void logExitInfo() {
    if( getLogger().isDebugEnabled()) {
      logRequestExitInfo(logger);
    }
  }
  
  public void process(Reader reader, Writer writer, String pathInfo) throws IOException {
    assert !StringUtils.isEmpty(pathInfo);
  
    this.requestString = IOUtils.toString(reader);
    this.eventName = getEventName(pathInfo);
    
    logEnterInfo();
    
    ResponseData response;
    try {
      RegisteredMethod method = getMethod();
      Object[] parameters = getParameters();
      Object result = getDispatcher().dispatch(method, parameters);
      response = createSuccessResponse(result);
    }
    catch( Exception t ) {
      response = createErrorResponse( t, getDebug());
      
      logErrorResponse( t );
    }  
    StringBuilder result = new StringBuilder();
    appendIndividualResponseJsonString(response, result);
    
    this.resultString = result.toString();
    writer.write( this.resultString );

    logExitInfo();
  }
  
}
