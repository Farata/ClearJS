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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softwarementors.extjs.djn.StringUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

public class RegisteredApi {
  @NonNull private Map<String, RegisteredAction> actions = new HashMap<String, RegisteredAction>();
  @NonNull private String fullApiFileName;
  @NonNull private String apiNamespace;
  @NonNull private String actionsNamespace;
  @NonNull private String name;
  @NonNull private Registry registry;
  @NonNull private String apiFile;
  
  /* package */ RegisteredApi( Registry registry, String name, String apiFile, String fullApiFileName, String apiNamespace, String actionsNamespace ) {
    assert registry != null;
    assert !StringUtils.isEmpty(name);
    assert !StringUtils.isEmpty(apiNamespace);
    assert !StringUtils.isEmpty(fullApiFileName);
    assert actionsNamespace != null;
    
    this.registry = registry;
    this.apiFile = apiFile;
    this.fullApiFileName = fullApiFileName;
    this.apiNamespace = apiNamespace;
    this.actionsNamespace = actionsNamespace;
    this.name = name;
  }
  
  public Registry getRegistry() {
    return this.registry;
  }

  public RegisteredAction addAction( Class<?> actionClass, String name) {
    assert actionClass != null;
    assert !StringUtils.isEmpty(name);
    
    RegisteredAction result = new RegisteredAction( this, actionClass, name );
    this.actions.put( name, result );
    getRegistry().registerAction(result);
    if( Registry.logger.isDebugEnabled()) {
      Registry.logger.debug( "Registered new Action. Name: '" + name + "', Java class: '" + actionClass.getName() + "'" );
    }
    return result;
  }
  
  public RegisteredAction getAction(String name) {
    assert !StringUtils.isEmpty(name);
    
    return this.actions.get(name);
  }

  public List<RegisteredAction> getActions() {
    return new ArrayList<RegisteredAction>(this.actions.values());
  }

  public String getName() {
    return this.name;
  }

  public String getApiFile() {
    return this.apiFile;
  }
  
  public String getFullApiFileName() {
    return this.fullApiFileName;
  }

  public String getApiNamespace() {
    return this.apiNamespace;
  }

  public String getActionsNamespace() {
    return this.actionsNamespace;
  }

  public List<RegisteredPollMethod> getPollMethods() {
    List<RegisteredPollMethod> result = new ArrayList<RegisteredPollMethod>();
    for( RegisteredAction action : this.actions.values()) {
      result.addAll( action.getPollMethods());
    }
    return result;
  }
}
