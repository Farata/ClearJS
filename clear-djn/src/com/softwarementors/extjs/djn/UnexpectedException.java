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

package com.softwarementors.extjs.djn;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

public class UnexpectedException extends DirectJNgineException {
  private static final long serialVersionUID = 5820146417838838357L;

  private UnexpectedException( String message, Throwable cause) {
    super( message, cause);
  }
  
  public static UnexpectedException forUnexpectedCodeBranchExecuted() {
    return new UnexpectedException( "Possible DirectJNgine BUG: This code branch should never have been entered", null);
  }
  
  public static UnexpectedException forExpectingUTF8UrlEncodingIsAlwaysSupportedByURLEncoder( UnsupportedEncodingException cause ) {
    assert cause != null;
    
    return new UnexpectedException( "Possible DirectJNgine BUG: We assumed that UTF8 encoding is always supported by URLEncoder.", cause);
  }

  public static UnexpectedException forExecutionExceptionShouldNotHappenBecauseProcessorHandlesExceptionsAsServerErrorResponses(ExecutionException cause) {
    assert cause != null;
    
    return new UnexpectedException( "Possible DirectJNgine BUG: We assumed that an ExecutionException must never happen because all Java method execution exceptions are caught and handled by the request processor and turned into server error responses -and are never re-thrown.", cause);
  }

  public static UnexpectedException forUnexpectedSecurityException(SecurityException cause) {
    assert cause != null;
    
    return new UnexpectedException( "Unexpected SecurityException: we assumed it is impossible because we assumed this field was introspected before", cause);
  }

}
