<?xml version="1.0" encoding="iso-8859-1" ?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dto2extjs="http://dto2extjs.faratasystems.com/"
  version="1.1" 
>
  <xsl:template match="/dto2extjs:class" mode="generated-file">
    <xsl:variable name="thisClass" select="@javaClass"/>
    <xsl:variable name="superclass" select="dto2extjs:superclass"/>
   	<xsl:variable name="hasChildren" select="boolean(dto2extjs:property/dto2extjs:OneToMany)"/>    
   	<xsl:variable name="superHasChildren" select="boolean(dto2extjs:features/dto2extjs:feature[contains(@name,'FXOneToMany') and not(@declared-by=$thisClass)])"/>    
 	<xsl:variable name="generatedClassName" select="concat('_', $className)"/>    
	<xsl:variable name="generatedPackageName">
		<xsl:call-template name="generated-package">
			<xsl:with-param name="packageName" select="$packageName"/>
			<xsl:with-param name="genPackage" select="$genPackage"/>
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="hasKeyProperties" select="boolean(dto2extjs:semantic-key/dto2extjs:semantic-key-part)"/>
   	<xsl:variable name="superHasKeyProperties" select="boolean(dto2extjs:features/dto2extjs:feature[contains(@name,'FXKeyColumn') and not(@declared-by=$thisClass)])"/>    
