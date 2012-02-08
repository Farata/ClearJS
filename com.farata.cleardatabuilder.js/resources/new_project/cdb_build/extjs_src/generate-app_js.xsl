<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	extension-element-prefixes="redirect" exclude-result-prefixes="xalan redirect">
	
	<xsl:template match="/" mode="generate-app">
		<xsl:param name="appName" />
		<xsl:param name="appFolderPath" />
		<xsl:param name="elementPrefix" />
		<xsl:param name="rootFilePath" />
		
		<redirect:write file="{$rootFilePath}">
Ext.Loader.setConfig({
 disableCaching: false,
 enabled: true,
 paths  : {
  CDB: '<xsl:value-of select="$appName"/>', Clear:'clear'
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
		appFolder: 'test/<xsl:value-of select="helper:replaceAll($appFolderPath, '.', '/')"/>',
		controllers:	[
			'<xsl:value-of select="concat($elementPrefix, '_Controller')"/>'
		],

		launch:function(){
			Ext.create('Ext.container.Viewport',{
				items:
					[
						{
							xtype: '<xsl:value-of select="concat($elementPrefix, '_Panel')"/>'
						}
					]
			});
		}
	});
});
		</redirect:write>
		
	</xsl:template>
	
</xsl:stylesheet>