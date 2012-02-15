<?xml version="1.0" encoding="iso-8859-1" ?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dto2extjs="http://dto2extjs.faratasystems.com/"
  xmlns:exsl="http://exslt.org/common"
  xmlns:u="xalan://com.farata.dto2extjs.asap"
  version="1.1" 
>
	<xsl:template match="/dto2extjs:class" mode="generated-file">
		<xsl:variable name="thisClass" select="@name"/>
    	<xsl:variable name="thisGeneratedClass">
			<xsl:call-template name="generated-superclass">
				<xsl:with-param name="packageName" select="$packageName"/>
				<xsl:with-param name="className" select="$className"/>   
				<xsl:with-param name="genPackage" select="$genPackage"/>   	
			</xsl:call-template>
    	</xsl:variable>
    	<xsl:variable name="superclass" select="dto2extjs:superclass"/>
		<xsl:variable name="scalarProperties" select="dto2extjs:property[not(@abstract = 'true') and (not(dto2extjs:OneToMany)) and (not(dto2extjs:ManyToOne))]"/>
		<xsl:variable name="relationProperties1M" select="dto2extjs:property[not(@abstract = 'true') and (dto2extjs:OneToMany)]"/>
		<xsl:variable name="relationPropertiesM1" select="dto2extjs:property[not(@abstract = 'true') and (dto2extjs:ManyToOne)]"/>
