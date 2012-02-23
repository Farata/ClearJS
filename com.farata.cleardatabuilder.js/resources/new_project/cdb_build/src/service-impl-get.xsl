<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:import href="utils.xsl"/>
	<xsl:output omit-xml-declaration="yes" method="text"/>	

	<xsl:template match="/|/|/" name="service-impl-get.xsl">
		<xsl:param name="interfaceName"/>
		<xsl:param name="methodNode"/>
		<xsl:param name="springEnabled"/>

		<xsl:variable name="getMethod" select="helper:getMethodAnnotation($interfaceName, $methodNode/@name, 'clear.cdb.js.annotations.CX_JSGetMethod')"/>
		<xsl:variable name="sync" select="boolean($getMethod/method[@name='sync']/@value = 'true')"/>
		<xsl:variable name="transferType" select="helper:getMethodTransferType($interfaceName, $methodNode/@name)"/>
		<xsl:variable name="mappedEntity">
			<xsl:variable name="ent" select="helper:genDTOtoEntity($transferType)"/>
			<xsl:choose>
				<xsl:when test="$ent">
					<xsl:value-of select="$ent"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$transferType"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
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
		
	public <xsl:value-of select="$methodNode/@to-string"/> {
		try {
			UserTransactionManager.joinUserTransaction();
			Session session = SessionFactoryUtils.getCurrentSession();
			<xsl:value-of select="$mappedEntity"/> entity = (<xsl:value-of select="$mappedEntity"/>)session.get(<xsl:value-of select="$mappedEntity"/>.class, <xsl:value-of select="$methodNode/parameters/parameter[1]/@name"/>);
			if (entity == null) {
				return null;
			}
			<xsl:value-of select="$transferType"/> dto = new <xsl:value-of select="$transferType"/>();
			<xsl:call-template name="copyDto">
				<xsl:with-param name="fromName" select="'entity'"/>
				<xsl:with-param name="fromType" select="$mappedEntity"/>
				<xsl:with-param name="toName" select="'dto'"/>
				<xsl:with-param name="toType" select="$transferType"/>
				<xsl:with-param name="indent" select="'&#09;&#09;&#09;'"/>
				<xsl:with-param name="fillChildren" select="boolean($getMethod/method[@name='fillChildren']/@value = 'true')"/>
			</xsl:call-template>
			UserTransactionManager.commitUserTransaction();
			return dto;
		} catch (Throwable e) {
			try {
				UserTransactionManager.rollbackUserTransaction();
			} catch (Throwable th) {
				throw new RuntimeException(th);
			}
			throw new RuntimeException(e);
		}
	}
		<xsl:if test="$sync">
	public <xsl:value-of select="concat($transferType, ' ', $methodNode/@name)"/>_create(<xsl:value-of select="$transferType"/> dto) {
		try {
			UserTransactionManager.joinUserTransaction();
			Session session = SessionFactoryUtils.getCurrentSession();
			<xsl:value-of select="$mappedEntity"/> entity = new <xsl:value-of select="$mappedEntity"/>();
			{
			<xsl:call-template name="copyDto">
				<xsl:with-param name="fromName" select="'dto'"/>
				<xsl:with-param name="fromType" select="$transferType"/>
				<xsl:with-param name="toName" select="'entity'"/>
				<xsl:with-param name="toType" select="$mappedEntity"/>
				<xsl:with-param name="indent" select="'&#09;&#09;&#09;&#09;'"/>
			</xsl:call-template>
			}				
			entity = (<xsl:value-of select="$mappedEntity"/>)session.merge(entity);
			{
			<xsl:call-template name="copyDto">
				<xsl:with-param name="fromName" select="'entity'"/>
				<xsl:with-param name="fromType" select="$mappedEntity"/>
				<xsl:with-param name="toName" select="'dto'"/>
				<xsl:with-param name="toType" select="$transferType"/>
				<xsl:with-param name="indent" select="'&#09;&#09;&#09;&#09;'"/>
			</xsl:call-template>
			}				
			UserTransactionManager.commitUserTransaction();
		} catch (Throwable e) {
			try {
				UserTransactionManager.rollbackUserTransaction();
			} catch (Throwable th) {
				throw new RuntimeException(th);
			}
			throw new RuntimeException(e);
		}
		return dto;
	}

	public <xsl:value-of select="concat($transferType, ' ', $methodNode/@name)"/>_update(<xsl:value-of select="$transferType"/> dto) {
		try {
			UserTransactionManager.joinUserTransaction();
			Session session = SessionFactoryUtils.getCurrentSession();
			<xsl:value-of select="$mappedEntity"/> entity = (<xsl:value-of select="$mappedEntity"/>)session.get(<xsl:value-of select="$mappedEntity"/>.class, dto.get<xsl:value-of select="helper:capitalizeString($keyPropertyNames[1]/element/@value)"/>());
			{
			<xsl:call-template name="copyDto">
				<xsl:with-param name="fromName" select="'dto'"/>
				<xsl:with-param name="fromType" select="$transferType"/>
				<xsl:with-param name="toName" select="'entity'"/>
				<xsl:with-param name="toType" select="$mappedEntity"/>
				<xsl:with-param name="indent" select="'&#09;&#09;&#09;&#09;'"/>
			</xsl:call-template>
			}				
			entity = (<xsl:value-of select="$mappedEntity"/>)session.merge(entity);
			{
			<xsl:call-template name="copyDto">
				<xsl:with-param name="fromName" select="'entity'"/>
				<xsl:with-param name="fromType" select="$mappedEntity"/>
				<xsl:with-param name="toName" select="'dto'"/>
				<xsl:with-param name="toType" select="$transferType"/>
				<xsl:with-param name="indent" select="'&#09;&#09;&#09;&#09;'"/>
			</xsl:call-template>
			}				
			UserTransactionManager.commitUserTransaction();
		} catch (Throwable e) {
			try {
				UserTransactionManager.rollbackUserTransaction();
			} catch (Throwable th) {
				throw new RuntimeException(th);
			}
			throw new RuntimeException(e);
		}
		return dto;
	}
	
	public <xsl:value-of select="concat($transferType, ' ', $methodNode/@name)"/>_delete(<xsl:value-of select="$transferType"/> dto) {
		try {
			UserTransactionManager.joinUserTransaction();
			Session session = SessionFactoryUtils.getCurrentSession();
			<xsl:value-of select="$mappedEntity"/> entity = (<xsl:value-of select="$mappedEntity"/>)session.get(<xsl:value-of select="$mappedEntity"/>.class, dto.get<xsl:value-of select="helper:capitalizeString($keyPropertyNames[1]/element/@value)"/>());
			session.delete(entity);
			UserTransactionManager.commitUserTransaction();
		} catch (Throwable e) {
			try {
				UserTransactionManager.rollbackUserTransaction();
			} catch (Throwable th) {
				throw new RuntimeException(th);
			}
			throw new RuntimeException(e);
		}
		return dto;
	}
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>