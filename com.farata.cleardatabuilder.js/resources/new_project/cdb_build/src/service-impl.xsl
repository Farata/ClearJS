<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:include href="service-impl-jpql.xsl" />
	<xsl:include href="service-impl-fill.xsl" />
	<xsl:include href="service-impl-get.xsl" />
	<xsl:include href="service-impl-fill-children.xsl" />

	<xsl:template match="/" name="service-impl.xsl">
		<xsl:param name="springEnabled"/>
		<xsl:param name="serviceName"/>
		<xsl:param name="interfaceName"/>
		<xsl:param name="rootPackage"/>
		<xsl:variable name="methods" select="annotated-types/annotated-type[@name=$interfaceName]/methods"/>
		<xsl:variable name="annotatedType" select="annotated-types/annotated-type[@name=$interfaceName]"/>
		<xsl:value-of select="helper:clearDTOMappings()"/>
		<xsl:for-each select="annotated-types/dto-mappings/dto-mapping">
			<xsl:value-of select="helper:addDTOMapping(@dto-name, @entity-name)"/>
		</xsl:for-each>	
package <xsl:value-of select="$rootPackage"/>;

import java.util.*;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.*;

import clear.transaction.*;
import clear.transaction.identity.PropertyRack;
import clear.utils.MessagingUtils;
import clear.cdb.utils.SessionFactoryUtils;
import clear.messaging.ThreadLocals;


import flex.data.*;
import flex.messaging.messages.Message;
import flex.data.messages.DataMessage;
import com.google.gson.Gson;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;

		<xsl:choose>
			<xsl:when test="$springEnabled">
public class <xsl:value-of select="$serviceName"/> implements _I<xsl:value-of select="substring($serviceName,2) "/> {
			</xsl:when>
			<xsl:otherwise>
public class <xsl:value-of select="$serviceName"/> implements <xsl:value-of select="$interfaceName"/> {
			</xsl:otherwise>
		</xsl:choose>
		<xsl:for-each select="$methods/method">
			<xsl:variable name="methodNode" select="current()"/>
			<xsl:choose>
				<xsl:when test="annotations/annotation[@name='clear.cdb.js.annotations.CX_JSJPQLMethod']">
					<xsl:call-template name="service-impl-jpql.xsl">
						<xsl:with-param name="springEnabled" select="$springEnabled" />
						<xsl:with-param name="interfaceName" select="$interfaceName" />
						<xsl:with-param name="methodNode" select="$methodNode" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="annotations/annotation[@name='clear.cdb.js.annotations.CX_JSFillMethod']">
					<xsl:call-template name="service-impl-fill.xsl">
						<xsl:with-param name="springEnabled" select="$springEnabled" />
						<xsl:with-param name="interfaceName" select="$interfaceName" />
						<xsl:with-param name="methodNode" select="$methodNode" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="annotations/annotation[@name='clear.cdb.js.annotations.CX_JSGetMethod']">
					<xsl:call-template name="service-impl-get.xsl">
						<xsl:with-param name="springEnabled" select="$springEnabled" />
						<xsl:with-param name="interfaceName" select="$interfaceName" />
						<xsl:with-param name="methodNode" select="$methodNode" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="annotations/annotation[@name='clear.cdb.js.annotations.CX_JSFillChildrenMethod']">
					<xsl:call-template name="service-impl-fill-children.xsl">
						<xsl:with-param name="springEnabled" select="$springEnabled" />
						<xsl:with-param name="interfaceName" select="$interfaceName" />
						<xsl:with-param name="methodNode" select="$methodNode" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
	public <xsl:value-of select="$methodNode/@to-string"/> {
					<xsl:if test="helper:methodHasReturnType($interfaceName, $methodNode/@name)">
		return null;</xsl:if>
	}				
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	
		private Object deserializeObject(Map&lt;String, String&gt; map, Class clazz){
		Gson gson = new Gson();
		
		String jsonItem = gson.toJson(map);
		return gson.fromJson(jsonItem, clazz);
		
	}
}
	</xsl:template>
</xsl:stylesheet>