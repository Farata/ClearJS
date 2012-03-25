<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:fb="http://ns.adobe.com/Fiber/1.0" 
	xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	exclude-result-prefixes="fb xalan">
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:param name="entityName"/>
	<xsl:param name="rootPackage"/>
	
	<xsl:template match="/">
package <xsl:value-of select="$rootPackage"/>;

import javax.persistence.*;
import java.util.*;

	<xsl:variable name="entity" select="/fb:model/fb:entity[@name=$entityName]"/>
@Entity
@Table(name="<xsl:call-template name="findTableName"><xsl:with-param name="entity" select="$entity"/></xsl:call-template>")
public class <xsl:value-of select="$entityName"/> {

		@Id @GeneratedValue
	<xsl:for-each select="$entity/fb:id"><xsl:call-template name="findProperty"><xsl:with-param name="property" select="current()"/></xsl:call-template></xsl:for-each>
	<xsl:for-each select="$entity/fb:property"><xsl:call-template name="findProperty"><xsl:with-param name="property" select="current()"/></xsl:call-template></xsl:for-each>
	<xsl:variable name="childEntities">
		<xsl:for-each select="/fb:model/fb:entity/fb:property[@type=$entity/@name]">
		      <xsl:copy-of select=".."/>
		</xsl:for-each>
	</xsl:variable>
	<xsl:for-each select="xalan:nodeset($childEntities)/fb:entity">
		<xsl:variable name="name" select="@name"/>
		<xsl:variable name="mappedBy" select="fb:property[@type=$entityName]/@name"/>
		<xsl:variable name="contentType">&lt;<xsl:value-of select="@name"/>&gt;</xsl:variable>
		<xsl:variable name="collectionVariable" select="concat(helper:decapitalizeString(@name),'s')"/>
		//bi-directional one-to-many association to Set<xsl:value-of select="$contentType"/>
		@OneToMany(mappedBy=&quot;<xsl:value-of select="$mappedBy"/>&quot;)
		private Set<xsl:value-of select="$contentType"/><xsl:text> </xsl:text><xsl:value-of select="$collectionVariable"/>;
		public Set<xsl:value-of select="$contentType"/> get<xsl:value-of select="@name"/>s() {
			return this.<xsl:value-of select="$collectionVariable"/>;
		}

		public void set<xsl:value-of select="@name"/>s(Set<xsl:value-of select="$contentType"/> value) {
			this.<xsl:value-of select="$collectionVariable"/> = value;
		}			
	</xsl:for-each>		
}
	</xsl:template>

	<xsl:template name="findTableName">
		<xsl:param name="entity"/>
		<xsl:value-of select="$entity/fb:annotation[@name='DMS']/@Table"/>
	</xsl:template>

	<xsl:template name="findProperty">
		<xsl:param name="property"/>
		<xsl:variable name="type" select="helper:capitalizeString($property/@type)"/>
		<xsl:variable name="upperName" select="helper:capitalizeString($property/@name)"/>
		<xsl:choose>
			<xsl:when test="not($property/fb:annotation[@name='DMS']/@ColumnName)">
		@Transient
		protected <xsl:value-of select="$type"/><xsl:text> </xsl:text><xsl:value-of select="$property/@name"/>;
		public <xsl:value-of select="$type"/> get<xsl:value-of select="$upperName"/>() {
			return <xsl:value-of select="$property/@name"/>; 
		}
		public void set<xsl:value-of select="$upperName"/>(<xsl:value-of select="$type"/> value) {
			<xsl:value-of select="$property/@name"/> = value; 
		}
			</xsl:when>
			<!--  xsl:when test="/fb:model/fb:entity[@name=$type]">
				<xsl:variable name="idType" select="/fb:model/fb:entity[@name=$type]/fb:id/@type"/>
		@Column(name="<xsl:value-of select="$property/fb:annotation[@name='DMS']/@ColumnName"/>")	    
		private <xsl:value-of select="helper:capitalizeString(string($idType))"/><xsl:text> </xsl:text><xsl:value-of select="$property/@name"/>;
		public <xsl:value-of select="helper:capitalizeString(string($idType))"/> get<xsl:value-of select="$upperName"/>() {
			return <xsl:value-of select="$property/@name"/>; 
		}
		public void set<xsl:value-of select="$upperName"/>(<xsl:value-of select="helper:capitalizeString(string($idType))"/> value) {
			<xsl:value-of select="$property/@name"/> = value; 
		}
			</xsl:when-->
			<xsl:otherwise><xsl:if test="/fb:model/fb:entity[@name=$type]">
		//bi-directional many-to-one association to <xsl:value-of select="$type"/>	
		@ManyToOne<xsl:if test="not($property/@required='true')">(optional=true)</xsl:if>  
		@JoinColumn(name="<xsl:value-of select="$property/fb:annotation[@name='DMS']/@ColumnName"/>")
		</xsl:if>
		<xsl:if test="not(/fb:model/fb:entity[@name=$type])">
		@Column(name="<xsl:value-of select="$property/fb:annotation[@name='DMS']/@ColumnName"/>")</xsl:if>
		private <xsl:value-of select="$type"/><xsl:text> </xsl:text><xsl:value-of select="$property/@name"/>;
		public <xsl:value-of select="$type"/> get<xsl:value-of select="$upperName"/>() {
			return <xsl:value-of select="$property/@name"/>; 
		}
		public void set<xsl:value-of select="$upperName"/>(<xsl:value-of select="$type"/> value) {
			<xsl:value-of select="$property/@name"/> = value; 
		}
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>