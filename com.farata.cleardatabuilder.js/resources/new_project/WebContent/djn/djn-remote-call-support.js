/*
 * Copyright © 2008, 2009 Pedro Agulló Soliveres.
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

Djn = Ext.namespace( "Djn");

Djn.RemoteCallSupport = {
  DIRECT_CALL_ERROR_PREFIX : 'Direct call error',
  validateCalls: false,
  addCallValidation : function( provider ) {
      /****************************************************************************
       * 
       * Checking method parameters is very complicated.
       * 
       * We rely on 'transaction.args' containing what the server will receive, 
       * minus callback functions at the end.
       * Besides, we assume that, once we have found a funtion, this is
       * not part of the arguments to send to the server -which, on the
       * other hand, can't possibly handle a javascript function!
       * 
       ****************************************************************************/
    
      provider.on( "beforecall", function(provider, transaction) {
        if( !Djn.RemoteCallSupport.validateCalls ) {
          return true;
        }
        ////////////////////        
        var arguments = transaction.args;
        var fullMethodName = transaction.action + "." + transaction.method;
        
        // Do not check form methods, they receive pairs paramName-value, which need-not/cannot be checked
        var isFormMethod = transaction.isForm !== undefined;
        if( isFormMethod ) {
          return true;
        }

        /////////////////// Utilities
        function typeOf(value){
          var s = typeof value;
          if (s === 'object') {
            if (value) {
              if (typeof value.length === 'number' &&
              !(value.propertyIsEnumerable('length')) &&
              typeof value.splice === 'function') {
                s = 'array';
              }
            }
            else {
              s = 'null';
            }
          }
          return s;
        }
 
        function isFunction(v) {
          return typeOf(v) === 'function';
        }
        
        function isArray( v )  {
          return typeOf(v) ===  'array';
        }
        
        function isObject( v ) {
          return typeOf(v) === 'object';
        }

        function arrayHasUndefinedValue( value ) {
          Ejn.Assert.isTrue( isArray(value));
          
          for( i = 0; i < value.length; i++ ) {
            var item = value[i];
            if( item === undefined ) {
              return true;
            }
            else if( isObject(item)) {
              return objectHasArrayWithUndefinedValue(item);
            }
            else if( isArray(item)) {
              return arrayHasUndefinedValue(item);
            }
          }
          return false;
        }
        
        function objectHasArrayWithUndefinedValue(value) {
          Ejn.Assert.isTrue( isObject(value));
          
          for( memberName in value ) {
            var memberValue = value[memberName];
            if( isArray(memberValue)) {
              return arrayHasUndefinedValue(memberValue)
            }
            else if( isObject(memberValue)) {
              return objectHasArrayWithUndefinedValue(memberValue);
            }
          }
          return false;
        }
        
        function arrayToString( a, count ) {
          var result = "[";
          for( i = 0; i < count; i++ ) {
            if( a[i] === undefined ) {
              result += 'undefined';
            }
            else if( a[i] === null ) {
              result += "null";
            }
            else if ( typeof(a[i]) === 'string') {
              result += "'" + a[i] + "'";
            }
            else {
              result += a[i].toString();
            }
            var isLastItem = i === count -1;
            if( !isLastItem ) {
              result += ",";
            }
          }
          result += "]";
          return result;
        };

        /////////////////// Check parameters count
        function getArgumentCountUpToFirstFunction( array ) {
          var result = 0;
          var i = 0;
          for(; i < array.length; i++ ) {
            if( typeOf( array[i]) === 'function') {
              return i;
            }
          }
          return i;
        }
        
        function getExpectedMethod( provider, transaction ) {
          var action = provider.actions[transaction.action];
          
          for( i = 0; i < action.length; i++ ) {
            var methodName = transaction.method;
            var method = action[i];
            if( method.name === methodName ) {
              return method;
            }
          }
          
          Ejn.Assert.isTrue(false, "We should have found the action and method specified in the transaction, '" + fullMethodName + "'");
          return;
        }
        
        var argsArgumentsCount = getArgumentCountUpToFirstFunction( transaction.args );
        var method = getExpectedMethod( provider, transaction );
        var expectedArgumentsCount = method.len;
        var isVariableLength = method.variableLength == 1000;
        if( !isVariableLength && argsArgumentsCount != expectedArgumentsCount) {
          expectedArgumentsCount = getExpectedMethod( provider, transaction ).len;
          
          var error = Djn.RemoteCallSupport.DIRECT_CALL_ERROR_PREFIX + ", '" + fullMethodName + "' received '" + argsArgumentsCount + "' parameters, but the call requires '" + expectedArgumentsCount + "' parameters.";
          throw error;          
          // return false
        }

        /////////////////////// Check parameters do not have invalid data
        if( arguments === null || arguments.length === 0 || isVariableLength ) {
          // No need to check parameter validity!
          return
        }

        function callHasIllegalDataInParameters( parameters, parameterCount ) {
          Ejn.Assert.isTrue( isArray(parameters));

          for( i = 0; i < parameterCount; i++ ) {
            var value = parameters[i]; 
            if( value === undefined) {
              return true;
            }
            else {
              if( isFunction(value)) {
                return true;
              }
              else if( isArray(value)) {
                return arrayHasUndefinedValue(value);
              }
              else if( isObject(value) ) {
                return objectHasArrayWithUndefinedValue(value);
              }
            }
          }          
          return false;
        }

        if( callHasIllegalDataInParameters(arguments, expectedArgumentsCount ) ) {
          var error = Djn.RemoteCallSupport.DIRECT_CALL_ERROR_PREFIX + ", '" + fullMethodName + "' must not receive undefined parameters. Call parameters: " + arrayToString( arguments, expectedArgumentsCount);
          // Ext.log( "ERROR: " + error );
          throw error;
          // return false
        }
        
      }
    );
    return true;
  }  
}
