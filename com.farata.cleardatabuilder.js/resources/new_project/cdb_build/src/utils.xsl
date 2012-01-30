<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	extension-element-prefixes="redirect" exclude-result-prefixes="xalan redirect">

	<xsl:output omit-xml-declaration="yes" method="text" indent="yes"/>

	<xsl:template name="copyDto" match="/|/">
		<xsl:param name="fromName" />
		<xsl:param name="fromType" />
		<xsl:param name="toName" />
		<xsl:param name="toType" />
		<xsl:param name="indent" />
		<xsl:param name="fillChildren" />
		<xsl:variable name="fromTypeIsEntity"
			select="boolean(helper:typeAnnotatedWith($fromType, 'javax.persistence.Entity'))" />
		<xsl:variable name="toTypeIsEntity"
			select="boolean(helper:typeAnnotatedWith($toType, 'javax.persistence.Entity'))" />
		<xsl:variable name="fromBeanProps" select="helper:getBeanProperties($fromType)"/>
		<xsl:variable name="toBeanProps" select="helper:getBeanProperties($toType)"/>
			<xsl:for-each select="$toBeanProps/property">
				<xsl:variable name="toBeanPropName" select="@name"/>
				<xsl:variable name="fromBeanProp" select="$fromBeanProps/property[@name=$toBeanPropName]"/>
				<xsl:if test="$fromBeanProp and $fromBeanProp/@name!='uid'">
					<xsl:choose>
						<xsl:when test="$fromTypeIsEntity and $toTypeIsEntity">
							<xsl:variable name="propIsEntity" select="helper:typeAnnotatedWith(@type, 'javax.persistence.Entity')"/>
							<xsl:variable name="propIsOneToMany" select="helper:getBeanPropertyAnnotation($toType, $toBeanPropName, 'javax.persistence.OneToMany')/exists"/>
<xsl:value-of select="'&#10;'"/>							
							<xsl:choose>
								<xsl:when test="$propIsEntity">
									<xsl:variable name="idProp" select="helper:getEntityIdBeanProperty(@type)"/>
<xsl:value-of select="$indent"/><xsl:value-of select="@type"/> _<xsl:value-of select="$toBeanPropName"/> = <xsl:value-of select="$fromName"/>.<xsl:value-of select="@readMethod"/>();<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="$indent"/>if (_<xsl:value-of select="$toBeanPropName"/> != null &amp;&amp; _<xsl:value-of select="$toBeanPropName"/>.<xsl:value-of select="$idProp/@readMethod"/>() != null) {<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="$indent"/>	_<xsl:value-of select="$toBeanPropName"/> = (<xsl:value-of select="@type"/>)session.get(<xsl:value-of select="@type"/>.class, _<xsl:value-of select="$toBeanPropName"/>.<xsl:value-of select="$idProp/@readMethod"/>());<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="concat($indent, '&#09;')"/><xsl:value-of select="$toName"/>.<xsl:value-of select="@writeMethod"/>(_<xsl:value-of select="$toBeanPropName"/>);<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="$indent"/>}<xsl:value-of select="'&#10;'"/>
								</xsl:when>
								<xsl:when test="$propIsOneToMany">
									<xsl:if test="$fillChildren">
									<xsl:call-template name="copyOneToMany">
										<xsl:with-param name="fromName" select="$fromName"/>
										<xsl:with-param name="toName" select="$toName"/>
										<xsl:with-param name="readMethod" select="@readMethod"/>
										<xsl:with-param name="writeMethod" select="@writeMethod"/>
										<xsl:with-param name="fromPropName" select="$fromBeanProp/@name"/>
										<xsl:with-param name="fromPropType" select="$fromBeanProp/@type"/>
										<xsl:with-param name="toPropName" select="$toBeanPropName"/>
										<xsl:with-param name="toPropType" select="@type"/>
										<xsl:with-param name="indent" select="$indent"/>
									</xsl:call-template>
									</xsl:if>
								</xsl:when>
								<xsl:otherwise>
