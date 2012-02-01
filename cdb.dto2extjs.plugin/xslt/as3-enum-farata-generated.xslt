<?xml version="1.0" encoding="iso-8859-1" ?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dto2extjs="http://dto2extjs.faratasystems.com/"
  version="1.1" 
>
  <xsl:template match="/dto2extjs:enum" mode="generated-file">
package <xsl:value-of select="$packageName"/> {
  <xsl:call-template name="import-types-by-class">
      <xsl:with-param name="class" select="."/>
    </xsl:call-template><xsl:text>
</xsl:text>
  import flash.utils.IDataInput;
  
  import com.farata.as3.lang.EnumBase;    
  import com.farata.as3.lang.EnumClass;  
  import com.farata.as3.lang.as3_lang;
  
  [RemoteClass(alias="<xsl:value-of select="@javaClass"/>")] 
  public class <xsl:value-of select="$className"/> extends com.farata.as3.lang.EnumBase {
    
    public function <xsl:value-of select="$className"/>(name:String = null):void { 
      super(Self, name); 
      if (name) _init(this);
    } 
    
    public function intern():<xsl:value-of select="@name"/> { return <xsl:value-of select="@name"/>(as3_lang::intern()); }
      
    private static const Self:com.farata.as3.lang.EnumClass = as3_lang::enumOf(<xsl:value-of select="@name"/>);
      
    public static function valueOf(name:String):<xsl:value-of select="@name"/> {
      return Self.valueMap[name];
    }
      
    private static function _(name:String):<xsl:value-of select="@name"/> {
      return <xsl:value-of select="@name"/>(Self.declare(name));
    }
    
    <xsl:apply-templates/>
    
    [ArrayElementType("<xsl:value-of select="@name"/>")]
    public static const values:Array = Self.values;
    
    // Custom code must be placed in include file
    include "<xsl:value-of select="$className"/>.inc";
  }
  
}</xsl:template>		

  <xsl:template match="dto2extjs:enum-entry">
    public static const <xsl:value-of select="@name"/>:<xsl:value-of select="/dto2extjs:enum/@name"/> = _("<xsl:value-of select="@name"/>");
  </xsl:template>

  <!-- 
  <xsl:template name="check-array-type">
    <xsl:param name="property"/>  
    <xsl:if test="$property/@type = 'Array' and $property/@contentType"
    >[ArrayElementType("<xsl:value-of select="$property/@contentType"/>")]</xsl:if>
  </xsl:template>
	
  <xsl:template match="dto2extjs:property"><xsl:text>
</xsl:text>    /* Property <xsl:value-of select="@name"/> */
    [Bindable(event="propertyChange")]<xsl:call-template 
    name="check-array-type"><xsl:with-param name="property" select="."/></xsl:call-template>
    function get <xsl:value-of select="@name"/>():<xsl:value-of select="@type"/>;
    function set <xsl:value-of select="@name"/>(value:<xsl:value-of select="@type"/>):void;
    
  </xsl:template>	
   -->
  
</xsl:stylesheet>