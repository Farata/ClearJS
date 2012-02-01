<?xml version="1.0" encoding="iso-8859-1" ?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dto2extjs="http://dto2extjs.faratasystems.com/"
  version="1.1" 
>
  <xsl:template match="/dto2extjs:enum" mode="custom-file"
>private static function _init(v:<xsl:value-of select="@name"/>):void {
  // Properties "name" and "ordinal" of parameter "v" are accessible here.
  // Note, that at this point not all enum constanst are assigned 
  // and class is not fully initialized yet 
}

/* Uncomment the following method if you want to
 * setup enum state after deserialization.
 * Please keep suggested method prologue "as is"
override public function readExternal(input:flash.utils.IDataInput):void {
  super.readExternal(input);
  const intern:<xsl:value-of select="@name"/> = Self.values[ordinal];
  // Copy properties from "intern" to "this" here
  // like this._someField = intern.someProp
}
*/</xsl:template>		

</xsl:stylesheet>