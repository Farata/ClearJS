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
import com.softwarementors.extjs.djn.router.TransferType;
import com.softwarementors.extjs.djn.router.processor.SuccessResponseData;

import edu.umd.cs.findbugs.annotations.NonNull;

public class StandardSuccessResponseData extends SuccessResponseData {
  @NonNull private Long tid;
  @NonNull private String action;
  @NonNull private String method;
  @NonNull private Object result;

  public  StandardSuccessResponseData( Long tid, String action, String method ) {
    super( TransferType.RPC );

    assert tid != null;
    assert !StringUtils.isEmpty( action );
    assert !StringUtils.isEmpty( method );

    this.tid = tid;
    this.action = action;
    this.method = method;
  }
  
  private /* package */ Long getTid() {
    return this.tid;
  }

  private /* package */ String getAction() {
    return this.action;
  }

  private /* package */ String getMethod() {
    return this.method;
  }

  @Override
  public String getFullLogDescription() {
    return super.getFullLogDescription() + ", Tid=" + getTid() + ", Action=" + getAction() + ", Method=" + getMethod();
  }

  @Override
  public Object getResult() {
    return this.result;
  }

  @Override
  public void setResult(Object result) {
    this.result = result;
  }
}
