package com.softwarementors.extjs.djn.router.processor;

import java.util.ArrayList;
import java.util.List;

import com.softwarementors.extjs.djn.ExceptionUtils;

/* Information about unexpected server exception, passed
 * to the Javascript side
 */
/* package */ class ServerExceptionInformation {
   
  /* package */ ExceptionInformation rootException;
  /* package */ ExceptionInformation exception;
  /* package */ List<ExceptionInformation> exceptions = new ArrayList<ExceptionInformation>();

  /* package */ ServerExceptionInformation( Throwable t, boolean debugOn ) {
    assert t != null;
    
    List<Throwable> exceptions = ExceptionUtils.getRelevantExceptions(t);
    for( Throwable e : exceptions ) {
      this.exceptions.add( ExceptionInformation.create(e, debugOn));
    }
    this.exception = this.exceptions.get(0);
    this.rootException = this.exceptions.get(this.exceptions.size()-1);
  }
  
  /* package */ static class ExceptionInformation {
    @edu.umd.cs.findbugs.annotations.SuppressWarnings( value="URF_UNREAD_FIELD", justification="Passed to JSON side only")
    /* package */ String type;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings( value="URF_UNREAD_FIELD", justification="Passed to JSON side only")
    /* package */ String message;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings( value="URF_UNREAD_FIELD", justification="Passed to JSON side only")
    /* package */ String where;
    // Future development: we may have an exception handler that allows
    // us to register exception processors to pass extra data
    // private Map<String, Object> extraData = new HashMap<String,Object>();
    
    private ExceptionInformation() {
      // Do nothing
    }
    
   private static ExceptionInformation create( Throwable e, boolean debugOn ) {
      assert e != null;
      
      ExceptionInformation result = new ExceptionInformation();
      result.type = e.getClass().getName();
      result.message = e.getMessage();
      result.where = ExceptionUtils.getExceptionWhere(e, debugOn);
      return result;
    }
  }
}

