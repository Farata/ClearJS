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

package com.softwarementors.extjs.djn.router.dispatcher;

import com.softwarementors.extjs.djn.DirectJNgineException;
import com.softwarementors.extjs.djn.StringUtils;
import com.softwarementors.extjs.djn.api.RegisteredMethod;

public class MethodExecutionException extends DirectJNgineException {

  private static final long serialVersionUID = 1921673466634321185L;

  private MethodExecutionException( String message, Throwable cause ) {
    super(message, cause);
    
    assert !StringUtils.isEmpty(message);
    assert cause != null;
  }
  
  public static MethodExecutionException forUnableToGetActionInstance(RegisteredMethod method, Throwable cause) {
    assert method != null;
    assert cause != null;
    
    return new MethodExecutionException( "Unable to get instance for '" + method.getFullName() + "'. Java class='" + method.getAction().getFullJavaClassName() + "'. This might be due to the class not having a default public constructor or similar causes", cause );
  }

  public static MethodExecutionException forJavaMethodInvocationError(RegisteredMethod method, Throwable cause) {
    assert method != null;
    assert cause != null;
    
    return new MethodExecutionException( "Error while invoking method '" + method.getFullName() + "'. Java method='" + method.getFullJavaMethodName() + "'", cause );
  }
}
