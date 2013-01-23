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

package com.softwarementors.extjs.djn.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import com.softwarementors.extjs.djn.EncodingUtils;
import com.softwarementors.extjs.djn.StringUtils;
import com.softwarementors.extjs.djn.Timer;
import com.softwarementors.extjs.djn.api.Registry;
import com.softwarementors.extjs.djn.config.ApiConfiguration;
import com.softwarementors.extjs.djn.config.GlobalConfiguration;
import com.softwarementors.extjs.djn.gson.GsonBuilderConfigurator;
import com.softwarementors.extjs.djn.jscodegen.CodeFileGenerator;
import com.softwarementors.extjs.djn.router.RequestRouter;
import com.softwarementors.extjs.djn.router.RequestType;
import com.softwarementors.extjs.djn.router.dispatcher.Dispatcher;
import com.softwarementors.extjs.djn.router.dispatcher.DispatcherConfigurationException;
import com.softwarementors.extjs.djn.router.processor.RequestException;
import com.softwarementors.extjs.djn.router.processor.poll.PollRequestProcessor;
import com.softwarementors.extjs.djn.router.processor.standard.form.upload.UploadFormPostRequestProcessor;
import com.softwarementors.extjs.djn.router.processor.standard.json.JsonRequestProcessorThread;
import com.softwarementors.extjs.djn.scanner.Scanner;
import com.softwarementors.extjs.djn.servlet.config.RegistryConfigurationException;
import com.softwarementors.extjs.djn.servlet.ssm.SsmDispatcher;
import com.softwarementors.extjs.djn.servlet.ssm.SsmJsonRequestProcessorThread;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

public class DirectJNgineServlet extends HttpServlet {

  private static final long serialVersionUID = -5621879599626932408L;

  @NonNull
  private static final Logger logger = Logger.getLogger( DirectJNgineServlet.class);

  /*********************************************************  
   * GlobalParameters and configuration
   *********************************************************/
  private static final String VALUES_SEPARATOR = ",";
  public static final String REGISTRY_CONFIGURATOR_CLASS = "registryConfiguratorClass";
  
  /* We handle processors and uploaders via a static map for several reasons:

     1. Public fields are not ok: ServletFileUpload is simply not serializable, and
        servlets are serializable. Besides, using servlet fields to hold state can
        be problematic.
     2. A single static variable is not ok, because it is possible to instantiate
        a servlet several times via <servlet> entries in web.xml, each one having
        a different configuration and therefore requiring a different RequestRouter and
        ServletFileUpload.

     The solution was to have static maps, keyed by servlet name, which is always
     unique in a web application.
  */
  @NonNull private static Map<String,RequestRouter> processors = new HashMap<String,RequestRouter>();
  @NonNull private static Map<String,ServletFileUpload> uploaders = new HashMap<String,ServletFileUpload>();
  
  // Non-mutable => no need to worry about thread-safety => can be an 'instance' variable
  protected RequestRouter getProcessor() {
    assert processors.containsKey(getServletName());
    
    return processors.get(getServletName());
  }
  
  // Non-mutable => no need to worry about thread-safety => can be an 'instance' variable
  protected ServletFileUpload getUploader() {
    assert uploaders.containsKey(getServletName());
    
    return uploaders.get(getServletName());
  }
  
  // This can be static: we do not worry if we get the same id 
  // in a load-balanced system
  private static long id = 1000; // It is good for formatting to get lots of ids with the same number of digits...

  public static class GlobalParameters {
    @NonNull public static final String PROVIDERS_URL = "providersUrl";
    @NonNull public static final String DEBUG = "debug";
    
    @NonNull private static final String APIS_PARAMETER = "apis";
    @NonNull private static final String MINIFY = "minify";