<xsl:value-of select="$indent"/><xsl:value-of select="$toName"/>.<xsl:value-of select="@writeMethod"/>(<xsl:value-of select="$fromName"/>.<xsl:value-of select="@readMethod"/>());<xsl:value-of select="'&#10;'"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>

						<xsl:when test="$fromTypeIsEntity and not($toTypeIsEntity)">
							<xsl:variable name="propIsEntity" select="helper:typeAnnotatedWith(@type, 'javax.persistence.Entity')"/>
							<xsl:variable name="entityByDTO" select="helper:getEntityByDTO(helper:genDTOtoDTO(@type))"/>
							<xsl:variable name="propIsOneToMany" select="helper:getBeanPropertyAnnotation($fromType, $toBeanPropName, 'javax.persistence.OneToMany')/exists"/>
							<xsl:variable name="propType" select="@type"/>
<xsl:value-of select="'&#10;'"/>							
							<xsl:choose>
								<xsl:when test="$propIsEntity">
									<xsl:variable name="idProp" select="helper:getEntityIdBeanProperty(@type)"/>
<xsl:value-of select="$indent"/><xsl:value-of select="@type"/> _<xsl:value-of select="$toBeanPropName"/> = <xsl:value-of select="$fromName"/>.<xsl:value-of select="@readMethod"/>();<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="$indent"/>if (_<xsl:value-of select="$toBeanPropName"/> != null &amp;&amp; _<xsl:value-of select="$toBeanPropName"/>.<xsl:value-of select="$idProp/@readMethod"/>() != null) {<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="$indent"/>	_<xsl:value-of select="$toBeanPropName"/> = (<xsl:value-of select="@type"/>)session.get(<xsl:value-of select="@type"/>.class, _<xsl:value-of select="$toBeanPropName"/>.<xsl:value-of select="$idProp/@readMethod"/>());<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="concat($indent, '&#09;')"/><xsl:value-of select="$toName"/>.<xsl:value-of select="@writeMethod"/>(_<xsl:value-of select="$toBeanPropName"/>);<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="$indent"/>}<xsl:value-of select="'&#10;'"/>
								</xsl:when>
								<xsl:when test="$propIsOneToMany">
									<xsl:if test="$fillChildren">
									<xsl:call-template name="copyOneToMany">
										<xsl:with-param name="fromName" select="$fromName"/>
										<xsl:with-param name="toName" select="$toName"/>
										<xsl:with-param name="readMethod" select="@readMethod"/>
										<xsl:with-param name="writeMethod" select="@writeMethod"/>
										<xsl:with-param name="fromPropName" select="$fromBeanProp/@name"/>
										<xsl:with-param name="fromPropType" select="$fromBeanProp/@type"/>
										<xsl:with-param name="toPropName" select="$toBeanPropName"/>
										<xsl:with-param name="toPropType" select="@type"/>
										<xsl:with-param name="indent" select="$indent"/>
									</xsl:call-template>
									</xsl:if>
								</xsl:when>
								<xsl:when test="$entityByDTO">
<xsl:value-of select="$indent"/><xsl:value-of select="$entityByDTO"/> _<xsl:value-of select="$toBeanPropName"/>Entity = <xsl:value-of select="$fromName"/>.<xsl:value-of select="@readMethod"/>();<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="$indent"/>if (_<xsl:value-of select="$toBeanPropName"/>Entity != null) {
<xsl:value-of select="concat($indent, '&#09;')"/><xsl:value-of select="@type"/> _<xsl:value-of select="$toBeanPropName"/>Dto = <xsl:value-of select="$toName"/>.<xsl:value-of select="@readMethod"/>();<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="concat($indent, '&#09;')"/>if (_<xsl:value-of select="$toBeanPropName"/>Dto == null) {
<xsl:value-of select="concat($indent, '&#09;', '&#09;')"/>_<xsl:value-of select="$toBeanPropName"/>Dto = new <xsl:value-of select="@type"/>();
<xsl:value-of select="concat($indent, '&#09;')"/>}<xsl:value-of select="'&#10;'"/>
								<xsl:variable name="beanProperties" select="helper:getBeanProperties($entityByDTO)"/>
								<xsl:for-each select="$beanProperties/property">
									<xsl:if test="not(@name='uid')">
										<xsl:variable name="oneToMany" select="helper:getBeanPropertyAnnotation($entityByDTO, @name, 'javax.persistence.OneToMany')"/>
										<xsl:variable name="manyToOne" select="helper:getBeanPropertyAnnotation($entityByDTO, @name, 'javax.persistence.ManyToOne')"/>
										<xsl:variable name="beanPropertyType" select="helper:getBeanPropertyType($propType, @name)"/>
										<xsl:if test="not($oneToMany/exists or $manyToOne/exists) and $beanPropertyType">			
<xsl:value-of select="concat($indent, '&#09;')"/>_<xsl:value-of select="$toBeanPropName"/>Dto.set<xsl:value-of select="helper:capitalizeString(@name)"/>(_<xsl:value-of select="$toBeanPropName"/>Entity.get<xsl:value-of select="helper:capitalizeString(@name)"/>());<xsl:value-of select="'&#10;'"/>
										</xsl:if>
									</xsl:if>
								</xsl:for-each>

<xsl:value-of select="concat($indent, '&#09;')"/><xsl:value-of select="$toName"/>.<xsl:value-of select="@writeMethod"/>(_<xsl:value-of select="$toBeanPropName"/>Dto);<xsl:value-of select="'&#10;'"/>	
<xsl:value-of select="$indent"/>}									
								</xsl:when>
								<xsl:otherwise>
