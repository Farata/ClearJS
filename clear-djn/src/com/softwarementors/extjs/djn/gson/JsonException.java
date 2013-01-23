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

package com.softwarementors.extjs.djn.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonParseException;
import com.softwarementors.extjs.djn.DirectJNgineException;
import com.softwarementors.extjs.djn.StringUtils;
import com.softwarementors.extjs.djn.api.RegisteredStandardMethod;
import com.softwarementors.extjs.djn.router.processor.ResponseData;

public class JsonException extends DirectJNgineException {
  private static final long serialVersionUID = 4992911540391211769L;

  private JsonException( String message, Throwable cause ) {
    super(message, cause);
    
    assert !StringUtils.isEmpty(message);
    assert cause != null;
  }
  
  public static JsonException forFailedConversionFromResponseToJson(ResponseData response, Throwable ex) {
    assert response != null;
    assert ex != null;
    
    return new JsonException( "Failed attempt to convert a response to json. " + response.getFullLogDescription(), ex );
  }

  public static JsonException forFailedConversionFromJsonStringToMethodParameters(RegisteredStandardMethod method, String jsonParametersString,
    Class<?>[] parameterTypes, Type[] gsonParameterTypes, JsonParseException ex) 
  {
    assert method != null;
    assert jsonParametersString != null;
    assert parameterTypes != null;
    assert ex != null;
    
    StringBuilder typeNames = getCommaSeparatedTypeNames(parameterTypes, gsonParameterTypes);
    return new JsonException( "Failed attempt to convert from a json string to java method parameters. Method='" + method.getFullName() + "', Json string='" + 
        jsonParametersString + "', ExpectedTypes='" + typeNames + "'", ex );
  }

  private static StringBuilder getCommaSeparatedTypeNames(Class<?>[] parameterTypes, Type[] gsonParameterTypes) 
  {
    StringBuilder typeNames = new StringBuilder();
    boolean hasGsonParametersTypes = gsonParameterTypes != null;
    for( int i = 0; i < parameterTypes.length; i++  ) {
      Class<?> type = parameterTypes[i];
      typeNames.append( type.getName() );
      if( hasGsonParametersTypes) {
        assert gsonParameterTypes != null;
        
        Type gsonType = gsonParameterTypes[i];
        if( gsonType != null) {
          typeNames.append( " ::GENERIC= ");
          typeNames.append( gsonType.toString());
          typeNames.append( ":: ");
        }
      }

      boolean atEnd = i == parameterTypes.length - 1;
      if( !atEnd ) {
        typeNames.append( ", ");
      }
    }
    
    return typeNames;
  }
}