    @NonNull public static final String BATCH_REQUESTS_MULTITHREADING_ENABLED = "batchRequestsMultithreadingEnabled";
    @NonNull public static final String BATCH_REQUESTS_MIN_THREADS_POOOL_SIZE = "batchRequestsMinThreadsPoolSize";
    @NonNull public static final String BATCH_REQUESTS_MAX_THREADS_POOOL_SIZE = "batchRequestsMaxThreadsPoolSize";
    @NonNull public static final String BATCH_REQUESTS_THREAD_KEEP_ALIVE_SECONDS = "batchRequestsMaxThreadKeepAliveSeconds";
    @NonNull public static final String BATCH_REQUESTS_MAX_THREADS_PER_REQUEST = "batchRequestsMaxThreadsPerRequest";
    @NonNull public static final String GSON_BUILDER_CONFIGURATOR_CLASS = "gsonBuilderConfiguratorClass";
    @NonNull public static final String DISPATCHER_CLASS = "dispatcherClass";
    @NonNull public static final String JSON_REQUEST_PROCESSOR_THREAD_CLASS = "jsonRequestProcessorThreadClass";
    @NonNull public static final String CONTEXT_PATH = "contextPath";
    @NonNull public static final String CREATE_SOURCE_FILES="createSourceFiles";
  }

  public static class ApiParameters {
    @NonNull public static final String API_FILE = "apiFile";
    @NonNull public static final String API_NAMESPACE = "apiNamespace";
    @NonNull public static final String ACTIONS_NAMESPACE = "actionsNamespace";
    @NonNull public static final String CLASSES = "classes";
  }

  private static synchronized long getUniqueRequestId() {
    return id++;
  }
  
  @Override
  public void init(ServletConfig configuration) throws ServletException
  {
    assert configuration != null;
    super.init(configuration);
    
    Timer timer = new Timer();
    createDirectJNgineRouter(configuration);
    timer.stop();
    timer.logDebugTimeInMilliseconds("Djn initialization: total DirectJNgine initialization time");
  }

  protected void createDirectJNgineRouter(ServletConfig configuration) throws ServletException {
    assert configuration != null;

    Timer subtaskTimer = new Timer();
    GlobalConfiguration globalConfiguration = createGlobalConfiguration(configuration);
    String registryConfiguratorClassName = ServletUtils.getParameter(configuration, REGISTRY_CONFIGURATOR_CLASS, null);   
    if( logger.isInfoEnabled() ) {
      String value = registryConfiguratorClassName;
      if( value == null) {
        value = "";
      }
      logger.info( "Servlet GLOBAL configuration: " + REGISTRY_CONFIGURATOR_CLASS + "=" + value ); 
    }
    
    Class<? extends ServletRegistryConfigurator> registryConfiguratorClass = getRegistryConfiguratorClass(registryConfiguratorClassName);
    List<ApiConfiguration> apiConfigurations = createApiConfigurationsFromServletConfigurationApi(configuration);
    subtaskTimer.stop();
    subtaskTimer.logDebugTimeInMilliseconds("Djn initialization: Servlet Configuration Load time");
    
    subtaskTimer.restart();
    Registry registry = new Registry( globalConfiguration );
    Scanner scanner = new Scanner(registry);
    scanner.scanAndRegisterApiConfigurations( apiConfigurations );
    subtaskTimer.stop();
    subtaskTimer.logDebugTimeInMilliseconds("Djn initialization: Standard Api processing time");

    if( registryConfiguratorClass != null ) {
      subtaskTimer.restart();
      performCustomRegistryConfiguration( registryConfiguratorClass, registry, configuration );
      subtaskTimer.stop();
      subtaskTimer.logDebugTimeInMilliseconds("Djn initialization: Custom Registry processing time");
    }
    
    subtaskTimer.restart();
    try {
      CodeFileGenerator.updateSource(registry, globalConfiguration.getCreateSourceFiles());      
      subtaskTimer.stop();
      subtaskTimer.logDebugTimeInMilliseconds("Djn initialization: Api Files creation time");
    }
    catch( IOException ex ) {
      ServletException e = new ServletException( "Unable to create DirectJNgine API files",  ex );
      logger.fatal( e.getMessage(), e );
       throw e;
    }

    subtaskTimer.restart();
    
    initializeRouter(globalConfiguration, registry);
    
    subtaskTimer.stop();
    subtaskTimer.logDebugTimeInMilliseconds("Djn initialization: Request Processor initialization time");    
  }

