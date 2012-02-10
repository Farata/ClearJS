<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" 
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	exclude-result-prefixes="xalan">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/|/|/" name="generate-model-jpql.xsl">
	<xsl:param name="dtoName"/>
	<xsl:param name="rootPackage"/>
	<xsl:param name="interfaceName"/>
	<xsl:param name="methodName"/>
		<xsl:variable name="methodNode" select="annotated-types/annotated-type[@name=$interfaceName]/methods/method[@name=$methodName]"/>
		<xsl:variable name="jpqlMethodNode" select="$methodNode/annotations/annotation[@name='clear.cdb.annotations.CX_JPQLMethod']"/>
		<xsl:variable name="types" select="$jpqlMethodNode/method[@name='query']/query"/>
		<xsl:variable name="updateInfo" select="$methodNode/annotations/annotation[@name='clear.cdb.annotations.CX_UpdateInfo'] | $jpqlMethodNode/method[@name='updateInfo']/value/annotation"/>
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
<xsl:text/>Ext.define('app.model.<xsl:value-of select="$rootPackage"/>.<xsl:value-of select="$dtoName"/>',{
	extend:'Ext.data.Model',
	fields:[
		 'uid'<xsl:text/>
		<xsl:variable name="mappedEntity" select="helper:genDTOtoEntity(concat($rootPackage, '.', $dtoName))"/>
		<xsl:for-each select="$types/types/type">
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
		,'<xsl:value-of select="@alias"/>'<xsl:text/>
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
	]
});
	
	</xsl:template>
</xsl:stylesheet>