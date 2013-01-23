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

package com.softwarementors.extjs.djn.router.processor.standard.form;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import com.softwarementors.extjs.djn.router.processor.standard.StandardRequestData;

import edu.umd.cs.findbugs.annotations.NonNull;

public class FormPostRequestData extends StandardRequestData {
  
  public static final String ACTION_ELEMENT = "extAction";
  public static final String METHOD_ELEMENT = "extMethod";
  public static final String TID_ELEMENT = "extTID";
  public static final String TYPE_ELEMENT = "extType";
  public static final String UPLOAD_ELEMENT = "extUpload";
  
  // Make transient so that it is not serialized by our json processor
  transient private boolean isUpload;
  @NonNull transient private Map<String,String> formParameters;
  @NonNull transient private Map<String, FileItem> fileFields; 
  
  public FormPostRequestData(String type, String action, String method, Long tid, boolean isUpload, Map<String, String> parameters, Map<String, FileItem> fileFields) {
    super( type, action, method, tid);
    
    assert parameters != null;
    assert !parameters.containsKey( ACTION_ELEMENT );
    assert !parameters.containsKey( METHOD_ELEMENT );
    assert !parameters.containsKey( TYPE_ELEMENT);
    assert !parameters.containsKey( TID_ELEMENT );
    assert !parameters.containsKey(UPLOAD_ELEMENT );

    Map<String,String> formParameters = new HashMap<String,String>(parameters);
    this.formParameters = formParameters;
    this.isUpload = isUpload;
    this.fileFields = fileFields;
  }

  public boolean isUpload() {
    return this.isUpload;
  }
  
  public Map<String,String> getFormParameters() {
    return this.formParameters;
  }
  
  public Map<String, FileItem> getFileFields() {
    return this.fileFields;
  }
}