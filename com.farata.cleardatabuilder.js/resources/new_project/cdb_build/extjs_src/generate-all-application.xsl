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

	<xsl:include href="generate-app_js.xsl" />
	<xsl:include href="generate-appjs-viewdeclaration.xsl" />
	<xsl:include href="generate-controller.xsl" />
	<xsl:include href="generate-grid.xsl" />
	<xsl:include href="generate-service.xsl" />
	<xsl:include href="generate-store-all.xsl" />
	<xsl:include href="generate-model-all.xsl" />


	<xsl:template match="/|/">

		<xsl:for-each select="annotated-types/annotated-type">
			<xsl:variable name="interfaceName" select="@name" />
			<xsl:variable name="cxService"
				select="annotations/annotation[@name='clear.cdb.annotations.CX_Service']" />
			<xsl:if test="$cxService">
				<xsl:variable name="testPath" select="concat($jsOutputFolder, '/test/')" />
				<xsl:variable name="typeName" select="helper:getTypeName($interfaceName)" />
				<xsl:variable name="packageName"
					select="helper:getPackageName($interfaceName)" />

				<xsl:apply-templates select="/" mode="traverse-methods">
					<xsl:with-param name="serviceName"
						select="helper:createSubServiceName($typeName)" />
					<xsl:with-param name="interfaceName" select="$interfaceName" />
					<xsl:with-param name="rootPackage" select="$packageName" />
					<xsl:with-param name="testPath" select="$testPath" />
					<xsl:with-param name="typeName" select="$typeName" />
					<xsl:with-param name="packageName" select="$packageName" />
				</xsl:apply-templates>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="/" mode="traverse-methods">
		<xsl:param name="serviceName" />
		<xsl:param name="interfaceName" />
		<xsl:param name="rootPackage" />
		<xsl:param name="testPath" />
		<xsl:param name="typeName" />
		<xsl:param name="packageName" />

		<xsl:variable name="methods"
			select="annotated-types/annotated-type[@name=$interfaceName]/methods/method" />
		<xsl:for-each select="$methods">
			<xsl:variable name="currentNode" select="current()" />
			<xsl:variable name="elementPrefix" select="concat($serviceName, '_', @name)" />
			<xsl:variable name="rootFilePath"
				select="concat($testPath, $elementPrefix, '_App.js')" />
			<xsl:variable name="appFolderPath"
				select="concat($packageName, '.', $serviceName, '.', @name)" />

			<xsl:apply-templates select="/" mode="generate-app">
				<xsl:with-param name="appName" select="$appName" />
				<xsl:with-param name="appFolderPath" select="$appFolderPath" />
				<xsl:with-param name="elementPrefix" select="$elementPrefix" />
				<xsl:with-param name="rootFilePath" select="$rootFilePath" />
			</xsl:apply-templates>

			<!-- start generate controllers -->
			<xsl:variable name="elementName"
				select="concat($elementPrefix, '_Controller')" />
			<xsl:variable name="fileName"
				select="concat($testPath, helper:replaceAll($appFolderPath, '.', '/'), '/controller/',  $elementName, '.js')" />

			<!-- generate controller -->
			<redirect:write file="{$fileName}">
				<xsl:call-template name="generate-controller.xsl">
					<xsl:with-param name="serviceName" select="$serviceName" />
					<xsl:with-param name="appName" select="$appName" />
					<xsl:with-param name="methodName" select="@name" />
					<xsl:with-param name="interfaceName" select="$interfaceName" />
				</xsl:call-template>
			</redirect:write>

			<!-- start generate panels -->
			<xsl:variable name="elementName" select="concat($elementPrefix, '_Panel')" />
			<xsl:variable name="fileName"
				select="concat($testPath, '/', helper:replaceAll($appFolderPath, '.', '/'), '/view/', $elementName, '.js' )" />

			<!-- generate panel -->
			<redirect:write file="{$fileName}">
				<xsl:call-template name="generate-grid.xsl">
					<xsl:with-param name="serviceName" select="$serviceName" />
					<xsl:with-param name="appName" select="$appName" />
					<xsl:with-param name="methodName" select="@name" />
					<xsl:with-param name="interfaceName" select="$interfaceName" />
				</xsl:call-template>
			</redirect:write>

			<!-- generate services -->
			<xsl:variable name="create" select="concat(@name,'_insertItems')" />
			<xsl:variable name="read" select="@name" />
			<xsl:variable name="update" select="concat(@name,'_updateItems')" />
			<xsl:variable name="destroy" select="concat(@name,'_deleteItems')" />


			<xsl:variable name="sName"
				select="concat($testPath, '/', helper:replaceAll($appFolderPath, '.', '/'), '/service/', $serviceName, '.js')" />

			<redirect:write file="{$sName}">
				<xsl:call-template name="generate-service.xsl">
					<xsl:with-param name="serviceName" select="$serviceName" />
					<xsl:with-param name="appName" select="$appName" />
					<xsl:with-param name="remoteActionNamespace" select="$remoteActionNamespace" />
					<xsl:with-param name="methodName" select="@name" />
					<xsl:with-param name="interfaceName" select="$interfaceName" />

					<xsl:with-param name="create" select="$create" />
					<xsl:with-param name="read" select="$read" />
					<xsl:with-param name="update" select="$update" />
					<xsl:with-param name="destroy" select="$destroy" />

				</xsl:call-template>
			</redirect:write>
		</xsl:for-each>

		<xsl:variable name="modelPath" select="concat($jsOutputFolder, '/app/model/')" />
		<xsl:call-template name="generate-model-all.xsl">
			<xsl:with-param name="outputFolder" select="$modelPath" />
			<xsl:with-param name="force" select="string('true')" />
		</xsl:call-template>
		
		<xsl:variable name="storePath" select="concat($jsOutputFolder, '/app/store/')" />
		<xsl:call-template name="generate-store-all.xsl">
			<xsl:with-param name="appName" select="$appName" />
			<xsl:with-param name="remoteActionNamespace" select="$remoteActionNamespace" />
			<xsl:with-param name="outputFolder" select="$storePath" />
			
		</xsl:call-template>
		

	</xsl:template>

</xsl:stylesheet>