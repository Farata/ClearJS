<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	extension-element-prefixes="redirect" exclude-result-prefixes="xalan redirect">

	<xsl:output omit-xml-declaration="yes" method="text" />

	<xsl:param name="flexOutputFolder" />
	<xsl:param name="javaSrcFolder" />
	<xsl:param name="force" />

	<xsl:include href="dataCollection.xsl" />
	<xsl:include href="dataCollection-subclass.xsl" />
	<xsl:include href="service-flex.xsl" />
	<xsl:include href="service-flex-subclass.xsl" />

	<xsl:template match="/|/">
		<xsl:for-each select="annotated-types/annotated-type">
			<xsl:variable name="interfaceName" select="@name" />
			<xsl:variable name="cxService"
				select="annotations/annotation[@name='clear.cdb.annotations.CX_Service']" />
			<xsl:if test="$cxService">
				<xsl:variable name="typeName" select="helper:getTypeName($interfaceName)" />
				<xsl:variable name="packageName"
					select="helper:getPackageName($interfaceName)" />
				<xsl:apply-templates select="/" mode="service-output">
					<xsl:with-param name="serviceName"
						select="helper:createGenServiceName($typeName)" />
					<xsl:with-param name="interfaceName" select="$interfaceName" />
					<xsl:with-param name="rootPackage" select="$packageName" />
					<xsl:with-param name="subServiceName"
						select="helper:createSubServiceName($typeName)" />
				</xsl:apply-templates>

				<!--  -->

				<xsl:for-each select="methods/method">
					<xsl:variable name="generateDataCollection"
						select="annotations/annotation[@name='clear.cdb.annotations.CX_GenerateDataCollection']" />
					<xsl:if test="$generateDataCollection">
						<xsl:variable name="collectionType"
							select="$generateDataCollection/method[@name='collectionType']/@value" />
						<xsl:variable name="fullCollectionName">
							<xsl:choose>
								<xsl:when test="$collectionType">
									<xsl:value-of select="$collectionType" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:variable name="fullDtoName"
										select="helper:getMethodTransferType($interfaceName, @name)" />
									<xsl:if test="$fullDtoName">
										<xsl:variable name="typeName"
											select="helper:createCollectionName(helper:getTypeName($fullDtoName))" />
										<xsl:variable name="packageName"
											select="helper:createCollectionPackageName(helper:getPackageName($fullDtoName))" />
										<xsl:value-of select="concat($packageName, '.', $typeName)" />
									</xsl:if>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:if test="$fullCollectionName">
							<xsl:variable name="collectionName"
								select="helper:getTypeName($fullCollectionName)" />
							<xsl:variable name="rootPackage"
								select="helper:getPackageName($fullCollectionName)" />
							<xsl:variable name="autoSyncEnabled">
								<xsl:variable name="jpqlMethodNode"
									select="annotations/annotation[@name='clear.cdb.annotations.CX_JPQLMethod']" />
								<xsl:variable name="javaFillMethodNode"
									select="annotations/annotation[@name='clear.cdb.annotations.CX_FillMethod']" />
								<xsl:variable name="updateInfo"
									select="annotations/annotation[@name='clear.cdb.annotations.CX_UpdateInfo'] | $jpqlMethodNode/method[@name='updateInfo']/value/annotation" />
								<xsl:value-of
									select="boolean($updateInfo/method[@name='autoSyncEnabled']/@value = 'true') or boolean($javaFillMethodNode/method[@name='autoSyncEnabled']/@value = 'true')" />
							</xsl:variable>
							<xsl:apply-templates select="/"
								mode="collection-output">
								<xsl:with-param name="rootPackage" select="$rootPackage" />
								<xsl:with-param name="interfaceName" select="$interfaceName" />
								<xsl:with-param name="fillMethodName" select="@name" />
								<xsl:with-param name="collectionName" select="$collectionName" />
								<xsl:with-param name="autoSyncEnabled" select="$autoSyncEnabled" />
							</xsl:apply-templates>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="/" mode="service-output">
		<xsl:param name="serviceName" />
		<xsl:param name="interfaceName" />
		<xsl:param name="rootPackage" />
		<xsl:param name="subServiceName" />

		<xsl:variable name="fullServiceName"
			select="concat($rootPackage, '.generated/', $serviceName)" />
		<xsl:variable name="fileName"
			select="concat($flexOutputFolder, '/', helper:replaceAll($fullServiceName, '.', '/'), '.as')" />
		<xsl:variable name="interfaceFileName"
			select="concat($javaSrcFolder, '/', helper:replaceAll($interfaceName, '.', '/'), '.java')" />
		<xsl:variable name="cmp"
			select="helper:compareLastModified($fileName, $interfaceFileName)" />
		<xsl:if test="$cmp != 1 or $force='true'">
			<redirect:write file="{$fileName}">
				<xsl:call-template name="service-flex.xsl">
					<xsl:with-param name="serviceName" select="$serviceName" />
					<xsl:with-param name="interfaceName" select="$interfaceName" />
					<xsl:with-param name="rootPackage"
						select="concat($rootPackage, '.generated')" />
				</xsl:call-template>
			</redirect:write>
		</xsl:if>
		<xsl:variable name="subclassFullServiceName"
			select="concat($rootPackage, '/', $subServiceName)" />
		<xsl:variable name="subclassFileName"
			select="concat($flexOutputFolder, '/', helper:replaceAll($subclassFullServiceName, '.', '/'), '.as')" />
		<xsl:if test="not(helper:fileExists($subclassFileName))">
			<redirect:write file="{$subclassFileName}">
				<xsl:call-template name="service-flex-subclass.xsl">
					<xsl:with-param name="subServiceName" select="$subServiceName" />
					<xsl:with-param name="superServiceName" select="$serviceName" />
					<xsl:with-param name="rootPackage" select="$rootPackage" />
				</xsl:call-template>
			</redirect:write>
		</xsl:if>
	</xsl:template>

	<xsl:template match="/" mode="collection-output">
		<xsl:param name="rootPackage" />
		<xsl:param name="interfaceName" />
		<xsl:param name="fillMethodName" />
		<xsl:param name="collectionName" />
		<xsl:param name="autoSyncEnabled" />

		<xsl:variable name="genFullCollectionName"
			select="concat($rootPackage, '.generated._', $collectionName)" />
		<xsl:variable name="genFileName"
			select="concat($flexOutputFolder, '/', helper:replaceAll($genFullCollectionName, '.', '/'), '.as')" />
		<xsl:variable name="interfaceFileName"
			select="concat($javaSrcFolder, '/', helper:replaceAll($interfaceName, '.', '/'), '.java')" />
		<xsl:variable name="cmp"
			select="helper:compareLastModified($genFileName, $interfaceFileName)" />
		<xsl:if test="$cmp != 1 or $force='true'">
			<redirect:write file="{$genFileName}">
				<xsl:call-template name="dataCollection.xsl">
					<xsl:with-param name="rootPackage"
						select="concat($rootPackage, '.generated')" />
					<xsl:with-param name="interfaceName" select="$interfaceName" />
					<xsl:with-param name="fillMethodName" select="$fillMethodName" />
					<xsl:with-param name="collectionName" select="concat('_', $collectionName)" />
					<xsl:with-param name="autoSyncEnabled" select="$autoSyncEnabled" />
				</xsl:call-template>
			</redirect:write>
		</xsl:if>

		<xsl:variable name="subclassFullCollectionName"
			select="concat($rootPackage, '.', $collectionName)" />
		<xsl:variable name="subclassFileName"
			select="concat($flexOutputFolder, '/', helper:replaceAll($subclassFullCollectionName, '.', '/'), '.as')" />
		<xsl:if test="not(helper:fileExists($subclassFileName))">
			<redirect:write file="{$subclassFileName}">
				<xsl:call-template name="dataCollection-subclass.xsl">
					<xsl:with-param name="rootPackage" select="$rootPackage" />
					<xsl:with-param name="collectionName" select="$collectionName" />
				</xsl:call-template>
			</redirect:write>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>