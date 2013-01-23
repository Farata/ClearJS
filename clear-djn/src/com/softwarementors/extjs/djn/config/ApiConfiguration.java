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

import java.util.List;

import com.softwarementors.extjs.djn.StringUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ApiConfiguration {
  
  public static final String DEFAULT_NAMESPACE_PREFIX = "Djn.";
  public static final String DEFAULT_API_FILE_SUFFIX = "-api.js";
  @NonNull private String name;
  @NonNull private String fullApiFileName;
  @NonNull private String apiNamespace;
  @NonNull private String actionsNamespace;
  @NonNull private List<Class<?>> classes;
  @NonNull private String apiFile;
  
  public ApiConfiguration( String name, String apiFile, String fullApiFileName, 
      String apiNamespace, String actionsNamespace, List<Class<?>> classes ) 
  {
    assert !StringUtils.isEmpty(name);
    assert !StringUtils.isEmpty(apiFile);
    assert !StringUtils.isEmpty( fullApiFileName );
    assert !StringUtils.isEmpty( apiNamespace );
    assert actionsNamespace != null;
    assert classes != null;

    this.name = name;
    this.apiFile = apiFile;
    this.fullApiFileName = fullApiFileName;
    this.apiNamespace = apiNamespace;
    this.actionsNamespace = actionsNamespace;
    this.classes = classes;
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

  public List<Class<?>> getClasses() {
    return this.classes;
  }
  
  public String getName() {
    return this.name;
  }
}