  private void initializeRouter(GlobalConfiguration globalConfiguration, Registry registry) {
    String servletName = getServletName();
    uploaders.put( servletName, UploadFormPostRequestProcessor.createFileUploader() );
    processors.put( servletName, createRequestRouter(registry, globalConfiguration) );
  }

  protected RequestRouter createRequestRouter(Registry registry, GlobalConfiguration globalConfiguration) {
    assert registry != null;
    assert globalConfiguration != null;
    
    return new RequestRouter( registry, globalConfiguration, createDispatcher(globalConfiguration.getDispatcherClass()) );
  }
  
  protected Dispatcher createDispatcher( Class<? extends Dispatcher> cls ) {
    assert cls != null;
    try {
      return cls.newInstance();
    }
    catch (InstantiationException e) {
      DispatcherConfigurationException ex = DispatcherConfigurationException.forUnableToInstantiateDispatcher(cls, e);
      logger.fatal( ex.getMessage(), ex);
      throw ex;
    }
    catch (IllegalAccessException e) {
      DispatcherConfigurationException ex = DispatcherConfigurationException.forUnableToInstantiateDispatcher(cls, e);
      logger.fatal( ex.getMessage(), ex);
      throw ex;
    }     
  }
  
  protected void performCustomRegistryConfiguration(Class<? extends ServletRegistryConfigurator> configuratorClass, Registry registry, ServletConfig config) {
    ServletRegistryConfigurator registryConfigurator = createCustomRegistryConfigurator( configuratorClass ); // registry.getGlobalConfiguration().getRegistryConfiguratorClass() ).configure( registry, configuration);
    registryConfigurator.configure(registry, config);
  }
  
  private static ServletRegistryConfigurator createCustomRegistryConfigurator( Class<? extends ServletRegistryConfigurator> configuratorClass ) {
    assert configuratorClass != null;
    
    try {
      return configuratorClass.newInstance();
    }
    catch (InstantiationException e) {
      RegistryConfigurationException ex = RegistryConfigurationException.forUnableToInstantiateRegistryConfigurator(configuratorClass, e);
      logger.fatal( ex.getMessage(), ex);
      throw ex;
    }
    catch (IllegalAccessException e) {
      RegistryConfigurationException ex = RegistryConfigurationException.forUnableToInstantiateRegistryConfigurator(configuratorClass, e);
      logger.fatal( ex.getMessage(), ex);
      throw ex;
    }     
  }

