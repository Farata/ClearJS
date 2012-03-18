<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/|/|/" name="service-interface-jpql.xsl">
		<xsl:param name="interfaceName"/>
		<xsl:param name="methodNode"/>

		<xsl:variable name="jpqlMethodNode" select="$methodNode/annotations/annotation[@name='clear.cdb.js.annotations.CX_JSJPQLMethod']"/>
		<xsl:variable name="updateInfo" select="$methodNode/annotations/annotation[@name='clear.cdb.js.annotations.CX_UpdateInfo'] | $jpqlMethodNode/method[@name='updateInfo']/value/annotation"/>
		<xsl:variable name="updateEntity" select="helper:replaceAll($updateInfo/method[@name='updateEntity']/@value, 'class ', '')"/>
	org.hibernate.Query <xsl:value-of select="$methodNode/@name"/>_preExecQuery(org.hibernate.Query query<xsl:if test="count($methodNode/parameters/parameter)>0">, <xsl:for-each select="$methodNode/parameters/parameter"><xsl:value-of select="concat(@type,' ')"/><xsl:value-of select="@name"/><xsl:if test="not(last() = position())">, </xsl:if></xsl:for-each></xsl:if>);
		<xsl:if test="$updateEntity">
	java.util.List&lt;clear.data.ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_sync(java.util.List&lt;clear.data.ChangeObject&gt; items) throws Throwable;

	java.util.List&lt;clear.data.ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_deleteItems(java.util.List&lt;clear.data.ChangeObject&gt; items) throws Exception;

	java.util.List&lt;clear.data.ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_updateItems(java.util.List&lt;clear.data.ChangeObject&gt; items) throws Exception;

	java.util.List&lt;clear.data.ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_insertItems(java.util.List&lt;clear.data.ChangeObject&gt; items) throws Exception;
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>