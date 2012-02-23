<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" 
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	exclude-result-prefixes="xalan">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/|/|/" name="service-dto-jpql.xsl">
	<xsl:param name="dtoName"/>
	<xsl:param name="rootPackage"/>
	<xsl:param name="interfaceName"/>
	<xsl:param name="methodName"/>package <xsl:value-of select="$rootPackage"/>;
		<xsl:variable name="methodNode" select="annotated-types/annotated-type[@name=$interfaceName]/methods/method[@name=$methodName]"/>
		<xsl:variable name="jpqlMethodNode" select="$methodNode/annotations/annotation[@name='clear.cdb.js.annotations.CX_JSJPQLMethod']"/>
		<xsl:variable name="types" select="$jpqlMethodNode/method[@name='query']/query"/>
		<xsl:variable name="updateInfo" select="$methodNode/annotations/annotation[@name='clear.cdb.js.annotations.CX_UpdateInfo'] | $jpqlMethodNode/method[@name='updateInfo']/value/annotation"/>
		<xsl:variable name="updateEntity" select="helper:replaceAll($updateInfo/method[@name='updateEntity']/@value, 'class ', '')"/>
		<xsl:variable name="annotatedType" select="annotated-types/annotated-type[@name=$interfaceName][1]"/>
		
		<xsl:variable name="updateEntityIdPropNames">
			<xsl:variable name="keyPropsValue" select="$updateInfo/method[@name='keyPropertyNames']/@value"/>
			<xsl:choose>
				<xsl:when test="(not($keyPropsValue) or $keyPropsValue='') and $updateEntity">
					<xsl:variable name="updateEntityProps" select="helper:getBeanProperties($updateEntity)"/>
					<xsl:for-each select="$updateEntityProps/property">
						<xsl:variable name="updateEntityId" select="helper:getBeanPropertyAnnotation($updateEntity, @name, 'javax.persistence.Id')"/>
						<xsl:variable name="updateEntityEmbId" select="helper:getBeanPropertyAnnotation($updateEntity, @name, 'javax.persistence.EmbeddedId')"/>
						<xsl:if test="$updateEntityId/exists or $updateEntityEmbId/exists">
							<xsl:value-of select="concat(@name, ',')"/>
						</xsl:if>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$keyPropsValue"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="keyPropertyNames" select="helper:split($updateEntityIdPropNames, ',')"/>
import com.farata.dto2extjs.annotations.*;

import clear.cdb.utils.SessionFactoryUtils;
import clear.data.IUID;

import java.io.Serializable;
import java.util.*;

import org.hibernate.Session;

@JSClass(kind=JSClassKind.EXT_JS, ignoreSuperclasses = { IUID.class })
public class <xsl:value-of select="$dtoName"/>  implements Serializable, IUID{

	private static final long serialVersionUID = 1L;
	
	private String uid;
	
	public String getUid() {
		if (uid == null) {
			<xsl:choose>
			<xsl:when test="count($keyPropertyNames/element) > 0">uid = "" + <xsl:for-each select="$keyPropertyNames"><xsl:value-of select="element/@value"/><xsl:if test="not(position()=last())">+"|"</xsl:if></xsl:for-each>;</xsl:when>
			<xsl:otherwise>uid = flex.messaging.util.UUIDUtils.createUUID(false);</xsl:otherwise>
		</xsl:choose>
		}
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
		<xsl:variable name="mappedEntity" select="helper:genDTOtoEntity(concat($rootPackage, '.', $dtoName))"/>
		<xsl:for-each select="$types/types/type">
	protected <xsl:value-of select="helper:entityToGenDTO(@name)"/> <xsl:value-of select="concat(' ',@alias)"/>;
<xsl:variable name="alias" select="@alias"/>
<xsl:variable name="isKey" select="$keyPropertyNames/element[@value=$alias]"/>
	
	<xsl:if test="$mappedEntity">
		<xsl:variable name="mappedEntityProps" select="helper:getBeanProperties($mappedEntity)"/>
		<xsl:variable name="propName" select="@alias"/>

		<xsl:variable name="propNode" select="$mappedEntityProps/property[@name=$propName]/@name"/>
		<xsl:if test="$propNode">
			<xsl:variable name="manyToOne" select="helper:getBeanPropertyAnnotation($mappedEntity, $propName, 'javax.persistence.ManyToOne')"/>
			<xsl:variable name="propType" select="@name"/>
			<xsl:if test="$manyToOne/method[@name = 'targetEntity']">
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
		
			</xsl:if>
		</xsl:if>
	</xsl:if>	
	public <xsl:value-of select="helper:entityToGenDTO(@name)"/> get<xsl:value-of select="helper:capitalizeString(@alias)"/>() {
		return <xsl:value-of select="@alias"/>;
	}
	public void set<xsl:value-of select="helper:capitalizeString(@alias)"/>(<xsl:value-of select="helper:entityToGenDTO(@name)"/> value) {
		<xsl:value-of select="@alias"/> = value;
	}
		</xsl:for-each>
		<xsl:if test="$mappedEntity">
			<xsl:variable name="mappedEntityProps" select="helper:getBeanProperties($mappedEntity)"/>
			<xsl:for-each select="$mappedEntityProps/property">
				<xsl:variable name="oneToMany" select="helper:getBeanPropertyAnnotation($mappedEntity, @name, 'javax.persistence.OneToMany')"/>
				<xsl:if test="$oneToMany/method[@name = 'targetEntity']">
	<xsl:variable name="typeName" select="helper:replaceEntitiesWithGenDTOs(@type)"/>
	protected <xsl:value-of select="$typeName"/> <xsl:value-of select="concat(' ', @name)"/>;
	
	public <xsl:value-of select="$typeName"/> get<xsl:value-of select="helper:capitalizeString(@name)"/>() {
		return <xsl:value-of select="@name"/>;
	}
	public void set<xsl:value-of select="helper:capitalizeString(@name)"/>(<xsl:value-of select="$typeName"/> value) {
		<xsl:value-of select="@name"/> = value;
	}
				</xsl:if>
			</xsl:for-each>
		</xsl:if>
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