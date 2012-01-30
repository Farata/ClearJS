<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/" name="dataCollection.xsl">
		<xsl:param name="rootPackage"/>
		<xsl:param name="interfaceName"/>
		<xsl:param name="fillMethodName"/>
		<xsl:param name="collectionName"/>
		<xsl:param name="autoSyncEnabled"/>
package <xsl:value-of select="$rootPackage"/>
{
	import clear.collections.DataCollection;

	public class <xsl:value-of select="$collectionName"/> extends DataCollection	
	{
		public function <xsl:value-of select="$collectionName"/>(source:Array=null) {
			super(source);
			destination="<xsl:value-of select="$interfaceName"/>"; 
			fillMethod="<xsl:value-of select="$fillMethodName"/>";<xsl:if test="$autoSyncEnabled='true'">			 
			// You have to enable autoSync on the client, otherwise DataCollection
			// will not listen to the changes pushed by the server
			autoSyncEnabled=true;</xsl:if>
		}
	}
}		
	</xsl:template>
</xsl:stylesheet>