<?xml version="1.0" encoding="iso-8859-1" ?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dto2extjs="http://dto2extjs.faratasystems.com/"
  exclude-result-prefixes="xsl dto2extjs"
  version="1.1" 
>
	<xsl:template match="/dto2extjs:class" mode="custom-file">
		<xsl:variable name="thisClass" select="@name"/>
    	<xsl:variable name="generatedSuperclass">
			<xsl:call-template name="generated-superclass">
				<xsl:with-param name="packageName" select="$packageName"/>
				<xsl:with-param name="className" select="$className"/>   
				<xsl:with-param name="genPackage" select="$genPackage"/>   	
			</xsl:call-template>
    	</xsl:variable>
    
    	<xsl:variable name="interfaces">
			<xsl:call-template name="class-implements-clause">
				<xsl:with-param name="class" select="."/>
				<xsl:with-param name="predefinedInterfaces"/>
			</xsl:call-template>
		</xsl:variable>        

Ext.define('<xsl:value-of select="$thisClass"/>', {
	extend: '<xsl:value-of select="$generatedSuperclass"/>'
});
	</xsl:template>
</xsl:stylesheet>
	  
