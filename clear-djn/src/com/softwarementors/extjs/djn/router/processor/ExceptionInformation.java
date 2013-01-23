package com.softwarementors.extjs.djn.router.processor;


public class ExceptionInformation {
  /* package */ String exceptionClass;
  /* package */ String message;
  // Future development: we may have an exception handler that allows
  // us to register exception processors to pass extra data
  // private Map<String, Object> extraData = new HashMap<String,Object>();
  
  private ExceptionInformation() {
    // Do nothing
  }
  
  public static ExceptionInformation create( Throwable e ) {
    assert e != null;
    
    ExceptionInformation result = new ExceptionInformation();
    result.exceptionClass = e.getClass().getName();
    result.message = e.getMessage();
    return result;
  }
}
