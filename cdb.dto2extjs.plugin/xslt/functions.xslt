<!-- Utility functions -->
<xsl:stylesheet version="2.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:io="xalan://java.io"
  exclude-result-prefixes="xsl io fn">
  
  <!-- define a last-part-of named template -->
  <xsl:template name="last-part-of">
    <!-- declare that it takes two parameters 
	  - the string and the char -->
    <xsl:param name="string" />
    <xsl:param name="char" />
    <xsl:choose>
      <!-- if the string contains the character... -->
      <xsl:when test="contains($string, $char)">
         <!-- call the template recursively... -->
         <xsl:call-template name="last-part-of">
           <!-- with the string being the string after the character
             -->
           <xsl:with-param name="string" select="substring-after($string, $char)" />
           <!-- and the character being the same as before -->
           <xsl:with-param name="char" select="$char" />
         </xsl:call-template>
      </xsl:when>
      <!-- otherwise, return the value of the string -->
      <xsl:otherwise><xsl:value-of select="$string" /></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <xsl:template name="package-of">
    <xsl:param name="name"/>
    <xsl:variable name="cname">
      <xsl:call-template name="last-part-of">
        <xsl:with-param name="string" select="$name" />
        <xsl:with-param name="char" select="'.'" />
      </xsl:call-template>    
    </xsl:variable>
    <xsl:if test="string-length($name) &gt; string-length($cname)">
      <xsl:value-of select="substring($name, 1, string-length($name) - string-length($cname) - 1)"/>
    </xsl:if>
   </xsl:template>
   
   <xsl:template name="import-if-necessary">
     <xsl:param name="thisClass"/>
     <xsl:param name="otherClass"/>
     <xsl:variable name="p1">
       <xsl:call-template name="package-of">
         <xsl:with-param name="name" select="$thisClass"/>
       </xsl:call-template>
     </xsl:variable>
     <xsl:variable name="p2">
       <xsl:call-template name="package-of">
         <xsl:with-param name="name" select="$otherClass"/>
       </xsl:call-template>
     </xsl:variable>
     <xsl:if test="$p1 != $p2"><xsl:text>
  <xsl:value-of select="concat('import ', $otherClass, ';')"/></xsl:text>
     </xsl:if>
   </xsl:template>

	<xsl:template name="file-exists">
		<xsl:param name="name"/>
	    <xsl:variable name="file" select="io:File.new(string($name))"/> 
    	<xsl:if test="io:exists($file)"><xsl:value-of select="'yes'"/></xsl:if>
	</xsl:template>
	
</xsl:stylesheet>
