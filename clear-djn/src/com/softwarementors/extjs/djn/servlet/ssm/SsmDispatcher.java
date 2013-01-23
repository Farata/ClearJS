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

package com.softwarementors.extjs.djn.servlet.ssm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.softwarementors.extjs.djn.api.RegisteredMethod;
import com.softwarementors.extjs.djn.router.dispatcher.DispatcherBase;

import edu.umd.cs.findbugs.annotations.NonNull;

public class SsmDispatcher extends DispatcherBase {
  @NonNull private static final Map<Class<?>, Scope> methodScopeCache =
    new HashMap<Class<?>, Scope>();

  @NonNull
  private static final Logger logger = Logger.getLogger(SsmDispatcher.class);
    
  @Override
  protected Object getInvokeInstanceForNonStaticMethod(RegisteredMethod method) throws Exception {
    assert method != null;

    Class<?> instanceClass = method.getActionClass();
    Scope scope = methodScopeCache.get(instanceClass);
    if( scope == null ) {
      ActionScope methodScope = instanceClass.getAnnotation(ActionScope.class);
      if( methodScope == null ) {
        scope = Scope.STATELESS;
      }
      else {
        scope = methodScope.scope();
      }
      methodScopeCache.put( instanceClass, scope);
      if( logger.isDebugEnabled() ) {
        logger.debug( "Action class '" + instanceClass + "' will be instantiated with " + scope.toString() + "' scope." );
      }
    }
    
    assert scope != null;
    Object result = getAction(method, scope);
    return result;
  }

  @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SF_SWITCH_NO_DEFAULT",
      justification="Missing a 'default' branch is not a problem with enums, given the appropriate compiler settings")
  private Object getAction(RegisteredMethod method, Scope scope) throws Exception{
    Object result = null;
    switch( scope ) {
      case SESSION:
        result = getSessionMethodInstance(method);
        break;
      case APPLICATION:
        result = getApplicationMethodInstance(method);
        break;
      case STATELESS:
        result = getStatelessMethodInstance(method);
        break;
    }
    assert result != null;
    return result;
  }

  private Object getStatelessMethodInstance(RegisteredMethod method) throws Exception {
    assert method != null;
    
    return createInvokeInstanceForMethodWithDefaultConstructor(method);
  }
  
  /* MUST be synchronized so that two competing threads do no instantiate and store two actions "at the same time".
   */
  private synchronized Object getSessionMethodInstance(RegisteredMethod method) throws Exception {
    assert method != null;

    HttpSession context = WebContextManager.get().getSession();
    Object result = WebContextManager.get().getSessionScopedObject(method.getActionName());
    if( result == null ) {
      String key = WebContext.getSessionScopedActionName(method.getActionName());
      result = createInvokeInstanceForMethodWithDefaultConstructor(method);
      context.setAttribute(key, result);
    }
    return result;
  }

  /* MUST be synchronized so that two competing threads do no instantiate and store two actions "at the same time".
   */
  private synchronized Object getApplicationMethodInstance(RegisteredMethod method) throws Exception {
    assert method != null;
    
    ServletContext context = WebContextManager.get().getServletContext();
    Object result = WebContextManager.get().getApplicationScopedObject(method.getActionName());
    if( result == null ) {
      result = createInvokeInstanceForMethodWithDefaultConstructor(method);
      String key = WebContext.getApplicationScopedActionName(method.getActionName());
      context.setAttribute(key, result);
    }
    return result;
  }

}
