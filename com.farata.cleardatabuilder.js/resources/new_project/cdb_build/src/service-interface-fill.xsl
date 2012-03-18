<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/|/|/" name="service-interface-fill.xsl">
		<xsl:param name="interfaceName"/>
		<xsl:param name="methodNode"/>

		<xsl:variable name="javaFillMethod" select="helper:getMethodAnnotation($interfaceName, $methodNode/@name, 'clear.cdb.js.annotations.CX_JSFillMethod')"/>
		<xsl:variable name="javaAutoSyncEnabled" select="boolean($javaFillMethod/method[@name='autoSyncEnabled']/@value = 'true')"/>
		<xsl:variable name="javaSync" select="boolean($javaFillMethod/method[@name='sync']/@value = 'true')"/>
		<xsl:if test="$javaSync">
 	java.util.List&lt;clear.data.ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_sync(java.util.List&lt;clear.data.ChangeObject&gt; items) throws Exception;

	java.util.List&lt;clear.data.ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_insertItems(java.util.List&lt;clear.data.ChangeObject&gt; items) throws Exception;

	java.util.List&lt;clear.data.ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_updateItems(java.util.List&lt;clear.data.ChangeObject&gt; items) throws Exception;

	java.util.List&lt;clear.data.ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_deleteItems(java.util.List&lt;clear.data.ChangeObject&gt; items) throws Exception;

	void <xsl:value-of select="$methodNode/@name"/>_doCreate(clear.data.ChangeObject changeObject);
	
	void <xsl:value-of select="$methodNode/@name"/>_doUpdate(clear.data.ChangeObject changeObject);

	void <xsl:value-of select="$methodNode/@name"/>_doDelete(clear.data.ChangeObject changeObject);
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>