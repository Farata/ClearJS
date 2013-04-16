<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	>

	<xsl:output  method="text" />	

	<xsl:param name="appName" />
	
	<xsl:template match="/">Ext.namespace( '<xsl:value-of select="$appName"/>.direct.config');
Ext.namespace( '<xsl:value-of select="$appName"/>.direct.action');

<xsl:value-of select="$appName"/>.direct.config.PROVIDER_BASE_URL=window.location.protocol + '//' + window.location.host + '/' + (window.location.pathname.split('/').length>2 ? window.location.pathname.split('/')[1]+ '/' : '')  + 'djn/directprovider';

<xsl:value-of select="$appName"/>.direct.config.POLLING_URLS = {
}

<xsl:value-of select="$appName"/>.direct.config.REMOTING_API = {
	url: <xsl:value-of select="$appName"/>.direct.config.PROVIDER_BASE_URL,
	type: 'remoting',
	namespace: <xsl:value-of select="$appName"/>.direct.action,
	actions: {
<xsl:for-each select="annotated-types/annotated-type">
	<xsl:variable name="subClassName" select="helper:createSubServiceName(@name)" />
<xsl:text>		</xsl:text><xsl:value-of select="helper:getTypeName($subClassName)" />:[
<xsl:variable name="methods" select="current()/methods"/>
	<xsl:for-each select="$methods/method">
				<xsl:variable name="methodNode" select="current()"/>
				<xsl:if test="$methodNode/annotations/annotation[starts-with(@name,'clear.cdb.extjs.annotations.JS') and not(starts-with(@name,'clear.cdb.extjs.annotations.JSG'))]">
<xsl:text>			</xsl:text>{
				name: '<xsl:value-of select="$methodNode/@name"/>_updateItems'/*(java.util.List) => java.util.List */,
				len: 1,
				formHandler: false
			},
			{
				name: '<xsl:value-of select="$methodNode/@name"/>_deleteItems'/*(java.util.List) => java.util.List */,
				len: 1,
				formHandler: false
			},
			{
				name: '<xsl:value-of select="$methodNode/@name"/>_insertItems'/*(java.util.List) => java.util.List */,
				len: 1,
				formHandler: false
			},
				</xsl:if>
			{
				name: '<xsl:value-of select="$methodNode/@name"/>',
				len: <xsl:value-of select="count($methodNode/parameters/parameter)"/>,
				formHandler: false
			}<xsl:if test="not(last() = position())">,
</xsl:if>
	</xsl:for-each>
		]<xsl:if test="not(last() = position())">,
</xsl:if>
</xsl:for-each>
	}
}
	</xsl:template>
</xsl:stylesheet>