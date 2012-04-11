<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	extension-element-prefixes="redirect" exclude-result-prefixes="xalan redirect">

	<xsl:output omit-xml-declaration="yes" method="text" />

	<xsl:include href="generate-grid.xsl" />
	<xsl:include href="generate-controller.xsl" />
	<xsl:include href="generate-app.xsl" />

	<xsl:template name="generate-test-all.xsl">
		<xsl:param name="appName" />
		<xsl:param name="remoteActionNamespace" />
		<xsl:param name="outputFolder" />

		<xsl:for-each select="annotated-types/annotated-type">
			<xsl:variable name="interfaceName" select="@name" />
			<xsl:variable name="cxService"
				select="annotations/annotation[@name='clear.cdb.extjs.annotations.JSService']" />
			<xsl:if test="$cxService">
				<xsl:for-each select="methods/method">
					<xsl:variable name="generateViewSample"
						select="helper:methodAnnotatedWith($interfaceName, @name, 'clear.cdb.extjs.annotations.JSGenerateSample')" />
					<xsl:if test="$generateViewSample">
						<xsl:variable name="generateStore"
							select="annotations/annotation[@name='clear.cdb.extjs.annotations.JSGenerateStore']" />
						<xsl:variable name="fullStoreName">
							<xsl:if test="$generateStore">
								<xsl:variable name="storeType"
									select="$generateStore/method[@name='storeType']/@value" />
								<xsl:choose>
									<xsl:when test="$storeType">
										<xsl:value-of select="$storeType" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:variable name="fullDtoName"
											select="helper:getMethodTransferType($interfaceName, @name)" />
										<xsl:if test="$fullDtoName">
											<xsl:value-of select="helper:getStoreNameFull($fullDtoName)" />
										</xsl:if>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:if>
						</xsl:variable>
						<xsl:variable name="fullDtoName"
							select="helper:getMethodTransferType($interfaceName, @name)" />
						<xsl:variable name="fillParams"
							select="helper:getMethodAnnotationValue($interfaceName, @name, 'clear.cdb.extjs.annotations.JSGenerateSample','defaultFillArguments')" />
						<xsl:if test="$fullDtoName">
							<xsl:apply-templates select="/" mode="view-output">
								<xsl:with-param name="interfaceName" select="$interfaceName" />
								<xsl:with-param name="fillParams" select="$fillParams" />
								<xsl:with-param name="methodName" select="@name" />
								<xsl:with-param name="dtoName" select="$fullDtoName" />
								<xsl:with-param name="storeName" select="$fullStoreName" />
								<xsl:with-param name="appName" select="$appName" />
								<xsl:with-param name="outputFolder" select="$outputFolder" />
							</xsl:apply-templates>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="/" mode="view-output">
		<xsl:param name="interfaceName" />
		<xsl:param name="fillParams" />
		<xsl:param name="methodName" />
		<xsl:param name="dtoName" />
		<xsl:param name="storeName" />
		<xsl:param name="appName" />
		<xsl:param name="outputFolder" />

		<xsl:variable name="gridFileName"
			select="concat($outputFolder, '/', helper:replaceAll($interfaceName, '.', '/'), '/', $methodName, '/view/SampleGridPanel.js')" />
		<redirect:write file="{$gridFileName}">
			<xsl:call-template name="generate-grid.xsl">
				<xsl:with-param name="appName" select="$appName" />
				<xsl:with-param name="dtoName" select="$dtoName" />
				<xsl:with-param name="storeName" select="$storeName" />
			</xsl:call-template>
		</redirect:write>
		
		<xsl:variable name="controllerFileName"
			select="concat($outputFolder, '/', helper:replaceAll($interfaceName, '.', '/'), '/', $methodName, '/controller/SampleController.js')" />
		<redirect:write file="{$controllerFileName}">
			<xsl:call-template name="generate-controller.xsl">
				<xsl:with-param name="appName" select="$appName" />
				<xsl:with-param name="dtoName" select="$dtoName" />
				<xsl:with-param name="storeName" select="$storeName" />
			</xsl:call-template>
		</redirect:write>
		
		<xsl:variable name="appFileName"
			select="concat($outputFolder, '/', helper:replaceAll($interfaceName, '.', '/'), '/', $methodName, '/sampleApp.js')" />
		<redirect:write file="{$appFileName}">
			<xsl:call-template name="generate-app.xsl">
				<xsl:with-param name="appName" select="$appName" />
				<xsl:with-param name="dtoName" select="$dtoName" />
				<xsl:with-param name="storeName" select="$storeName" />
			</xsl:call-template>
		</redirect:write>
	</xsl:template>
</xsl:stylesheet>