  protected GlobalConfiguration createGlobalConfiguration(ServletConfig configuration) {
    assert configuration != null;
    
    ServletUtils.checkRequiredParameters(configuration, GlobalParameters.PROVIDERS_URL);
    
    boolean isDebug = ServletUtils.getBooleanParameter( configuration, GlobalParameters.DEBUG, GlobalConfiguration.DEFAULT_DEBUG_VALUE);
    String providersUrl = ServletUtils.getRequiredParameter(configuration, GlobalParameters.PROVIDERS_URL);
    String gsonConfiguratorClassName = ServletUtils.getParameter(configuration, GlobalParameters.GSON_BUILDER_CONFIGURATOR_CLASS, GlobalConfiguration.DEFAULT_GSON_BUILDER_CONFIGURATOR_CLASS.getName());
    String dispatcherClassName = ServletUtils.getParameter(configuration, GlobalParameters.DISPATCHER_CLASS, /*GlobalConfiguration.DEFAULT_DISPATCHER_CLASS.getName()*/ getDefaultDispatcherClass().getName());    
    String jsonRequestProcessorThreadClassName = ServletUtils.getParameter(configuration, GlobalParameters.JSON_REQUEST_PROCESSOR_THREAD_CLASS, /*GlobalConfiguration.DEFAULT_JSON_REQUEST_PROCESSOR_THREAD_CLASS.getName()*/ getDefaultJsonRequestProcessoThreadClass().getName());    
    
    // Global multithreaded-batched requests support parameters
    boolean isBatchRequestsMultithreadingEnabled = ServletUtils.getBooleanParameter( configuration, GlobalParameters.BATCH_REQUESTS_MULTITHREADING_ENABLED, GlobalConfiguration.DEFAULT_BATCH_REQUESTS_MULTITHREADING_ENABLED_VALUE);
    boolean minifyEnabled = ServletUtils.getBooleanParameter( configuration, GlobalParameters.MINIFY, GlobalConfiguration.DEFAULT_MINIFY_VALUE);
    
    int batchRequestsMinThreadsPoolSize = ServletUtils.getIntParameterGreaterOrEqualToValue(
       configuration, GlobalParameters.BATCH_REQUESTS_MIN_THREADS_POOOL_SIZE, 
       GlobalConfiguration.MIN_BATCH_REQUESTS_MIN_THREAD_POOL_SIZE, GlobalConfiguration.DEFAULT_BATCH_REQUESTS_MIN_THREAD_POOL_SIZE);
    int batchRequestsMaxThreadsPoolSize = ServletUtils.getIntParameterGreaterOrEqualToValue(
        configuration, GlobalParameters.BATCH_REQUESTS_MAX_THREADS_POOOL_SIZE, 
        GlobalConfiguration.MIN_BATCH_REQUESTS_MAX_THREAD_POOL_SIZE, GlobalConfiguration.DEFAULT_BATCH_REQUESTS_MAX_THREAD_POOL_SIZE);
    int batchRequestsThreadKeepAliveSeconds = ServletUtils.getIntParameterGreaterOrEqualToValue(
        configuration, GlobalParameters.BATCH_REQUESTS_THREAD_KEEP_ALIVE_SECONDS, 
        GlobalConfiguration.MIN_BATCH_REQUESTS_THREAD_KEEP_ALIVE_SECONDS, GlobalConfiguration.DEFAULT_BATCH_REQUESTS_THREAD_KEEP_ALIVE_SECONDS);
    int batchRequestsMaxThreadsPerRequest = ServletUtils.getIntParameterGreaterOrEqualToValue(
        configuration, GlobalParameters.BATCH_REQUESTS_MAX_THREADS_PER_REQUEST, 
        GlobalConfiguration.MIN_BATCH_REQUESTS_MAX_THREADS_PER_REQUEST, GlobalConfiguration.DEFAULT_BATCH_REQUESTS_MAX_THREADS_PER_REQUEST);
    String contextPath = configuration.getInitParameter( GlobalParameters.CONTEXT_PATH );
    boolean createSourceFiles = ServletUtils.getBooleanParameter(configuration, GlobalParameters.CREATE_SOURCE_FILES, GlobalConfiguration.DEFAULT_CREATE_SOURCE_FILES);
    
    if( batchRequestsMinThreadsPoolSize > batchRequestsMaxThreadsPoolSize ) {
      ServletConfigurationException ex = ServletConfigurationException.forMaxThreadPoolSizeMustBeEqualOrGreaterThanMinThreadPoolSize(batchRequestsMinThreadsPoolSize, batchRequestsMaxThreadsPoolSize);
      logger.fatal( ex.getMessage(), ex );
      throw ex;
    }
    
    if( logger.isInfoEnabled() ) {
      String contextPathInfo = contextPath;
      if( contextPathInfo == null ) {
        contextPathInfo = "--not specified: calculated via Javascript--";
      }
      logger.info( "Servlet GLOBAL configuration: " + 
        GlobalParameters.DEBUG + "=" + isDebug + ", " +
        GlobalParameters.PROVIDERS_URL + "=" + providersUrl + ", " +
        GlobalParameters.MINIFY + "=" + minifyEnabled + ", " +
        GlobalParameters.BATCH_REQUESTS_MULTITHREADING_ENABLED + "=" + isBatchRequestsMultithreadingEnabled + ", " + 
        GlobalParameters.BATCH_REQUESTS_MIN_THREADS_POOOL_SIZE + "=" + batchRequestsMinThreadsPoolSize + ", " + 
        GlobalParameters.BATCH_REQUESTS_MAX_THREADS_POOOL_SIZE + "=" + batchRequestsMaxThreadsPoolSize + ", " + 
        GlobalParameters.BATCH_REQUESTS_MAX_THREADS_PER_REQUEST + "=" + batchRequestsMaxThreadsPerRequest + ", " + 
        GlobalParameters.BATCH_REQUESTS_THREAD_KEEP_ALIVE_SECONDS + "=" + batchRequestsThreadKeepAliveSeconds + ", " + 
        GlobalParameters.GSON_BUILDER_CONFIGURATOR_CLASS + "=" + gsonConfiguratorClassName + ", " +
        GlobalParameters.DISPATCHER_CLASS + "=" + dispatcherClassName + ", " +
        GlobalParameters.JSON_REQUEST_PROCESSOR_THREAD_CLASS + "=" + jsonRequestProcessorThreadClassName + ", " +
        GlobalParameters.CONTEXT_PATH + "=" + contextPathInfo + ", " +
        GlobalParameters.CREATE_SOURCE_FILES + "=" + createSourceFiles
      );
    }
    
    Class<? extends GsonBuilderConfigurator> gsonConfiguratorClass = getGsonBuilderConfiguratorClass(gsonConfiguratorClassName);
    Class<? extends Dispatcher> dispatcherClass = getDispatcherClass(dispatcherClassName);
    Class<? extends JsonRequestProcessorThread> jsonRequestProcessorClass = getJsonRequestProcessorThreadClass(jsonRequestProcessorThreadClassName);
    
    GlobalConfiguration result = new GlobalConfiguration( 
      contextPath,
      providersUrl, isDebug, 
      gsonConfiguratorClass, jsonRequestProcessorClass, dispatcherClass,
      minifyEnabled, 
      isBatchRequestsMultithreadingEnabled, batchRequestsMinThreadsPoolSize, batchRequestsMaxThreadsPoolSize,
      batchRequestsThreadKeepAliveSeconds, batchRequestsMaxThreadsPerRequest,
      createSourceFiles);
    return result;
  }

