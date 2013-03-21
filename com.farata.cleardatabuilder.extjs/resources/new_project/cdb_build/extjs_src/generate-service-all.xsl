<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	extension-element-prefixes="redirect" exclude-result-prefixes="xalan redirect">

	<xsl:output omit-xml-declaration="yes" method="text" />

	<xsl:include href="generate-service.xsl" />
	<xsl:include href="generate-service-subclass.xsl" />

	<xsl:template name="generate-service-all.xsl">
		<xsl:param name="appName" />
		<xsl:param name="remoteActionNamespace" />
		<xsl:param name="outputFolder" />

		<xsl:for-each select="annotated-types/annotated-type">
			<xsl:variable name="interfaceName" select="@name" />
			<xsl:variable name="cxService"
				select="annotations/annotation[@name='clear.cdb.extjs.annotations.JSService']" />
			<xsl:if test="$cxService">
				<xsl:variable name="fullJsServiceName">
					<xsl:variable name="serviceName" select="helper:createSubServiceName($interfaceName)" />
					<xsl:value-of select="helper:getServiceNameFull($serviceName)" />
				</xsl:variable>
				<xsl:if test="$fullJsServiceName">
					<xsl:variable name="jsServiceName"
						select="helper:getTypeName($fullJsServiceName)" />
					<xsl:variable name="rootPackage"
						select="helper:getPackageName($fullJsServiceName)" />
					<xsl:apply-templates select="/" mode="service-output">
						<xsl:with-param name="rootPackage" select="$rootPackage" />
						<xsl:with-param name="interfaceName" select="$interfaceName" />
						<xsl:with-param name="jsServiceName" select="$jsServiceName" />
						<xsl:with-param name="appName" select="$appName" />
						<xsl:with-param name="remoteActionNamespace"
							select="$remoteActionNamespace" />
						<xsl:with-param name="outputFolder" select="$outputFolder" />
					</xsl:apply-templates>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="/" mode="service-output">
		<xsl:param name="rootPackage" />
		<xsl:param name="interfaceName" />
		<xsl:param name="jsServiceName" />
		<xsl:param name="appName" />
		<xsl:param name="remoteActionNamespace" />
		<xsl:param name="outputFolder" />
		
		<xsl:variable name="fullJsServiceName"
			select="concat($rootPackage, '.', $jsServiceName)" />
		<xsl:variable name="genFileName"
			select="concat($outputFolder, '/', helper:getServicePathByServiceName($fullJsServiceName), '/generated/_', $jsServiceName, '.js')" />
		<redirect:write file="{$genFileName}">
			<xsl:call-template name="generate-service.xsl">
				<xsl:with-param name="rootPackage"
					select="concat($rootPackage, '.generated')" />
				<xsl:with-param name="interfaceName" select="$interfaceName" />
				<xsl:with-param name="jsServiceName" select="concat('_', $jsServiceName)" />
				<xsl:with-param name="appName" select="$appName" />
				<xsl:with-param name="remoteActionNamespace" select="$remoteActionNamespace" />
			</xsl:call-template>
		</redirect:write>

		<xsl:variable name="subclassFileName"
			select="concat($outputFolder, '/', helper:getServicePathByServiceName($fullJsServiceName), '/', $jsServiceName, '.js')" />
		<xsl:if test="not(helper:fileExists($subclassFileName))">
			<redirect:write file="{$subclassFileName}">
				<xsl:call-template name="generate-service-subclass.xsl">
					<xsl:with-param name="rootPackage" select="$rootPackage" />
					<xsl:with-param name="jsServiceName" select="$jsServiceName" />
					<xsl:with-param name="appName" select="$appName" />
				</xsl:call-template>
			</redirect:write>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>