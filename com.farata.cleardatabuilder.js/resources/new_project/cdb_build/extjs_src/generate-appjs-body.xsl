<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	extension-element-prefixes="redirect" exclude-result-prefixes="xalan redirect">
	
	<xsl:template match="/" mode="generate-appbody">
		<redirect:write file="{$rootFilePath}" append="true">
			],
		
		launch:function(){
			Ext.create('Ext.container.Viewport',{
				items:
				[
		</redirect:write>
	</xsl:template>
	
</xsl:stylesheet>