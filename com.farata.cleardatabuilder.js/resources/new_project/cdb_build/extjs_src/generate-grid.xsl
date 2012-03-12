<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/" name="generate-grid.xsl">
		<xsl:param name="storeName"/>
		<xsl:param name="appName" />
		<xsl:param name="dtoName" />
		
		<xsl:variable name="fields" select="helper:getBeanProperties($dtoName)" />
	
<xsl:text/>Ext.define('<xsl:value-of select="$appName"/>.view.GridTest',{

	extend: 'Ext.grid.Panel',
	store:	'<xsl:value-of select="$storeName"/>',
	alias:	'widget.GridTest',
		
	columns : [
	<xsl:for-each select="$fields/property">
<xsl:text/>{ 
		header:'<xsl:value-of select="@name" />', 
		dataIndex: '<xsl:value-of select="@name" />',
		flex:1<xsl:text/>
		<xsl:if test="@type='java.lang.String'" >
		,editor:{xtype:'textfield'} 
		</xsl:if>
<xsl:text/>
	}<xsl:if test="not(last() = position())">,
	</xsl:if>
	</xsl:for-each>
<xsl:text/>],
		
	tbar : [ 
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

	plugins : [ 
	{
		ptype : 'cellediting'
	} 
	]
		
});
	</xsl:template>
	
	
</xsl:stylesheet>