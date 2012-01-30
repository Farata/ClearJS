<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	exclude-result-prefixes="xalan helper"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	<xsl:output method="xml" indent="yes" xalan:indent-amount="4" />

	<xsl:template match="/">
		<xsl:text>&#10;</xsl:text>
		<service id="remoting-service" class="flex.messaging.services.RemotingService">
			<adapters>
				<adapter-definition
					class="clear.messaging.services.remoting.adapters.JavaAdapter"
					default="true" id="java-object" />
			</adapters>

			<default-channels>
				<channel ref="my-amf" />
			</default-channels>

			<destination id="batchGateway">
				<properties>
					<source>clear.transaction.BatchGateway</source>
				</properties>
			</destination>
		</service>
	</xsl:template>
</xsl:stylesheet>