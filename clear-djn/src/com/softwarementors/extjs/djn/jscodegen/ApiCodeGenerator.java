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

package com.softwarementors.extjs.djn.jscodegen;

import java.lang.reflect.Method;
import java.util.List;

import com.softwarementors.extjs.djn.ClassUtils;
import com.softwarementors.extjs.djn.api.RegisteredAction;
import com.softwarementors.extjs.djn.api.RegisteredApi;
import com.softwarementors.extjs.djn.api.RegisteredPollMethod;
import com.softwarementors.extjs.djn.api.RegisteredStandardMethod;
import com.softwarementors.extjs.djn.config.GlobalConfiguration;
import com.softwarementors.extjs.djn.router.processor.poll.PollRequestProcessor;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ApiCodeGenerator {
  
  private static final String REMOTING_TYPE = "remoting";
  @NonNull private RegisteredApi api;
  @NonNull private GlobalConfiguration globalConfiguration;
  
  public ApiCodeGenerator( GlobalConfiguration globalConfiguration, RegisteredApi api ) {
    assert globalConfiguration != null;
    assert api != null;
    
    this.globalConfiguration = globalConfiguration;
    this.api = api;
  }

  private static final String TABS_1 = "  ";
  private static final String TABS_2 = "    ";
  private static final String TABS_3 = "      ";
  
  public void appendCode( StringBuilder result, boolean minify) {
    assert result != null;
    
    if( !minify ) {
      result.append( "/**********************************************************************\n");
      result.append( " * \n");
      result.append( " * Code generated automatically by DirectJNgine\n");
      result.append( " * Copyright (c) 2009, Pedro Agulló Soliveres\n");
      result.append( " * \n");
      result.append( " * DO NOT MODIFY MANUALLY!!\n");
      result.append( " * \n");
      result.append( " **********************************************************************/\n");
      result.append( "\n");
    }
    appendNamespaceAndProviderUrlSection(result);
    appendPollingUrlsSection(result, minify);
    appendActionsSection(result, minify);
  }

  private void appendNamespaceAndProviderUrlSection(StringBuilder result) {
    assert result != null;
    
    result.append( "Ext.namespace( '"); result.append( this.api.getApiNamespace()); result.append("');\n" );
    if( !this.api.getActionsNamespace().equals("")) {
      result.append( "Ext.namespace( '"); result.append( this.api.getActionsNamespace()); result.append("');\n" );
    }
    result.append('\n');
    result.append( this.api.getApiNamespace() ); result.append( ".PROVIDER_BASE_URL=" ); appendJsExpressionToGetBaseUrl(result); result.append( this.globalConfiguration.getProvidersUrl() );  result.append( "';");
    result.append('\n');
    result.append('\n');
  }

  private void appendJsExpressionToGetBaseUrl(StringBuilder result) {
    assert result != null;
    
    String contextPath = this.globalConfiguration.getContextPath();
    if( contextPath == null ) {
      String JSCRIPT_CALCULATED_CONTEXT_PATH = "(window.location.pathname.split('/').length>2 ? window.location.pathname.split('/')[1]+ '/' : '')  + '";
      result.append( "window.location.protocol + '//' + window.location.host + '/' + " + JSCRIPT_CALCULATED_CONTEXT_PATH );
    }
    else {
      if( !contextPath.startsWith("/")) {
        contextPath = "/" + contextPath;
      }
      if( !contextPath.endsWith("/")) {
        contextPath += "/";
      }
      result.append( "window.location.protocol + '//' + window.location.host + '" + contextPath + "' + '" );
    }
  }

  private void appendPollingUrlsSection(StringBuilder result, boolean minify) {
    assert result != null;
  
    result.append( this.api.getApiNamespace() ); result.append( ".POLLING_URLS = {\n");
    List<RegisteredPollMethod> pollMethods = this.api.getPollMethods();
    for( int i = 0; i < pollMethods.size(); i++  ) {
      RegisteredPollMethod pollMethod = pollMethods.get(i);
      boolean isLast = i == pollMethods.size() - 1;
      appendPollUrl(result, pollMethod, minify);
      if( !isLast ) {
       result.append( ", ");
      }
      result.append( "\n");
    }
    result.append( "}\n");
    result.append( "\n");
  }

  private void appendPollUrl(StringBuilder result, RegisteredPollMethod pollMethod, boolean minify) {
    assert result != null;
    assert pollMethod != null;
    
    result.append( TABS_1 ); result.append( pollMethod.getName()); result.append( " : ");
    result.append( this.api.getApiNamespace() ); result.append( ".PROVIDER_BASE_URL + " );  result.append( "'" ); result.append( PollRequestProcessor.PATHINFO_POLL_PREFIX); result.append( pollMethod.getName() ); result.append( "' " );
    if( !minify ) {
      appendPollUrlExtraInformation(result, pollMethod);
    }
  }

  private static void appendPollUrlExtraInformation(StringBuilder result, RegisteredPollMethod pollMethod) {
    result.append( "/* "); appendMethodExtraInformation(pollMethod.getMethod(), pollMethod.getActionClass(), false, result);
    result.append( " -- calls "); result.append( ClassUtils.getNicePrintableName(pollMethod.getActionClass()) ); result.append( "."); result.append( pollMethod.getMethod().getName() );
    result.append( " */");
  }

  private void appendActionsSection(StringBuilder result, boolean minify) {
    assert result != null;
    
    appendActionsStart(result);    
    List<RegisteredAction> actions = this.api.getActions();
    
    for( int i = 0; i < actions.size(); i++ ) {
      boolean isLast = i == actions.size() - 1;
      appendAction( actions.get(i), result, isLast, minify );
    }
    appendActionsEnd(result);
  }

  private void appendActionsStart( StringBuilder result ) {
    assert result != null;
    
    result.append( this.api.getApiNamespace() ); result.append( ".REMOTING_API"); result.append( " = {\n" );
    
    result.append("  url: " ); result.append( this.api.getApiNamespace() ); result.append( ".PROVIDER_BASE_URL") ; result.append( ",\n" );
    result.append("  type: '" ); result.append( REMOTING_TYPE ); result.append( "',\n" );
    if( !this.api.getActionsNamespace().equals("")) {
      result.append( "  namespace: " ); result.append( this.api.getActionsNamespace()); result.append( ",\n");
    }
    result.append("  actions: {\n" );
  }

  private static void appendActionsEnd( StringBuilder result ) {
    assert result != null;
    
    result.append( "  }\n" );
    result.append( "}\n" );
    result.append( '\n');
  }
  
  private static void appendAction(RegisteredAction action, StringBuilder result, boolean isLast, boolean minify ) {
    assert action != null;
    assert result != null;
  
    result.append( TABS_2 ); result.append( action.getName() ); result.append( ": [\n");
    
    List<RegisteredStandardMethod> methods = action.getStandardMethods();
    for( int i = 0; i < methods.size(); i++ ) {
      boolean isLastMethod = i == methods.size() - 1;
      appendMethod( methods.get(i), result, isLastMethod, minify );
    }
    
    result.append( TABS_2 ); result.append( "]" ); if( !isLast ) { result.append(","); } result.append('\n');
  }

  private static void appendMethod(RegisteredStandardMethod method, StringBuilder result, boolean isLast, boolean minify) {
    assert method != null;
    assert result != null;

    result.append( TABS_3 ); result.append( "{\n");
    result.append( TABS_3 ); result.append( "  name: '"); result.append( method.getName() ); result.append( "'" );
    if( !minify ) {
      appendMethodExtraInformation(method, result); 
    }
    result.append( ",\n");
    result.append( TABS_3 ); result.append( "  len: "); result.append( method.getClientParameterCount() ); result.append( ",\n");
    result.append( TABS_3 ); result.append( "  formHandler: "); result.append( method.isFormHandler() ); result.append( "\n");
    result.append( TABS_3 ); result.append( "}" ); if( !isLast ) { result.append(","); } result.append( '\n');
  }

  private static void appendMethodExtraInformation(RegisteredStandardMethod method, StringBuilder result) {
    assert method != null;
    assert result != null;
    
    result.append( "/*");

    appendMethodExtraInformation( method.getMethod(), method.getActionClass(), !method.isFormHandler(), result );

    if( method.isFormHandler()) {
      result.append( " -- FORM HANDLER");
    }
    result.append( " */");
  }
  
  private static void appendMethodExtraInformation( Method method, Class<?> classOwningMethod, boolean writeJavaParameterTypes, StringBuilder result ) {
    assert method != null;
    assert classOwningMethod != null;
    assert result != null;
    
    // Append parameters
    result.append( "(");
    if( writeJavaParameterTypes ) {
      Class<?>[] parameterTypes = method.getParameterTypes();
      for( int i=0; i < parameterTypes.length; i++ ) {
        boolean isLast = i == parameterTypes.length - 1;
        Class<?> cls = parameterTypes[i];
        result.append( ClassUtils.getNicePrintableName(cls) );
        if( !isLast ) {
          result.append( ", ");
        }
      }
    }
    result.append( ")");
    
    // Append result type
    Class<?> resultType = method.getReturnType();
    if( !resultType.equals(Void.class)) {
      result.append( " => ");
      result.append( ClassUtils.getNicePrintableName(resultType));
    }
    
  }
}
