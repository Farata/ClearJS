package com.softwarementors.extjs.djn.router.processor;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.softwarementors.extjs.djn.EncodingUtils;
import com.softwarementors.extjs.djn.StringUtils;
import com.softwarementors.extjs.djn.UnexpectedException;

public class RequestProcessorUtils {
  
  // ************************************************************
  // * Request string handling support
  // ************************************************************
  public static Map<String, String> getDecodedRequestParameters(String requestString) {
    assert requestString != null;
    Map<String,String> result = new HashMap<String,String>();
    if( !requestString.equals("")) {
      String[] entries = requestString.split( "&");
      for( String entry : entries ) {
        String[] keyValue = entry.split("=");
        assert keyValue.length >= 1 && keyValue.length <= 2;
        String key = keyValue[0];
        assert !StringUtils.isEmpty(key);

        String value = "";
        if( keyValue.length == 2 )
          value = keyValue[1];
        try {
          key = URLDecoder.decode( key, EncodingUtils.UTF8);
//ññ: Why not decode this from String to JSON object?
          value = URLDecoder.decode( value, EncodingUtils.UTF8);
        }
        catch (UnsupportedEncodingException e) {
          UnexpectedException.forExpectingUTF8UrlEncodingIsAlwaysSupportedByURLEncoder(e);
        }
        result.put( key, value );
        // addEscapedIfIsKeysIsMultiValued( result, key, value );
      }
    }
    return result;
  }

/*  
  private static final String MULTIPLE_VALUES_SEPARATOR = "\n";
  
  private static String escape(String value ) {
    return value;
  }
  
  private static boolean isEscaped(String value) {
    return value.endsWith(MULTIPLE_VALUES_SEPARATOR);
  }
  
  private static void addEscapedIfIsKeysIsMultiValued( Map<String, String> map, String key, String value ) {
    String newValue = value;
    String oldValue = map.get(key);
    
    if( oldValue != null ) {
      newValue = escape( value );
      if( isEscaped(oldValue) ) {
        newValue = oldValue + newValue + MULTIPLE_VALUES_SEPARATOR;
      }
      else {
        oldValue = escape(oldValue);
        newValue = oldValue + MULTIPLE_VALUES_SEPARATOR + newValue + MULTIPLE_VALUES_SEPARATOR;
      }
    }
    map.put( key, newValue );
  }
  
  public static boolean isMultiValued( Map<String, String> map, String key ) {
    String value = map.get(key);
    if( value == null ) {
      return false;
    }
    return isEscaped(value);
  }
  
  public static List<String> getValues( Map<String,String> map, String key) {
    String value = map.get(key);
    if( value != null ) {
      List<String> result = new ArrayList<String>();
      if( !isEscaped(value)) {
        result.add(value);
      }
      else {
        String[] resultStrings = value.split(MULTIPLE_VALUES_SEPARATOR);
        Collections.addAll( result, resultStrings );
      }
      return result; 
    }
    return null;
  }
*/  
}
