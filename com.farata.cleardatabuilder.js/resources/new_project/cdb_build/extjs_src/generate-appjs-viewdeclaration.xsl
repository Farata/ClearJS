<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	extension-element-prefixes="redirect" exclude-result-prefixes="xalan redirect">
	
	<xsl:template match="/" mode="add-vew-declaration">
	<xsl:param name="rootFilePath" />
	<xsl:param name="elementName"  />
		<redirect:write file="{$rootFilePath}" append="true">
					{
						xtype:'<xsl:value-of select="$elementName"/>'
					}
		</redirect:write>
	</xsl:template>
</xsl:stylesheet>