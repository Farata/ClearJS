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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import clear.djn.DirectOptions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.softwarementors.extjs.djn.ClassUtils;
import com.softwarementors.extjs.djn.ParallelTask;
import com.softwarementors.extjs.djn.StringUtils;
import com.softwarementors.extjs.djn.Timer;
import com.softwarementors.extjs.djn.UnexpectedException;
import com.softwarementors.extjs.djn.api.RegisteredStandardMethod;
import com.softwarementors.extjs.djn.api.Registry;
import com.softwarementors.extjs.djn.config.GlobalConfiguration;
import com.softwarementors.extjs.djn.gson.JsonDeserializationManager;
import com.softwarementors.extjs.djn.gson.JsonException;
import com.softwarementors.extjs.djn.router.dispatcher.Dispatcher;
import com.softwarementors.extjs.djn.router.processor.RequestException;
import com.softwarementors.extjs.djn.router.processor.standard.StandardErrorResponseData;
import com.softwarementors.extjs.djn.router.processor.standard.StandardRequestProcessorBase;
import com.softwarementors.extjs.djn.router.processor.standard.StandardSuccessResponseData;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

public class JsonRequestProcessor extends StandardRequestProcessorBase {
  /* Will not release this until extensive testings is performed */
  private final static boolean SUPPORTS_OBJECT_TYPE_PARAMETER = false;

  @NonNull private static final Logger logger = Logger.getLogger( JsonRequestProcessor.class);
  // We need a globally unique thread-pool, not a pool per processor!
  @CheckForNull private static volatile ExecutorService individualRequestsThreadPool; 
  @NonNull private JsonParser parser = new JsonParser(); 

  protected JsonParser getJsonParser() {
    return this.parser;
  }

  @edu.umd.cs.findbugs.annotations.SuppressWarnings( value="NP_NONNULL_RETURN_VIOLATION", 
      justification="This method will never return null, because if the value it should return is null on entry, it assigns it first")
      private ExecutorService getIndividualRequestsThreadPool() {
    synchronized (JsonRequestProcessor.class) {
      if( individualRequestsThreadPool == null ) {
        individualRequestsThreadPool = createThreadPool();
      }
      return individualRequestsThreadPool;
    }
  }

  private ExecutorService createThreadPool() {
    assert getGlobalConfiguration() != null;

    ExecutorService result = new ThreadPoolExecutor( getGlobalConfiguration().getBatchRequestsMinThreadsPoolSize(),
        getGlobalConfiguration().getBatchRequestsMaxThreadsPoolSize(),
        getGlobalConfiguration().getBatchRequestsThreadKeepAliveSeconds(),
        TimeUnit.SECONDS,        
        new LinkedBlockingQueue<Runnable>());
    return result;
  }

  public JsonRequestProcessor(Registry registry, Dispatcher dispatcher, GlobalConfiguration globalConfiguration) {
    super(registry, dispatcher, globalConfiguration);
  }

  public String process(Reader reader, Writer writer) throws IOException {
    String requestString = IOUtils.toString(reader);    
    if( logger.isDebugEnabled() ) {
      logger.debug( "Request data (JSON)=>" + requestString );
    }

    JsonRequestData[] requests = getIndividualJsonRequests( requestString );        
    final boolean isBatched = requests.length > 1;
    if( isBatched ) {
      if( logger.isDebugEnabled() ) {
        logger.debug( "Batched request: " + requests.length + " individual requests batched");
      }
    }

    Collection<String> responses = null;
    boolean useMultipleThreadsIfBatched = isBatched && getGlobalConfiguration().getBatchRequestsMultithreadingEnabled();
    if( useMultipleThreadsIfBatched ) {
      responses = processIndividualRequestsInMultipleThreads( requests);
    }
    else {
      responses = processIndividualRequestsInThisThread(requests);
    }

    String result = convertInvididualResponsesToJsonString( responses);
    writer.write( result );
    if( logger.isDebugEnabled() ) {
      logger.debug( "ResponseData data (JSON)=>" + result );
    }
    return result;
  }

  private Collection<String> processIndividualRequestsInThisThread(JsonRequestData[] requests) {
    Collection<String> responses;
    boolean isBatched = requests.length > 1;
    responses = new ArrayList<String>(requests.length);
    int requestNumber = 1;
    for( JsonRequestData request : requests ) {
      String response = processIndividualRequest( request, isBatched, requestNumber  );
      responses.add( response );
      requestNumber++;
    }
    return responses;
  }