  private static Class<? extends JsonRequestProcessorThread> getDefaultJsonRequestProcessoThreadClass() {
    return SsmJsonRequestProcessorThread.class;
  }

  private static Class<? extends Dispatcher> getDefaultDispatcherClass() {
    return SsmDispatcher.class;
  }

  @SuppressWarnings("unchecked")
  private static Class<? extends GsonBuilderConfigurator> getGsonBuilderConfiguratorClass(String gsonConfiguratorClassName) {
    assert !StringUtils.isEmpty(gsonConfiguratorClassName);
    
    Class<? extends GsonBuilderConfigurator> configuratorClass;
    try {
      configuratorClass = (Class<GsonBuilderConfigurator>)Class.forName(gsonConfiguratorClassName);
      if( !GsonBuilderConfigurator.class.isAssignableFrom(configuratorClass)) {
        ServletConfigurationException ex = ServletConfigurationException.forGsonBuilderConfiguratorMustImplementGsonBuilderConfiguratorInterface(gsonConfiguratorClassName ); 
        logger.fatal( ex.getMessage(), ex );
        throw ex;
      }
      return configuratorClass;
    }
    catch( ClassNotFoundException ex ) {
      ServletConfigurationException e = ServletConfigurationException.forClassNotFound(gsonConfiguratorClassName, ex ); 
      logger.fatal( e.getMessage(), e );
      throw e;
    }
  }

