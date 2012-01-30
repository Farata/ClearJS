<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pers="http://java.sun.com/xml/ns/persistence"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="pers xalan">
	<xsl:output method="xml" indent="yes" xalan:indent-amount="4" />

	<xsl:template match="/">
		<xsl:text disable-output-escaping="yes"><![CDATA[
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">
]]></xsl:text>

		<hibernate-configuration>
			<session-factory>
				<property name="connection.datasource">
					<xsl:value-of
						select="/pers:persistence/pers:persistence-unit/pers:jta-data-source" />
				</property>
				<xsl:variable name="prop1"
					select="/pers:persistence/pers:persistence-unit/pers:properties/pers:property[@name='transaction.auto_close_session']" />
				<xsl:if test="not($prop1)">
					<property name="transaction.auto_close_session">true</property>
				</xsl:if>
				<xsl:variable name="prop2"
					select="/pers:persistence/pers:persistence-unit/pers:properties/pers:property[@name='connection.pool_size']" />
				<xsl:if test="not($prop2)">
					<property name="connection.pool_size">1</property>
				</xsl:if>
				<xsl:variable name="prop3"
					select="/pers:persistence/pers:persistence-unit/pers:properties/pers:property[@name='current_session_context_class']" />
				<xsl:if test="not($prop3)">
					<property name="current_session_context_class">jta</property>
				</xsl:if>
				<xsl:variable name="prop4"
					select="/pers:persistence/pers:persistence-unit/pers:properties/pers:property[@name='transaction.manager_lookup_class']" />
				<xsl:if test="not($prop4)">
					<property name="transaction.manager_lookup_class">org.hibernate.transaction.JOTMTransactionManagerLookup</property>
				</xsl:if>
				<xsl:variable name="prop5"
					select="/pers:persistence/pers:persistence-unit/pers:properties/pers:property[@name='transaction.factory_class']" />
				<xsl:if test="not($prop5)">
					<property name="transaction.factory_class">org.hibernate.transaction.JTATransactionFactory</property>
				</xsl:if>
				<xsl:variable name="prop6"
					select="/pers:persistence/pers:persistence-unit/pers:properties/pers:property[@name='cache.provider_class']" />
				<xsl:if test="not($prop6)">
					<property name="cache.provider_class">org.hibernate.cache.NoCacheProvider</property>
				</xsl:if>
				<xsl:variable name="prop7"
					select="/pers:persistence/pers:persistence-unit/pers:properties/pers:property[@name='show_sql']" />
				<xsl:if test="not($prop7)">
					<property name="show_sql">true</property>
				</xsl:if>
				<xsl:for-each
					select="/pers:persistence/pers:persistence-unit/pers:properties/pers:property">
					<property name="{@name}">
						<xsl:value-of select="@value"></xsl:value-of>
					</property>
				</xsl:for-each>
				<xsl:for-each select="/pers:persistence/pers:persistence-unit/pers:class">
					<mapping class="{text()}" />
				</xsl:for-each>
			</session-factory>
		</hibernate-configuration>
	</xsl:template>
</xsl:stylesheet>