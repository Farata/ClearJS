<?xml version="1.0" encoding="iso-8859-1" ?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fn="http://www.w3.org/2005/xpath-functions"  
  xmlns:dto2extjs="http://dto2extjs.faratasystems.com/"
  exclude-result-prefixes="dto2extjs xsl fn"
  version="1.1" 
>
  <xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes"
	standalone="yes" indent="yes"
	media-type="text/xml"/>
	
  <!-- Import utility functions -->
  <xsl:import href="functions.xslt"/>
  
  <!-- Import type-processing functions -->
  <xsl:import href="types.xslt"/>
  
  <!-- Import chunker code -->
  <xsl:import href="chunker.xslt"/>  
  
  <xsl:param name="base" select="."/>
  <xsl:param name="metadata-dump" select="no"/>
  
  <!-- Global variables --> 
  <xsl:variable name="className">
    <xsl:call-template name="last-part-of">
      <xsl:with-param name="string" select="/dto2extjs:enum/@name" />
      <xsl:with-param name="char" select="'.'" />
    </xsl:call-template>
  </xsl:variable>  
  <xsl:variable name="packageName">
    <xsl:call-template name="package-of">
      <xsl:with-param name="name" select="/dto2extjs:enum/@name"/>
    </xsl:call-template>
  </xsl:variable>        

  <xsl:template match="/dto2extjs:enum">
    <xsl:variable name="path">
      <xsl:choose>
        <xsl:when test="$packageName">
          <xsl:value-of select="concat($base, '/', translate($packageName, '.', '/'))"/>
        </xsl:when>    
        <xsl:otherwise>
         <xsl:value-of select="$base"/>
        </xsl:otherwise>   
      </xsl:choose> 
    </xsl:variable>
    
    <!-- Writing generated enum file, overwrite always -->    
    <xsl:variable name="generated_file">
      <xsl:value-of select="concat($path, '/', $className, '.js')"/>
    </xsl:variable>
	<xsl:call-template name="write.text.chunk">
		<xsl:with-param name="filename" select="$generated_file"/>
		<xsl:with-param name="encoding" select="'utf-8'"/>
		<xsl:with-param name="media-type" select="'text/action-script'"/>	  
		<xsl:with-param name="content"><xsl:apply-templates 
			select="." mode="generated-file"/></xsl:with-param>
	</xsl:call-template>	    

    <xsl:if test="$metadata-dump = 'yes'">
	    <xsl:variable name="metadata_file">
	      <xsl:value-of select="concat($path, '/', $className, '.xml')"/>
	    </xsl:variable>
		<xsl:call-template name="write.chunk">
			<xsl:with-param name="filename" select="$metadata_file"/>
			<xsl:with-param name="method" select="'xml'"/>
			<xsl:with-param name="standalone" selct="'yes'"/>
			<xsl:with-param name="omit-xml-declaration" select="'no'"/>						
			<xsl:with-param name="encoding" select="'utf-8'"/>
			<xsl:with-param name="media-type" select="'text/xml'"/>	  
			<xsl:with-param name="indent" select="'yes'"/>
			<xsl:with-param name="content" select="."/>
		</xsl:call-template>    	
    </xsl:if>
        
  </xsl:template>		
  
  
  <xsl:template match="/dto2extjs:enum" mode="generated-file">
package <xsl:value-of select="$packageName"/> {

  public class <xsl:value-of select="$className"/> {
    
    public function <xsl:value-of select="$className"/>():void {
      throw new Error("Private constructor"); 
    } 
    
    <xsl:apply-templates/>
    
    [ArrayElementType("String")]
    public static const values:Array = [<xsl:for-each select="./dto2extjs:enum-entry"><xsl:text>
      </xsl:text><xsl:value-of select="concat(/dto2extjs:enum/@name, '.', @name)"/><xsl:if test="position() != last()">,</xsl:if>
    </xsl:for-each>
    ];
  }
  
}</xsl:template>		

  <xsl:template match="dto2extjs:enum-entry">
    public static const <xsl:value-of select="@name"/>:String = "<xsl:value-of select="@name"/>";
  </xsl:template>
  

</xsl:stylesheet>