  private Collection<String> processIndividualRequestsInMultipleThreads( JsonRequestData[] requests) {
    assert requests != null;

    int individualRequestNumber = 1;
    Collection<Callable<String>> tasks = new ArrayList<Callable<String>>(requests.length);
    for (final JsonRequestData request  : requests) {      
      JsonRequestProcessorThread thread = createJsonRequestProcessorThread();
      thread.initialize(this, request, individualRequestNumber);
      tasks.add(thread);
      individualRequestNumber++;
    }    

    try {
      ParallelTask<String> task = new ParallelTask<String>(
          getIndividualRequestsThreadPool(), tasks, getGlobalConfiguration().getBatchRequestsMaxThreadsPerRequest());
      Collection<String> responses = task.get();
      return responses;
    }
    catch (InterruptedException e) {
      List<String> responses = new ArrayList<String>(requests.length);
      logger.error( "(Controlled) server error cancelled a batch of " + requests.length + " individual requests due to an InterruptedException exception. " + e.getMessage(), e);
      for (final JsonRequestData request  : requests) {
        StandardErrorResponseData response = createJsonServerErrorResponse(request, e);
        responses.add(getGson().toJson(response));
      }
      return responses;
    }
    catch (ExecutionException e) {
      UnexpectedException ex = UnexpectedException.forExecutionExceptionShouldNotHappenBecauseProcessorHandlesExceptionsAsServerErrorResponses(e);
      logger.error( ex.getMessage(), ex );
      throw ex;
    }
  }

  private JsonRequestProcessorThread createJsonRequestProcessorThread() {
    Class<? extends JsonRequestProcessorThread> cls = getGlobalConfiguration().getJsonRequestProcessorThreadClass();
    try {      
      return cls.newInstance();      
    }
    catch (InstantiationException e) {
      JsonRequestProcessorThreadConfigurationException ex = JsonRequestProcessorThreadConfigurationException.forUnableToInstantiateJsonRequestProcessorThread(cls, e);
      logger.fatal( ex.getMessage(), ex);
      throw ex;
    }
    catch (IllegalAccessException e) {
      JsonRequestProcessorThreadConfigurationException ex = JsonRequestProcessorThreadConfigurationException.forUnableToInstantiateJsonRequestProcessorThread(cls, e);
      logger.fatal( ex.getMessage(), ex);
      throw ex;
    }     
  }

  private JsonRequestData[] getIndividualJsonRequests( String requestString ) {
    assert !StringUtils.isEmpty(requestString);

    JsonObject[] individualJsonRequests = parseIndividualJsonRequests(requestString, getJsonParser());
    JsonRequestData[] individualRequests = new JsonRequestData[individualJsonRequests.length];

    int i = 0;
    for( JsonObject individualRequest : individualJsonRequests ) {
      individualRequests[i] = createIndividualJsonRequest(individualRequest);
      i++;
    }

    return individualRequests;
  }

  private static String convertInvididualResponsesToJsonString(Collection<String> responses) {
    assert responses != null;
    assert !responses.isEmpty();

    StringBuilder result = new StringBuilder();
    if( responses.size() > 1 ) {
      result.append( "[\n" );
    }
    int j = 0;
    for( String response : responses   ) {
      result.append(response);
      boolean isLast = j == responses.size() - 1;
      if( !isLast) {
        result.append( ",");
      }
      j++;
    }
    if( responses.size() > 1 ) {
      result.append( "]");
    }
    return result.toString();
  }

  private Object[] getIndividualRequestParameters(JsonRequestData request) {
    assert request != null;

    RegisteredStandardMethod method = getStandardMethod( request.getAction(), request.getMethod());
    assert method != null;

    Object[] parameters;
    if( !method.getHandleParametersAsJsonArray()) {
      checkJsonMethodParameterTypes( request.getJsonData(), method );
      parameters = jsonDataToMethodParameters(method, request.getJsonData(), method.getParameterTypes(), method.getGsonParameterTypes() );
    }
    else {
      parameters = new Object[] { request.getJsonData() };
    }
    return parameters;
  }

