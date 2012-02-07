<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/" name="generate-store.xsl">
		<xsl:param name="serviceName"/>
		<xsl:param name="appName" />
		<xsl:param name="remoteActionNamespace"/>
		<xsl:param name="methodName" />
		<xsl:param name="interfaceName" />
		<xsl:param name="create" />
		<xsl:param name="read"   />
		<xsl:param name="update" />
		<xsl:param name="destroy" />
		
	<xsl:variable name="transferType" select="helper:getMethodTransferType($interfaceName, $methodName)"/>
		
Ext.define('<xsl:value-of select="$appName"/>.store._generated.<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Generated_Store',{

	extend: 'Clear.data.DirectStore',
	requires  : ['Ext.direct.Manager','<xsl:value-of select="$appName"/>.model.<xsl:value-of select="helper:getTypeName($transferType)"/>','Ext.window.MessageBox'],
	model:'<xsl:value-of select="$appName"/>.model.<xsl:value-of select="helper:getTypeName($transferType)"/>',
		
	api: {
		create:<xsl:value-of select="concat($remoteActionNamespace,'.',$serviceName,'.',$create)"/>,
		read : <xsl:value-of select="concat($remoteActionNamespace,'.',$serviceName,'.',$read)"/>,
		update:<xsl:value-of select="concat($remoteActionNamespace,'.',$serviceName,'.',$update)"/>,
		destroy:<xsl:value-of select="concat($remoteActionNamespace,'.',$serviceName,'.',$destroy)"/>
    },
	
	autoLoad: true,

		listeners: {
	
		beforeload: function(store, operation) {
						console.log("beforeload");
		},
		load: function(store, records, successful, error){
			if (successful) {
				Ext.MessageBox.alert( "Information", Ext.String.format("Loaded {0} records",records.length));
			} else {
				Ext.MessageBox.alert( error.message, Ext.String.format("{0}::{1} failed: {2}", error.action, error.method, error.where));

			}
		}
	}
});
	</xsl:template>
</xsl:stylesheet>