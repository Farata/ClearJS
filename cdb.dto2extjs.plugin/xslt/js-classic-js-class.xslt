<?xml version="1.0" encoding="iso-8859-1" ?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dto2extjs="http://dto2extjs.faratasystems.com/"
  exclude-result-prefixes="dto2extjs xsl"  
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
  
  <!-- Import functionality -->  
  <xsl:import href="js-classic-js-class-generated.xslt"/>
  <xsl:import href="js-classic-js-class-custom.xslt"/>
  
  <xsl:param name="base" select="."/>
  <xsl:param name="metadata-dump" select="'no'"/>
  <xsl:param name="generated-pckg" select="'generated'"/>
  
  <xsl:variable name="kind" select="/dto2extjs:class/@kind"/>
  <xsl:variable name="packageName">
    <xsl:call-template name="package-of">
      <xsl:with-param name="name" select="/dto2extjs:class/@name"/>
    </xsl:call-template>
  </xsl:variable>        
  <xsl:variable name="className">
    <xsl:call-template name="last-part-of">
      <xsl:with-param name="string" select="/dto2extjs:class/@name" />
      <xsl:with-param name="char" select="'.'" />
    </xsl:call-template>
  </xsl:variable> 
  <xsl:variable name="genPackage" select="$generated-pckg"/>
      
  <!-- Global variables -->     
  <xsl:template match="/dto2extjs:class">
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
    
    <!-- Writing generated managed class file, overwrite always -->
    <xsl:variable name="generated_file">
      <xsl:call-template name="generated-file">
      	<xsl:with-param name="path" select="$path"/>
      	<xsl:with-param name="className" select="$className"/>
      	<xsl:with-param name="genPackage" select="translate($genPackage, '.', './')"/>
      </xsl:call-template>
    </xsl:variable>
	<xsl:call-template name="write.text.chunk">
		<xsl:with-param name="filename" select="$generated_file"/>
		<xsl:with-param name="encoding" select="'utf-8'"/>
		<xsl:param name="media-type" select="'text/action-script'"/>	  
		<xsl:with-param name="content"><xsl:apply-templates 
			select="." mode="generated-file"/></xsl:with-param>
	</xsl:call-template>	
	
    <!-- Writing custom managed class file, only when missing -->	
    <xsl:variable name="custom_file">
      <xsl:value-of select="concat($path, '/', $className, '.js')"/>
    </xsl:variable>
    <xsl:variable name="custom_file_exists">
    	<xsl:call-template name="file-exists">
    		<xsl:with-param name="name" select="$custom_file"/>
    	</xsl:call-template>
    </xsl:variable>
    <xsl:if test="$custom_file_exists != 'yes'">
		<xsl:call-template name="write.text.chunk">
			<xsl:with-param name="filename" select="$custom_file"/>
			<xsl:with-param name="encoding" select="'utf-8'"/>
			<xsl:param name="media-type" select="'text/action-script'"/>	  
			<xsl:with-param name="content"><xsl:apply-templates 
				select="." mode="custom-file"/></xsl:with-param>
		</xsl:call-template>	
    </xsl:if>
	
	
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
			<xsl:param name="media-type" select="'text/xml'"/>	  
			<xsl:with-param name="indent" select="'yes'"/>
			<xsl:with-param name="content" select="."/>
		</xsl:call-template>    	
    </xsl:if>
	
	
  </xsl:template>		
</xsl:stylesheet>