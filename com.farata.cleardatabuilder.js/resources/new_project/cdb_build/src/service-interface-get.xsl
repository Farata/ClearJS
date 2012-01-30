<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/|/|/" name="service-interface-get.xsl">
		<xsl:param name="interfaceName"/>
		<xsl:param name="methodNode"/>
		<xsl:variable name="getMethod" select="helper:getMethodAnnotation($interfaceName, $methodNode/@name, 'clear.cdb.annotations.CX_GetMethod')"/>
		<xsl:variable name="sync" select="boolean($getMethod/method[@name='sync']/@value = 'true')"/>
		<xsl:variable name="transferType" select="helper:getMethodTransferType($interfaceName, $methodNode/@name)"/>
		<xsl:if test="$sync">
	public <xsl:value-of select="concat($transferType, ' ', $methodNode/@name)"/>_create(<xsl:value-of select="$transferType"/> dto);

	public <xsl:value-of select="concat($transferType, ' ', $methodNode/@name)"/>_update(<xsl:value-of select="$transferType"/> dto);
	
	public <xsl:value-of select="concat($transferType, ' ', $methodNode/@name)"/>_delete(<xsl:value-of select="$transferType"/> dto);
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>