  @SuppressWarnings("unchecked")
  private static Class<? extends Dispatcher> getDispatcherClass(String dispatcherClassName) {
    assert !StringUtils.isEmpty(dispatcherClassName);
    
    Class<? extends Dispatcher> configuratorClass;
    try {
      configuratorClass = (Class<Dispatcher>)Class.forName(dispatcherClassName);
      if( !Dispatcher.class.isAssignableFrom(configuratorClass)) {
        ServletConfigurationException ex = ServletConfigurationException.forDispatcherMustImplementDispatcherInterface(dispatcherClassName ); 
        logger.fatal( ex.getMessage(), ex );
        throw ex;
      }
      return configuratorClass;
    }
    catch( ClassNotFoundException ex ) {
      ServletConfigurationException e = ServletConfigurationException.forClassNotFound(dispatcherClassName, ex ); 
      logger.fatal( e.getMessage(), e );
      throw e;
    }
  }

  @SuppressWarnings("unchecked")
  private static Class<? extends JsonRequestProcessorThread> getJsonRequestProcessorThreadClass(String jsonRequestProcessorThreadClassName) {
    assert !StringUtils.isEmpty(jsonRequestProcessorThreadClassName);
    
    Class<? extends JsonRequestProcessorThread> cls;
    try {
      cls = (Class<JsonRequestProcessorThread>)Class.forName(jsonRequestProcessorThreadClassName);
      if( !JsonRequestProcessorThread.class.isAssignableFrom(cls)) {
        ServletConfigurationException ex = ServletConfigurationException.forJsonRequestProcessorThreadMustImplementJsonRequestProcessorThreadInterface(jsonRequestProcessorThreadClassName ); 
        logger.fatal( ex.getMessage(), ex );
        throw ex;
      }
      return cls;
    }
    catch( ClassNotFoundException ex ) {
      ServletConfigurationException e = ServletConfigurationException.forClassNotFound(jsonRequestProcessorThreadClassName, ex ); 
      logger.fatal( e.getMessage(), e );
      throw e;
    }
  }

  @SuppressWarnings("unchecked")
  @CheckForNull private static Class<? extends ServletRegistryConfigurator> getRegistryConfiguratorClass(String registryConfiguratorClassName) {
    if( StringUtils.isEmpty(registryConfiguratorClassName)) {
      return null;
    }
    
    Class<? extends ServletRegistryConfigurator> configuratorClass;
    try {
      configuratorClass = (Class<ServletRegistryConfigurator>)Class.forName(registryConfiguratorClassName);
      if( !ServletRegistryConfigurator.class.isAssignableFrom(configuratorClass)) {
        ServletConfigurationException ex = ServletConfigurationException.forRegistryConfiguratorMustImplementGsonBuilderConfiguratorInterface(registryConfiguratorClassName ); 
        logger.fatal( ex.getMessage(), ex );
        throw ex;
      }
      return configuratorClass;
    }
    catch( ClassNotFoundException ex ) {
      ServletConfigurationException e = ServletConfigurationException.forClassNotFound(registryConfiguratorClassName, ex ); 
      logger.fatal( e.getMessage(), e );
      throw e;
    }
  }

  protected List<ApiConfiguration> createApiConfigurationsFromServletConfigurationApi(ServletConfig configuration) {
    assert configuration != null;

    List<ApiConfiguration> result = new ArrayList<ApiConfiguration>();    
    String apisParameter = ServletUtils.getRequiredParameter(configuration, GlobalParameters.APIS_PARAMETER);
    List<String> apis = StringUtils.getNonBlankValues(apisParameter, VALUES_SEPARATOR);
    logger.info( "Servlet APIs configuration: " + GlobalParameters.APIS_PARAMETER + "=" + apisParameter ); 
    
    for( String api : apis) {
      ApiConfiguration apiConfiguration = createApiConfigurationFromServletConfigurationApi( configuration, api );
      result.add( apiConfiguration );
    }
    
    if( result.isEmpty() ) {
      logger.warn( "No apis specified");
    }
    
    return result;
  }

