<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template name="generate-service.xsl">
		<xsl:param name="serviceName"/>
		<xsl:param name="appName" />
		<xsl:param name="remoteActionNamespace"/>
		<xsl:param name="methodName" />
		<xsl:param name="interfaceName" />
		<xsl:param name="create" />
		<xsl:param name="read"   />
		<xsl:param name="update" />
		<xsl:param name="destroy" />
		
		
Ext.define('<xsl:value-of select="$appName"/>.service.<xsl:value-of select="$serviceName"/>',{

	extend: 'Ext.Base',
	
	constructor: function (config) {
    	var me = this;
    	Ext.apply(me, config);
    	me.callParent([config]);
    	
    },
    
    create : function(){
    	<xsl:value-of select="concat($remoteActionNamespace,'.',$serviceName,'.',$create)"/>(items,function(result, e) {
    		console.log(result);
    	});
    },
    read : function(){
    	<xsl:value-of select="concat($remoteActionNamespace,'.',$serviceName,'.',$read)"/>(function(result, e) {
    		return result;
    	});
    }
    update : function(){
    	<xsl:value-of select="concat($remoteActionNamespace,'.',$serviceName,'.',$update)"/>(items,function(result, e) {
    		console.log(result);
    	});
    },
    destroy : function(){
    	<xsl:value-of select="concat($remoteActionNamespace,'.',$serviceName,'.',$destroy)"/>(items,function(result, e) {
    		console.log(result);
    	});
    }
	
});
	</xsl:template>
</xsl:stylesheet>