  private Object[] jsonDataToMethodParameters(RegisteredStandardMethod method, JsonArray jsonParametersArray, Class<?>[] parameterTypes, Type[] gsonParameterTypes) {
    assert method != null;
    assert parameterTypes != null;

    try {
      JsonElement[] jsonParameters = getJsonElements(jsonParametersArray);
      Object[] result = getMethodParameters(method, jsonParameters);
      return result;
    }
    catch( JsonParseException ex ) {
      throw JsonException.forFailedConversionFromJsonStringToMethodParameters( method, jsonParametersArray.toString(), parameterTypes, gsonParameterTypes, ex);
    }
  }

  private static JsonElement[] getJsonElements(JsonArray jsonParameters) {
    if( jsonParameters == null ) {
      return new JsonElement[] {};
    }

    JsonElement[] parameters;

    JsonArray dataArray = jsonParameters;
    parameters = new JsonElement[dataArray.size()];
    for( int i = 0; i < dataArray.size(); i++ ) {
      parameters[i] = dataArray.get(i);
    }
    return parameters;
  }

  private static boolean isString( JsonElement element ) {
    assert element != null;

    return element.isJsonPrimitive() && ((JsonPrimitive)element).isString();
  }

  private Object[] getMethodParameters(RegisteredStandardMethod method , JsonElement[] jsonParameters) {
    assert method != null;
    assert jsonParameters != null;

    Class<?>[] parameterTypes = method.getParameterTypes();

    Object[] result = new Object[jsonParameters.length];
    for( int i = 0; i < jsonParameters.length; i++ ) {
      JsonElement jsonValue = jsonParameters[i];
      Class<?> parameterType = parameterTypes[i]; 
      Object value = null;

      Type gsonType = null;
      if( method.getGsonParameterTypes() != null ) {
        gsonType = method.getGsonParameterTypes()[i];
      }

      value = jsonToJavaObject(jsonValue, parameterType, gsonType);
      result[i] = value;
    }
    return result;
  }

  private @CheckForNull Object jsonToJavaObject(JsonElement jsonValue, Class<?> parameterType, Type gsonType) {
    Object value;
    if( jsonValue.isJsonNull() ) {
      return null;
    }

    // Handle string in a special way, due to possibility of having a Java char type in the Java
    // side
    if( isString(jsonValue)) {
      if( parameterType.equals(String.class)) {
        value = jsonValue.getAsString();
        return value;
      }
      if(parameterType.equals(char.class) || parameterType.equals( Character.class) ) {
        value = Character.valueOf(jsonValue.getAsString().charAt(0));
        return value;
      }
    }

    // If Java parameter is Object, we perform 'magic': json string, number, boolean and
    // null are converted to Java String, Double, Boolean and null. For json objects,
    // we create a Map<String,Object>, and for json arrays an Object[], and then perform
    // internal object conversion recursively using the same technique
    if( parameterType.equals( Object.class ) && SUPPORTS_OBJECT_TYPE_PARAMETER) {
      value = toSimpleJavaType(jsonValue);
      return value;
    }

    // If the Java parameter is an array, but we are receiving a single item, we try to convert
    // the item to a single item array so that the Java method can digest it
    boolean useCustomGsonType = gsonType != null;
    boolean fakeJsonArrayForManyValuedClasses = JsonDeserializationManager.isManyValuedClass(parameterType) && !jsonValue.isJsonArray();
    
    Type typeToInstantiate = parameterType;   
    if( useCustomGsonType ) {
      typeToInstantiate = gsonType;
    }
    
    JsonElement json = jsonValue;
    if( fakeJsonArrayForManyValuedClasses ) {
      JsonArray fakeJson = new JsonArray();
      fakeJson.add(jsonValue);
      json = fakeJson;
    }
    value = getGson().fromJson(json, typeToInstantiate);
    return value;
  }

