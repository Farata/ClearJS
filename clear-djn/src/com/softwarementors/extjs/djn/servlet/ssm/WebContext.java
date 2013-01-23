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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.softwarementors.extjs.djn.StringUtils;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

public class WebContext {
  @NonNull private HttpServletRequest request;
  @NonNull private HttpServletResponse response;
  @NonNull private HttpServlet servlet;
  private boolean open;

  /* package */ WebContext( @NonNull WebContext context ) {
    this( context.getServlet(), context.getRequest(), context.getResponse());
  }  
  
  /* package */ WebContext( HttpServlet servlet, HttpServletRequest request, HttpServletResponse response ) {
    assert servlet != null;
    assert request != null;
    assert response != null;

    this.servlet = servlet;
    this.request = request;
    this.response = response;
    this.open = true;
  }
  
  /* package */ void close() {
    assert isOpen();

    /*
    this.servlet = null;
    this.request = null;
    this.response = null;
    */
    this.open = false;
  }
  
  private boolean isOpen() {
    return this.open;
  }
  
  public HttpServletRequest getRequest() {
    assert isOpen();

    return this.request;
  }
  
  public HttpServletResponse getResponse() {
    assert isOpen();

    return this.response;
  }
  
  public HttpSession getSession() {
    assert isOpen();
    
    return this.request.getSession();
  }
  
  public ServletContext getServletContext() {
    assert isOpen();
    
    return this.servlet.getServletContext();
  }
  
  public ServletConfig getServletConfig() {
    assert isOpen();
    
    return this.servlet.getServletConfig();
  }
  
  public HttpServlet getServlet() {
    assert isOpen();
    
    return this.servlet;
  }
  
  @CheckForNull public Object getSessionScopedObject( String actionName ) {
    assert !StringUtils.isEmpty(actionName);

    HttpSession context = getSession();
    String key = getSessionScopedActionName(actionName);
    Object result = context.getAttribute(key);
    return result;
  }
  
  public static String getSessionScopedActionName( String actionName ) {
    assert !StringUtils.isEmpty(actionName);
    
    String key = "DirectJNgine.SESSION." + actionName;
    return key;    
  }

  @CheckForNull public Object getApplicationScopedObject( String actionName ) {
    assert !StringUtils.isEmpty(actionName);
    
    ServletContext context = getServletContext();
    String key = getApplicationScopedActionName(actionName);
    Object result = context.getAttribute(key);
    return result;
  }
  
  public static String getApplicationScopedActionName( String actionName ) {
    assert !StringUtils.isEmpty(actionName);
    
    String key = "DirectJNgine.APPLICATION." + actionName;
    return key; 
  }
}
