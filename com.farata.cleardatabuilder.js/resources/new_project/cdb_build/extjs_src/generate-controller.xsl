<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	<xsl:template match="/" name="generate-controller.xsl">
		<xsl:param name="serviceName"/>
		<xsl:param name="appName" />
		<xsl:param name="methodName" />
		<xsl:param name="interfaceName" />
		
	Ext.define('<xsl:value-of select="$appName"/>.controller.<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Controller',{

		extend: 'Ext.app.Controller',
		stores:	['<xsl:value-of select="$appName"/>.store.<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Store'],
		views:	['<xsl:value-of select="$appName"/>.view.<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Panel'],
		
		init:	function(){
		
			this.control({
			
				'<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Panel button[text=Load]':{
					click: this.onLoad
				},
				
				'<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Panel button[text=Add]':{
					click: this.onAdd
				},
				
				'<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Panel button[text=Delete]':{
					click: this.onDelete
				},
				
				'<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Panel button[text=Save]':{
					click: this.onSave
				}
			});
		},
		
		
		onLoad:	function(){
			this.getStore('<xsl:value-of select="$appName"/>.store.<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Store').load();
		},
	
		onAdd:	function()	{
			var store = this.getStore('<xsl:value-of select="$appName"/>.store.<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Store'),
				edit = Ext.ComponentQuery.query('<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Panel')[0].plugins[0];
			
			store.sort('id', 'asc');
			rec = Ext.create('<xsl:value-of select="$appName"/>.model.CompanyDTO',{
			id:			store.last().getId()+1,
	        c: 'new_company'
		    });
		
		    edit.cancelEdit();
			store.insert(store.count(), rec);
			store.sync();
		    edit.startEditByPosition({
		    	row:store.count()-1, column:1
	    	});
	    	
    	 	
		},
	
		onDelete:	function(){
			var store = this.getStore('<xsl:value-of select="$appName"/>.store.<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Store');
			var delitingRecord = Ext.ComponentQuery.query('<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Panel')[0].getSelectionModel().getSelection()[0];
			store.remove(delitingRecord);
		},
	
		onSave:	function(){
			this.getStore('<xsl:value-of select="$appName"/>.store.<xsl:value-of select="$serviceName"/>_<xsl:value-of select="$methodName"/>_Store').sync();
			
			
		}
		
	});
	</xsl:template>
</xsl:stylesheet>