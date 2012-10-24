<!-- Utility functions -->
<xsl:stylesheet version="2.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:dto2extjs="http://dto2extjs.faratasystems.com/"
  xmlns:exslt="http://exslt.org/common"
  xmlns:str="http://exslt.org/strings"  
  exclude-result-prefixes="dto2extjs xsl fn exslt str"
  >
  
  <xsl:template name="import-type">
    <xsl:param name="thisClass" />  
    <xsl:param name="type"/>
    <xsl:choose>
    	<xsl:when test="
    	    $type = 'void'or
    		$type = 'int' or $type = 'uint' or $type = 'Number' or 
    		$type = 'Object' or
    		$type = 'XML' or
    		$type = 'String' or 
    		$type = 'Boolean' or 
    		$type = 'Date' or 
    		$type = 'Array' or
    		$type = 'Function' "/>
    	<xsl:otherwise>
          <xsl:variable name="doTypeImport">
            <xsl:call-template name="import-if-necessary">
              <xsl:with-param name="thisClass" select="$thisClass"/>
              <xsl:with-param name="otherClass" select="$type"/>            
            </xsl:call-template>
          </xsl:variable>
          <xsl:value-of select="$doTypeImport"/>
    	</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="import-types">
    <xsl:param name="thisClass" />  
    <xsl:param name="types" />
    <xsl:for-each select="$types/type[not(@name=preceding::type/@name)]">
		<xsl:call-template name="import-type">
			<xsl:with-param name="thisClass" select="$thisClass"/>
			<xsl:with-param name="type" select="@name"/>
		</xsl:call-template>    
    </xsl:for-each>
  </xsl:template>
  
  <xsl:template name="import-types-by-class">
    <xsl:param name="class" />
    <xsl:param name="qualifiedClassName"/>
    <xsl:param name="genInterfaces" select="'no'"/>
    <xsl:param name="genPackage" select="''"/>    
    <xsl:variable name="allTypes">
    	<xsl:choose>
    		<xsl:when test="$genInterfaces = 'yes'">
		    	<xsl:for-each select="$class/dto2extjs:superclass/@name | $class/dto2extjs:property/@type | $class/dto2extjs:property/@contentType">
			      <type name="{.}"/>
		    	</xsl:for-each>
		    	<xsl:for-each select="$class/dto2extjs:interfaces/dto2extjs:interface/@name">
		    	  <type>
		    	  	<xsl:attribute name="name">
      			      <xsl:call-template name="generated-superclass-by-class">
      				    <xsl:with-param name="qualifiedClassName" select="."/>
      				    <xsl:with-param name="genPackage" select="$genPackage"/>
      			      </xsl:call-template>		    	  	
		    	  	</xsl:attribute>
		    	  </type>
		    	</xsl:for-each>
    		</xsl:when>
    		<xsl:otherwise>
		    	<xsl:for-each select="$class/dto2extjs:superclass/@name | $class/dto2extjs:interfaces/dto2extjs:interface/@name | $class/dto2extjs:property/@type | $class/dto2extjs:property/@contentType">
			      <type name="{.}"/>
		    	</xsl:for-each>
    		</xsl:otherwise>
    	</xsl:choose>
    </xsl:variable>
    <xsl:variable name="actualClassName">
    	<xsl:choose>
    		<xsl:when test="$qualifiedClassName"><xsl:value-of select="$qualifiedClassName"/></xsl:when>
    		<xsl:otherwise><xsl:value-of select="$class/@name"/></xsl:otherwise>
    	</xsl:choose>
    </xsl:variable>
    <xsl:call-template name="import-types">
      <xsl:with-param name="thisClass" select="$actualClassName"/>    
      <xsl:with-param name="types" select="exslt:node-set($allTypes)"/>    
    </xsl:call-template>
  </xsl:template>  
  
  <xsl:template name="import-interfaces">
    <xsl:param name="class" />
    <xsl:variable name="allTypes">
    	<xsl:for-each select="$class/dto2extjs:interfaces/dto2extjs:interface/@name">
	      <xsl:sort select="string(.)"/>
	      <type name="{.}"/>
    	</xsl:for-each>
    </xsl:variable>
    <xsl:call-template name="import-types">
      <xsl:with-param name="thisClass" select="$class/@name"/>    
      <xsl:with-param name="types" select="exslt:node-set($allTypes)"/>    
    </xsl:call-template>
  </xsl:template>    
  
  <xsl:template name="implements-clause">
    <xsl:param name="class"/>
    <xsl:param name="predefinedInterfaces"/>
    <xsl:param name="generated" select="'no'"/>
    <xsl:param name="genPackage" select="''"/>
    <xsl:param name="keyword"/>
    <xsl:variable name="pad" select="str:padding(string-length($keyword) + 5, ' ')"/>
    <xsl:variable name="count" select="count($class/dto2extjs:interfaces/dto2extjs:interface)"/>
    <xsl:if test="$predefinedInterfaces">
      <xsl:if test="$count &gt; 0">
          <xsl:text>
   </xsl:text>          
      </xsl:if>  
      <xsl:text> </xsl:text><xsl:value-of select="$keyword"/><xsl:text> </xsl:text><xsl:value-of select="$predefinedInterfaces"/>
    </xsl:if>
    <xsl:for-each select="$class/dto2extjs:interfaces/dto2extjs:interface/@name">
      <xsl:sort select="string(.)"/>
      <xsl:variable name="qualifiedInterfaceName">
      	<xsl:choose>
      		<xsl:when test="$generated = 'yes'">
      			<xsl:call-template name="generated-superclass-by-class">
      				<xsl:with-param name="qualifiedClassName" select="."/>
      				<xsl:with-param name="genPackage" select="$genPackage"/>
      			</xsl:call-template>
      		</xsl:when>
      		<xsl:otherwise>
      			<xsl:value-of select="."/>
      		</xsl:otherwise>
      	</xsl:choose>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="not($predefinedInterfaces) and position()=1">
          <xsl:if test="$count &gt; 1">
          <xsl:text>
   </xsl:text>          
          </xsl:if>
          <xsl:text> </xsl:text><xsl:value-of select="$keyword"/><xsl:text> </xsl:text><xsl:value-of select="$qualifiedInterfaceName"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>, 
