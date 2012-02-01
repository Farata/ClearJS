/*
 * Copyright © 2008, 2009 Pedro Agulló Soliveres.
 * 
 * This file is part of ExtJsNgine.
 *
 * ExtJsNgine is free software: you can redistribute it and/or modify
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
 * along with ExtJsNgine.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * This software uses the ExtJs library (http://extjs.com), which is 
 * distributed under the GPL v3 license (see http://extjs.com/license).
 */
En = Ext.namespace( "Ejn");

Ejn.Assert = function() {
  // ***************** Private variables and functions 
  // var privateVar = "privateVar";
  var assert = function( booleanExpression, optionalMessage, optionalShowDialog ) {
      if( booleanExpression === undefined ) {
        throw "'assert()' requires a boolean argument at least";  
      }
  
      if (!booleanExpression) {
        var message = optionalMessage
        if (message === undefined || message === null || message === '') {
          debugger;
          message = "-- Assertion violation -- ";    
        }
        if (optionalShowDialog !== undefined && optionalShowDialog) {
          alert('Assertion violation', message);
        }
        throw message;
      }    
    }  
    
  var internalAssert = function ( booleanExpression ) {
    if( !booleanExpression)
      throw "Internal 'assert' assertion violation";
  }
    
  return {
  // ***************** Public variables
    // publicVar : "publicVar",
    
  // ***************** Privileged (public+access to private members) functions
    isTrue: function(booleanExpression, optionalMessage) {
      internalAssert( booleanExpression !== undefined && booleanExpression !== null )
      
      assert(booleanExpression, optionalMessage, false);
    },

    isFalse: function(booleanExpression, optionalMessage) {
      internalAssert( booleanExpression !== undefined && booleanExpression !== null )

      assert(!booleanExpression, optionalMessage, false);
    },

    notNullOrEmpty: function(){
      for (argument in arguments) {
        assert(value !== undefined);
        assert(value !== null);
      }  
    },

    exactArgumentsCount: function (arguments, count ) {
      internalAssert( arguments !== undefined && arguments !== null )
      internalAssert( count !== undefined && arguments !== null )
      
      assert( arguments.length === count )
    },
    
    minArgumentsCount : function (arguments, min ) {
      internalAssert( arguments !== undefined && arguments !== null )
      internalAssert( count !== undefined && arguments !== null )
      
      assert( arguments.length >= min );
    },
    
    noArguments : function( arguments ) {
      internalAssert( arguments !== undefined && arguments !== null )
      internalAssert( count !== undefined && arguments !== null )
      
      assert( arguments.length === 0 );
    },
    
    type : function( value, typeString ) {
      assert( Ejn.remedial.typeOf(value) === typeString );
    }
  }
} ();
