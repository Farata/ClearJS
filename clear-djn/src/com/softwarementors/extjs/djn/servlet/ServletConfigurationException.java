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

import java.util.List;

import com.softwarementors.extjs.djn.DirectJNgineException;
import com.softwarementors.extjs.djn.StringUtils;
import com.softwarementors.extjs.djn.gson.GsonBuilderConfigurator;
import com.softwarementors.extjs.djn.router.dispatcher.Dispatcher;
import com.softwarementors.extjs.djn.router.processor.standard.json.JsonRequestProcessorThread;

public class ServletConfigurationException extends DirectJNgineException {
  private static final long serialVersionUID = -3328869605189206809L;

  private ServletConfigurationException( String message ) {
    super(message);
    
    assert !StringUtils.isEmpty(message);
  }

  private ServletConfigurationException( String message, Throwable cause ) {
    super(message, cause);
    
    assert !StringUtils.isEmpty(message);
    assert cause != null;
  }

  public static ServletConfigurationException forMissingRequiredConfigurationParameter(List<String> missingParameters) {
    assert missingParameters != null;    
    assert !missingParameters.isEmpty();
    
    String parameterNames = StringUtils.concatWithSeparator(missingParameters, ", ");
    return new ServletConfigurationException( "The following servlet configuration parameters are missing: " + parameterNames + ". Please, check your servlet configuration in web.xml." );
  }

  public static ServletConfigurationException forClassNotFound(String className, ClassNotFoundException cause) {
    assert cause != null;
    
    return new ServletConfigurationException( "Unable to find class '" + className + "'", cause );
  }

  public static ServletConfigurationException forGsonBuilderConfiguratorMustImplementGsonBuilderConfiguratorInterface(
      String configuratorClassName) {
    return new ServletConfigurationException( "The specified gson configurator class, '" + configuratorClassName + "', must implement the '" + GsonBuilderConfigurator.class + "' interface");
  }

  public static ServletConfigurationException forRegistryConfiguratorMustImplementGsonBuilderConfiguratorInterface(
      String registryConfiguratorClassName) {
    return new ServletConfigurationException( "The specified registry configurator class, '" + registryConfiguratorClassName + "', must implement the '" + ServletRegistryConfigurator.class + "' interface");
  }
  
  public static ServletConfigurationException forParameterMustBeAnIntegerGreaterOrEqualToValue(String parameterName,
      int result, int minValue) {
    assert !StringUtils.isEmpty(parameterName);
    
    return new ServletConfigurationException( "Parameter, '" + parameterName + "' must be greater than or equal to '" + minValue + "'. It was '" + result + "'.");
  }

  public static ServletConfigurationException forParameterMustBeAValidInteger(String parameterName, String resultString) {
    assert !StringUtils.isEmpty(parameterName);
    assert !StringUtils.isEmpty(resultString);
    
    return new ServletConfigurationException( "Parameter, '" + parameterName + "' must be a valid integer. It was '" +resultString + "'.");
  }

  public static ServletConfigurationException forMaxThreadPoolSizeMustBeEqualOrGreaterThanMinThreadPoolSize(
      int batchRequestsMinThreadsPoolSize, int batchRequestsMaxThreadsPoolSize) {
    return new ServletConfigurationException( "The maximum batch request pool ('" + DirectJNgineServlet.GlobalParameters.BATCH_REQUESTS_MAX_THREADS_POOOL_SIZE +"') size was " + batchRequestsMaxThreadsPoolSize + 
        ". It must be greater or equal to the minimum request pool size ('" + DirectJNgineServlet.GlobalParameters.BATCH_REQUESTS_MIN_THREADS_POOOL_SIZE +"'), which was " + batchRequestsMinThreadsPoolSize);
  }

  public static ServletConfigurationException forDispatcherMustImplementDispatcherInterface(String dispatcherClassName) {
    return new ServletConfigurationException( "The specified dispatcher class, '" + dispatcherClassName + "', must implement the '" + Dispatcher.class + "' interface");
  }

  public static ServletConfigurationException forJsonRequestProcessorThreadMustImplementJsonRequestProcessorThreadInterface(
      String jsonRequestProcessorThreadClassName) {
    return new ServletConfigurationException( "The specified json request processor thread class, '" + jsonRequestProcessorThreadClassName + "', must implement the '" + JsonRequestProcessorThread.class + "' interface");
  }

}
