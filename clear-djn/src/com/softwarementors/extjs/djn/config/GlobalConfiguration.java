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

import com.softwarementors.extjs.djn.StringUtils;
import com.softwarementors.extjs.djn.gson.DefaultGsonBuilderConfigurator;
import com.softwarementors.extjs.djn.gson.GsonBuilderConfigurator;
import com.softwarementors.extjs.djn.router.dispatcher.Dispatcher;
import com.softwarementors.extjs.djn.router.processor.standard.json.DefaultJsonRequestProcessorThread;
import com.softwarementors.extjs.djn.router.processor.standard.json.JsonRequestProcessorThread;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

public class GlobalConfiguration {
  public static final boolean DEFAULT_DEBUG_VALUE = false;
  @CheckForNull private String contextPath;
  @NonNull private String providersUrl;
  private boolean debug;
  @NonNull private Class<? extends GsonBuilderConfigurator> gsonBuilderConfiguratorClass;
  @NonNull private Class<? extends JsonRequestProcessorThread> jsonRequestProcessorThreadClass;
  @NonNull private Class<? extends Dispatcher> dispatcherClass;
  private boolean batchRequestsMultithreadingEnabled;
  private int batchRequestsMinThreadsPoolSize;
  private int batchRequestsMaxThreadsPoolSize; 
  private int batchRequestsThreadKeepAliveSeconds;
  private int batchRequestsMaxThreadsPerRequest;
  private boolean minify;
  private boolean createSourceFiles;
  
  public static final int MIN_BATCH_REQUESTS_MIN_THREAD_POOL_SIZE = 0;
  public static final int MIN_BATCH_REQUESTS_MAX_THREAD_POOL_SIZE = 1;
  public static final int MIN_BATCH_REQUESTS_MAX_THREADS_PER_REQUEST = 1;
  public static final int MIN_BATCH_REQUESTS_THREAD_KEEP_ALIVE_SECONDS = 0;

  public static final boolean DEFAULT_BATCH_REQUESTS_MULTITHREADING_ENABLED_VALUE = true;
  public static final int DEFAULT_BATCH_REQUESTS_MAX_THREADS_PER_REQUEST = 8;
  public static final int DEFAULT_BATCH_REQUESTS_MIN_THREAD_POOL_SIZE = DEFAULT_BATCH_REQUESTS_MAX_THREADS_PER_REQUEST * 2;
  public static final int DEFAULT_BATCH_REQUESTS_MAX_THREAD_POOL_SIZE = DEFAULT_BATCH_REQUESTS_MAX_THREADS_PER_REQUEST * 10;
  public static final int DEFAULT_BATCH_REQUESTS_THREAD_KEEP_ALIVE_SECONDS = 60;
  public static final boolean DEFAULT_MINIFY_VALUE = true;
  public static final boolean DEFAULT_CREATE_SOURCE_FILES = true;
  @NonNull public static final Class<? extends GsonBuilderConfigurator> DEFAULT_GSON_BUILDER_CONFIGURATOR_CLASS = DefaultGsonBuilderConfigurator.class;
  @NonNull public static final Class<? extends JsonRequestProcessorThread> DEFAULT_JSON_REQUEST_PROCESSOR_THREAD_CLASS = DefaultJsonRequestProcessorThread.class;

  public GlobalConfiguration( String contextPath, String providersUrl, boolean debug, 
      Class<? extends GsonBuilderConfigurator> gsonBuilderConfiguratorClass,
      Class<? extends JsonRequestProcessorThread> jsonRequestProcessorThreadClass,
      Class<? extends Dispatcher> dispatcherClass,
      boolean minify,
      boolean batchRequestsMultithreadingEnabled, int batchRequestsMinThreadsPoolSize, int batchRequestsMaxThreadsPoolSize, 
      int batchRequestsThreadKeepAliveSeconds, int batchRequestsMaxThreadsPerRequest, boolean createSourceFiles) 
  {
    assert !StringUtils.isEmpty( providersUrl );
    assert batchRequestsMinThreadsPoolSize >= MIN_BATCH_REQUESTS_MIN_THREAD_POOL_SIZE;
    assert batchRequestsMaxThreadsPoolSize >= MIN_BATCH_REQUESTS_MAX_THREAD_POOL_SIZE;
    assert batchRequestsThreadKeepAliveSeconds >= MIN_BATCH_REQUESTS_THREAD_KEEP_ALIVE_SECONDS;
    assert batchRequestsMaxThreadsPerRequest >= MIN_BATCH_REQUESTS_MAX_THREADS_PER_REQUEST;
    assert batchRequestsMinThreadsPoolSize <= batchRequestsMaxThreadsPoolSize;
    assert gsonBuilderConfiguratorClass != null;
    assert jsonRequestProcessorThreadClass != null;
    assert dispatcherClass != null;
    
    this.contextPath = contextPath;
    this.providersUrl = providersUrl;
    this.debug = debug;
    this.minify = minify;
    this.gsonBuilderConfiguratorClass = gsonBuilderConfiguratorClass;
    this.jsonRequestProcessorThreadClass = jsonRequestProcessorThreadClass;
    this.dispatcherClass = dispatcherClass;
        
    this.batchRequestsMultithreadingEnabled = batchRequestsMultithreadingEnabled;
    this.batchRequestsMinThreadsPoolSize = batchRequestsMinThreadsPoolSize;
    this.batchRequestsMaxThreadsPoolSize = batchRequestsMaxThreadsPoolSize; 
    this.batchRequestsThreadKeepAliveSeconds = batchRequestsThreadKeepAliveSeconds;
    this.batchRequestsMaxThreadsPerRequest = batchRequestsMaxThreadsPerRequest;
    
    this.createSourceFiles = createSourceFiles;
  }
  
  public String getProvidersUrl() {
    return this.providersUrl;
  }
  
  public boolean getDebug() {
    return this.debug;
  }

  public Class<? extends GsonBuilderConfigurator> getGsonBuilderConfiguratorClass() {
    return this.gsonBuilderConfiguratorClass;
  }

  public Class<? extends JsonRequestProcessorThread> getJsonRequestProcessorThreadClass() {
    return this.jsonRequestProcessorThreadClass;
  }
  
  public Class<? extends Dispatcher> getDispatcherClass() {
    return this.dispatcherClass;
  }
  
  public boolean getMinify() {
    return this.minify;
  }

  public boolean getBatchRequestsMultithreadingEnabled() {
    return this.batchRequestsMultithreadingEnabled;
  }

  public int getBatchRequestsMinThreadsPoolSize() {
    return this.batchRequestsMinThreadsPoolSize;
  }

  public int getBatchRequestsMaxThreadsPoolSize() {
    return this.batchRequestsMaxThreadsPoolSize;
  }

  public int getBatchRequestsThreadKeepAliveSeconds() {
    return this.batchRequestsThreadKeepAliveSeconds;
  }

  public int getBatchRequestsMaxThreadsPerRequest() {
    return this.batchRequestsMaxThreadsPerRequest;
  }
 
  @CheckForNull public String getContextPath() {
    return this.contextPath;
  }
  
  public boolean getCreateSourceFiles() {
    return this.createSourceFiles;
  }
}
