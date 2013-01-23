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

import java.lang.reflect.Field;

import edu.umd.cs.findbugs.annotations.Nullable;


public final class ClassUtils {
  private ClassUtils() {
    // Disable class instantiation
  }

  // Returns null if field does not exist
  @Nullable public static Class<?> getFieldType( Class<?> cls, String field ) {
    assert cls != null;
    assert field != null;
    
    Class<?> vClass = cls;
    Class<?> fieldType = null;
    while(true) {
      Field[] fields = vClass.getDeclaredFields();
      for( Field f : fields ) {
        if( f.getName().equals(field)) {
          fieldType = f.getType();
          return fieldType;
        }
      }
      
      vClass = vClass.getSuperclass();
      if( vClass.equals(Object.class) ) {
        return null;
      }
    }
  }
  
  public static boolean isNumericType( Class<?> cls ) {
    assert cls != null;
  
    if( cls== float.class || cls == double.class || cls == byte.class || cls == short.class || cls == int.class || cls == long.class) {
      return true;
    }
    
    Class<?> currentClass = cls;
    while( currentClass != null ) {
      if( currentClass.equals(Number.class) )
        return true;
      currentClass = currentClass.getSuperclass();        
    }
    return false;
  }

  // JDK 1.4 does not implement Class.getSimpleName()
  public static String getSimpleName( Class<?> cls ) {
    assert cls != null;
    
    String result = cls.getName();
    
    // Special case: inner classes!
    int simpleNameStart = result.lastIndexOf("$");
    if( simpleNameStart >= 0 ) {
      simpleNameStart++; // Skip the "$"
      // Need to skip the digits immediately after the "$"
      for( ; simpleNameStart < result.length(); simpleNameStart++ ) {
        if( !Character.isDigit(result.charAt(simpleNameStart)) ) {
          return result.substring(simpleNameStart);
        }
      }
    }
    else {
      simpleNameStart = result.lastIndexOf( ".");
      if( simpleNameStart >= 0) {
        return result.substring(simpleNameStart+1);
      }
    }
    return result;
  }

  // Note: would be nice to make recursive, so that we can handle array of array of array of ...,
  //       but in reality pretty-printing just one-dimensional arrays is ok
  public static String getNicePrintableName( Class<?> cls ) {
    assert cls != null;
    
    Class<?> type = cls;
    if( cls.isArray() ) {
      type = cls.getComponentType();
    }
    
    String typeName = type.getName();
    if( typeName.startsWith( "java.lang") ) {
      typeName = getSimpleName(type);
    }
    
    if( cls.isArray() ) {
      typeName += "[]";
    }
    return typeName;
  }
  
}
