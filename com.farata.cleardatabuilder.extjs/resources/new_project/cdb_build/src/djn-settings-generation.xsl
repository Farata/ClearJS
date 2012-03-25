<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	>

	<xsl:output  method="text" />	

	<xsl:param name="apis" />
	<xsl:param name="package" />
	
	<xsl:template match="/">
		<xsl:for-each select="annotated-types/annotated-type">
				<xsl:variable name="interfaceName" select="@name" /><xsl:variable name="packageName" select="helper:getPackageName($interfaceName)" />
<xsl:value-of select="helper:createSubServiceName($interfaceName)" /><xsl:if test="not(last() = position())">,
</xsl:if>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>