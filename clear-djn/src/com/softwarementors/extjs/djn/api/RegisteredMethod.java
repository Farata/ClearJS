package com.softwarementors.extjs.djn.api;

import java.lang.reflect.Method;

import com.softwarementors.extjs.djn.StringUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class RegisteredMethod {
  @NonNull private RegisteredAction action;
  @NonNull private Method method;
  @NonNull private String name;
  
  protected RegisteredMethod( RegisteredAction action, Method method, String name ) {
    assert action != null;
    assert method != null;
    assert !StringUtils.isEmpty(name);
    
    this.action = action;
    this.method = method;
    this.name = name;    
  }

  public RegisteredAction getAction() {
    return this.action;
  }
  
  public Method getMethod() {
    return this.method;
  }

  public Class<?>[] getParameterTypes() {
    return this.method.getParameterTypes();
  }
  
  public int getParameterCount() {
    return getParameterTypes().length;
  }

  public Class<?> getReturnType() {
    return this.method.getReturnType();
  }
  
  public Class<?> getActionClass() {
    return this.action.getActionClass();
  }

  public String getName() {
    return this.name;
  }

  public String getActionName() {
    return getAction().getName();
  }
  
  public String getFullName() {
    return getActionName() + "." + getName();
  }

  public String getFullJavaMethodName() {
    return getAction().getFullJavaClassName() + "." + getMethod().getName();
  }
  
  public abstract RegisteredMethodType getType();
}
