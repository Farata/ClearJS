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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.softwarementors.extjs.djn.StringUtils;
import com.softwarementors.extjs.djn.config.GlobalConfiguration;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

public class Registry {
  @NonNull /* package */ static final Logger logger = Logger.getLogger( Registry.class );
  
  @NonNull private Map<String, RegisteredApi> apisByName = new HashMap<String, RegisteredApi>();
  @NonNull private Map<String, RegisteredAction> actionsByName = new HashMap<String, RegisteredAction>();
  @NonNull private Map<String, RegisteredPollMethod> pollMethodsByName = new HashMap<String, RegisteredPollMethod>();
  @NonNull private Map<String, String> sources = new HashMap<String, String>();
  @NonNull private GlobalConfiguration globalConfiguration;
  @NonNull private static final Map<Method, Type[]> gsonGenericParameterTypes = new HashMap<Method, Type[]>();
  
  public static void registerParameterType( Method method, int parameter, Type type ) {
    assert method != null;
    assert parameter < method.getParameterTypes().length;
    assert type != null;
    
    Type[] methodParameterizedTypes = gsonGenericParameterTypes.get(method);
    if( methodParameterizedTypes == null ) {
      methodParameterizedTypes = new Type[method.getParameterTypes().length];
      gsonGenericParameterTypes.put( method, methodParameterizedTypes);
    }
    methodParameterizedTypes[parameter] = type;
  }
  
  public static Type[] getParameterTypes(Method m ) {
    Type[] result = gsonGenericParameterTypes.get(m);
    return result;
  }
  
  public Registry( GlobalConfiguration globalConfiguration ) {
    assert globalConfiguration != null;
    
    this.globalConfiguration = globalConfiguration;
  }

  public RegisteredApi addApi(String name, String apiFileName, String fullApiFileName, String apiNamespace, String actionsNamespace) {
    assert !StringUtils.isEmpty(name);
    assert !StringUtils.isEmpty(apiNamespace);
    assert !StringUtils.isEmpty(apiFileName);
    assert !StringUtils.isEmpty(fullApiFileName);
    assert actionsNamespace != null;
    assert !hasApi(name);
    
    RegisteredApi result = new RegisteredApi( this, name, apiFileName, fullApiFileName, apiNamespace, actionsNamespace);
    this.apisByName.put( name, result );    
    if( logger.isDebugEnabled() ) {
      logger.debug( "Registered new Api: " + name);
    }
    return result;
  }
  
  public boolean hasSource( String sourceName ) {
    assert !StringUtils.isEmpty(sourceName);
    
    return this.sources.containsKey(sourceName); 
  }
  
  public void addSource( String sourceName, String code) {
    assert !StringUtils.isEmpty(sourceName);
    assert code != null;
    assert !hasSource( sourceName );
    
    this.sources.put( sourceName, code);
  }
  
  public String getSource( String sourceName) {
    assert !StringUtils.isEmpty(sourceName);
    
    return this.sources.get(sourceName);
  }

  public RegisteredApi getApi( String api ) {
    assert !StringUtils.isEmpty(api);
    assert hasApi( api );
    
    return this.apisByName.get( api );
  }

  public boolean hasApi( String name ) {
    assert !StringUtils.isEmpty(name);
    
    return this.apisByName.containsKey(name);
  }
  
  public List<RegisteredApi> getApis() {
    List<RegisteredApi> result = new ArrayList<RegisteredApi>(this.apisByName.values());
    return result;
  }
  
  public RegisteredAction getAction( String name ) {
    assert !StringUtils.isEmpty( name );
    assert hasAction( name );

    return this.actionsByName.get(name);
  }

  public boolean hasAction(String name) {
    assert !StringUtils.isEmpty( name );

    return this.actionsByName.containsKey(name);
  }

  public List<RegisteredAction> getActions() {
    List<RegisteredAction> result = new ArrayList<RegisteredAction>(this.actionsByName.values());
    return result;
  }

  public boolean hasPollMethod( String eventName ) {
    assert !StringUtils.isEmpty(eventName);
    
    return this.pollMethodsByName.containsKey(eventName);
  }
  
  @CheckForNull public RegisteredPollMethod getPollMethod(String eventName) {
    assert !StringUtils.isEmpty(eventName);
    assert hasPollMethod(eventName);
    
    return this.pollMethodsByName.get(eventName);
  }

  public List<RegisteredPollMethod> getPollMethods() {
    List<RegisteredPollMethod> result = new ArrayList<RegisteredPollMethod>(this.pollMethodsByName.values());
    return result;
  }
  
  public GlobalConfiguration getGlobalConfiguration() {
    return this.globalConfiguration;
  }

  /* package */ void registerPollMethod(RegisteredPollMethod result) {
    assert result != null;
    assert !this.pollMethodsByName.containsKey(result.getName());
    
    this.pollMethodsByName.put( result.getName(), result );
  }

  /* package */ void registerAction(RegisteredAction result) {
    assert result != null;
    assert !this.actionsByName.containsKey(result.getName());
    
    this.actionsByName.put( result.getName(), result );
  }

}
