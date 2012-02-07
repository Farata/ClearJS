<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/" name="generate-extended-store.xsl">
		<xsl:param name="serviceName"/>
		<xsl:param name="appName" />
		<xsl:param name="methodName" />
	
Ext.define('<xsl:value-of select="$appName"/>.store.<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Store',{
	extend: '<xsl:value-of select="$appName"/>.store._generated.<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Generated_Store'
});
	</xsl:template>
</xsl:stylesheet>