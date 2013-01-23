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

package com.softwarementors.extjs.djn.router.processor;

import com.softwarementors.extjs.djn.ExceptionUtils;
import com.softwarementors.extjs.djn.router.TransferType;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class ErrorResponseData extends ResponseData {
  @edu.umd.cs.findbugs.annotations.SuppressWarnings( value="URF_UNREAD_FIELD", justification="Passed to JSON side only")
  @NonNull /* package */ String message;
  @edu.umd.cs.findbugs.annotations.SuppressWarnings( value="URF_UNREAD_FIELD", justification="Passed to JSON side only")
  @NonNull /* package */ String where;
  @edu.umd.cs.findbugs.annotations.SuppressWarnings( value="URF_UNREAD_FIELD", justification="Passed to JSON side only")
  @NonNull /* package */ ServerExceptionInformation serverException;
  /*
  @NonNull private String serverExceptionMessage;
  @NonNull private String serverExceptionType;
  @NonNull private List<ExceptionInformation> exceptionsInformation = new ArrayList<ExceptionInformation>();
  */

  protected ErrorResponseData( Throwable exception, boolean debugOn ) {
    super( TransferType.SERVER_EXCEPTION);
    assert exception != null;
    
    Throwable reportedException = ExceptionUtils.getFirstRelevantExceptionToReport(exception);
    String message = ExceptionUtils.getExceptionMessage(reportedException);
    String where = ExceptionUtils.getExceptionWhere(reportedException, debugOn);
    
    this.message = message;
    this.where = where;
    this.serverException = new ServerExceptionInformation( exception, debugOn );
  }
}
