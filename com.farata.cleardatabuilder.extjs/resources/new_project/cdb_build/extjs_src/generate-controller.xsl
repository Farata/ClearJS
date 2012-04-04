<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	<xsl:template match="/" name="generate-controller.xsl">
		<xsl:param name="storeName"/>
		<xsl:param name="appName" />
		<xsl:param name="dtoName" />
<xsl:text/>Ext.define('<xsl:value-of select="$appName"/>.controller.SampleController',{
	extend: 'Ext.app.Controller',
	stores:	['<xsl:value-of select="$storeName"/>'], 
    refs: [{
       ref: 'ThePanel',
       selector: 'samplegridpanel'
    }],
		
	init: function(){
		this.control({
			'samplegridpanel button[action=load]':{
				click: this.onLoad
			},			
			'samplegridpanel button[action=add]':{
				click: this.onAdd
			},				
			'samplegridpanel button[action=remove]':{
				click: this.onRemove
			},				
			'samplegridpanel button[action=sync]':{
				click: this.onSync
			}
		});
	},
		
	onLoad:	function(){
		var store = this.getStore('<xsl:value-of select="$storeName"/>');
		store.load();
	},
	onAdd: function()	{
		var store = this.getStore('<xsl:value-of select="$storeName"/>'),
			record = store.createModel({});
			
		record.setId(store.getLocalIdentity());
		return store.add(record);	    	
	},	
	onRemove: function(){
		var store = this.getStore('<xsl:value-of select="$storeName"/>'),
			panel = this.getThePanel(),
            view = panel.getView(),
            selectionModel = view.getSelectionModel(),
            selectedRecords = selectionModel.getSelection();
		store.remove(selectedRecords);    		
	},	
	onSync:	function(){
		var store = this.getStore('<xsl:value-of select="$storeName"/>');
		store.sync();
	}		
});
	</xsl:template>
</xsl:stylesheet>