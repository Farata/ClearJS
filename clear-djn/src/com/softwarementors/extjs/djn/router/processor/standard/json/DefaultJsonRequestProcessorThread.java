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

package com.softwarementors.extjs.djn.router.processor.standard.json;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DefaultJsonRequestProcessorThread implements JsonRequestProcessorThread {

  @NonNull private JsonRequestProcessor processor;
  @NonNull private JsonRequestData request;
  private int requestNumber;
  
  public DefaultJsonRequestProcessorThread() {
    // Do nothing
  }

  public void initialize(JsonRequestProcessor processor, JsonRequestData request, int requestNumber) {
    this.processor = processor;
    this.request = request;
    this.requestNumber = requestNumber;
  }
  
  // @Override
  public final String call() throws Exception {
    /*
    NDC.push( "thread=" + Thread.currentThread().getId() );
    try {
    */
    /*
      WebContextManager.attachWebContextToCurrentThread(this.webContext);
      try {
    */
     return processRequest();
    /*
      }
      finally {
        WebContextManager.detachFromCurrentThread();
      }
    */
    /*
    }
    finally {
      NDC.pop();
    }
    */
  }

  public String processRequest() {
    return this.processor.processIndividualRequest( this.request, true, this.requestNumber  );
  }
}
