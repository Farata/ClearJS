package com.softwarementors.extjs.djn;

public class StringBuilderUtils {
  protected StringBuilderUtils() {
    // Avoid instantiation
  }
  
  public static void appendAll( StringBuilder out, String...values ) {
    assert out != null;
    assert values != null;
    
    for( String value : values ) {
      out.append( value );
    }
  }
}
