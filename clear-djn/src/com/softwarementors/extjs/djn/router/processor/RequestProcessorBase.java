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

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.softwarementors.extjs.djn.api.Registry;
import com.softwarementors.extjs.djn.config.GlobalConfiguration;
import com.softwarementors.extjs.djn.gson.GsonBuilderConfigurator;
import com.softwarementors.extjs.djn.gson.GsonBuilderConfiguratorException;
import com.softwarementors.extjs.djn.gson.JsonException;
import com.softwarementors.extjs.djn.router.dispatcher.Dispatcher;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class RequestProcessorBase {
  
  @NonNull private static Logger logger = Logger.getLogger(RequestProcessorBase.class);  
  @NonNull private Dispatcher dispatcher;
  @NonNull private Registry registry;
  @NonNull private GlobalConfiguration globalConfiguration;
  /************************ Per-thread */
  @CheckForNull private static Gson gson; // = new ThreadLocal<Gson>();
  /************************ Per-thread: end */
  
  protected GlobalConfiguration getGlobalConfiguration() {
    return this.globalConfiguration;
  }
    
  protected RequestProcessorBase( Registry registry, Dispatcher dispatcher, GlobalConfiguration globalConfiguration ) {
    assert registry != null;
    assert dispatcher != null;
    assert globalConfiguration != null;
      
    this.dispatcher = dispatcher;
    this.globalConfiguration = globalConfiguration;
    this.registry = registry;
  }
    
  protected Registry getRegistry() {
    return this.registry;
  }
  
  protected Dispatcher getDispatcher() {
    return this.dispatcher;
  }
    
  protected boolean getDebug() {
    return this.globalConfiguration.getDebug();
  }
  
  // ************************************************************
  // * JSON support
  // ************************************************************
  protected void appendIndividualResponseJsonString(ResponseData response, StringBuilder result) {
    assert response != null;
    assert result != null;
    
    try {
      getGson().toJson( response, result );
    }
    catch( JsonParseException ex ) {
      JsonException.forFailedConversionFromResponseToJson(response, ex);
    }
  }    
  
  protected synchronized Gson getGson() {
    /****************************** Per-thread */
    /*
    Gson result = this.gson.get();
    if( result == null ) {
      GsonBuilder builder = new GsonBuilder();
      createGsonBuilderConfigurator().configure( builder, getGlobalConfiguration());
      result = builder.create();
      this.gson.set( result );
    }
    return result;
    */
    /****************************** Per-thread: end */

    /****************************** Per-instance */
    if( gson == null ) {
      GsonBuilder builder = new GsonBuilder();
      createGsonBuilderConfigurator().configure( builder, getGlobalConfiguration());
      gson = builder.create();
    }
    return gson;
    /******************************* Per-intance: end */
  }

  private GsonBuilderConfigurator createGsonBuilderConfigurator() {
    Class<? extends GsonBuilderConfigurator> configuratorClass = getGlobalConfiguration().getGsonBuilderConfiguratorClass(); 
    try {
      return configuratorClass.newInstance();
    }
    catch (InstantiationException e) {
      GsonBuilderConfiguratorException ex = GsonBuilderConfiguratorException.forUnableToInstantiateGsonBuilder(configuratorClass, e);
      logger.fatal( ex.getMessage(), ex);
      throw ex;
    }
    catch (IllegalAccessException e) {
      GsonBuilderConfiguratorException ex = GsonBuilderConfiguratorException.forUnableToInstantiateGsonBuilder(configuratorClass, e);
      logger.fatal( ex.getMessage(), ex);
      throw ex;
    }     
  }
  
}
