<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/" name="generate-grid.xsl">
		<xsl:param name="serviceName"/>
		<xsl:param name="appName" />
		<xsl:param name="methodName" />
		<xsl:param name="interfaceName" />
	
	Ext.define('<xsl:value-of select="$appName"/>.view.<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Panel',{

		extend: 'Ext.grid.Panel',
		store:	'<xsl:value-of select="$appName" />.store.<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Store',
		alias:	'widget.<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Panel',
		
		columns:	[
		<xsl:for-each select="//method/query/types">
			{ 
				<xsl:variable name="vartype" select="type/@name" />
				
				header:'<xsl:value-of select="type/@alias" />', 
				dataIndex: '<xsl:value-of select="type/@alias" />',
				flex:1
				
				<xsl:if test="contains($vartype, 'String')" >,editor:{xtype:'textfield'} </xsl:if>
			},
		</xsl:for-each>
			
			
		],
		
		tbar : 		[ 
	         {
	        	 	xtype : 'button',
        	 		text : 'Load'
	         },
	         {
	     			xtype : 'button',
	     			text : 'Add'
	     	 },
	         {
	     		 	xtype : 'button',
	     			text : 'Delete'
	     	 },
	     	 {
	     		 	xtype : 'button',
	     		 	text : 'Save'
	     	 }
     	],
		
		plugins:[{ptype:	'cellediting'}]
		
	});
	</xsl:template>
	
	
</xsl:stylesheet>