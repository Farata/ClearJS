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

package com.softwarementors.extjs.djn.config;

import java.lang.reflect.Method;

import com.softwarementors.extjs.djn.DirectJNgineException;
import com.softwarementors.extjs.djn.StringUtils;
import com.softwarementors.extjs.djn.api.RegisteredAction;

public class ApiConfigurationException extends DirectJNgineException {
  private static final long serialVersionUID = -3426947847986039782L;

  private ApiConfigurationException( String message ) {
    super(message);
    
    assert !StringUtils.isEmpty(message);
  }

  /*
  public static ApiConfigurationException forClassAlreadyRegisteredAsAction( Class<?> actionClass ) {
    assert actionClass != null;
    
    return new ApiConfigurationException( "Class '" + actionClass.getName() + "' already registered. It is not possible to register the same class twice" );
  }
  */

  public static ApiConfigurationException forActionAlreadyRegistered(String newActionName, Class<?> newActionClass, Class<?> registeredActionClass ) {
    assert !StringUtils.isEmpty(newActionName);
    assert newActionClass != null;
    assert registeredActionClass != null;

    return new ApiConfigurationException("Action '" + newActionName + "' is already registered for class '" + 
        registeredActionClass.getName() + "', but you are attempting to register it for class '" + 
        newActionClass.getName() + "'. It is not possible to register two classes with the same action name." );
  }

  public static ApiConfigurationException forMethodAlreadyRegisteredInAction(String registeredMethodName, String actionName ) {
    assert !StringUtils.isEmpty(registeredMethodName);
    assert !StringUtils.isEmpty(actionName);

    return new ApiConfigurationException("Method '" + registeredMethodName + "' is already registered for action '" + 
        actionName + "'");
  }

  public static ApiConfigurationException forMethodHasWrongParametersForAFormHandler(String actionName, String methodName) {
    assert !StringUtils.isEmpty(actionName);
    assert !StringUtils.isEmpty(methodName);
    
    return new ApiConfigurationException("Method '" + methodName + "' in action '" + actionName + "' can't be registered as a form handler because the corresponding Java method does not have the right parameter types, '(Map<String,String>, Map<String,FileItem>)'" );
  }

  public static ApiConfigurationException forMethodCantBeStandardAndFormPostMethodAtTheSameTime(
      RegisteredAction action, Method method) {
    assert action != null;
    assert method != null;

    return new ApiConfigurationException("Method '" + method.getName() + "' in class '" + action.getActionClass().getName() + "' is a standard and a form post method at the same time: please, check your annotations and method naming to remove the ambiguity." );
  }

  public static ApiConfigurationException forPollMethodCantBeStandardOrFormPostMethodAtTheSameTime(
      RegisteredAction action, Method method) {
    assert action != null;
    assert method != null;

    return new ApiConfigurationException("Method '" + method.getName() + "' in class '" + action.getActionClass().getName() + "' can't be a poll method and a standard/form post method at the same time: please, check your annotations and method naming to remove the ambiguity." );
  }

  public static ApiConfigurationException forMethodHasWrongParametersForAPollHandler(
      Method method) {
    assert method != null;
    
    return new ApiConfigurationException("Method '" + method.getName() + "' in class '" + method.getDeclaringClass().getName() + "' can't be registered as a poll handler because the corresponding Java method does not have the right parameter types, '(Map<String,String>)'" );
  }

  public static ApiConfigurationException forPollEventAlreadyRegistered(String eventName) {
    assert !StringUtils.isEmpty(eventName);
    
    return new ApiConfigurationException( "Poll event '" + eventName + "' already registered . It is not possible to register the same event twice" );
  }

  public static ApiConfigurationException forApiAlreadyRegistered(String name) {
    assert !StringUtils.isEmpty(name);
    
    return new ApiConfigurationException( "Api '" + name + "' already registered. It is not possible to register the same api twice");
  }
}