  private @CheckForNull Object toSimpleJavaType(JsonElement jsonValue) {
	if (jsonValue==null) return null; //VR
    Object value = null;
    if( !jsonValue.isJsonNull() ) {
      if( jsonValue.isJsonPrimitive()) {
        JsonPrimitive primitive = jsonValue.getAsJsonPrimitive();
        if( primitive.isBoolean()) {
          value = Boolean.valueOf( primitive.getAsBoolean() );
        }
        else if( primitive.isNumber()) {
          value = Double.valueOf(primitive.getAsDouble());
        }
        else if( primitive.isString()) {
          value = primitive.getAsString();
        }
        else {
          throw UnexpectedException.forUnexpectedCodeBranchExecuted();
        }
      }
      else if( jsonValue.isJsonArray()) {
        //This simply does not work (?) 
        JsonArray array = jsonValue.getAsJsonArray();
        Object[] result = new Object[array.size()];
        for( int i = 0; i < array.size(); i++ ) {
          result[i] = toSimpleJavaType(array.get(i));
        }
        value = result;
      }
      else if( jsonValue.isJsonObject() ) {
        //This simply does not work (?)
        //value = getGson().fromJson(jsonValue, Map.class );

        JsonObject obj = jsonValue.getAsJsonObject();
        Iterator<Entry<String,JsonElement>> properties = obj.entrySet().iterator();
        Map<String, Object> result = new HashMap<String,Object>();
        while( properties.hasNext() ) {
          Entry<String,JsonElement> property = properties.next();
          JsonElement propertyValue = property.getValue();
          result.put( property.getKey(), toSimpleJavaType(propertyValue));  
        }
        value = result;
      }
      else {
        throw UnexpectedException.forUnexpectedCodeBranchExecuted();
      }
    }

    return value;
  }

  private static JsonElement[] getIndividualRequestJsonParameters(JsonArray jsonParameters) {
    if( jsonParameters == null ) { 
      return new JsonElement[] {};
    }

    JsonElement[] parameters;

    parameters = new JsonElement[jsonParameters.size()];
    for( int i = 0; i < jsonParameters.size(); i++ ) {
      parameters[i] = jsonParameters.get(i);
    }
    return parameters;
  }

  private static void checkJsonMethodParameterTypes(JsonArray jsonData, RegisteredStandardMethod method) {
    assert method != null;
    JsonElement[] jsonParameters = getIndividualRequestJsonParameters( jsonData );
    Class<?>[] parameterTypes = method.getParameterTypes();

    assert jsonParameters.length == parameterTypes.length;

    for( int i = 0; i < parameterTypes.length; i++ ) {
      Class<?> parameterType = parameterTypes[i];
      JsonElement jsonElement = jsonParameters[i];
      if( !isValidJsonTypeForJavaType(jsonElement, parameterType )) {
        throw new IllegalArgumentException( "'" + jsonElement.toString() + "' is not a valid json text for the '" + parameterType.getName() + "' Java type");
      }
    }
  }

  private static boolean isValidJsonTypeForJavaType(JsonElement jsonElement, Class<?> parameterType) {
    assert jsonElement != null;
    assert parameterType != null;

    // Check json nulls
    if( jsonElement.isJsonNull() ) {
      return !parameterType.isPrimitive();
    }

    if( parameterType.isArray() ) {
      // This is *always* ok because if the value is not a json array
      // we will instantiate a single item array and attempt conversion
      return true;
    }

    if( parameterType.equals( Boolean.class ) || parameterType.equals( boolean.class ) ) {
      return jsonElement.isJsonPrimitive() && ((JsonPrimitive)jsonElement).isBoolean();
    }
    else if( parameterType.equals( char.class ) || parameterType.equals( Character.class ) ) {
      if( jsonElement.isJsonPrimitive() && ((JsonPrimitive)jsonElement).isString() ) {
        return jsonElement.getAsString().length() == 1;
      }
      return false;
    }
    else if( parameterType.equals( String.class ) ) {
      return jsonElement.isJsonPrimitive() && ((JsonPrimitive)jsonElement).isString();
    }
    else if( ClassUtils.isNumericType(parameterType)) {
      return jsonElement.isJsonPrimitive() && ((JsonPrimitive)jsonElement).isNumber();
    }

    // If we arrived here, assume somebody will know how to handle the json element, maybe customizing Gson's serialization
    return true;
  }

