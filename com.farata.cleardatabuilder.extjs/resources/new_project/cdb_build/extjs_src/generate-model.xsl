<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	exclude-result-prefixes="xalan">

	<xsl:output omit-xml-declaration="yes" method="text" />
	<xsl:include href="generate-model-jpql.xsl" />

	<xsl:template match="/" name="generate-model.xsl">
		<xsl:param name="dtoName" />
		<xsl:param name="rootPackage" />
		<xsl:param name="interfaceName" />
		<xsl:param name="methodName" />
		<xsl:param name="appName" />
		
		<xsl:variable name="methodNode"
			select="annotated-types/annotated-type[@name=$interfaceName]/methods/method[@name=$methodName]" />
		<xsl:variable name="jpqlMethodNode"
			select="$methodNode/annotations/annotation[@name='clear.cdb.extjs.annotations.JSJPQLMethod']" />
		<xsl:variable name="getMethodNode"
			select="$methodNode/annotations/annotation[@name='clear.cdb.extjs.annotations.JSGetMethod']" />
		<xsl:variable name="fillChildrenMethodNode"
			select="$methodNode/annotations/annotation[@name='clear.cdb.extjs.annotations.JSFillChildrenMethod']" />

		<xsl:choose>
			<xsl:when test="$jpqlMethodNode">
				<xsl:call-template name="generate-model-jpql.xsl">
					<xsl:with-param name="dtoName" select="$dtoName" />
					<xsl:with-param name="rootPackage" select="$rootPackage" />
					<xsl:with-param name="interfaceName" select="$interfaceName" />
					<xsl:with-param name="methodName" select="$methodName" />
					<xsl:with-param name="appName" select="$appName" />
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>