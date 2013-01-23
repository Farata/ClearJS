package com.softwarementors.extjs.djn;

import java.util.Collection;

public class CollectionUtils {
  private CollectionUtils() {
    // Avoid instantiation
  }
  
  public static <T> void removeAll( Collection<T> c, T... items ) {
    assert c != null;
    assert items != null;
    
    for( T item : items ) {
      c.remove(item);
    }
  }
  
}
