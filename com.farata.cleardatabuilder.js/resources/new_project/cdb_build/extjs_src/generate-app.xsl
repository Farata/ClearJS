<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	extension-element-prefixes="redirect" exclude-result-prefixes="xalan redirect">
	
	<xsl:template match="/" name="generate-app.xsl">
		<xsl:param name="storeName"/>
		<xsl:param name="appName" />
		<xsl:param name="dtoName" />
		
<xsl:text/>Ext.Loader.setConfig({
	disableCaching: false,
	enabled: true,
	paths: {
		<xsl:value-of select="$appName"/>: 'app', Clear:'clear'
	}
});

Ext.require(
	['Ext.direct.Manager'],

	function() {
		var providerConfig = Clear.direct.REMOTING_API;
		providerConfig.enableBuffer = 0;
		var provider = Ext.Direct.addProvider( providerConfig);
		Djn.RemoteCallSupport.addCallValidation(provider);
		Djn.RemoteCallSupport.validateCalls = true;

		Ext.application({
			name:'<xsl:value-of select="$appName" />',
			appFolder: 'app',
			controllers: ['ControllerTest'],

			launch:function() {
				Ext.create('Ext.container.Viewport', {
					items:
					[
						{
							xtype: 'GridTest'
						}
					]
				});
			}
		});
	}
);
		
	</xsl:template>
	
</xsl:stylesheet>