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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softwarementors.extjs.djn.StringUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

public class RegisteredAction {

  @NonNull private Map<String, RegisteredPollMethod> pollMethods = new HashMap<String, RegisteredPollMethod>();
  @NonNull private Class<?> actionClass;
  @NonNull private String name;
  @NonNull private Map<String, RegisteredStandardMethod> standardMethodsByName = new HashMap<String,RegisteredStandardMethod>();
  @NonNull private RegisteredApi api;
  
  /* package */ RegisteredAction( RegisteredApi api, Class<?> actionClass, String name ) {
    assert api != null;
    assert actionClass != null;
    assert !StringUtils.isEmpty(name);
    
    this.actionClass = actionClass;
    this.name = name;
    this.api = api;
  }
  
  public RegisteredApi getApi() {
    return this.api;
  }
  
  public Registry getRegistry() {
    return getApi().getRegistry();
  }
  
  public RegisteredPollMethod addPollMethod(String name, Method method) {
    assert !StringUtils.isEmpty(name);
    assert method != null;
    assert RegisteredPollMethod.isValidPollMethod(method);
    
    assert !getRegistry().hasPollMethod(name);
    RegisteredPollMethod result = new RegisteredPollMethod( this, name, method );
    this.pollMethods.put( name, result );
    getRegistry().registerPollMethod( result );
    if( Registry.logger.isDebugEnabled()) {
      Registry.logger.debug( "  - Registered new Poll Method. Name: '" + name + "', Java method: '" + result.getActionClass() + "." + result.getMethod().getName() + "'" );
    }
    return result;
  }
  
  public RegisteredPollMethod getPollMethod(String eventName) {
    assert !StringUtils.isEmpty(eventName);

    return this.pollMethods.get(eventName);
  }
  
  public List<RegisteredPollMethod> getPollMethods() {
    return new ArrayList<RegisteredPollMethod>(this.pollMethods.values());
  }

  public RegisteredStandardMethod addStandardMethod( String name, Method method, boolean formHandler) {
    assert !StringUtils.isEmpty(name);
    assert method != null;
    
    RegisteredStandardMethod result = new RegisteredStandardMethod( this, name, method, formHandler);
    this.standardMethodsByName.put( name, result );
    
    
    if( Registry.logger.isDebugEnabled() ) {
      String type = "Standard";
      if( formHandler) {
        type = "Form";
      }
        
      Registry.logger.debug( "  - Registered new " + type + " Method. Name: '" + result.getFullName() + "'. Java method: '" + getActionClass().getName() + "." + method.getName() + "'" );
    }
    return result;
  }
  
  public RegisteredStandardMethod getStandardMethod(String methodName) {
    assert !StringUtils.isEmpty(methodName);
    
    return this.standardMethodsByName.get( methodName );
  }

  public boolean hasStandardMethod(String method) {
    assert !StringUtils.isEmpty(method);
    
    return this.standardMethodsByName.containsKey(method);
  }

  public List<RegisteredStandardMethod> getStandardMethods() {
    return new ArrayList<RegisteredStandardMethod>( this.standardMethodsByName.values() );
  }

  public Class<?> getActionClass() {
    return this.actionClass;
  }
  
  public static final String NAME_PROPERTY = "name";
  
  public String getName() {
    return this.name;
  }

  /*package*/ void addStandardMethod(RegisteredStandardMethod method) {
    assert method != null;
    assert !this.standardMethodsByName.containsKey(method.getName());
    
    this.standardMethodsByName.put( method.getName(), method );
  }

  public String getFullJavaClassName() {
    return getActionClass().getName();
  }
  
}
