<?xml version="1.0" encoding="iso-8859-1" ?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dto2extjs="http://dto2extjs.faratasystems.com/"
  version="1.1" 
>

  <xsl:template match="/dto2extjs:interface" mode="custom-file">
    <xsl:variable name="generatedSuperclass">
      <xsl:call-template name="generated-superclass">
      	<xsl:with-param name="packageName" select="$packageName"/>
      	<xsl:with-param name="className" select="$className"/>   
      	<xsl:with-param name="genPackage" select="$genPackage"/>   	
      </xsl:call-template>
    </xsl:variable>
      
package <xsl:value-of select="$packageName"/> {

  <xsl:variable name="superClassImport">
    <xsl:call-template name="import-if-necessary">
  		<xsl:with-param name="thisClass"><xsl:value-of select="./@name"/></xsl:with-param>
  		<xsl:with-param name="otherClass"><xsl:value-of select="$generatedSuperclass"/></xsl:with-param>
  	</xsl:call-template>
  </xsl:variable>
  <xsl:value-of select="$superClassImport"/>
  
  public interface <xsl:value-of select="$className"/> extends <xsl:value-of select="$generatedSuperclass"/> {
  }

}</xsl:template>		
  
</xsl:stylesheet>