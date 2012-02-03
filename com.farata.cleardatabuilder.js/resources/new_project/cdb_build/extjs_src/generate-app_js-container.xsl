<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	extension-element-prefixes="redirect" 
	exclude-result-prefixes="xalan redirect">


	<xsl:output omit-xml-declaration="yes" method="text" />

	<xsl:param name="jsOutputFolder" />
	<xsl:param name="appName" />
	
	
	<xsl:template match="/|/">
		<xsl:apply-templates select="/" mode="header-output" />
	</xsl:template>
	
	<xsl:template match="/" mode="header-output">
		<redirect:write file="{$jsOutputFolder}/app.js" append="true">
	
		],
		
		launch:function(){
			Ext.create('Ext.container.Viewport',{
				items:
				[
		</redirect:write>
	</xsl:template>
	
	
	
</xsl:stylesheet>