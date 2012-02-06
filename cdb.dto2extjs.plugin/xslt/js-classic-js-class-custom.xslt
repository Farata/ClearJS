<?xml version="1.0" encoding="iso-8859-1" ?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dto2extjs="http://dto2extjs.faratasystems.com/"
  version="1.1" 
>
  <xsl:template match="/dto2extjs:class" mode="custom-file">
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
    
    <xsl:variable name="finalPrefix">
      <xsl:if test="@final='true'">final<xsl:text> </xsl:text></xsl:if>
    </xsl:variable>

    <xsl:variable name="simpleClassName">
    	<xsl:call-template name="unqualifyClassName"><xsl:with-param name="string" select="$className"/></xsl:call-template>	  	
    </xsl:variable>
    
    <xsl:variable name="defaultCollectionName">
    	<xsl:call-template name="collectionsPackageFromDTOPackage"><xsl:with-param name="string" select="$packageName"/><xsl:value-of select="$className"/></xsl:call-template>.<xsl:choose>
    		<xsl:when test="substring($simpleClassName,string-length($simpleClassName) - 2,3)='DTO'"><xsl:value-of select="substring($simpleClassName,1,string-length($simpleClassName) - 3)"/></xsl:when>
    		<xsl:otherwise><xsl:value-of select="$simpleClassName"/></xsl:otherwise>
    	</xsl:choose>Collection</xsl:variable>
    
package <xsl:value-of select="$packageName"/> {

  <xsl:variable name="superClassImport">
    <xsl:call-template name="import-if-necessary">
  		<xsl:with-param name="thisClass"><xsl:value-of select="./@name"/></xsl:with-param>
  		<xsl:with-param name="otherClass"><xsl:value-of select="$generatedSuperclass"/></xsl:with-param>
  	</xsl:call-template>
  </xsl:variable>
  <xsl:value-of select="$superClassImport"/>
  
  <xsl:call-template name="import-interfaces">
     <xsl:with-param name="class" select="."/>
  </xsl:call-template>
    
  <xsl:choose>
    <xsl:when test="@abstract = 'true'">
    
  // Abstract class, not used in remoting
  /*
   * [RemoteClass(alias="<xsl:value-of select="@javaClass"/>")]
   */<xsl:text>
</xsl:text></xsl:when>
    <xsl:otherwise>
  [DefaultDataCollection(name="<xsl:value-of select="$defaultCollectionName"/>")]    
  [RemoteClass(alias="<xsl:value-of select="@javaClass"/>")]<xsl:text>
</xsl:text>  
    </xsl:otherwise>
  </xsl:choose>
  public <xsl:value-of select="$finalPrefix"/>class <xsl:value-of select="$className"/> extends <xsl:value-of select="$generatedSuperclass"/><xsl:value-of select="$interfaces"/> {

    /* Constructor */
    public function <xsl:value-of select="$className"/>():void {
      super();
    }  
    
  }

}</xsl:template>		

<xsl:template name="collectionsPackageFromDTOPackage">
  <xsl:param name="string" />
  <xsl:param name="delimiter" select="'.'" />
  <xsl:choose>
    <xsl:when test="$delimiter and contains($string, $delimiter)"><xsl:value-of select="substring-before($string, $delimiter)" />.<xsl:call-template name="collectionsPackageFromDTOPackage">
        <xsl:with-param name="string" 
                        select="substring-after($string, $delimiter)" />
        <xsl:with-param name="delimiter" select="$delimiter" />
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>collections</xsl:otherwise>
  </xsl:choose>
</xsl:template>  
<xsl:template name="unqualifyClassName">
  <xsl:param name="string" />
  <xsl:param name="delimiter" select="'.'" />
  <xsl:choose>
    <xsl:when test="$delimiter and contains($string, $delimiter)"><xsl:call-template name="unqualifyClassName">
        <xsl:with-param name="string" 
                        select="substring-after($string, $delimiter)" />
        <xsl:with-param name="delimiter" select="$delimiter" />
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise><xsl:value-of select="$string" /></xsl:otherwise>
  </xsl:choose>
</xsl:template> 
</xsl:stylesheet>