<xsl:value-of select="$indent"/><xsl:value-of select="$toName"/>.<xsl:value-of select="@writeMethod"/>(<xsl:value-of select="$fromName"/>.<xsl:value-of select="@readMethod"/>());<xsl:value-of select="'&#10;'"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>

						<xsl:when test="not($fromTypeIsEntity) and $toTypeIsEntity">
							<xsl:variable name="propIsEntity" select="helper:typeAnnotatedWith(@type, 'javax.persistence.Entity')"/>
							<xsl:variable name="propIsOneToMany" select="helper:getBeanPropertyAnnotation($toType, $toBeanPropName, 'javax.persistence.OneToMany')/exists"/>
<xsl:value-of select="'&#10;'"/>							
							<xsl:choose>
								<xsl:when test="$propIsEntity">
									<xsl:variable name="toEntityMethod" select="helper:getMethods($fromBeanProp/@type)/method[@name='toEntity']"/>
									<xsl:variable name="idProp" select="helper:getEntityIdBeanProperty($fromBeanProp/@type)"/>
									<xsl:choose>
										<xsl:when test="$toEntityMethod">
<xsl:value-of select="$indent"/><xsl:value-of select="$fromBeanProp/@type"/> _<xsl:value-of select="$toBeanPropName"/>Dto = <xsl:value-of select="$fromName"/>.<xsl:value-of select="@readMethod"/>();<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="$indent"/>if (_<xsl:value-of select="$toBeanPropName"/>Dto != null) {<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="concat($indent, '&#09;')"/><xsl:value-of select="@type"/> _<xsl:value-of select="$toBeanPropName"/> = _<xsl:value-of select="$toBeanPropName"/>Dto.toEntity();<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="concat($indent, '&#09;')"/>if (_<xsl:value-of select="$toBeanPropName"/> != null) {<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="concat($indent, '&#09;', '&#09;')"/><xsl:value-of select="$toName"/>.<xsl:value-of select="@writeMethod"/>(_<xsl:value-of select="$toBeanPropName"/>);<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="concat($indent, '&#09;')"/>}<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="$indent"/>}<xsl:value-of select="'&#10;'"/>
										</xsl:when>
										<xsl:when test="$idProp">
