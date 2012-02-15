<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/" name="generate-model-subclass.xsl">
		<xsl:param name="dtoName"/>
		<xsl:param name="rootPackage"/>
		<xsl:param name="appName" />

<xsl:text/>Ext.define('<xsl:value-of select="$appName"/>.model.<xsl:value-of select="$rootPackage"/>.<xsl:value-of select="$dtoName"/>',{
	extend: '<xsl:value-of select="$appName"/>.model.<xsl:value-of select="$rootPackage"/>.gen._<xsl:value-of select="$dtoName"/>'
});
	</xsl:template>
</xsl:stylesheet>