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

package com.softwarementors.extjs.djn.router.processor.standard.form.upload;

import com.softwarementors.extjs.djn.DirectJNgineException;

public class FileUploadException extends DirectJNgineException {
  private static final long serialVersionUID = -379193882664927495L;

  protected FileUploadException(String message, Throwable cause) {
    super(message, cause);
  }

  public static FileUploadException forFileUploadException( org.apache.commons.fileupload.FileUploadException cause ) {
    assert cause != null;
    
    // The FileUploadException raised by commons-fileupload always
    // hides an inner exception that is the real cause
    Throwable realCause = cause.getCause();
    assert realCause != null;
    
    String message = "Unable to process the files to upload.";
    if( realCause.getMessage() != null ) {
      message += ' ' + realCause.getMessage();
    }
    FileUploadException ex = new FileUploadException(  message, realCause );
    return ex;
  }
}
