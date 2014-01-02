<?xml version="1.0" encoding="iso-8859-1" ?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dto2extjs="http://dto2extjs.faratasystems.com/"
  version="1.1" 
>
  <xsl:template match="/dto2extjs:class" mode="generated-file">
    <xsl:variable name="superclass" select="dto2extjs:superclass"/>
    <xsl:variable name="hasDomainId" select="boolean(dto2extjs:semantic-key/dto2extjs:semantic-key-part)"/>
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
      <xsl:with-param name="genInterfaces" select="'yes'"/>
      <xsl:with-param name="genPackage" select="$genPackage"/>
      <xsl:with-param name="qualifiedClassName">
      	<xsl:choose>
      		<xsl:when test="$generatedPackageName"><xsl:value-of select="concat($generatedPackageName, '.', $generatedClassName)"/></xsl:when>
      		<xsl:otherwise><xsl:value-of select="$generatedClassName"/></xsl:otherwise>
      	</xsl:choose>
      </xsl:with-param>
    </xsl:call-template>

  import flash.utils.Dictionary;<xsl:if test="not($superclass)">
  import flash.events.EventDispatcher;
  
  import mx.core.mx_internal;
  import mx.core.IUID;
  
  import mx.events.PropertyChangeEvent;
  
  import mx.utils.UIDUtil;
  import mx.utils.StringUtil;<xsl:if test="$hasDomainId">
  import clear.collections.dto.IDomainID;</xsl:if>
    </xsl:if>
    <xsl:if test="not($superclass[@kind = 'managed'])">
  import mx.data.IManaged;
    </xsl:if>
  import mx.data.utils.Managed;
    
    <xsl:variable name="interfaces">
      <xsl:call-template name="class-implements-clause">
        <xsl:with-param name="class" select="."/>
        <xsl:with-param name="predefinedInterfaces">
          <xsl:if test="not($superclass[@kind = 'managed'])">mx.data.IManaged<xsl:if test="$hasDomainId">,clear.collections.dto.IDomainID</xsl:if></xsl:if>        </xsl:with-param>
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
      super();
    }   

    [Transient]
    override public function get properties():Dictionary {                   
      var properties:Dictionary = super.properties;   <xsl:for-each select="dto2extjs:property[@declare-getter]">
      properties["<xsl:value-of select="@name"/>"] = this.<xsl:value-of select="@name"/>;</xsl:for-each>
      return properties;
    }    
    
    override public function set properties(properties:Dictionary):void {
      super.properties = properties;<xsl:for-each select="dto2extjs:property[@declare-setter]">
      this.<xsl:value-of select="@name"/> = properties["<xsl:value-of select="@name"/>"];</xsl:for-each>
    }    

	override public function toXML():XML {
      var xml:XML = super.toXML();<xsl:for-each select="dto2extjs:property[@declare-getter]" >
      xml.appendChild(<xsl:element name="{@name}">{<xsl:value-of select="@name"/>}</xsl:element>);</xsl:for-each>
      return xml;	 
	}  
    <xsl:apply-templates/>
  }
}   
    </xsl:when>
    <xsl:otherwise>
  /* [ExcludeClass] */
  public class <xsl:value-of select="$generatedClassName"/> extends flash.events.EventDispatcher<xsl:value-of select="$interfaces"/> {
  
    /* Constructor */
    public function <xsl:value-of select="$generatedClassName"/>():void {
      super();
    }
    
    private var _uid:String;
    
    [Bindable(event="propertyChange")] 
    public function get uid():String {
      if (_uid === null) {
        _uid = UIDUtil.createUID()
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
      properties["<xsl:value-of select="@name"/>"] = this.<xsl:value-of select="@name"/>;</xsl:for-each>
      return properties;
    }
        
    public function set properties(properties:Dictionary):void {<xsl:for-each select="dto2extjs:property">
      this.<xsl:value-of select="@name"/> = properties["<xsl:value-of select="@name"/>"];</xsl:for-each>     
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
        <xsl:element name="{@name}">{this.<xsl:value-of select="@name"/>}</xsl:element></xsl:for-each>
      </DATA>;
      return xml;	 
    }    
	
    public function toAnnotatedXML( rootName : String = "DATA", qualifier:String =""):XML {
      var xml:XML = new XML(String.fromCharCode(60) +  rootName + "/" + String.fromCharCode(62) );
      if (qualifier != "" )
        xml.@qualifier = qualifier;<xsl:for-each select="dto2extjs:property" >
      xml.appendChild(<xsl:element name="{@name}"><xsl:attribute name="label"><xsl:value-of select="@label"/></xsl:attribute><xsl:attribute name="resource"><xsl:value-of select="@resource"/></xsl:attribute><xsl:attribute name="formatString"><xsl:value-of select="@formatString"/></xsl:attribute>{toSimpleString(<xsl:value-of select="@name"/>)}</xsl:element>);</xsl:for-each>
      return xml;	 
    }
	
    public override function toString():String {
      var result:String = mx.utils.StringUtil.substitute("{0}{<xsl:for-each select="dto2extjs:property" ><xsl:value-of select="@name"/>:{<xsl:value-of select="position()"/>}<xsl:if test="position()!=last()">, </xsl:if></xsl:for-each>}","_<xsl:value-of select="@name"/>"<xsl:for-each select="dto2extjs:property" >, <xsl:value-of select="@name"/></xsl:for-each>);
      return result;	 
    }
    
    protected static function toSimpleString(o:Object) : String {
      return o == null ? "" : "" + o;
    }         
    <xsl:apply-templates/>
    
    // These are special requirements of any object that wants to be
    // "managed" by a DataService.
    // The referencedIds is a list of object IDs that belong to properties that
    // are lazily loaded.
    // The destination is used to look up metadata information about the
    // associations this object has been configured with in the
    // flex-dataservices.xml file located at the remote destination.

    mx_internal var referencedIds:Object = {};
    mx_internal var destination:String;
  }
  
}
      </xsl:otherwise>
    </xsl:choose>  
  </xsl:template>		
	
  <xsl:template match="dto2extjs:property[not(@abstract = 'true')]"><xsl:text>
