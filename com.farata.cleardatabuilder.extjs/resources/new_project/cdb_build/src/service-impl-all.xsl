<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	extension-element-prefixes="redirect" exclude-result-prefixes="xalan redirect">

	<xsl:output omit-xml-declaration="yes" method="text" />

	<xsl:param name="outputFolder" />
	<xsl:param name="force" />
	<xsl:param name="springIntegration"/>

	<xsl:include href="service-impl.xsl" />
	<xsl:include href="service-subclass.xsl" />
	<xsl:include href="service-interface.xsl" />

	<xsl:template match="/|/|/|/">
		<xsl:for-each select="annotated-types/annotated-type">
			<xsl:variable name="interfaceName" select="@name" />
			<xsl:variable name="cxService"
				select="annotations/annotation[@name='clear.cdb.extjs.annotations.JSService']" />
			<xsl:if test="$cxService">
				<xsl:variable name="typeName" select="helper:getTypeName($interfaceName)" />
				<xsl:variable name="packageName"
					select="helper:getPackageName($interfaceName)" />
				<xsl:apply-templates select="/" mode="output">
					<xsl:with-param name="serviceName"
						select="helper:createGenServiceName($typeName)" />
					<xsl:with-param name="interfaceName" select="$interfaceName" />
					<xsl:with-param name="rootPackage" select="$packageName" />
					<xsl:with-param name="subServiceName"
						select="helper:createSubServiceName($typeName)" />
				</xsl:apply-templates>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="/" mode="output">
		<xsl:param name="serviceName" />
		<xsl:param name="interfaceName" />
		<xsl:param name="rootPackage" />
		<xsl:param name="subServiceName" />

		<xsl:variable name="fullServiceName"
			select="concat($rootPackage, '.generated/', $serviceName)" />
		<xsl:variable name="fileName"
			select="concat($outputFolder, '/', helper:replaceAll($fullServiceName, '.', '/'), '.java')" />
		<xsl:variable name="interfaceFileName"
			select="concat($outputFolder, '/', helper:replaceAll($interfaceName, '.', '/'), '.java')" />
		<xsl:variable name="cmp"
			select="helper:compareLastModified($fileName, $interfaceFileName)" />
		<xsl:if test="$cmp != 1 or $force='true'">
			<redirect:write file="{$fileName}">
				<xsl:call-template name="service-impl.xsl">
					<xsl:with-param name="springEnabled" select="boolean('true')" />
					<xsl:with-param name="serviceName" select="$serviceName" />
					<xsl:with-param name="interfaceName" select="$interfaceName" />
					<xsl:with-param name="rootPackage"
						select="concat($rootPackage, '.generated')" />
				</xsl:call-template>
			</redirect:write>
			<xsl:variable name="fileName"
				select="concat($outputFolder, '/', helper:replaceAll($fullServiceName, '.', '/'), '.java')" />
			<xsl:variable name="iServiceName"
				select="concat('_I', substring($serviceName,2))" />
			<xsl:variable name="iFileName"
				select="helper:replaceAll($fileName, $serviceName, $iServiceName)" />
			<redirect:write file="{$iFileName}">
				<xsl:call-template name="service-interface.xsl">
					<xsl:with-param name="serviceName" select="$iServiceName" />
					<xsl:with-param name="interfaceName" select="$interfaceName" />
					<xsl:with-param name="rootPackage"
						select="concat($rootPackage, '.generated')" />
				</xsl:call-template>
			</redirect:write>
			
		</xsl:if>
		
		<xsl:variable name="subclassFullServiceName"
			select="concat($rootPackage, '/', $subServiceName)" />
		<xsl:variable name="subclassFileName"
			select="concat($outputFolder, '/', helper:replaceAll($subclassFullServiceName, '.', '/'), '.java')" />
		<xsl:if test="not(helper:fileExists($subclassFileName))">
			<redirect:write file="{$subclassFileName}">
				<xsl:call-template name="service-subclass.xsl">
					<xsl:with-param name="springIntegration" select="$springIntegration"/>
					<xsl:with-param name="subServiceName" select="$subServiceName" />
					<xsl:with-param name="superServiceName" select="$serviceName" />
					<xsl:with-param name="interfaceName" select="$interfaceName" />
					<xsl:with-param name="rootPackage" select="$rootPackage" />
				</xsl:call-template>
			</redirect:write>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>