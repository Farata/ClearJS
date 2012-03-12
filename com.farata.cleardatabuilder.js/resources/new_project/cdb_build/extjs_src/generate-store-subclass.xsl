<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/" name="generate-store-subclass.xsl">
		<xsl:param name="storeName"/>
		<xsl:param name="appName" />
		<xsl:param name="rootPackage" />
	
<xsl:text/>Ext.define('<xsl:value-of select="$rootPackage"/>.<xsl:value-of select="$storeName"/>',{
	extend: '<xsl:value-of select="$rootPackage"/>.generated._<xsl:value-of select="$storeName"/>'
});
	</xsl:template>
</xsl:stylesheet>