</xsl:text><xsl:value-of select="$pad"/><xsl:value-of select="$qualifiedInterfaceName"/>        </xsl:otherwise>
      </xsl:choose>    
    </xsl:for-each>
  </xsl:template>
  
  <xsl:template name="class-implements-clause">
    <xsl:param name="class"/>
    <xsl:param name="predefinedInterfaces"/>
    <xsl:param name="generated" select="'no'"/>
    <xsl:param name="genPackage" select="''"/>
    <xsl:call-template name="implements-clause">
      <xsl:with-param name="class" select="$class"/>
      <xsl:with-param name="predefinedInterfaces" select="$predefinedInterfaces"/>
      <xsl:with-param name="generated" select="$generated"/>
      <xsl:with-param name="genPackage" select="$genPackage"/>
      <xsl:with-param name="keyword">implements</xsl:with-param>            
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template name="interface-implements-clause">
    <xsl:param name="class"/>
    <xsl:param name="predefinedInterfaces"/>
    <xsl:param name="generated" select="'no'"/>
    <xsl:param name="genPackage" select="''"/>
    <xsl:call-template name="implements-clause">
      <xsl:with-param name="class" select="$class"/>
      <xsl:with-param name="predefinedInterfaces" select="$predefinedInterfaces"/>
      <xsl:with-param name="generated" select="$generated"/>
      <xsl:with-param name="genPackage" select="$genPackage"/>
      <xsl:with-param name="keyword">extends</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template name="write-property-meta">
    <xsl:param name="property"/>  
	<xsl:if test="not($property/@abstract) and $property/@declare-getter and $property/@declare-setter">[Bindable(event="propertyChange")]<xsl:text>
	</xsl:text></xsl:if>
	<xsl:if	test="$property/@type = 'Array' and $property/@contentType">[ArrayElementType("<xsl:value-of select="$property/@contentType"/>")]<xsl:text>
	</xsl:text></xsl:if>
  </xsl:template>  
  
  <xsl:template match="dto2extjs:property[@abstract = 'true']"><xsl:text>