</xsl:text>    /* Property "<xsl:value-of select="@name"/>" */
    private var _<xsl:value-of select="@name"/>:<xsl:value-of select="@type"/>;
    
    <xsl:call-template name="write-property-meta">
      <xsl:with-param name="property" select="."/>
    </xsl:call-template>
    <xsl:if test="@declare-getter">
    <xsl:if test="@override-getter"><xsl:text>override </xsl:text></xsl:if>public function get <xsl:value-of select="@name"/>():<xsl:value-of select="@type"/> {
      const currentValue:<xsl:value-of select="@type"/> = this._<xsl:value-of select="@name"/>;
      const managedValue:<xsl:value-of select="@type"/> = mx.data.utils.Managed.getProperty(
        this, "<xsl:value-of select="@name"/>", currentValue
      );
      if (currentValue !== managedValue)
        this._<xsl:value-of select="@name"/> = managedValue;
      return managedValue;
    }
    </xsl:if>

    <xsl:if test="@declare-setter">
    <xsl:if test="@override-setter"><xsl:text>override </xsl:text></xsl:if>public function set <xsl:value-of select="@name"/>(value:<xsl:value-of select="@type"/>):void {
      <xsl:if test="@enum">if (value) value = value.intern();
      </xsl:if>const previous:<xsl:value-of select="@type"/> = this._<xsl:value-of select="@name"/>;
      this._<xsl:value-of select="@name"/> = value;
      mx.data.utils.Managed.setProperty(
        this, "<xsl:value-of select="@name"/>", previous, _<xsl:value-of select="@name"/>
      );
    }
    </xsl:if>
    
  </xsl:template>	  
</xsl:stylesheet>