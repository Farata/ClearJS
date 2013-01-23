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

package com.softwarementors.extjs.djn.router.processor;


import java.util.List;

import com.softwarementors.extjs.djn.DirectJNgineException;
import com.softwarementors.extjs.djn.StringUtils;
import com.softwarementors.extjs.djn.api.RegisteredMethod;

public class RequestException extends DirectJNgineException {
  private static final long serialVersionUID = -5221533510455590438L;

  private RequestException( String message ) {
    super(message);
    
    assert !StringUtils.isEmpty(message);
  }

  private RequestException( String message, Throwable cause ) {
    super(message, cause);
    
    assert !StringUtils.isEmpty(message);
    assert cause != null;
  }

  public static RequestException forJsonElementMustBeAJsonArray(String elementName, String jsonString ) {
    assert !StringUtils.isEmpty(elementName);
    assert !StringUtils.isEmpty(jsonString);
    
    return new RequestException( "The method arguments must be a json array, but it is not. Json=" + jsonString );
  }

  public static RequestException forActionNotFound(String actionName) {
    assert !StringUtils.isEmpty( actionName );
    
    return new RequestException( "No action registered as '" + actionName + "'" );
  }

  public static RequestException forActionMethodNotFound(String actionName, String methodName) {
    assert !StringUtils.isEmpty( actionName );
    assert !StringUtils.isEmpty( methodName );

    return new RequestException( "No method registered as '" + methodName + "' in action '" + actionName + "'" );
  }

  public static RequestException forRequestBatchMustHaveAtLeastOneRequest() {
    return new RequestException( "A batched request must have at least one request, but the json array had no elements." );
  }

  public static RequestException forRequestBatchItemMustBeAValidJsonObject(int itemPosition) {
    assert itemPosition >= 0;
    
    return new RequestException( "Item " + itemPosition + " in the batched request json array is not a valid json object");
  }

  public static RequestException forRequestMustBeAValidJsonObjectOrArray() {
    return new RequestException( "The request must be a valid json object or array");
  }

  public static RequestException forRequestFormatNotRecognized() {
    return new RequestException( "Unable to recognize the request format.");
  }

  public static RequestException forJsonElementMustBeANonNullOrEmptyValue(String elementName, Class<?> primitiveType) {
    assert !StringUtils.isEmpty( elementName);
    assert primitiveType != null;

    return new RequestException( "The json '" + elementName + "' element is missing, null or emtpy, or it is not of type " + primitiveType.getName() + ".");
  }

  public static RequestException forJsonElementMissing(String elementName) {
    assert !StringUtils.isEmpty( elementName);
    
    return new RequestException( "The json '" + elementName + "' element is missing.");
  }

  public static RequestException forWrongMethodArgumentCount(RegisteredMethod method,
      int realArgumentCount) 
  {
    assert method != null;
    assert realArgumentCount >= 0;
    
    int expectedArgumentCount = method.getParameterCount();
    return new RequestException( "Error attempting to call '" + method.getFullName() + "' (Java method='" + method.getFullJavaMethodName() + "'). Expected '" + expectedArgumentCount + "' arguments, but found '" + realArgumentCount + 
       "'. Note: this can happen sometimes when passing 'undefined' values or just because the JavaScript call was missing some parameters");
  }

  public static RequestException forFormPostMissingParameters(List<String> missingParameters) {
    assert missingParameters != null;
    assert !missingParameters.isEmpty();
    
    return new RequestException( "Form post request is missing the following parameters: " + StringUtils.concatWithSeparator( missingParameters, ", " ) );
  }

  public static RequestException forPollEventNotFound(String eventName) {
    assert !StringUtils.isEmpty( eventName );

    return new RequestException( "No method registered for poll event '" + eventName + "'" );
  }

  public static RequestException forSourceNotFound( String sourceName) {
    assert !StringUtils.isEmpty(sourceName);
    
    return new RequestException( "Unable to find source for '" + sourceName + "'");
  }
}
