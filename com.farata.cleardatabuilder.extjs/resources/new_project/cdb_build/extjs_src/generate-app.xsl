<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	extension-element-prefixes="redirect" exclude-result-prefixes="xalan redirect">
	
	<xsl:template match="/" name="generate-app.xsl">
		<xsl:param name="storeName"/>
		<xsl:param name="appName" />
		<xsl:param name="dtoName" />
		
		<xsl:variable name="fields" select="helper:getBeanProperties($dtoName)" />
<xsl:text/>Ext.Loader.setConfig({
	disableCaching: false,
	enabled: true,
	paths: {
		<xsl:value-of select="$appName"/>: 'app', 
		Clear: 'clear'
	}
});

Ext.syncRequire('<xsl:value-of select="$appName"/>.init.InitDirect');
// Define GridPanel
Ext.define('<xsl:value-of select="$appName"/>.view.SampleGridPanel',{
	extend: 'Ext.grid.Panel',
	store:	'<xsl:value-of select="$storeName"/>',
	alias:	'widget.samplegridpanel',
	plugins : [ 
		{ ptype : 'cellediting'} 
	],				
	columns : [<xsl:for-each select="$fields/property">
		{ header:'<xsl:value-of select="@name" />', dataIndex: '<xsl:value-of select="@name" />'<xsl:if test="@type='java.lang.String'" >, editor:{xtype:'textfield'}</xsl:if>, flex:1}<xsl:if test="not(last() = position())">, </xsl:if> </xsl:for-each>
	],		
	tbar : [ 
		{text : 'Load', action: 'load'}, 
		{text : 'Add', action:'add'}, 
		{text : 'Remove', action:'remove'}, 
		{text : 'Sync', action:'sync'} 
	]
});
// Launch the application 
Ext.application({
    name: '<xsl:value-of select="$appName"/>',
    requires: [
        'Clear.override.ExtJSOverrider'
    ],
    controllers: [
         'SampleController'
    ],
    launch:function() {
         Ext.create('Ext.container.Viewport', {
			items: [
				{ xtype: 'samplegridpanel' }
			]
         });
    }
});
	</xsl:template>	
</xsl:stylesheet>