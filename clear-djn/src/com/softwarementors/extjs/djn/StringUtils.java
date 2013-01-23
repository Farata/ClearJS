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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class StringUtils {
  private StringUtils() {
    // Disallow instantiation
  }
  
  public static boolean isEmpty( String value ) {
    return value == null || value.equals( "");
  }

  public static String concatWithSeparator(List<String> missingParameters, String separator) {
    assert missingParameters != null;
    assert separator != null;
    
    if( missingParameters.isEmpty() ) {
      return "";
    }
    
    StringBuilder result = new StringBuilder();
    for( int i = 0; i < missingParameters.size() - 1; i++) {
      result.append( missingParameters.get(i));
      result.append( separator );
    }
    
    result.append( missingParameters.get( missingParameters.size() - 1));
    
    return result.toString();
  }

  // Returns a list of strings form a 'delimiter' separated string.
  // It removes whitespace from every value, and ignores empty strings
  public static List<String> getNonBlankValues( String delimitedValues, String delimiter ) {
    assert !StringUtils.isEmpty( delimitedValues );
    assert !StringUtils.isEmpty(delimiter);
    
    List<String> result = new ArrayList<String>();
    String[] values = delimitedValues.split( delimiter);
    for( String value : values ) {
      value = value.trim();
      if( !value.equals( "")) {
        result.add( value );
      }
    }
    
    return result;
  }
  
  public static boolean startsWithCaseInsensitive( String s1, String s2) {
    assert s2 != null;

    if( s1 == null ) {
      return false;
    }
    String s1lower = s1.toLowerCase(Locale.getDefault());
    String s2lower = s2.toLowerCase(Locale.getDefault());
    return s1lower.startsWith(s2lower);
  }
}
