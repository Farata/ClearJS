<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/" name="dataCollection-subclass.xsl">
		<xsl:param name="rootPackage"/>
		<xsl:param name="collectionName"/>
package <xsl:value-of select="$rootPackage"/>
{
	import <xsl:value-of select="$rootPackage"/>.generated._<xsl:value-of select="$collectionName"/>;

	public class <xsl:value-of select="$collectionName"/> extends _<xsl:value-of select="$collectionName"/>
	{
		public function <xsl:value-of select="$collectionName"/>(source:Array=null) {
			super(source);
		}
	}
}		
	</xsl:template>
</xsl:stylesheet>