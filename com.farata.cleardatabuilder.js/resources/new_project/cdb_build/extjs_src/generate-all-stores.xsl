<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	extension-element-prefixes="redirect" exclude-result-prefixes="xalan redirect">

	<xsl:output omit-xml-declaration="yes" method="text" />

	<xsl:param name="jsOutputFolder" />
	<xsl:param name="appName" />
	<xsl:param name="remoteActionNamespace"/>
	
	<xsl:include href="generate-store.xsl" />
	
	<xsl:template match="/|/">
		<xsl:for-each select="annotated-types/annotated-type">
			<xsl:variable name="interfaceName" select="@name" />
			<xsl:variable name="cxService" select="annotations/annotation[@name='clear.cdb.annotations.CX_Service']" />
			<xsl:if test="$cxService">
				<xsl:variable name="typeName" select="helper:getTypeName($interfaceName)" />
				<xsl:variable name="packageName" select="helper:getPackageName($interfaceName)" />
				<xsl:apply-templates select="/" mode="service-output">
					<xsl:with-param name="serviceName"	  select="helper:createSubServiceName($typeName)" />
					<xsl:with-param name="interfaceName"  select="$interfaceName" />
					<xsl:with-param name="rootPackage"    select="$packageName" />
					<xsl:with-param name="subServiceName" select="helper:createSubServiceName($typeName)" />
				</xsl:apply-templates>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="/" mode="service-output">
		<xsl:param name="serviceName" />
		<xsl:param name="interfaceName" />
		<xsl:param name="rootPackage" />
		<xsl:param name="subServiceName" />
		<xsl:variable name="methods" select="annotated-types/annotated-type[@name=$interfaceName]/methods/method"/>
		<xsl:for-each select="$methods">
	<xsl:variable name="methodNode" select="current()" />
	<xsl:choose>
		<xsl:when
			test="annotations/annotation[@name='clear.cdb.annotations.CX_JPQLMethod']">
			<xsl:variable name="fileName" select="concat($jsOutputFolder, '/', $serviceName, '_', @name, '_Store.js')" />
			<redirect:write file="{$fileName}">
				<xsl:call-template name="generate-store.xsl">
					<xsl:with-param name="serviceName"    select="$serviceName" />
					<xsl:with-param name="appName" 		  select="$appName" />
					<xsl:with-param name="remoteActionNamespace" select="$remoteActionNamespace" />
					<xsl:with-param name="methodName" 	  select="@name" />
					<xsl:with-param name="interfaceName"  select="$interfaceName" />
					
					<xsl:with-param name="create"  select="concat($methodNode/@name,'_insertItems')" />
					<xsl:with-param name="read"  select="$methodNode/@name" />
					<xsl:with-param name="update"  select="concat($methodNode/@name,'_updateItems')" />
					<xsl:with-param name="destroy"  select="concat($methodNode/@name,'_deleteItems')" />
					
				</xsl:call-template>
			</redirect:write>
		</xsl:when>
		<xsl:when
			test="annotations/annotation[@name='clear.cdb.annotations.CX_FillMethod']">
			<xsl:variable name="fileName" select="concat($jsOutputFolder, '/', $serviceName, '_', @name, '_Store.js')" />
			<redirect:write file="{$fileName}">
				<xsl:call-template name="generate-store.xsl">
					<xsl:with-param name="serviceName"    select="$serviceName" />
					<xsl:with-param name="appName" 		  select="$appName" />
					<xsl:with-param name="remoteActionNamespace" select="$remoteActionNamespace" />
					<xsl:with-param name="methodName" 	  select="@name" />
					<xsl:with-param name="interfaceName"  select="$interfaceName" />
					
					<xsl:with-param name="create"  select="concat($methodNode/@name,'_insertItems')" />
					<xsl:with-param name="read"    select="$methodNode/@name" />
					<xsl:with-param name="update"  select="concat($methodNode/@name,'_updateItems')" />
					<xsl:with-param name="destroy" select="concat($methodNode/@name,'_deleteItems')" />
					
					
					
				</xsl:call-template>
			</redirect:write>
		</xsl:when>
		<xsl:when
			test="annotations/annotation[@name='clear.cdb.annotations.CX_GetMethod']">
			<xsl:variable name="fileName" select="concat($jsOutputFolder, '/', $serviceName, '_', @name, '_Store.js')" />
			<redirect:write file="{$fileName}">
				<xsl:call-template name="generate-store.xsl">
					<xsl:with-param name="serviceName" 	  select="$serviceName" />
					<xsl:with-param name="appName" 		  select="$appName" />
					<xsl:with-param name="remoteActionNamespace" select="$remoteActionNamespace" />
					<xsl:with-param name="methodName" 	  select="@name" />
					<xsl:with-param name="interfaceName"  select="$interfaceName" />
					
					<xsl:with-param name="create"  select="concat($methodNode/@name,'_create')" />
					<xsl:with-param name="read"    select="$methodNode/@name" />
					<xsl:with-param name="update"  select="concat($methodNode/@name,'_update')" />
					<xsl:with-param name="destroy" select="concat($methodNode/@name,'_delete')" />
					
				</xsl:call-template>
			</redirect:write>
		</xsl:when>
		<xsl:when
			test="annotations/annotation[@name='clear.cdb.annotations.CX_FillChildrenMethod']">
			<xsl:variable name="fileName" select="concat($jsOutputFolder, '/', $serviceName, '_', @name, '_Store.js')" />
			<redirect:write file="{$fileName}">
				<xsl:call-template name="generate-store.xsl">
					<xsl:with-param name="serviceName"    select="$serviceName" />
					<xsl:with-param name="appName" 		  select="$appName" />
					<xsl:with-param name="remoteActionNamespace" select="$remoteActionNamespace" />
					<xsl:with-param name="methodName" 	  select="@name" />
					<xsl:with-param name="interfaceName"  select="$interfaceName" />
					
					<xsl:with-param name="create"  select="concat($methodNode/@name,'_insertItems')" />
					<xsl:with-param name="read"    select="$methodNode/@name" />
					<xsl:with-param name="update"  select="concat($methodNode/@name,'_updateItems')" />
					<xsl:with-param name="destroy" select="concat($methodNode/@name,'_deleteItems')" />
					
					
				</xsl:call-template>
			</redirect:write>
		</xsl:when>
	</xsl:choose>
</xsl:for-each>
		
		
	</xsl:template>

	
</xsl:stylesheet>