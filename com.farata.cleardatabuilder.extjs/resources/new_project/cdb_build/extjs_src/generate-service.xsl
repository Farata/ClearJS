<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template name="generate-service.xsl">
		<xsl:param name="rootPackage" />
		<xsl:param name="interfaceName" />
		<xsl:param name="jsServiceName" />
		<xsl:param name="appName" />
		<xsl:param name="remoteActionNamespace" />

		<xsl:variable name="serviceName" select="helper:createSubServiceName(helper:getTypeName($interfaceName))" />
		
<xsl:text/>Ext.define('<xsl:value-of select="$rootPackage"/>.<xsl:value-of select="$jsServiceName"/>', {
	
    /**
     * @private {Object} Direct action
     */
    service: <xsl:value-of select="$appName"/>.direct.action.<xsl:value-of select="$serviceName"/>,
    
	constructor: function(config) {
		Ext.apply(this, config || {});
	},
	
	faultHandler: function(message, remotingEvent) {
	    var transaction = remotingEvent.getTransaction(),
	        action = transaction.action, 
	        method = transaction.method; 

        Ext.Error.raise("Error" + message + " invoking " + action + "." + method);
	},

<xsl:for-each select="annotated-types/annotated-type[@name=$interfaceName]/methods/method">
<xsl:text>	</xsl:text><xsl:value-of select="@name"/>: function(resultHandler, faultHandler, scope) {
	        
		if (Ext.isFunction(resultHandler)) {			
			this.service.<xsl:value-of select="@name"/>( 
				function(result, remotingEvent) {
					if (remotingEvent.status) {
						resultHandler = Ext.Function.bind(resultHandler, scope);
						resultHandler(result, remotingEvent);
					} else {
						if (!Ext.isFunction(faultHandler)) 
							faultHandler = this.faultHandler;
						else
							faultHandler = Ext.Function.bind(faultHandler, scope);
						
						faultHandler(remotingEvent.message, remotingEvent);
					}
				}
			);
		}
	},

</xsl:for-each>});    
	</xsl:template>
</xsl:stylesheet>