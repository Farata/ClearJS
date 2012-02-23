<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	exclude-result-prefixes="xalan helper"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	<xsl:output method="xml" indent="yes" xalan:indent-amount="4" />

	<xsl:template match="/">
		<xsl:text>&#10;</xsl:text>

		<service id="message-service" class="flex.messaging.services.MessageService">

			<adapters>
				<adapter-definition id="actionscript"
					class="flex.messaging.services.messaging.adapters.ActionScriptAdapter"
					default="true" />
				<!--
					<adapter-definition id="jms"
					class="flex.messaging.services.messaging.adapters.JMSAdapter"/>
				-->
			</adapters>

			<default-channels>
				<channel ref="my-streaming-amf" />
				<channel ref="my-polling-amf" />
			</default-channels>
			<xsl:for-each select="/annotated-types/annotated-type">
				<xsl:variable name="interfaceName">
					<xsl:value-of select="string(@name)" />
				</xsl:variable>
				<xsl:for-each select="methods/method">
					<xsl:variable name="jpqlMethodNode"
						select="annotations/annotation[@name='clear.cdb.js.annotations.CX_JSJPQLMethod']" />
					<xsl:variable name="updateInfo"
						select="annotations/annotation[@name='clear.cdb.js.annotations.CX_UpdateInfo'] | $jpqlMethodNode/method[@name='updateInfo']/value/annotation" />

					<xsl:variable name="autoSyncEnabled"
						select="boolean($updateInfo/method[@name='autoSyncEnabled']/@value = 'true')" />

					<xsl:variable name="javaFillMethod"
						select="helper:getMethodAnnotation($interfaceName, @name, 'clear.cdb.js.annotations.CX_JSFillMethod')" />
					<xsl:variable name="javaAutoSyncEnabled"
						select="boolean($javaFillMethod/method[@name='autoSyncEnabled']/@value = 'true')" />

					<xsl:if test="$autoSyncEnabled or $javaAutoSyncEnabled">
						<xsl:variable name="methodName">
							<xsl:value-of select="@name" />
						</xsl:variable>
						<destination id="{concat($interfaceName, '.', $methodName)}">
							<properties>
								<server>
									<allow-subtopics>true</allow-subtopics>
								</server>
							</properties>
						</destination>
					</xsl:if>
				</xsl:for-each>
			</xsl:for-each>
		</service>
	</xsl:template>
</xsl:stylesheet>