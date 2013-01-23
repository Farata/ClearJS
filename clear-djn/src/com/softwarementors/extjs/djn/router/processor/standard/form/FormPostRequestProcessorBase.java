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

package com.softwarementors.extjs.djn.router.processor.standard.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import com.softwarementors.extjs.djn.StringUtils;
import com.softwarementors.extjs.djn.api.Registry;
import com.softwarementors.extjs.djn.config.GlobalConfiguration;
import com.softwarementors.extjs.djn.router.dispatcher.Dispatcher;
import com.softwarementors.extjs.djn.router.processor.RequestException;
import com.softwarementors.extjs.djn.router.processor.ResponseData;
import com.softwarementors.extjs.djn.router.processor.standard.StandardErrorResponseData;
import com.softwarementors.extjs.djn.router.processor.standard.StandardRequestProcessorBase;
import com.softwarementors.extjs.djn.router.processor.standard.StandardSuccessResponseData;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class FormPostRequestProcessorBase extends StandardRequestProcessorBase {
  
  @NonNull
  private static Logger logger = Logger.getLogger( FormPostRequestProcessorBase.class );
  
  protected FormPostRequestProcessorBase( Registry registry, Dispatcher dispatcher, GlobalConfiguration globalConfiguration ) {
    super( registry, dispatcher, globalConfiguration);
  }
  
  protected String process(Map<String,String> formParameters, Map<String, FileItem> fileFields) {
    assert formParameters != null;
    assert fileFields != null;
     
    checkNoMissingParameters(formParameters);
    FormPostRequestData request = createRequestObject(formParameters, fileFields);

    ResponseData response = processRequest( request );
    StringBuilder result = new StringBuilder();
    appendIndividualResponseJsonString(response, result);
    
    return result.toString();
  }

  private static String getAndRemove( Map<String, String> keyValues, String key) {
    String result = keyValues.get(key);
    keyValues.remove(key);
    return result;
  }
  
  private static FormPostRequestData createRequestObject(Map<String, String> formParameters, Map<String, FileItem> fileFields) {
    assert formParameters != null;
    assert fileFields != null;
    
    Map<String,String> parameters = new HashMap<String,String>(formParameters);
    
    String type = getAndRemove(parameters,  FormPostRequestData.TYPE_ELEMENT);
    String action = getAndRemove( parameters, FormPostRequestData.ACTION_ELEMENT );
    String method = getAndRemove(parameters,  FormPostRequestData.METHOD_ELEMENT );
    Long tid = Long.valueOf( Long.parseLong( getAndRemove(parameters,  FormPostRequestData.TID_ELEMENT)) );
    boolean isUpload = Boolean.parseBoolean(getAndRemove(parameters, FormPostRequestData.UPLOAD_ELEMENT) );   
   
    return new FormPostRequestData( type, action, method, tid, isUpload, parameters, fileFields);
  }

  private static void checkNoMissingParameters(Map<String, String> parameters) {
    assert parameters != null;
    
    List<String> missingParameters = new ArrayList<String>();
    addParameterIfMissing( parameters, FormPostRequestData.ACTION_ELEMENT, missingParameters );
    addParameterIfMissing( parameters, FormPostRequestData.METHOD_ELEMENT, missingParameters );
    addParameterIfMissing( parameters, FormPostRequestData.TYPE_ELEMENT, missingParameters );
    addParameterIfMissing( parameters, FormPostRequestData.TID_ELEMENT, missingParameters );
    addParameterIfMissing( parameters, FormPostRequestData.UPLOAD_ELEMENT, missingParameters );
    
    if( !missingParameters.isEmpty() ) {
      RequestException ex = RequestException.forFormPostMissingParameters( missingParameters );
      logger.error( ex.getMessage(), ex ); 
      throw ex;
    }
  }

  private static void addParameterIfMissing( Map<String, String> parameters, String parameterName, List<String> missingParameters ) {
    assert parameters != null;
    assert !StringUtils.isEmpty(parameterName);
    assert missingParameters != null;
    
    if( !parameters.containsKey(parameterName)) {
      missingParameters.add( parameterName );
    }
  }
  
  private ResponseData processRequest( FormPostRequestData request ) {
    assert request != null;
    
    try {
      Object[] parameters;
      parameters = new Object[] {request.getFormParameters(), request.getFileFields()};      
      Object result = dispatchStandardMethod( request.getAction(), request.getMethod(), parameters );
      StandardSuccessResponseData response = new StandardSuccessResponseData( request.getTid(), request.getAction(), request.getMethod() );
      response.setResult( result);
      return response;
    }
    catch( Exception t ) {        
      StandardErrorResponseData response = createJsonServerErrorResponse(request, t);
      logger.error( "(Controlled) server error: " + t.getMessage() + " for Form Post Method " + request.getFullMethodName(), t); 
      return response;
    }  
  }
  
}