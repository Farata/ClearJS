<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/" name="service-dto-subclass.xsl">
		<xsl:param name="dtoName"/>
		<xsl:param name="rootPackage"/>
	
package <xsl:value-of select="$rootPackage"/>;

import com.farata.dto2fx.annotations.*;

@FXClass(kind=FXClassKind.REMOTE)
public class <xsl:value-of select="$dtoName"/>  extends <xsl:value-of select="$rootPackage"/>.gen._<xsl:value-of select="$dtoName"/>{
}		
	</xsl:template>
</xsl:stylesheet>