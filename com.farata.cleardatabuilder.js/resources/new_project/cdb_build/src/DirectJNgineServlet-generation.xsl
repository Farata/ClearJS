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


public class DirectJNgineServlet extends com.softwarementors.extjs.djn.servlet.DirectJNgineServlet {	

		private static final long serialVersionUID = 1L;

		protected RequestRouter createRequestRouter(Registry registry, GlobalConfiguration globalConfiguration) {
		    assert registry != null;
		    assert globalConfiguration != null;
		    	
		    return new RequestRouter( registry, globalConfiguration, createDispatcher(globalConfiguration.getDispatcherClass()) );
	}		  
}
	</xsl:template>
</xsl:stylesheet>