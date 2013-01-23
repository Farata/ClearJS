package com.softwarementors.extjs.djn;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.softwarementors.extjs.djn.router.dispatcher.MethodExecutionException;

public class ExceptionUtils {
  private ExceptionUtils() {
    // Avoid instantiation
  }
  
  public static List<Throwable> getRelevantExceptions(Throwable e) {
    List<Throwable> result = new ArrayList<Throwable>();
    Throwable t = e;
    while( t != null ) {
      if( !skipException(t)) {
        result.add(t);
      }
      t = t.getCause();
    }
    return result;
  }
  
  public static boolean skipException( Throwable t ) {
    assert t != null;
    return t instanceof MethodExecutionException || t instanceof InvocationTargetException;
  }
  
  public static Throwable getFirstRelevantExceptionToReport(Throwable t) {
    assert t != null;
    
    Throwable reportedException = t;
    while( skipException(reportedException) ) {
      reportedException = reportedException.getCause();
      assert reportedException != null;
    }
    return reportedException;
  }

  public static String getExceptionMessage(Throwable t) {
    assert t!= null;

    String result = ClassUtils.getSimpleName( t.getClass() );
    if( t.getMessage() != null ) {
      result += ": " + t.getMessage();
    }
    return result;
  }

  public static String getExceptionWhere(Throwable t, boolean debugMode) {
    assert t != null;
    
    if( debugMode ) {
      Writer where = new StringWriter();
      PrintWriter printWriter = new PrintWriter( where );
      t.printStackTrace(printWriter);
      return where.toString();
    }
    return "";
  }

}
