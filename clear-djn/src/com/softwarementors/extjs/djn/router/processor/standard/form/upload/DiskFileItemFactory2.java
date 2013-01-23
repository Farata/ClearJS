/* Very important!
 * 
 * This is a modification of the DiskFileItemFactory implemented in Apache
 * File Upload library, intended to be used in an
 * scenario where files can't be written to disk -i.e., AppEngine. 
 * 
 */
/*
 * Copyright 2001-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.softwarementors.extjs.djn.router.processor.standard.form.upload;

import org.apache.commons.fileupload.FileItem;

public class DiskFileItemFactory2 extends org.apache.commons.fileupload.disk.DiskFileItemFactory {
  @Override
  public FileItem createItem(
      String fieldName,
      String contentType,
      boolean isFormField,
      String fileName
      ) {
  return new DiskFileItem2(fieldName, contentType,
          isFormField, fileName, Integer.MAX_VALUE, getRepository());
  }

}
