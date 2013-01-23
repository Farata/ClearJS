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

package com.softwarementors.extjs.djn.router.processor.standard;

import com.softwarementors.extjs.djn.StringUtils;
import com.softwarementors.extjs.djn.api.RegisteredAction;
import com.softwarementors.extjs.djn.api.RegisteredStandardMethod;
import com.softwarementors.extjs.djn.api.Registry;
import com.softwarementors.extjs.djn.config.GlobalConfiguration;
import com.softwarementors.extjs.djn.router.dispatcher.Dispatcher;
import com.softwarementors.extjs.djn.router.processor.RequestException;
import com.softwarementors.extjs.djn.router.processor.RequestProcessorBase;

public abstract class StandardRequestProcessorBase extends RequestProcessorBase {  
  protected StandardErrorResponseData createJsonServerErrorResponse(StandardRequestData request, Throwable t) {
    assert request != null;
    assert t != null;
    
    StandardErrorResponseData response = new StandardErrorResponseData( 
        request.getTid(), request.getAction(), request.getMethod(), t, getDebug() );
    return response;
  }
  
  protected StandardRequestProcessorBase( Registry registry, Dispatcher dispatcher, GlobalConfiguration globalConfiguration ) {
    super(registry,dispatcher,globalConfiguration);
  }

  protected RegisteredStandardMethod getStandardMethod(String actionName, String methodName) {
    assert !StringUtils.isEmpty(actionName);
    assert !StringUtils.isEmpty(methodName);

    RegisteredAction action = getRegistry().getAction(actionName); 
    if( action == null ) {
      throw RequestException.forActionNotFound( actionName );
    }

    RegisteredStandardMethod method = action.getStandardMethod(methodName);
    if( method == null ) {
      throw RequestException.forActionMethodNotFound( action.getName(), methodName );
    }
    return method;
  }

  protected Object dispatchStandardMethod( String actionName, String methodName, Object[] parameters ) {
    assert !StringUtils.isEmpty(actionName);  
    assert !StringUtils.isEmpty(methodName);
    assert parameters != null;
    
    RegisteredStandardMethod method = getStandardMethod( actionName, methodName);
    Object result = getDispatcher().dispatch(method, parameters);
    return result;
  }

}