Ext.define('<xsl:value-of select="$thisGeneratedClass"/>', {
	extend: '<xsl:choose>
	<xsl:when test="$superclass"><xsl:value-of select="$superclass/@name"/></xsl:when>
		<xsl:otherwise>Ext.data.Model</xsl:otherwise>
	</xsl:choose>',
	<xsl:if test="$scalarProperties">
	fields: [
<xsl:apply-templates select="$scalarProperties" mode="scalarProperty"/>
	],
	</xsl:if>
	<xsl:if test="$relationProperties1M">
	hasMany: [
<xsl:apply-templates select="$relationProperties1M" mode="relationProperty1M"/>
	],
	</xsl:if>
	<xsl:if test="$relationPropertiesM1">
	belongsTo: [
<xsl:apply-templates select="$relationPropertiesM1" mode="relationPropertyM1"/>
	],
	</xsl:if>
	requires: [
		<xsl:key name="distinct-custom-types" match="dto2extjs:property[not(starts-with(@type, 'Ext.data.Types.')) or @contentType]" 
			use="@contentType | @type"/>
		<xsl:variable name="required-types">
			<xsl:if test="dto2extjs:property[starts-with(@type, 'Ext.data.Types.')]"><t>Ext.data.Types</t></xsl:if>
			<xsl:for-each select="dto2extjs:property[not(starts-with(@type, 'Ext.data.Types.')) or @contentType]">
				<xsl:if test="generate-id() = generate-id(key('distinct-custom-types', @contentType | @type))">
				<xsl:variable name="v">
					<xsl:choose>
						<xsl:when test="@contentType"><xsl:value-of select="@contentType"/></xsl:when>
						<xsl:otherwise><xsl:value-of select="@type"/></xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
	      		<t><xsl:value-of select="$v"/></t>
	      		</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:for-each select="exsl:node-set($required-types)/t">
		'<xsl:value-of select="."/>'<xsl:if test="last() != position()">,</xsl:if>
		</xsl:for-each>
	]
}
	</xsl:template>		

	<xsl:template match="dto2extjs:property" mode="relationPropertyM1">
		{
			model: '<xsl:value-of select="@type"/>',  
			getterName:'<xsl:value-of select="u:XsltUtils.getterFor(@name)"/>', 
			setterName:'<xsl:value-of select="u:XsltUtils.setterFor(@name)"/>',
			foreignKey:'<xsl:value-of select="dto2extjs:ManyToOne/@foreignKey"/>',
			primaryKey:'id'
		},
	</xsl:template>

  <xsl:template match="dto2extjs:property" mode="relationProperty1M">
  
	  <xsl:variable name="contentType">
	  	<xsl:call-template name="replace-once">
	  		<xsl:with-param name="text" select="@contentType"/>
	  		<xsl:with-param name="replace" select="'gen._'"/>
	  		<xsl:with-param name="by" select="''"/>
	  	</xsl:call-template>
	  </xsl:variable>
	  	
	  <xsl:variable name="dataCollectionClass">
	  		<xsl:choose>
	  			<xsl:when test="not(dto2extjs:OneToMany/@dataCollectionClass='')"><xsl:value-of select="dto2extjs:OneToMany/@dataCollectionClass"/></xsl:when>
	  			<xsl:otherwise>
	  				<xsl:call-template name="inferDataCollectionClass">
	  					<xsl:with-param name="contentType" select="$contentType"/>
	  				</xsl:call-template>
	  			</xsl:otherwise>
	  		</xsl:choose>
	  </xsl:variable>
  
		{
			model: '<xsl:value-of select="$contentType"/>',
			name: '<xsl:value-of select="u:XsltUtils.getterFor(@name)"/>', 
			foreignKey:'<xsl:value-of select="dto2extjs:OneToMany/@foreignKey"/>',
			primaryKey:'id',
			autoLoad: true,
			storeClassName:'<xsl:value-of select="$dataCollectionClass"/>'
		},
		<!--   		
	  <xsl:variable name="fillArguments" select="dto2extjs:OneToMany/@fillArguments"/>
	  <xsl:variable name="ranking" select="dto2extjs:OneToMany/@ranking"/>
	  <xsl:text>
</xsl:text>    import <xsl:value-of select="$dataCollectionClass" />;
		/* One to many property "<xsl:value-of select="@name"/>"; collection of <xsl:value-of select="$contentType"/> */
	    private var _<xsl:value-of select="@name"/>:<xsl:value-of select="$dataCollectionClass"/>;
	    
	    [Transient]<xsl:call-template name="write-property-meta">
	      <xsl:with-param name="property" select="."/>
	    </xsl:call-template>
	    <xsl:if test="@declare-getter">
	    <xsl:if test="@override-getter"><xsl:text>override </xsl:text></xsl:if>public function get <xsl:value-of select="@name"/>():<xsl:value-of select="@type"/> {
	      if (_<xsl:value-of select="@name"/>==null) {
	      	_<xsl:value-of select="@name"/> = new <xsl:value-of select="$dataCollectionClass"/>();
	      	hierarchicalDTOAdapter.addCollection(_<xsl:value-of select="@name"/>, <xsl:value-of select="$ranking" />);     	
	      	if (!DataCollectionUtils.isLocalItem(this)) {
	      		<xsl:choose>
	      		<xsl:when test="not($fillArguments='')">_<xsl:value-of select="@name"/>.fill(<xsl:value-of select="$fillArguments"/>);</xsl:when>
	      		<xsl:otherwise>_<xsl:value-of select="@name"/>.fill.apply(_<xsl:value-of select="@name"/>, keyPropertyValues);</xsl:otherwise>
	      	</xsl:choose>
	      	}
	      }	
	      return _<xsl:value-of select="@name"/>;
	    }
	    </xsl:if>
	    
	    
	    <xsl:if test="@declare-setter">
	    <xsl:if test="@override-setter"><xsl:text>override </xsl:text></xsl:if>public function set <xsl:value-of select="@name"/>(value:<xsl:value-of select="@type"/>):void {
	      const oldValue:<xsl:value-of select="@type"/> = this._<xsl:value-of select="@name"/>;
	      var newValue:<xsl:value-of select="$dataCollectionClass"/> = value as <xsl:value-of select="$dataCollectionClass"/>;
	      if (oldValue != newValue) {
	      	if (_<xsl:value-of select="@name"/>==null) {
			  if (DataCollectionUtils.isLocalItem(this)) {
				hierarchicalDTOAdapter.addCollection(newValue, <xsl:value-of select="$ranking" />);  		  
			  } else { 
				throw Error(&quot;Nested collection property of the server-originated item can not be assigned on the client.&quot;); 
			  }
			} else { 
			  throw Error(&quot;Existing non-null nested collection property can not be reassigned.&quot;);
			}	
	      
	        this._<xsl:value-of select="@name"/> = newValue;
	        dispatchUpdateEvent("<xsl:value-of select="@name"/>", oldValue, newValue);            
	      }
	    }
	    </xsl:if>
	     -->    
  </xsl:template>	

  <xsl:template match="dto2extjs:property" mode="scalarProperty"><xsl:text>
</xsl:text>
		{
			name: '<xsl:value-of select="@name"/>',
			type: <xsl:value-of select="@type"/>,
			useNull: true
		},
    <!-- 
    <xsl:call-template name="write-property-meta">
      <xsl:with-param name="property" select="."/>
    </xsl:call-template>
   
    <xsl:variable name="propertyName"><xsl:value-of select="@name"/></xsl:variable> 
    <xsl:if test="/dto2extjs:class/dto2extjs:semantic-key[dto2extjs:semantic-key-part=$propertyName]">
    [Key]
    </xsl:if>
    <xsl:if test="@declare-getter">
    <xsl:if test="@override-getter"><xsl:text>override </xsl:text></xsl:if>public function get <xsl:value-of select="@name"/>():<xsl:value-of select="@type"/> {
      return _<xsl:value-of select="@name"/>;
    }
    </xsl:if>
    
    <xsl:if test="@declare-setter">
    <xsl:if test="@override-setter"><xsl:text>override </xsl:text></xsl:if>public function set <xsl:value-of select="@name"/>(value:<xsl:value-of select="@type"/>):void {
      <xsl:if test="@enum">if (value) value = value.intern();
      </xsl:if>const oldValue:<xsl:value-of select="@type"/> = this._<xsl:value-of select="@name"/>;
      if (oldValue != value) {
        this._<xsl:value-of select="@name"/> = value;
        dispatchUpdateEvent("<xsl:value-of select="@name"/>", oldValue, value);            
      }
    }
    </xsl:if>
	-->    
  </xsl:template>	
  
<xsl:template name="tokenize">
  <xsl:param name="string" />
  <xsl:param name="delimiter" select="','" />
  <xsl:choose>
    <xsl:when test="$delimiter and contains($string, $delimiter)">"<xsl:value-of select="substring-before($string, $delimiter)" />", <xsl:call-template name="tokenize">
        <xsl:with-param name="string" 
                        select="substring-after($string, $delimiter)" />
        <xsl:with-param name="delimiter" select="$delimiter" />
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>"<xsl:value-of select="$string" />"</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="inferDataCollectionClass">
 <xsl:param name="contentType" />
  <xsl:variable name="contentClassName">
    <xsl:call-template name="unqualifyClassName"><xsl:with-param name="string" select="$contentType"/></xsl:call-template>	  	
  </xsl:variable>
  <xsl:variable name="contentPackageName" select="substring-before($contentType, concat('.', $contentClassName))"/>
  <xsl:call-template name="collectionsPackageFromDTOPackage"><xsl:with-param name="string" select="$contentPackageName"/><xsl:value-of select="$className"/></xsl:call-template>.<xsl:choose>
    	<xsl:when test="substring($contentClassName,string-length($contentClassName) - 2,3)='DTO'"><xsl:value-of select="substring($contentClassName,1,string-length($contentClassName) - 3)"/></xsl:when>
		<xsl:otherwise><xsl:value-of select="$contentClassName"/></xsl:otherwise>
	</xsl:choose>Collection</xsl:template>   	

<xsl:template name="inferFillArguments">
 <xsl:param name="contentType" />
  <xsl:variable name="contentClassName">
    <xsl:call-template name="unqualifyClassName"><xsl:with-param name="string" select="$contentType"/></xsl:call-template>	  	
  </xsl:variable>
  <xsl:variable name="contentPackageName" select="substring-before($contentType, concat('.', $contentClassName))"/>
  <xsl:call-template name="collectionsPackageFromDTOPackage"><xsl:with-param name="string" select="$contentPackageName"/><xsl:value-of select="$className"/></xsl:call-template>.<xsl:choose>
    	<xsl:when test="substring($contentClassName,string-length($contentClassName) - 2,3)='DTO'"><xsl:value-of select="substring($contentClassName,1,string-length($contentClassName) - 3)"/></xsl:when>
		<xsl:otherwise><xsl:value-of select="$contentClassName"/></xsl:otherwise>
	</xsl:choose>Collection</xsl:template>  
 <xsl:template name="replace-once">
    <xsl:param name="text" />
    <xsl:param name="replace" />
    <xsl:param name="by" />
    <xsl:choose>
      <xsl:when test="contains($text, $replace)">
        <xsl:value-of select="substring-before($text,$replace)" />
        <xsl:value-of select="$by" />
      	<xsl:value-of select="substring-after($text,$replace)" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  
 <xsl:template name="replace-all">
    <xsl:param name="text" />
    <xsl:param name="replace" />
    <xsl:param name="by" />
    <xsl:choose>
      <xsl:when test="contains($text, $replace)">
        <xsl:value-of select="substring-before($text,$replace)" />
        <xsl:value-of select="$by" />
        <xsl:call-template name="replace-all">
          <xsl:with-param name="text"
          select="substring-after($text,$replace)" />
          <xsl:with-param name="replace" select="$replace" />
          <xsl:with-param name="by" select="$by" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
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