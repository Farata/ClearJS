<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">

	<xsl:output omit-xml-declaration="yes" method="text" />

	<xsl:template match="/" name="generate-store.xsl">
		<xsl:param name="rootPackage" />
		<xsl:param name="interfaceName" />
		<xsl:param name="fillMethodName" />
		<xsl:param name="storeName" />
		<xsl:param name="autoSyncEnabled" />
		<xsl:param name="appName" />
		<xsl:param name="remoteActionNamespace" />
		<xsl:param name="serviceName" />

		<xsl:variable name="dtoName"
			select="helper:getMethodTransferType($interfaceName, $fillMethodName)" />

		<xsl:variable name="create"
			select="concat($fillMethodName,'_insertItems')" />
		<xsl:variable name="read" select="$fillMethodName" />
		<xsl:variable name="update"
			select="concat($fillMethodName,'_updateItems')" />
		<xsl:variable name="destroy"
			select="concat($fillMethodName,'_deleteItems')" />
		
<xsl:text/>Ext.define('<xsl:value-of select="$appName"/>.store.<xsl:value-of select="$rootPackage"/>.<xsl:value-of select="$storeName"/>',{

	extend: 'Clear.data.DirectStore',
	requires  : ['Ext.direct.Manager','<xsl:value-of select="$appName"/>.model.<xsl:value-of select="$dtoName"/>'],
	model:'<xsl:value-of select="$appName"/>.model.<xsl:value-of select="$dtoName"/>',
		
	api: {
		create:<xsl:value-of select="concat($remoteActionNamespace,'.',$serviceName,'.',$create)"/>,
		read : <xsl:value-of select="concat($remoteActionNamespace,'.',$serviceName,'.',$read)"/>,
		update:<xsl:value-of select="concat($remoteActionNamespace,'.',$serviceName,'.',$update)"/>,
		destroy:<xsl:value-of select="concat($remoteActionNamespace,'.',$serviceName,'.',$destroy)"/>
    }

});
	</xsl:template>
</xsl:stylesheet>