</xsl:text>    /* Abstract property "<xsl:value-of select="@name"/>" */
    <xsl:call-template name="write-property-meta">
      <xsl:with-param name="property" select="."/>
    </xsl:call-template>
    <xsl:if test="@declare-getter">
    <xsl:if test="@override-getter"><xsl:text>override </xsl:text></xsl:if>public function get <xsl:value-of select="@name"/>():<xsl:value-of select="@type"/> {
      throw new Error("Not implemented");
    }
    </xsl:if>
    <xsl:if test="@declare-setter">
    <xsl:if test="@override-setter"><xsl:text>override </xsl:text></xsl:if>public function set <xsl:value-of select="@name"/>(value:<xsl:value-of select="@type"/>):void {
      throw new Error("Not implemented");
    }
    </xsl:if>
  </xsl:template>	
  
  <xsl:template name="generated-superclass-by-class">
  	<xsl:param name="qualifiedClassName"/>
  	<xsl:param name="genPackage"/>
	<xsl:variable name="packageName">
	  <xsl:call-template name="package-of">
	    <xsl:with-param name="name" select="$qualifiedClassName"/>
	  </xsl:call-template>
	</xsl:variable>        
	<xsl:variable name="className">
	  <xsl:call-template name="last-part-of">
	    <xsl:with-param name="string" select="$qualifiedClassName" />
	    <xsl:with-param name="char" select="'.'" />
	  </xsl:call-template>
	</xsl:variable> 
	<xsl:call-template name="generated-superclass">
      	<xsl:with-param name="packageName" select="$packageName"/>
      	<xsl:with-param name="className" select="$className"/>   
      	<xsl:with-param name="genPackage" select="$genPackage"/>   	
	</xsl:call-template>  	
  </xsl:template>  
  
  <xsl:template name="generated-superclass">
  	<xsl:param name="packageName"/>
  	<xsl:param name="className"/>
  	<xsl:param name="genPackage"/>
  	<xsl:choose>
  		<xsl:when test="$genPackage">
		    <xsl:choose>
		      <xsl:when test="$packageName">
		        <xsl:value-of select="concat($packageName, '.', $genPackage, '._', $className)"/>
		      </xsl:when>    
		      <xsl:otherwise>
		       <xsl:value-of select="concat($genPackage, '._', $className)"/>
		      </xsl:otherwise>   
		    </xsl:choose>   	
  		</xsl:when>
  		<xsl:otherwise>
		    <xsl:choose>
		      <xsl:when test="$packageName">
		        <xsl:value-of select="concat($packageName, '._', $className)"/>
		      </xsl:when>    
		      <xsl:otherwise>
		       <xsl:value-of select="concat('_', $className)"/>
		      </xsl:otherwise>   
		    </xsl:choose>     		
  		</xsl:otherwise>
  	</xsl:choose>
  </xsl:template>  
  
  <xsl:template name="generated-package">
   	<xsl:param name="packageName"/>
  	<xsl:param name="genPackage"/>
  	<xsl:choose>
  		<xsl:when test="$genPackage">
		    <xsl:choose>
		      <xsl:when test="$packageName">
		        <xsl:value-of select="concat($packageName, '.', $genPackage)"/>
		      </xsl:when>    
		      <xsl:otherwise>
		       <xsl:value-of select="$genPackage"/>
		      </xsl:otherwise>   
		    </xsl:choose>   	
  		</xsl:when>
  		<xsl:otherwise>
	       <xsl:value-of select="$packageName"/>
  		</xsl:otherwise>
  	</xsl:choose>
  </xsl:template>
  
  <xsl:template name="generated-file">
  	<xsl:param name="path"/>
  	<xsl:param name="className"/>
  	<xsl:param name="genPackage"/>
  	<xsl:choose>
  		<xsl:when test="$genPackage">
		    <xsl:choose>
		      <xsl:when test="$path">
		        <xsl:value-of select="concat($path, '/', $genPackage, '/_', $className, '.js')"/>
		      </xsl:when>    
		      <xsl:otherwise>
		       <xsl:value-of select="concat($genPackage, '/_', $className, '.js')"/>
		      </xsl:otherwise>   
		    </xsl:choose>   	
  		</xsl:when>
  		<xsl:otherwise>
		    <xsl:choose>
		      <xsl:when test="$path">
		        <xsl:value-of select="concat($path, '/_', $className, '.js')"/>
		      </xsl:when>    
		      <xsl:otherwise>
		       <xsl:value-of select="concat('_', $className, '.js')"/>
		      </xsl:otherwise>   
		    </xsl:choose>     		
  		</xsl:otherwise>
  	</xsl:choose>
  </xsl:template>  

  <!-- Generate keys  -->
  <!-- Primary (synthetic) key  -->
  <xsl:template match="dto2extjs:class/dto2extjs:synthetic-key">
    <xsl:variable name="symbolOfId" select="text()"/>
    <xsl:variable name="typeOfId" select="parent::dto2extjs:class/dto2extjs:property[@name = $symbolOfId]/@type"/><xsl:text>
</xsl:text>    /* Primary key "<xsl:value-of select="$symbolOfId"/>" */
    public function get __id__():<xsl:value-of select="$typeOfId"/> { return <xsl:value-of select="$symbolOfId"/>; }
  </xsl:template>
  
  <!-- IKey:keyEquals  -->
  <xsl:template match="dto2extjs:class/dto2extjs:semantic-key"><xsl:text>
</xsl:text>    /*  IKey */
    public function keyEquals(item:Object):Boolean {
      return (!(<xsl:for-each select="dto2extjs:semantic-key-part">
        !(ObjectUtil.isSimple(this.<xsl:value-of select="."/>)?(this.<xsl:value-of select="."/>===item.<xsl:value-of select="."/>):(ObjectUtil.compare(this.<xsl:value-of select="."/>,item.<xsl:value-of select="."/>)==0))
        <xsl:if test="position() != last()">||</xsl:if></xsl:for-each>
      )); 
    }
  </xsl:template>  

  <!-- Suppress output of unnecessary nodes  -->
  <xsl:template match="dto2extjs:interfaces"></xsl:template>
  <xsl:template match="dto2extjs:superclass"></xsl:template>  
</xsl:stylesheet>