  private ApiConfiguration createApiConfigurationFromServletConfigurationApi(ServletConfig configuration, String api) {
    assert configuration != null;
    assert !StringUtils.isEmpty(api);
    
    String apiFile = ServletUtils.getParameter( configuration, api + "." + ApiParameters.API_FILE, api + ApiConfiguration.DEFAULT_API_FILE_SUFFIX );
    String fullGeneratedApiFile = getServletContext().getRealPath(apiFile);    
    String apiNamespace = ServletUtils.getParameter( configuration, api + "." + ApiParameters.API_NAMESPACE, "" );
    assert apiNamespace != null;
    String actionsNamespace = ServletUtils.getParameter( configuration, api + "." + ApiParameters.ACTIONS_NAMESPACE, "" );

    // If apiNamespace is empty, try to use actionsNamespace: if still empty, use the api name itself
    if( apiNamespace.equals("")) {
      if( actionsNamespace.equals("")) {
        apiNamespace = ApiConfiguration.DEFAULT_NAMESPACE_PREFIX + api;
        if( logger.isDebugEnabled() ) {
          logger.debug( "Using the api name, prefixed with '" + ApiConfiguration.DEFAULT_NAMESPACE_PREFIX + "' as the value for " + ApiParameters.API_NAMESPACE);          
        }
      }
      else {
        apiNamespace = actionsNamespace;
        logger.debug( "Using " + ApiParameters.ACTIONS_NAMESPACE + " as the value for " + ApiParameters.API_NAMESPACE);                  
      }
    }
    
    String classNames = ServletUtils.getParameter( configuration, api + "." + ApiParameters.CLASSES, "" );
    List<Class<?>> classes = getClasses( classNames );
    
    if( logger.isInfoEnabled() ) {
      logger.info( "Servlet '" + api + "' Api configuration: " +
        ApiParameters.API_NAMESPACE + "=" + apiNamespace + ", " +
        ApiParameters.ACTIONS_NAMESPACE + "=" + actionsNamespace + ", " +
        ApiParameters.API_FILE + "=" + apiFile + " => Full api file: " + fullGeneratedApiFile + ", " +
        ApiParameters.CLASSES + "=" + classNames);
    }
    
    if( classes.isEmpty() ) {
      logger.warn( "There are no action classes to register for api '" + api + "'");
    }
    ApiConfiguration apiConfiguration = new ApiConfiguration( api, apiFile, fullGeneratedApiFile, apiNamespace, actionsNamespace, classes );
        
    return apiConfiguration;
  }

  private static List<Class<?>> getClasses( String classes )  {
    assert classes != null;
    
    List<Class<?>> result = new ArrayList<Class<?>>();
    if( StringUtils.isEmpty(classes) ) {
      return result;
    }
    List<String> classNames = StringUtils.getNonBlankValues( classes, VALUES_SEPARATOR );
    
    for( String className : classNames ) {
      try {
        Class<?> cls = Class.forName( className );
        result.add( cls );
      }
      catch( ClassNotFoundException ex ) {
        logger.fatal( ex.getMessage(), ex );
        ServletConfigurationException e = ServletConfigurationException.forClassNotFound(className, ex ); 
        throw e;
      }
    }
    
    return result;
  }
  
