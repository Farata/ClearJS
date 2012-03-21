<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	extension-element-prefixes="redirect" exclude-result-prefixes="xalan redirect">

	<xsl:output omit-xml-declaration="yes" method="text" />

	<xsl:param name="jsOutputFolder" />
	<xsl:param name="appName" />
	<xsl:param name="remoteActionNamespace" />

	<xsl:include href="generate-store-all.xsl" />
	<xsl:include href="generate-model-all.xsl" />
	<xsl:include href="generate-test-all.xsl" />


	<xsl:template match="/|/">

		<!-- xsl:variable name="modelPath"
			select="concat($jsOutputFolder, '/app/model/')" />
		<xsl:call-template name="generate-model-all.xsl">
			<xsl:with-param name="outputFolder" select="$modelPath" />
			<xsl:with-param name="appName" select="$appName" />
			<xsl:with-param name="force" select="string('true')" />
		</xsl:call-template -->

		<xsl:variable name="storePath"
			select="concat($jsOutputFolder, '')" />
		<xsl:call-template name="generate-store-all.xsl">
			<xsl:with-param name="appName" select="$appName" />
			<xsl:with-param name="remoteActionNamespace" select="$remoteActionNamespace" />
			<xsl:with-param name="outputFolder" select="$storePath" />
		</xsl:call-template>

		<xsl:variable name="testPath" select="concat($jsOutputFolder, '/samples/')" />
		<xsl:call-template name="generate-test-all.xsl">
			<xsl:with-param name="appName" select="$appName" />
			<xsl:with-param name="remoteActionNamespace" select="$remoteActionNamespace" />
			<xsl:with-param name="outputFolder" select="$testPath" />
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>