  /* package */ String processIndividualRequest( JsonRequestData request, boolean isBatched, int requestNumber ) {
    assert request != null;
    boolean resultReported = false;

    Timer timer = new Timer();
    try {
      if( isBatched ) {
        if( logger.isDebugEnabled() ) {
          logger.debug( "  - Individual request #" + requestNumber + " request data=>" + getGson().toJson(request) );
        }
      }
      Object[] parameters = getIndividualRequestParameters( request);
      String action = request.getAction();
      String method = request.getMethod();
      StandardSuccessResponseData response = new StandardSuccessResponseData( request.getTid(), action, method);

      JsonDeserializationManager mgr = JsonDeserializationManager.getManager();
      try {
//VR BEGIN
    	Map<String,Object> directOptions = (Map<String,Object>)toSimpleJavaType(request.getJsonDirectOptions());
    	DirectOptions.setOptions(directOptions);
//VR END    	      	  
        Object result = dispatchStandardMethod(action, method, parameters);
        mgr.friendOnlyAccess_setRoot(result);
        response.setResult(result);
        String json = getGson().toJson(response);
        if( isBatched ) {
          if( logger.isDebugEnabled() ) {
            timer.stop();
            timer.logDebugTimeInMilliseconds( "  - Individual request #" + requestNumber + " response data=>" + json );
            resultReported = true;
          }
        }
        return json;
      }
      finally {
        mgr.friendOnlyAccess_dispose(); // Cleanup in case we are reusing thread
      }
    }
    catch( Exception t ) {        
      StandardErrorResponseData response = createJsonServerErrorResponse(request, t);
      String json = getGson().toJson(response);
      logger.error( "(Controlled) server error: " + t.getMessage() + " for Method '" + request.getFullMethodName() + "'", t);
      return json;
    }
    finally {
      if( !resultReported ) {
        timer.stop();
        // No point in logging individual requests when the request is not batched
        if( isBatched ) {
          if( logger.isDebugEnabled() ) {
            timer.logDebugTimeInMilliseconds( "  - Individual request #" + requestNumber + ": " + request.getFullMethodName() + ". Time");
          }
        }
      }
    }
  }

//VR BEGIN  
@SuppressWarnings("unchecked")
protected Object dispatchStandardMethod( String actionName, String methodName, Object[] parameters ) {
	  	Object records = super.dispatchStandardMethod(actionName, methodName, parameters);
		@SuppressWarnings("rawtypes")
		Map resultMap = null;
	  	if (records instanceof List<?>) {
	  		if (DirectOptions.getOption("total")!=null) {
	  			resultMap = new HashMap();
	  			resultMap.put("success", true);
	  			resultMap.put("total", DirectOptions.getOption("total"));
	  			resultMap.put("records", records);
	  		}
	  	}
	    return (resultMap!=null)? resultMap:records;
 }
//VR END  
  
  private static JsonObject[] parseIndividualJsonRequests(String requestString, JsonParser parser) {
    assert !StringUtils.isEmpty(requestString);
    assert parser != null;

    JsonObject[] individualRequests;
    JsonElement root = parser.parse( requestString );
    if( root.isJsonArray() ) {
      JsonArray rootArray = (JsonArray)root;
      if( rootArray.size() == 0 ) {
        RequestException ex = RequestException.forRequestBatchMustHaveAtLeastOneRequest();
        logger.error( ex.getMessage(), ex ); 
        throw ex;
      }

      individualRequests = new JsonObject[rootArray.size()];
      int i = 0;
      for( JsonElement item : rootArray ) {
        if( !item.isJsonObject()) {
          RequestException ex = RequestException.forRequestBatchItemMustBeAValidJsonObject(i);
          logger.error( ex.getMessage(), ex ); 
          throw ex;
        }
        individualRequests[i] = (JsonObject)item; 
        i++;
      }
    }
    else if( root.isJsonObject() ) {
      individualRequests = new JsonObject[] {(JsonObject)root};
    }
    else {
      RequestException ex = RequestException.forRequestMustBeAValidJsonObjectOrArray();
      logger.error( ex.getMessage(), ex ); 
      throw ex;
    }

    return individualRequests;
  }

  private static JsonRequestData createIndividualJsonRequest( JsonObject element ) {
    assert element != null;

    String action = getNonEmptyJsonString( element, JsonRequestData.ACTION_ELEMENT );  
    String method = getNonEmptyJsonString( element, JsonRequestData.METHOD_ELEMENT ); 
    Long tid = getNonEmptyJsonLong( element, JsonRequestData.TID_ELEMENT ); 
    String type = getNonEmptyJsonString( element, JsonRequestData.TYPE_ELEMENT ); 
    JsonArray jsonData = getMethodParametersJsonData(element);
    // VR BEGIN
    JsonObject jsonOptions = getMethodParametersJsonOptions(element);
    JsonRequestData result = new JsonRequestData( type, action, method, tid, jsonData, jsonOptions );
    // VR END
    return result;
  }

