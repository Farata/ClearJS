<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/" name="service-subclass.xsl">
		<xsl:param name="subServiceName"/>
		<xsl:param name="superServiceName"/>
		<xsl:param name="interfaceName"/>
		<xsl:param name="rootPackage"/>
	
package <xsl:value-of select="$rootPackage"/>;

import <xsl:value-of select="$rootPackage"/>.generated.*;

public class <xsl:value-of select="$subServiceName"/> extends <xsl:value-of select="$superServiceName"/> {
}
	</xsl:template>
</xsl:stylesheet>