<xsl:value-of select="$indent"/><xsl:value-of select="@type"/> _<xsl:value-of select="$toBeanPropName"/> = <xsl:value-of select="$fromName"/>.<xsl:value-of select="@readMethod"/>();<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="$indent"/>if (_<xsl:value-of select="$toBeanPropName"/> != null &amp;&amp; _<xsl:value-of select="$toBeanPropName"/>.<xsl:value-of select="$idProp/@readMethod"/>() != null) {<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="$indent"/>	_<xsl:value-of select="$toBeanPropName"/> = (<xsl:value-of select="@type"/>)session.get(<xsl:value-of select="@type"/>.class, _<xsl:value-of select="$toBeanPropName"/>.<xsl:value-of select="$idProp/@readMethod"/>());<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="concat($indent, '&#09;')"/><xsl:value-of select="$toName"/>.<xsl:value-of select="@writeMethod"/>(_<xsl:value-of select="$toBeanPropName"/>);<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="$indent"/>}<xsl:value-of select="'&#10;'"/>
										</xsl:when>
									</xsl:choose>
								</xsl:when>
								<xsl:when test="$propIsOneToMany">
									<xsl:if test="$fillChildren">
									<xsl:call-template name="copyOneToMany">
										<xsl:with-param name="fromName" select="$fromName"/>
										<xsl:with-param name="toName" select="$toName"/>
										<xsl:with-param name="readMethod" select="@readMethod"/>
										<xsl:with-param name="writeMethod" select="@writeMethod"/>
										<xsl:with-param name="fromPropName" select="$fromBeanProp/@name"/>
										<xsl:with-param name="fromPropType" select="$fromBeanProp/@type"/>
										<xsl:with-param name="toPropName" select="$toBeanPropName"/>
										<xsl:with-param name="toPropType" select="@type"/>
										<xsl:with-param name="indent" select="$indent"/>
									</xsl:call-template>
									</xsl:if>
								</xsl:when>
								<xsl:otherwise>
<xsl:value-of select="$indent"/><xsl:value-of select="$toName"/>.<xsl:value-of select="@writeMethod"/>(<xsl:value-of select="$fromName"/>.<xsl:value-of select="@readMethod"/>());<xsl:value-of select="'&#10;'"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						
						<xsl:when test="not($fromTypeIsEntity) and not($toTypeIsEntity)">
						</xsl:when>
					</xsl:choose>
				</xsl:if>
			</xsl:for-each>
	</xsl:template>


	<xsl:template name="copyOneToMany">
	<xsl:param name="toPropType"/>
	<xsl:param name="fromPropType"/>
	<xsl:param name="toPropName"/>
	<xsl:param name="fromPropName"/>
	<xsl:param name="fromName"/>
	<xsl:param name="toName"/>
	<xsl:param name="readMethod"/>
	<xsl:param name="writeMethod"/>
	<xsl:param name="indent"/>
									<xsl:variable name="toCollectionType" select="helper:getTypeParameter($toPropType)"/>
									<xsl:variable name="fromCollectionType" select="helper:getTypeParameter($fromPropType)"/>
<xsl:value-of select="$indent"/>java.util.ArrayList&lt;<xsl:value-of select="$toCollectionType"/>&gt; _<xsl:value-of select="$toPropName"/>Dtos = new java.util.ArrayList&lt;<xsl:value-of select="$toCollectionType"/>&gt;();<xsl:value-of select="'&#10;'"/>								
<xsl:value-of select="$indent"/><xsl:value-of select="$fromPropType"/> _<xsl:value-of select="$toPropName"/> = <xsl:value-of select="$fromName"/>.<xsl:value-of select="$readMethod"/>();<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="$indent"/>for (<xsl:value-of select="$fromCollectionType"/> _<xsl:value-of select="$fromPropName"/>Entry : _<xsl:value-of select="$fromPropName"/>) {<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="concat($indent, '&#09;')"/><xsl:value-of select="$toCollectionType"/> _<xsl:value-of select="$toPropName"/>Dto = new <xsl:value-of select="$toCollectionType"/>();<xsl:value-of select="'&#10;'"/>
									<xsl:call-template name="copyDto">
										<xsl:with-param name="fromName" select="concat('_', $fromPropName, 'Entry')"/>
										<xsl:with-param name="fromType" select="$fromCollectionType"/>
										<xsl:with-param name="toName" select="concat('_', $toPropName, 'Dto')"/>
										<xsl:with-param name="toType" select="$toCollectionType"/>
										<xsl:with-param name="indent" select="'&#09;&#09;&#09;&#09;'"/>
										<xsl:with-param name="fillChildren" select="boolean('false')"/>
									</xsl:call-template>
<xsl:value-of select="concat($indent, '&#09;')"/>_<xsl:value-of select="$toPropName"/>Dtos.add(_<xsl:value-of select="$toPropName"/>Dto);<xsl:value-of select="'&#10;'"/>									
<xsl:value-of select="$indent"/>}<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="$indent"/><xsl:value-of select="$toName"/>.<xsl:value-of select="$writeMethod"/>(_<xsl:value-of select="$toPropName"/>Dtos);<xsl:value-of select="'&#10;'"/>
	</xsl:template>
</xsl:stylesheet>