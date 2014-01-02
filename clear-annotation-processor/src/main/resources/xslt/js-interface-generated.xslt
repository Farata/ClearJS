<?xml version="1.0" encoding="iso-8859-1" ?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dto2extjs="http://dto2extjs.faratasystems.com/"
  version="1.1" 
>

  <xsl:template match="/dto2extjs:interface" mode="generated-file">
  	<xsl:variable name="generatedClassName" select="concat('_', $className)"/>    
	<xsl:variable name="generatedPackageName">
		<xsl:call-template name="generated-package">
			<xsl:with-param name="packageName" select="$packageName"/>
			<xsl:with-param name="genPackage" select="$genPackage"/>
		</xsl:call-template>
	</xsl:variable>
package <xsl:value-of select="$generatedPackageName"/> {
  <xsl:call-template name="import-types-by-class">
      <xsl:with-param name="class" select="."/>
      <xsl:with-param name="qualifiedClassName">
      	<xsl:choose>
      		<xsl:when test="$generatedPackageName"><xsl:value-of select="concat($generatedPackageName, '.', $generatedClassName)"/></xsl:when>
      		<xsl:otherwise><xsl:value-of select="$generatedClassName"/></xsl:otherwise>
      	</xsl:choose>
      </xsl:with-param>      
    </xsl:call-template><xsl:text>
</xsl:text>
    <xsl:choose>
      <xsl:when test="$kind = 'managed'">
  import mx.data.IManaged;
      </xsl:when>
      <xsl:when test="$kind = 'remote'">
  import flash.events.IEventDispatcher;    
  import mx.core.IUID;
      </xsl:when>
    </xsl:choose>
    
    <xsl:variable name="interfaces">
      <xsl:call-template name="interface-implements-clause">
        <xsl:with-param name="class" select="."/>
        <xsl:with-param name="predefinedInterfaces">
          <xsl:choose>
            <xsl:when test="$kind = 'managed'"><xsl:text>mx.data.IManaged</xsl:text></xsl:when>
            <xsl:when test="$kind = 'remote'"><xsl:text>flash.events.IEventDispatcher, mx.core.IUID</xsl:text></xsl:when>
          </xsl:choose>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:variable>
  /* [ExcludeClass] */
  public interface <xsl:value-of select="$generatedClassName"/><xsl:value-of select="$interfaces"/> {
      <xsl:apply-templates/>
  }
  
}</xsl:template>		

  <xsl:template match="dto2extjs:property"><xsl:text>
</xsl:text>    /* Property <xsl:value-of select="@name"/> */
    <xsl:call-template name="write-property-meta">
      <xsl:with-param name="property" select="."/>
    </xsl:call-template>
    <xsl:if test="@declare-getter">function get <xsl:value-of select="@name"/>():<xsl:value-of select="@type"/>;
    </xsl:if>
    <xsl:if test="@declare-setter">function set <xsl:value-of select="@name"/>(value:<xsl:value-of select="@type"/>):void;
    </xsl:if>
    
  </xsl:template>	
  
  
</xsl:stylesheet>