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
		
<xsl:text/>Ext.define('<xsl:value-of select="$appName"/>.controller.ControllerTest',{

	extend: 'Ext.app.Controller',
	stores:	['<xsl:value-of select="$storeName"/>'],
	views: ['<xsl:value-of select="$appName"/>.view.GridTest'],
		
	init: function(){

		this.control({
			'GridTest button[text=Load]':{
				click: this.onLoad
			},
				
			'GridTest button[text=Add]':{
				click: this.onAdd
			},
				
			'GridTest button[text=Delete]':{
				click: this.onDelete
			},
				
			'GridTest button[text=Save]':{
				click: this.onSave
			}
		});
	},
		
	onLoad:	function(){
		this.getStore('<xsl:value-of select="$storeName"/>').load();
	},

	onAdd: function()	{
		var store = this.getStore('<xsl:value-of select="$storeName"/>'),
		edit = Ext.ComponentQuery.query('GridTest')[0].plugins[0];
		
		store.sort('id', 'asc');
		rec = Ext.create('<xsl:value-of select="helper:getModelNameFull($dtoName)"/>',{
			id:	store.last().getId()+1,
	        c: 'new_company'
		});
		
		edit.cancelEdit();
		store.insert(store.count(), rec);
		store.sync();
		edit.startEditByPosition({
		   	row:store.count()-1, column:1
	    });
	    	
	},
	
	onDelete: function(){
		var store = this.getStore('<xsl:value-of select="$storeName"/>');
		var deletingRecord = Ext.ComponentQuery.query('GridTest')[0].getSelectionModel().getSelection()[0];
		store.remove(deletingRecord);
	},
	
	onSave:	function(){
		this.getStore('<xsl:value-of select="$storeName"/>').sync();
	}
		
});
	</xsl:template>
</xsl:stylesheet>