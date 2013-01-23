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

package com.softwarementors.extjs.djn.api;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.JsonArray;
import com.softwarementors.extjs.djn.StringUtils;

public class RegisteredStandardMethod extends RegisteredMethod {
  private boolean formHandler;
  private boolean handleParametersAsJsonArray;
  
  
  /* package */ RegisteredStandardMethod( RegisteredAction action, String name, Method method, boolean formHandler) {
    super( action, method, name );
    assert !StringUtils.isEmpty(name);

    this.formHandler = formHandler;
    this.handleParametersAsJsonArray = !formHandler && method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals( JsonArray.class);
    assert !this.formHandler || isValidFormHandlingMethod(method);
    
    assert !action.hasStandardMethod(this.getName());
    action.addStandardMethod(this);
  }
  
  public Type[] getGsonParameterTypes() {
    return Registry.getParameterTypes(getMethod());
  }
  
  public static boolean isValidFormHandlingMethod( Method method ) {
    assert method != null;
    
    return method.getParameterTypes().length == 2 && method.getParameterTypes()[0].equals( Map.class) || !method.getParameterTypes()[1].equals( Map.class);
  }
  
  public boolean isFormHandler() {
    return this.formHandler;
  }

  public int getClientParameterCount() {
    // If this is a form handler, then the client must pass just one parameter
    if( isFormHandler() ) {
      return 1;
    }
    return getParameterCount();
  }

  public boolean getHandleParametersAsJsonArray() {
    return this.handleParametersAsJsonArray;
  }

  @Override
  public RegisteredMethodType getType() {
    return RegisteredMethodType.STANDARD;
  }  
}
