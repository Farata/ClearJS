<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:include href="service-interface-jpql.xsl" />
	<xsl:include href="service-interface-fill.xsl" />
	<xsl:include href="service-interface-get.xsl" />
	<xsl:include href="service-interface-fill-children.xsl" />

	<xsl:template match="/" name="service-interface.xsl">
		<xsl:param name="serviceName"/>
		<xsl:param name="interfaceName"/>
		<xsl:param name="rootPackage"/>
		<xsl:variable name="methods" select="annotated-types/annotated-type[@name=$interfaceName]/methods"/>
		<xsl:variable name="annotatedType" select="annotated-types/annotated-type[@name=$interfaceName]"/>
		<xsl:value-of select="helper:clearDTOMappings()"/>
		<xsl:for-each select="annotated-types/dto-mappings/dto-mapping">
			<xsl:value-of select="helper:addDTOMapping(@dto-name, @entity-name)"/>
		</xsl:for-each>	
package <xsl:value-of select="$rootPackage"/>;

public interface <xsl:value-of select="$serviceName"/> extends <xsl:value-of select="$interfaceName"/> {
		<xsl:for-each select="$methods/method">
			<xsl:variable name="methodNode" select="current()"/>
			<xsl:choose>
				<xsl:when test="annotations/annotation[@name='clear.cdb.extjs.annotations.JSJPQLMethod']">
					<xsl:call-template name="service-interface-jpql.xsl">
						<xsl:with-param name="interfaceName" select="$interfaceName" />
						<xsl:with-param name="methodNode" select="$methodNode" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="annotations/annotation[@name='clear.cdb.extjs.annotations.JSFillMethod']">
					<xsl:call-template name="service-interface-fill.xsl">
						<xsl:with-param name="interfaceName" select="$interfaceName" />
						<xsl:with-param name="methodNode" select="$methodNode" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="annotations/annotation[@name='clear.cdb.extjs.annotations.JSGetMethod']">
					<xsl:call-template name="service-interface-get.xsl">
						<xsl:with-param name="interfaceName" select="$interfaceName" />
						<xsl:with-param name="methodNode" select="$methodNode" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="annotations/annotation[@name='clear.cdb.extjs.annotations.JSFillChildrenMethod']">
					<xsl:call-template name="service-interface-fill-children.xsl">
						<xsl:with-param name="interfaceName" select="$interfaceName" />
						<xsl:with-param name="methodNode" select="$methodNode" />
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
}
	</xsl:template>
</xsl:stylesheet>