  @CheckForNull private static JsonArray getMethodParametersJsonData(JsonObject object) {
    assert object != null;

    JsonElement data = object.get(JsonRequestData.DATA_ELEMENT);
    if( data == null ) {
      RequestException ex = RequestException.forJsonElementMissing(JsonRequestData.DATA_ELEMENT);
      logger.error( ex.getMessage(), ex );
      throw ex;
    }

    if( data.isJsonNull()) {
      return null;
    }

    if( !data.isJsonNull() && !data.isJsonArray()) {
      RequestException ex = RequestException.forJsonElementMustBeAJsonArray(JsonRequestData.DATA_ELEMENT, data.toString());
      logger.error( ex.getMessage(), ex );
      throw ex;
    }

    return (JsonArray)data;
  }
  
  /*
   * VR BEGIN
   */
  
  @CheckForNull private static JsonObject getMethodParametersJsonOptions(JsonObject object) {
	    assert object != null;

	    JsonElement options = object.get(JsonRequestData.OPTIONS_ELEMENT);
	    if (( options != null )&&!options.isJsonNull()) {
	    	if( !options.isJsonObject()) {
	    		RequestException ex = RequestException.forRequestMustBeAValidJsonObjectOrArray();
	    		logger.error( ex.getMessage(), ex );
	    		throw ex;
	    	}
	    }
	    return (JsonObject)options;
   }

  /*
   * END
   */


  private static <T> T getNonEmptyJsonPrimitiveValue( JsonObject object, String elementName, PrimitiveJsonValueGetter<T> getter ) {
    assert object != null;
    assert !StringUtils.isEmpty(elementName);

    try {
      JsonElement element = object.get( elementName );
      if( element == null ) {
        RequestException ex = RequestException.forJsonElementMissing(elementName);
        logger.error( ex.getMessage(), ex ); 
        throw ex;          
      }

      // Take into account that the element must be a primitive, and then a string!
      T result = null;
      if( element.isJsonPrimitive() ) {
        result = getter.checkedGet( (JsonPrimitive) element );
      }

      if( result == null ) {
        RequestException ex = RequestException.forJsonElementMustBeANonNullOrEmptyValue(elementName, getter.getValueType() );
        logger.error( ex.getMessage(), ex );
        throw ex;          
      }

      return result;
    }
    catch( JsonParseException e ) {
      String message = "Probably a DirectJNgine BUG: there should not be JSON parse exceptions: we should have checked ALL error conditions. " + e.getMessage();
      logger.error( message, e );
      assert false : message;
      throw e; // Just to make the compiler happy -because of the assert
    }
  }

  // A simple interface that helps us avoid duplicated code
  // Its purpose is to retrieve a primitive value, or null
  // if the primitive value is null or "empty" (makes sense for strings...)
  interface PrimitiveJsonValueGetter<T> {
    // Must return null if the specified primitive is not of type T or is "empty"
    @CheckForNull T checkedGet( JsonPrimitive value );
    Class<T> getValueType();
  }

  private static class PrimitiveJsonLongGetter implements PrimitiveJsonValueGetter<Long> {
    public Long checkedGet(JsonPrimitive value) {
      assert value != null;

      if( value.isNumber() ) {
        String v = value.toString();
        try {
          return Long.valueOf( Long.parseLong(v) );
        }
        catch( NumberFormatException ex ) {
          return null;
        }
      }
      return null;
    }

    public Class<Long> getValueType() {
      return Long.class;
    }
  }

  private static class PrimitiveJsonStringGetter implements PrimitiveJsonValueGetter<String> {
    // @Override
    public String checkedGet(JsonPrimitive value) {
      assert value != null;

      if( value.isString() ) {
        String result = value.getAsString();
        if( result.equals("") )
          result = null;
        return result;
      }
      return null;
    }

    // @Override
    public Class<String> getValueType() {
      return String.class;
    }

  }

  private static Long getNonEmptyJsonLong( JsonObject object, String elementName ) {
    assert object != null;
    assert !StringUtils.isEmpty(elementName);

    return getNonEmptyJsonPrimitiveValue( object, elementName, new PrimitiveJsonLongGetter() );
  }

  private static String getNonEmptyJsonString( JsonObject object, String elementName ) {
    assert object != null;
    assert !StringUtils.isEmpty(elementName);

    return getNonEmptyJsonPrimitiveValue( object, elementName,  new PrimitiveJsonStringGetter() );
  }

}