package <xsl:value-of select="$generatedPackageName"/> {
    <xsl:call-template name="import-types-by-class">
      <xsl:with-param name="class" select="."/>
      <xsl:with-param name="genInterfaces" select="'yes'"/>
      <xsl:with-param name="genPackage" select="$genPackage"/>
      <xsl:with-param name="qualifiedClassName">
      	<xsl:choose>
      		<xsl:when test="$generatedPackageName"><xsl:value-of select="concat($generatedPackageName, '.', $generatedClassName)"/></xsl:when>
      		<xsl:otherwise><xsl:value-of select="$generatedClassName"/></xsl:otherwise>
      	</xsl:choose>
      </xsl:with-param>
    </xsl:call-template><xsl:if test="$superclass">
  import <xsl:value-of select="$superclass/@name"/>;</xsl:if><xsl:if test="$hasChildren">
  import clear.collections.DataCollection;
  import clear.collections.dto.IAutoSyncSubtopic;
  import clear.collections.dto.IHierarchicalDTO;
  import clear.collections.dto.HierarchicalDTOAdapter;
  import clear.utils.DataCollectionUtils;</xsl:if><xsl:if test="$hasKeyProperties or $superHasKeyProperties"> 
  import clear.collections.dto.IKey;</xsl:if>   
  import flash.utils.Dictionary;<xsl:if test="$hasChildren"> 
  import mx.collections.ArrayCollection;</xsl:if>
  import mx.core.IUID;<xsl:if test="not($superclass)">
  import flash.events.EventDispatcher;   
  import mx.events.PropertyChangeEvent;      
  import mx.core.IPropertyChangeNotifier;  
  import mx.utils.StringUtil;
  import mx.utils.ObjectUtil;
  import mx.utils.UIDUtil;</xsl:if>
    <xsl:variable name="interfaces">
      <xsl:call-template name="class-implements-clause">
        <xsl:with-param name="class" select="."/>
        <xsl:with-param name="predefinedInterfaces">
          mx.core.IUID<xsl:if test="not($superclass)">, mx.core.IPropertyChangeNotifier</xsl:if><xsl:if test="$hasChildren and not($superHasChildren)">,
          IAutoSyncSubtopic, IHierarchicalDTO </xsl:if><xsl:if test="$hasKeyProperties and not($superHasKeyProperties)">,
          IKey</xsl:if>	
        </xsl:with-param>
        <xsl:with-param name="generated" select="'yes'"/>
        <xsl:with-param name="genPackage" select="$genPackage"/>
      </xsl:call-template>
    </xsl:variable>        
    
    <xsl:choose>
      <xsl:when test="$superclass">
  
  /* [ExcludeClass] */
  public class <xsl:value-of select="$generatedClassName"/> extends <xsl:value-of select="$superclass/@name"/><xsl:value-of select="$interfaces"/> {
    /* Constructor */
    public function <xsl:value-of select="$generatedClassName"/>():void {
      super();<xsl:if test="$hasChildren and not($superHasChildren)">
      _adapter = new HierarchicalDTOAdapter(this);</xsl:if>
    }<xsl:if test="$hasChildren"><xsl:if test="not($superHasChildren)">
	/* Implementation of the IHierarchicalDTO interface */    
	private var _adapter:HierarchicalDTOAdapter;
	protected function get hierarchicalDTOAdapter():HierarchicalDTOAdapter {
		return _adapter;
	}
	public function get childCollections():Array {
      return hierarchicalDTOAdapter.childCollections;
	}
	
	public function get childRanking():Array {
		return hierarchicalDTOAdapter.childRanking;
	}

 	public function getAutoSyncPropertyNames(childCollection:DataCollection):Array {
		<xsl:choose>
		<xsl:when test="$hasKeyProperties or $superHasKeyProperties">return keyPropertyNames;</xsl:when>
		<xsl:otherwise>return [];</xsl:otherwise>
		</xsl:choose>
	}</xsl:if>	
	
 	</xsl:if>
 	<xsl:if test="$hasKeyProperties">
	/* Key properties derived from @FXKeyColumn */
	public function get keyPropertyNames():Array {
		<xsl:choose>
		<xsl:when test="not($superHasKeyProperties)">return [<xsl:for-each select="dto2extjs:semantic-key/dto2extjs:semantic-key-part"><xsl:if test="position()!=1">,</xsl:if>"<xsl:value-of select="."/>"</xsl:for-each>];</xsl:when>
		<xsl:otherwise>var propNames:Array = super.keyPropertyNames;<xsl:for-each select="dto2extjs:semantic-key/dto2extjs:semantic-key-part">
		propNames.push(<xsl:value-of select="."/>);</xsl:for-each>
		return propNames;
		</xsl:otherwise>
		</xsl:choose>
	}
	public function get keyPropertyValues():Array {
		var propValues:Array=<xsl:choose>
			<xsl:when test="not($superHasKeyProperties)">[];
		var names:Array = keyPropertyNames;
		for each ( var name:String in names ) {
			propValues.push(this[name]);
		}</xsl:when>
		<xsl:otherwise> = super.keyPropertyValues;<xsl:for-each select="dto2extjs:semantic-key/dto2extjs:semantic-key-part">
		propValues.push(this['<xsl:value-of select="."/>']);</xsl:for-each></xsl:otherwise>
	</xsl:choose>
		return propValues;			
	}	
	</xsl:if>
	<xsl:if test="dto2extjs:property[@declare-getter]"> 
    [Transient]
    override public function get properties():Dictionary {                   
      var properties:Dictionary = super.properties;   <xsl:for-each select="dto2extjs:property[@declare-getter]">
      properties["<xsl:value-of select="@name"/>"] = this._<xsl:value-of select="@name"/>;</xsl:for-each>
      return properties;
    }    
    
    override public function set properties(properties:Dictionary):void {
      super.properties = properties;<xsl:for-each select="dto2extjs:property[@declare-setter and not(dto2extjs:OneToMany)]">
      this._<xsl:value-of select="@name"/> = properties["<xsl:value-of select="@name"/>"];</xsl:for-each>
    }    

    override public function toXML():XML {
      var xml:XML = super.toXML();<xsl:for-each select="dto2extjs:property[@declare-getter]" >
      xml.appendChild(<xsl:element name="{@name}">{this.<xsl:value-of select="@name"/>}</xsl:element>);</xsl:for-each>
      return xml;	 
    }
	</xsl:if>      
    <xsl:apply-templates/>
  }
}   
    </xsl:when>
    <xsl:otherwise>
  /* [ExcludeClass] */
  public class <xsl:value-of select="$generatedClassName"/> extends flash.events.EventDispatcher<xsl:value-of select="$interfaces"/> {
  
    /* Constructor */
    public function <xsl:value-of select="$generatedClassName"/>():void {
      super();<xsl:if test="$hasChildren">
      _adapter = new HierarchicalDTOAdapter(this);</xsl:if>
    }<xsl:if test="$hasChildren">
    /* Implementation of the IHierarchicalDTO interface */    
	private var _adapter:HierarchicalDTOAdapter;
	protected function get hierarchicalDTOAdapter():HierarchicalDTOAdapter {
		return _adapter;
	}
	public function get childCollections():Array {
      return hierarchicalDTOAdapter.childCollections;
	}
	
	public function get childRanking():Array {
		return hierarchicalDTOAdapter.childRanking;
	}

	public function getAutoSyncPropertyNames(childCollection:DataCollection):Array {
		<xsl:choose>
		<xsl:when test="$hasKeyProperties">return keyPropertyNames;</xsl:when>
		<xsl:otherwise>return [];</xsl:otherwise>
		</xsl:choose>
	}	
	</xsl:if>

	<xsl:if test="$hasKeyProperties">
	/* Key properties derived from @FXKeyColumn */
	public function get keyPropertyNames():Array {
		return [<xsl:for-each select="dto2extjs:semantic-key/dto2extjs:semantic-key-part"><xsl:if test="position()!=1">,</xsl:if>"<xsl:value-of select="."/>"</xsl:for-each>];
	}</xsl:if>

	public function get keyPropertyValues():Array {
		var propValues:Array=[];
		<xsl:for-each select="dto2extjs:semantic-key/dto2extjs:semantic-key-part">
		propValues.push(this['<xsl:value-of select="."/>']);</xsl:for-each>
		return propValues;			
	}
	
    private var _uid:String;
    
    [Bindable(event="propertyChange")] 
    public function get uid():String {
      if (_uid === null) {
        _uid = UIDUtil.createUID();
      }
      return _uid;
    }

    public function set uid(value:String):void {
      const oldValue:String = _uid;
      if (oldValue !== value) {
        _uid = value;
        dispatchUpdateEvent("uid", oldValue, value);            
      }
    }

    [Transient]
    public function get properties():Dictionary {                   
      var properties:Dictionary = new Dictionary();   <xsl:for-each select="dto2extjs:property">
      properties["<xsl:value-of select="@name"/>"] = this._<xsl:value-of select="@name"/>;</xsl:for-each>
      return properties;
    } 
       
       
    public function set properties(properties:Dictionary):void {<xsl:for-each select="dto2extjs:property[not(dto2extjs:OneToMany)]">
      this._<xsl:value-of select="@name"/> = properties["<xsl:value-of select="@name"/>"];</xsl:for-each>     
    }
    	
    protected function dispatchUpdateEvent(propertyName:String, oldValue:Object, value:Object):void {
      dispatchEvent(
        PropertyChangeEvent.createUpdateEvent(this, propertyName, oldValue, value)
      );                      
    }
            
    public function clone():Object {
      var result:Object = newInstance();
      result.properties = this.properties;
      return result;
    }
    
    public function newInstance():Object { 
      var c:Class = this["constructor"];
      return new c();
    }
    
    public function toXML():XML {
      var xml:XML = <DATA><xsl:for-each select="dto2extjs:property" >
        <xsl:element name="{@name}">{this._<xsl:value-of select="@name"/>}</xsl:element></xsl:for-each>
      </DATA>;
      return xml;	 
    }   
	 
    public function toAnnotatedXML( rootName : String = "DATA", qualifier:String =""):XML {
      var xml:XML = new XML(String.fromCharCode(60) +  rootName + "/" + String.fromCharCode(62) );
      if (qualifier != "" )
        xml.@qualifier = qualifier;<xsl:for-each select="dto2extjs:property" >
      xml.appendChild(<xsl:element name="{@name}"><xsl:attribute name="label"><xsl:value-of select="@label"/></xsl:attribute><xsl:attribute name="resource"><xsl:value-of select="@resource"/></xsl:attribute><xsl:attribute name="formatString"><xsl:value-of select="@formatString"/></xsl:attribute>{toSimpleString(_<xsl:value-of select="@name"/>)}</xsl:element>);</xsl:for-each>
      return xml;	 
    }
	    
    public override function toString():String {
      var result:String = mx.utils.StringUtil.substitute("{0}{<xsl:for-each select="dto2extjs:property" ><xsl:value-of select="@name"/>:{<xsl:value-of select="position()"/>}<xsl:if test="position()!=last()">, </xsl:if></xsl:for-each>}", "<xsl:value-of select="@name"/>"<xsl:for-each select="dto2extjs:property" >, this._<xsl:value-of select="@name"/></xsl:for-each>);
      return result;	 
    }   
	
    protected static function toSimpleString(o:Object):String {
      return o == null ? "" : "" + o;
    }
    <xsl:apply-templates/>
  }
}
      </xsl:otherwise>
    </xsl:choose>  
  </xsl:template>		


  <xsl:template match="dto2extjs:property[not(@abstract = 'true') and dto2extjs:OneToMany]">
  
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
  		</xsl:choose></xsl:variable>
  		
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
  </xsl:template>	

  <xsl:template match="dto2extjs:property[not(@abstract = 'true') and (not(dto2extjs:OneToMany))]"><xsl:text>
</xsl:text>    /* Property "<xsl:value-of select="@name"/>" */
    private var _<xsl:value-of select="@name"/>:<xsl:value-of select="@type"/>;
    
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