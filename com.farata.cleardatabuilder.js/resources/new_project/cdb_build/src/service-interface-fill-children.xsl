<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/|/|/" name="service-interface-fill-children.xsl">
		<xsl:param name="interfaceName"/>
		<xsl:param name="methodNode"/>
	java.util.List&lt;flex.data.ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_sync(java.util.List&lt;flex.data.ChangeObject&gt; items) throws Throwable;

	java.util.List&lt;flex.data.ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_deleteItems(java.util.List&lt;flex.data.ChangeObject&gt; items) throws Exception;

	java.util.List&lt;flex.data.ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_updateItems(java.util.List&lt;flex.data.ChangeObject&gt; items) throws Exception;

	java.util.List&lt;flex.data.ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_insertItems(java.util.List&lt;flex.data.ChangeObject&gt; items) throws Exception;
		
	void <xsl:value-of select="$methodNode/@name"/>_beforeDelete(org.hibernate.Session session, Object entity);

	void <xsl:value-of select="$methodNode/@name"/>_afterDelete(org.hibernate.Session session, Object entity);
	
	void <xsl:value-of select="$methodNode/@name"/>_beforeInsert(org.hibernate.Session session, Object entity);

	void <xsl:value-of select="$methodNode/@name"/>_afterInsert(org.hibernate.Session session, Object entity);

	void <xsl:value-of select="$methodNode/@name"/>_beforeUpdate(org.hibernate.Session session, Object entity);

	void <xsl:value-of select="$methodNode/@name"/>_afterUpdate(org.hibernate.Session session, Object entity);
	</xsl:template>
</xsl:stylesheet>