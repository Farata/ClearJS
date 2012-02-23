<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" 
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	exclude-result-prefixes="xalan">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/|/|/" name="service-dto-get.xsl">
	<xsl:param name="dtoName"/>
	<xsl:param name="rootPackage"/>
	<xsl:param name="interfaceName"/>
	<xsl:param name="methodName"/>package <xsl:value-of select="$rootPackage"/>;
		<xsl:variable name="methodNode" select="annotated-types/annotated-type[@name=$interfaceName]/methods/method[@name=$methodName]"/>
		<xsl:variable name="getMethodNode" select="$methodNode/annotations/annotation[@name='clear.cdb.js.annotations.CX_JSGetMethod']"/>
		<xsl:variable name="annotatedType" select="annotated-types/annotated-type[@name=$interfaceName][1]"/>
		<xsl:variable name="transferInfo" select="$getMethodNode/method[name='transferInfo']/value"/>
		<xsl:variable name="mappedEntity" select="helper:genDTOtoEntity(concat($rootPackage, '.', $dtoName))"/>
		<xsl:variable name="beanProperties" select="helper:getBeanProperties($mappedEntity)"/>
		<xsl:variable name="mappedEntityIdPropNames">
			<xsl:for-each select="$beanProperties/property">
				<xsl:variable name="mappedEntityId" select="helper:getBeanPropertyAnnotation($mappedEntity, @name, 'javax.persistence.Id')"/>
				<xsl:variable name="mappedEntityEmbId" select="helper:getBeanPropertyAnnotation($mappedEntity, @name, 'javax.persistence.EmbeddedId')"/>
				<xsl:if test="$mappedEntityId/exists or $mappedEntityEmbId/exists">
					<xsl:value-of select="concat(@name, ',')"/>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="keyPropertyNames" select="helper:split($mappedEntityIdPropNames, ',')"/>
import com.farata.dto2extjs.annotations.*;

import clear.cdb.utils.SessionFactoryUtils;
import clear.data.IUID;
import flex.messaging.util.UUIDUtils;

import java.io.Serializable;
import java.util.*;

import org.hibernate.Session;

@JSClass(kind=JSClassKind.EXT_JS, ignoreSuperclasses = { IUID.class })
public class <xsl:value-of select="$dtoName"/>  implements Serializable, IUID{

	private static final long serialVersionUID = 1L;
	
	private String uid;
	@JSIgnore
	public String getUid() {
		if (uid == null) {
			<xsl:choose>
			<xsl:when test="count($keyPropertyNames/element) > 0">uid = "" + <xsl:for-each select="$keyPropertyNames"><xsl:value-of select="element/@value"/><xsl:if test="not(position()=last())">+"|"</xsl:if></xsl:for-each>;</xsl:when>
			<xsl:otherwise>uid = UUIDUtils.createUUID(false);</xsl:otherwise>
		</xsl:choose>
		}
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	<xsl:variable name="mappedEntity" select="helper:genDTOtoEntity(concat($rootPackage, '.', $dtoName))"/>
	<xsl:for-each select="$beanProperties/property">
		<xsl:if test="not(@name='uid')">
	protected <xsl:value-of select="helper:replaceEntitiesWithGenDTOs(@type)"/> <xsl:value-of select="concat(' ',@name)"/>;
		<xsl:variable name="oneToMany" select="helper:getBeanPropertyAnnotation($mappedEntity, @name, 'javax.persistence.OneToMany')"/>
		<xsl:variable name="manyToOne" select="helper:getBeanPropertyAnnotation($mappedEntity, @name, 'javax.persistence.ManyToOne')"/>
		<xsl:variable name="propType" select="@type"/>
		<xsl:choose>
			<xsl:when test="$oneToMany/exists">
	@JSOneToMany
			</xsl:when>
			<xsl:when test="$manyToOne/method[@name = 'targetEntity']">
				<xsl:variable name="refDtoProps" select="helper:getBeanProperties($propType)"/>
				<xsl:variable name="keyColumn">
					<xsl:for-each select="$refDtoProps/property">
						<xsl:variable name="fxKeyColumn" select="helper:getBeanPropertyAnnotation($propType, @name, 'javax.persistence.Id')"/>
						<xsl:variable name="fxEmbKeyColumn" select="helper:getBeanPropertyAnnotation($propType, @name, 'javax.persistence.EmbeddedId')"/>
						<xsl:if test="$fxKeyColumn/exists or $fxEmbKeyColumn/exists">
							<xsl:value-of select="@name"/>
						</xsl:if>
					</xsl:for-each>
				</xsl:variable>
	@JSManyToOne(parent = "<xsl:value-of select="helper:entityToGenDTO(@type)"/>", property = "<xsl:value-of select="helper:entityToGenDTO($keyColumn)"/>")			
			</xsl:when>
		</xsl:choose>
	public <xsl:value-of select="helper:replaceEntitiesWithGenDTOs(@type)"/> get<xsl:value-of select="helper:capitalizeString(@name)"/>() {
		return <xsl:value-of select="@name"/>;
	}
	public void set<xsl:value-of select="helper:capitalizeString(@name)"/>(<xsl:value-of select="helper:replaceEntitiesWithGenDTOs(@type)"/> value) {
		<xsl:value-of select="@name"/> = value;
	}
		</xsl:if>
	</xsl:for-each>
	<xsl:if test="$mappedEntity">
	public <xsl:value-of select="$mappedEntity"/> toEntity() {	
		Session session = SessionFactoryUtils.getCurrentSession();
		<xsl:variable name="refDtoProps" select="helper:getBeanProperties($mappedEntity)"/>
			<xsl:variable name="keyColumn">
			<xsl:for-each select="$refDtoProps/property">
				<xsl:variable name="fxKeyColumn" select="helper:getBeanPropertyAnnotation($mappedEntity, @name, 'javax.persistence.Id')"/>
				<xsl:variable name="embKeyColumn" select="helper:getBeanPropertyAnnotation($mappedEntity, @name, 'javax.persistence.EmbeddedId')"/>
				
				<xsl:if test="$fxKeyColumn/exists or $embKeyColumn/exists">
					<xsl:value-of select="@name"/>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		if (<xsl:value-of select="$keyColumn"/> != null) {
			<xsl:value-of select="$mappedEntity"/> entity = (<xsl:value-of select="$mappedEntity"/>) session.get(<xsl:value-of select="$mappedEntity"/>.class, <xsl:value-of select="$keyColumn"/>);
			return entity;
		} else {
			return null;
		}
	}
	</xsl:if>
}
	</xsl:template>
</xsl:stylesheet>