  private static RequestType getFromRequestContentType( HttpServletRequest request ) {
    assert request != null;
    
    String contentType = request.getContentType();
    String pathInfo = request.getPathInfo();
    
    if( !StringUtils.isEmpty(pathInfo) && pathInfo.startsWith( PollRequestProcessor.PATHINFO_POLL_PREFIX)) {
      return RequestType.POLL;
    }
    else if( StringUtils.startsWithCaseInsensitive( contentType, "application/json") ) {
      return RequestType.JSON;
    }
    else if( StringUtils.startsWithCaseInsensitive( contentType, "application/x-www-form-urlencoded") && request.getMethod().equalsIgnoreCase("post")) {
      return RequestType.FORM_SIMPLE_POST;
    }
    else if( ServletFileUpload.isMultipartContent(request)) {
      return RequestType.FORM_UPLOAD_POST;
    }
    else if( RequestRouter.isSourceRequest(pathInfo)) {
      return RequestType.SOURCE;
    }
    else {
      String requestInfo = ServletUtils.getDetailedRequestInformation(request);      
      RequestException ex = RequestException.forRequestFormatNotRecognized();
      logger.error( "Error during file uploader: " + ex.getMessage() + "\nAdditional request information: " + requestInfo, ex );
      throw ex;
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    assert request != null;
    assert response != null;
    doPost(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    assert request != null;
    assert response != null;

    NDC.push( "rid=" + Long.toString(getUniqueRequestId()) );
    try {
      Timer timer = new Timer();
      try {
        attachThreadLocalData( request, response);
        try {
          if( logger.isTraceEnabled()) {
            String requestInfo = ServletUtils.getDetailedRequestInformation(request);
            logger.trace( "Request info: " + requestInfo);
          }        

          String requestEncoding = request.getCharacterEncoding();
          // If we don't know what the request encoding is, assume it to be UTF-8
          if( StringUtils.isEmpty(requestEncoding)) {
            request.setCharacterEncoding(EncodingUtils.UTF8);
          }
          response.setCharacterEncoding(EncodingUtils.UTF8);

          RequestType type = getFromRequestContentType(request);
          processRequest(request, response, type);
        }
        finally {
          detachThreadLocalData();
        }
      }
      finally {
        timer.stop();
        timer.logDebugTimeInMilliseconds("Total servlet processing time");
      }

    }
    finally {
      NDC.pop();
    }
  }

  @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SF_SWITCH_NO_DEFAULT",
      justification="Missing a 'default' branch is not a problem with enums, given the appropriate compiler settings")
  private void processRequest(HttpServletRequest request, HttpServletResponse response, RequestType type)
      throws IOException {
   final String JSON_CONTENT_TYPE = "application/json";
   final String JAVASCRIPT_CONTENT_TYPE = "text/javascript"; // *YES*, shoul be "application/javascript", but then there is IE, and the fact that this is really cross-browser (sigh!)
   final String HTML_CONTENT_TYPE = "text/html";
    
   RequestRouter processor = getProcessor();
    switch( type ) {    
    case FORM_SIMPLE_POST:
      response.setContentType(JSON_CONTENT_TYPE); 
      processor.processSimpleFormPostRequest( request.getReader(), response.getWriter() );
      break;
    case FORM_UPLOAD_POST:
      response.setContentType(HTML_CONTENT_TYPE); // MUST be "text/html" for uploads to work!
      processUploadFormPost(request, response);
      break;
    case JSON:
      response.setContentType(JSON_CONTENT_TYPE); 
      processor.processJsonRequest( request.getReader(), response.getWriter() );
      break;
    case POLL:
      response.setContentType(JSON_CONTENT_TYPE); 
      processor.processPollRequest( request.getReader(), response.getWriter(), request.getPathInfo() );
      break;
    case SOURCE:
      response.setContentType(JAVASCRIPT_CONTENT_TYPE); 
      processor.processSourceRequest( request.getReader(), response.getWriter(), request.getPathInfo());
      break;
    }
  }

  protected void attachThreadLocalData( HttpServletRequest request, HttpServletResponse response) {
    WebContextManager.initializeWebContextForCurrentThread(this, request, response);
  }

  protected void detachThreadLocalData() {
    WebContextManager.detachFromCurrentThread();
  }
  
  private void processUploadFormPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    assert request != null;
    assert response != null;
    
    RequestRouter router = getProcessor();
    UploadFormPostRequestProcessor processor = router.createUploadFromProcessor();
    try {
       router.processUploadFormPostRequest( processor, getFileItems(request), response.getWriter() );
    }
    catch( FileUploadException e ) {
      processor.handleFileUploadException( e );
    }
  }

  @SuppressWarnings("unchecked")
  private List<FileItem> getFileItems(HttpServletRequest request) throws FileUploadException {
    assert request != null;

    ServletFileUpload uploader = getUploader();
    return uploader.parseRequest(request);
  }

}
