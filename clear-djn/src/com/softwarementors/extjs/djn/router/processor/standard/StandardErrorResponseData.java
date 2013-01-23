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
import com.softwarementors.extjs.djn.router.processor.ErrorResponseData;

import edu.umd.cs.findbugs.annotations.NonNull;


public class StandardErrorResponseData extends ErrorResponseData {
  @NonNull private Long tid;
  @NonNull private String action;
  @NonNull private String method;
  
  public StandardErrorResponseData( Long tid, String action, String method, Throwable exception, boolean debugOn ) {
    super( exception, debugOn);
    
    assert tid != null;
    assert !StringUtils.isEmpty( action );
    assert !StringUtils.isEmpty( method );

    this.tid = tid;
    this.action = action;
    this.method = method;
  }
  
  public Long getTid() {
    return this.tid;
  }

  public String getAction() {
    return this.action;
  }

  public String getMethod() {
    return this.method;
  }
}
