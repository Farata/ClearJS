<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect" xmlns:mx="http://www.adobe.com/2006/mxml"
	extension-element-prefixes="redirect" exclude-result-prefixes="xalan redirect">

	<xsl:output method="xml" cdata-section-elements="mx:Script"
		indent="yes" xalan:indent-amount="4" />

	<xsl:param name="javaSrcFolder" />
	<xsl:param name="testFolder" />
	<xsl:param name="force" />

	<xsl:include href="application.xsl" />

	<xsl:template match="/|/">
		<xsl:for-each select="annotated-types/annotated-type">
			<xsl:variable name="interfaceName" select="@name" />
			<xsl:variable name="cxService"
				select="annotations/annotation[@name='clear.cdb.annotations.CX_Service']" />
			<xsl:if test="$cxService">
				<xsl:for-each select="methods/method">
					<xsl:variable name="generateMXMLSample"
						select="helper:methodAnnotatedWith($interfaceName, @name, 'clear.cdb.annotations.CX_GenerateMXMLSample')" />
					<xsl:if test="$generateMXMLSample">
						<xsl:variable name="fullDtoName"
							select="helper:getMethodTransferType($interfaceName, @name)" />
						<xsl:variable name="fillParams"
							select="helper:getMethodAnnotationValue($interfaceName, @name, 'clear.cdb.annotations.CX_GenerateMXMLSample','defaultFillArguments')" />
						<xsl:if test="$fullDtoName">
							<xsl:apply-templates select="/" mode="mxml-output">
								<xsl:with-param name="interfaceName" select="$interfaceName" />
								<xsl:with-param name="fillParams" select="$fillParams" />
								<xsl:with-param name="methodName" select="@name" />
								<xsl:with-param name="dtoName" select="$fullDtoName" />
							</xsl:apply-templates>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="/" mode="mxml-output">
		<xsl:param name="interfaceName" />
		<xsl:param name="fillParams" />
		<xsl:param name="methodName" />
		<xsl:param name="dtoName" />

		<xsl:variable name="fileName"
			select="concat($testFolder, '/', helper:replaceAll($interfaceName, '.', '/'), '/', $methodName, '/GridTest.mxml')" />
		<xsl:variable name="interfaceFileName"
			select="concat($javaSrcFolder, '/', helper:replaceAll($interfaceName, '.', '/'), '.java')" />
		<xsl:variable name="cmp"
			select="helper:compareLastModified($fileName, $interfaceFileName)" />
		<xsl:if test="$cmp != 1 or $force='true'">
			<redirect:write file="{$fileName}">
				<xsl:call-template name="application.xsl">
					<xsl:with-param name="interfaceName" select="$interfaceName" />
					<xsl:with-param name="fillParams" select="$fillParams" />
					<xsl:with-param name="methodName" select="$methodName" />
					<xsl:with-param name="dtoName" select="$dtoName" />
				</xsl:call-template>
			</redirect:write>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>