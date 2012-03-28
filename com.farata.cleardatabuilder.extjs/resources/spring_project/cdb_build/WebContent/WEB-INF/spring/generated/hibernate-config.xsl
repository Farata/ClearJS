<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pers="http://java.sun.com/xml/ns/persistence"
	xmlns:xalan="http://xml.apache.org/xslt" xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:tx="http://www.springframework.org/schema/tx"
	xmlns:context="http://www.springframework.org/schema/context"
	exclude-result-prefixes="pers xalan">
	<xsl:output method="xml" indent="yes" xalan:indent-amount="4" />

	<xsl:template match="/">
		<beans
			xsi:schemaLocation="http://www.springframework.org/schema/beans	http://www.springframework.org/schema/beans/spring-beans-2.5.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd">
			<tx:jta-transaction-manager />
			<context:annotation-config />
			<context:component-scan base-package="." />
			<bean id="sessionFactory"
				class="org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean">
				<xsl:variable name="unitName"
					select="/pers:persistence/pers:persistence-unit/@name" />
				<property name="dataSource" ref="{$unitName}DataSource" />
				<property name="jtaTransactionManager">
					<bean factory-bean="transactionManager" factory-method="getTransactionManager" />
				</property>
				<property name="annotatedClasses">
					<list>
						<xsl:for-each select="/pers:persistence/pers:persistence-unit/pers:class">
							<value>
								<xsl:value-of select="text()" />
							</value>
						</xsl:for-each>
					</list>
				</property>
				<property name="hibernateProperties">
					<props>
						<xsl:variable name="prop3"
							select="/pers:persistence/pers:persistence-unit/pers:properties/pers:property[@name='current_session_context_class']" />
						<xsl:if test="not($prop3)">
							<prop key="current_session_context_class">jta</prop>
						</xsl:if>
						<xsl:variable name="prop6"
							select="/pers:persistence/pers:persistence-unit/pers:properties/pers:property[@name='cache.provider_class']" />
						<xsl:if test="not($prop6)">
							<prop key="cache.provider_class">org.hibernate.cache.NoCacheProvider</prop>
						</xsl:if>
						<xsl:variable name="prop7"
							select="/pers:persistence/pers:persistence-unit/pers:properties/pers:property[@name='hibernate.show_sql']" />
						<xsl:if test="not($prop7)">
							<prop key="hibernate.show_sql">true</prop>
						</xsl:if>
						<xsl:for-each
							select="/pers:persistence/pers:persistence-unit/pers:properties/pers:property">
							<prop key="{@name}">
								<xsl:value-of select="@value"></xsl:value-of>
							</prop>
						</xsl:for-each>
					</props>
				</property>
			</bean>
			<bean id="hibernateTemplate" class="org.springframework.orm.hibernate3.HibernateTemplate">
				<constructor-arg ref="sessionFactory" />
			</bean>
		    <bean class="clear.cdb.utils.SessionFactoryUtils" factory-method="setSessionFactory" depends-on="sessionFactory">
		    	<constructor-arg ref="sessionFactory"/>
		    </bean>
		</beans>
	</xsl:template>
</xsl:stylesheet>