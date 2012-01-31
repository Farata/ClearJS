<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/" name="generate-store.xsl">
		<xsl:param name="serviceName"/>
		<xsl:param name="appName" />
		<xsl:param name="methodName" />
		<xsl:param name="interfaceName" />
		<xsl:param name="create" />
		<xsl:param name="read"   />
		<xsl:param name="update" />
		<xsl:param name="destroy" />
	<xsl:variable name="transferType" select="helper:getMethodTransferType($interfaceName, $methodName)"/>
	
	Ext.define('<xsl:value-of select="$appName"/>.store.<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Store',{

		extend: 'Clear.data.DirectStore',
		model:'<xsl:value-of select="helper:getTypeName($transferType)"/>',
		
		api: {
			create:'<xsl:value-of select="concat($serviceName,'.',$create)"/>',
			read : '<xsl:value-of select="concat($serviceName,'.',$read)"/>',
			update:'<xsl:value-of select="concat($serviceName,'.',$update)"/>',
			destroy:'<xsl:value-of select="concat($serviceName,'.',$destroy)"/>'
		},

		constructor: function(config) {
			var me = this;
			var cnfg = config || {};
			Ext.copyTo(cnfg, me, "paramOrder,paramsAsHash,directFn,api,simpleSortMode", true);
			me.callParent([cnfg]);
		}
	});
	</xsl:template>
</xsl:stylesheet>