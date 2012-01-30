<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	extension-element-prefixes="redirect" exclude-result-prefixes="xalan redirect">

	<xsl:output omit-xml-declaration="yes" method="text" />

	<xsl:param name="package" />
	
	
	<xsl:template match="/">
	
package <xsl:value-of select="helper:replaceAll($package, '/', '.')"/>;



import com.softwarementors.extjs.djn.api.Registry;
import com.softwarementors.extjs.djn.config.GlobalConfiguration;

import com.softwarementors.extjs.djn.router.dispatcher.Dispatcher;

public class RequestRouter extends
		com.softwarementors.extjs.djn.router.RequestRouter {

	  
	
	  private static RequestRouter instance;
	  private Registry _registry;
	  private Dispatcher _dispatcher;
	  public static RequestRouter getRequestRouter() {
		  return instance;
	  }
	  public Registry getRegistry()  {
		  return _registry;
	  }
	  public Dispatcher getDispatcher()  {
		  return _dispatcher;
	  }
	
	  public RequestRouter( Registry registry, GlobalConfiguration globalConfiguration, Dispatcher dispatcher ) {
		  super(registry, globalConfiguration, dispatcher);
		  _dispatcher = dispatcher;
		  _registry = registry;
		  instance = this;		  
	  }
}
	</xsl:template>
</xsl:stylesheet>