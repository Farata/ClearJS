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

package com.softwarementors.extjs.djn.servlet;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.softwarementors.extjs.djn.StringBuilderUtils;
import com.softwarementors.extjs.djn.StringUtils;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

public final class ServletUtils {
  @NonNull
  private static final Logger logger = Logger.getLogger( ServletUtils.class);
  
  public ServletUtils() {
    // Avoid instantiation
  }

  public static String getRequiredParameter( ServletConfig conf, String parameterName ) {
    assert conf != null;
    assert !StringUtils.isEmpty(parameterName);
    
    String result = conf.getInitParameter( parameterName );
    assert !StringUtils.isEmpty(result);
    return result;
  }
  
  @CheckForNull public static String getParameter( ServletConfig conf, String parameterName, String valueIfNotSpecified ) {
    assert conf != null;
    assert !StringUtils.isEmpty(parameterName);
    
    String result = conf.getInitParameter( parameterName );
    if( result != null ) {
      result = result.trim();
    }
    if( StringUtils.isEmpty(result) ) {
      return valueIfNotSpecified;
    }
    return result;
  }

  public static boolean getBooleanParameter( ServletConfig conf, String parameterName, boolean valueIfNotSpecified ) {
    assert conf != null;
    assert !StringUtils.isEmpty(parameterName);
    
    String valueIfNotSpecifiedString = "false";
    if( valueIfNotSpecified) {
      valueIfNotSpecifiedString = "true";
    }
    String valueString = getParameter( conf, parameterName, valueIfNotSpecifiedString);
    assert valueString != null;
    boolean result = valueString.equalsIgnoreCase("true") || valueString.equals("1");
    return result;
  }
  
  public static int getIntParameterGreaterOrEqualToValue( ServletConfig conf, String parameterName, int minValue, int valueIfNotSpecified ) {
    assert conf != null;
    assert !StringUtils.isEmpty(parameterName);
    
    assert valueIfNotSpecified >= minValue;
    
    String resultString = getParameter( conf, parameterName, Integer.toString(valueIfNotSpecified));
    if( StringUtils.isEmpty(resultString) ) {
      return valueIfNotSpecified;
    }
    
    try {
      int result = Integer.parseInt(resultString);
      if( result < minValue ) {
        ServletConfigurationException ex = ServletConfigurationException.forParameterMustBeAnIntegerGreaterOrEqualToValue( parameterName, result, minValue );
        logger.fatal( ex.getMessage(), ex );
        throw ex;
      }
      return result;
    }
    catch( NumberFormatException e) {
      ServletConfigurationException ex = ServletConfigurationException.forParameterMustBeAValidInteger( parameterName, resultString );
      logger.fatal( ex.getMessage(), ex );
      throw ex;
    }
    
  }
  
  public static void checkRequiredParameters( ServletConfig conf, String... parameterNames ) {
    assert conf != null;
    assert parameterNames != null;
    assert parameterNames.length > 0;
    
    List<String> missingParameters = new ArrayList<String>();
    for( String parameterName : parameterNames ) {
      String result = conf.getInitParameter( parameterName );
      if( StringUtils.isEmpty(result) ) {
        missingParameters.add( "'" + parameterName + "'" );
      }
    }
    if( !missingParameters.isEmpty() ) {
      ServletConfigurationException ex = ServletConfigurationException.forMissingRequiredConfigurationParameter( missingParameters );
      logger.fatal(ex);
      throw ex;
    }
  }
  
  public static String getDetailedRequestInformation(HttpServletRequest request) {
    assert request != null;
    
    String contentType = request.getContentType();
    if( contentType == null ) {
      contentType = "";
    }
    String method = request.getMethod();    
    StringBuilder result = new StringBuilder(); 
    StringBuilderUtils.appendAll( result, "RequestType=", contentType, ", Method=", method,
        ", ContextPath=", request.getContextPath(),
        ", ServletPath=", request.getServletPath(), 
        ", PathInfo=", request.getPathInfo(),
        ", QueryString=", request.getQueryString(),
        ", CharacterEncoding=", request.getCharacterEncoding(),
        ", AuthType=", request.getAuthType(),
        ", ContentType=", request.getContentType(),
        ", Scheme=", request.getScheme(),
        ", Locale=", request.getLocale().toString(),
        ". " );
    
        
    result.append( "Headers: " ); 
    Enumeration<?> headers = request.getHeaderNames();
    while( headers.hasMoreElements() ) {
      String headerName = (String)headers.nextElement();
      StringBuilderUtils.appendAll( result, "'", headerName, "'=" );
      Enumeration<?> headerContent = request.getHeaders(headerName);
      while( headerContent.hasMoreElements() ) {
        String headerValue = (String)headerContent.nextElement();
        result.append( headerValue );
        result.append( ", " );
      }
    }
    return result.toString();
  }
}
