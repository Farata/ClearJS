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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;

import com.softwarementors.extjs.djn.ClassUtils;
import com.softwarementors.extjs.djn.Timer;
import com.softwarementors.extjs.djn.api.RegisteredMethod;
import com.softwarementors.extjs.djn.router.processor.RequestException;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class DispatcherBase implements Dispatcher {
  public Object dispatch( RegisteredMethod method, Object[] parameters ) {
    assert method != null;
    assert parameters != null;

    Method javaMethod = method.getMethod();
    int expectedArgumentCount = method.getParameterCount();
    if( parameters.length != expectedArgumentCount ) {
      throw RequestException.forWrongMethodArgumentCount( method, parameters.length );
    }

    Timer timer = new Timer();
    try {
      Object actionInstance = null;
      try {
        actionInstance = getInvokeInstance(method);
      }
      catch( Exception e ) {
        throw MethodExecutionException.forUnableToGetActionInstance( method, e );
      }
      try {
        Object result = invokeMethod( method, actionInstance, parameters);
        return result;
      }
      catch( Exception e ) {
        throw MethodExecutionException.forJavaMethodInvocationError( method, e );
      }
    }
    finally {
      timer.stop();
      if( Timer.logger.isDebugEnabled()) {
        timer.logDebugTimeInMilliseconds("  - Java method dispatch time (" + ClassUtils.getSimpleName(javaMethod.getDeclaringClass()) + "." + method.getName() + ")" );
      }
    }
  }

  private @CheckForNull Object getInvokeInstance(RegisteredMethod method) throws Exception {
    Object actionInstance = null;
    if( !Modifier.isStatic(method.getMethod().getModifiers())) {
      actionInstance = getInvokeInstanceForNonStaticMethod(method);
      assert actionInstance != null;
    }
    return actionInstance;
  }

  protected abstract Object getInvokeInstanceForNonStaticMethod(RegisteredMethod method) throws Exception;

  protected Object createInvokeInstanceForMethodWithDefaultConstructor(RegisteredMethod method) throws Exception {
    assert method != null;

    Class<?> instanceClass = method.getActionClass();
    Object methodInstance;
    Constructor<?> c = instanceClass.getConstructor();
    // Invoke private constructors too
    boolean accessible = c.isAccessible();
    try {
      c.setAccessible(true);
      methodInstance = c.newInstance();
    }
    finally {
      c.setAccessible(accessible);
    }
    return methodInstance;
  }

  // This class is a gadget needed to invoke the Method.setAccessible method
  // 'the correct way' according to FindBugs: it flags direct usage as dangerous.
  private static class MethodVisibilityModifier implements PrivilegedAction<Object> {
    private boolean accessible;
    private @NonNull Method method;

    public MethodVisibilityModifier(@NonNull Method method) {
      assert method != null;
      this.method = method;
    }

    public Object run() {
      this.method.setAccessible(this.accessible);
      return null;
    }

    public void setAccessible( boolean accessible ) {
      this.accessible = accessible;
    }
  }

  protected Object invokeMethod(RegisteredMethod method, Object actionInstance, Object[] parameters) throws Exception {
    assert method != null;
    assert parameters != null;

    Method javaMethod = method.getMethod();
    return invokeJavaMethod( actionInstance, javaMethod, parameters );
  }

  protected static final Object invokeJavaMethod(Object instance, @NonNull Method method, @NonNull Object[] parameters) throws Exception {
    boolean accessible = method.isAccessible();
    MethodVisibilityModifier visibilityModifier = new MethodVisibilityModifier(method);
    try {
      visibilityModifier.setAccessible(true);
      AccessController.doPrivileged(visibilityModifier);
      return method.invoke( instance, parameters );
    }
    finally {
      visibilityModifier.setAccessible(accessible);
      AccessController.doPrivileged(visibilityModifier);
    }
  }

}
