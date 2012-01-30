<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/" name="service-flex.xsl">
		<xsl:param name="serviceName"/>
		<xsl:param name="interfaceName"/>
		<xsl:param name="rootPackage"/>
		
		<xsl:variable name="methods" select="annotated-types/annotated-type[@name=$interfaceName]/methods"/>
package <xsl:value-of select="$rootPackage"/>
{
	import clear.rpc.remoting.mxml.RemoteObject;
	import mx.controls.Alert;
	import mx.core.IMXMLObject;
	import mx.rpc.AsyncResponder;
	import mx.rpc.AsyncToken;
	import mx.rpc.events.FaultEvent;
	import flash.utils.Dictionary;
<xsl:variable name="imports">
		<xsl:for-each select="$methods/method/parameters/parameter">
			<xsl:variable name="parType" select="@type"/>
			<xsl:variable name="precedingParameters" select="preceding::parameter[parent::parameters/parent::method/parent::methods/parent::annotated-type/@name=$interfaceName]"/>
			<xsl:variable name="alreadyUsed" select="$precedingParameters/@type=$parType"/>
			<xsl:if test="not($alreadyUsed)">
				<xsl:variable name="flexType" select="helper:javaType2FlexType(@type)"/>
				<xsl:if test="contains($flexType, '.')"><xsl:value-of select="concat('&#09;import ', $flexType, ';&#13;')"/></xsl:if>
			</xsl:if>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="getMethodImports">
		<xsl:for-each select="$methods/method">
			<xsl:variable name="getMethod" select="helper:getMethodAnnotation($interfaceName, @name, 'clear.cdb.annotations.CX_GetMethod')"/>
			<xsl:if test="$getMethod/exists">
				<xsl:variable name="sync" select="boolean($getMethod/method[@name='sync']/@value = 'true')"/>
				<xsl:if test="$sync">
					<xsl:variable name="transferType" select="helper:getMethodTransferType($interfaceName, @name)"/>
					<xsl:if test="contains($transferType, '.')"><xsl:value-of select="concat('&#09;import ', $transferType, ';&#13;')"/></xsl:if>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
	</xsl:variable>
	
<xsl:value-of select="$getMethodImports"/>
<xsl:value-of select="$imports"/>
	public class <xsl:value-of select="$serviceName"/> implements IMXMLObject
	{
		public var remoteObject:RemoteObject=new RemoteObject("<xsl:value-of select="$interfaceName"/>");
		
		public function <xsl:value-of select="$serviceName"/>():void
		{
		}

		public var document:Object;
		public var id:String;
		public function initialized(document:Object, id:String):void
		{
			this.document=document; 
			this.id=id;
		}
		
		protected function faultHandler(event:FaultEvent, token:AsyncToken):void
		{
			Alert.show(event.toString(), "Error");
		}		
		<xsl:for-each select="$methods/method">
		public function <xsl:value-of select="@name"/>(<xsl:for-each select="parameters/parameter"><xsl:value-of select="@name"/>:<xsl:value-of select="helper:javaType2FlexType(string(@type))"/>, </xsl:for-each>resultHandler:Function=null, faultHandler:Function=null, properties:Dictionary=null):AsyncToken 
		{
			var token:AsyncToken=remoteObject.invoke("<xsl:value-of select="@name"/>", [<xsl:for-each select="parameters/parameter"><xsl:value-of select="@name"/><xsl:if test="not(last() = position())">, </xsl:if></xsl:for-each>], properties);
			if (resultHandler!=null) {
				if (faultHandler==null) {
					faultHandler=this.faultHandler;
				}
				token.addResponder(new AsyncResponder(resultHandler, faultHandler, token)) ;
			}
			return token;
		}
			<xsl:variable name="getMethod" select="helper:getMethodAnnotation($interfaceName, @name, 'clear.cdb.annotations.CX_GetMethod')"/>
			<xsl:if test="$getMethod/exists">
				<xsl:variable name="sync" select="boolean($getMethod/method[@name='sync']/@value = 'true')"/>
				<xsl:if test="$sync">
					<xsl:variable name="transferType" select="helper:getMethodTransferType($interfaceName, @name)"/>
		public function <xsl:value-of select="@name"/>_create(dto:<xsl:value-of select="$transferType"/>, resultHandler:Function=null, faultHandler:Function=null):AsyncToken 
		{
			var token:AsyncToken=remoteObject.<xsl:value-of select="@name"/>_create(dto);
			if (resultHandler!=null) {
				if (faultHandler==null) {
					faultHandler=this.faultHandler;
				}
				token.addResponder(new AsyncResponder(resultHandler, faultHandler, token)) ;
			}
			return token;
		}

		public function <xsl:value-of select="@name"/>_update(dto:<xsl:value-of select="$transferType"/>, resultHandler:Function=null, faultHandler:Function=null):AsyncToken 
		{
			var token:AsyncToken=remoteObject.<xsl:value-of select="@name"/>_update(dto);
			if (resultHandler!=null) {
				if (faultHandler==null) {
					faultHandler=this.faultHandler;
				}
				token.addResponder(new AsyncResponder(resultHandler, faultHandler, token)) ;
			}
			return token;
		}

		public function <xsl:value-of select="@name"/>_delete(dto:<xsl:value-of select="$transferType"/>, resultHandler:Function=null, faultHandler:Function=null):AsyncToken 
		{
			var token:AsyncToken=remoteObject.<xsl:value-of select="@name"/>_delete(dto);
			if (resultHandler!=null) {
				if (faultHandler==null) {
					faultHandler=this.faultHandler;
				}
				token.addResponder(new AsyncResponder(resultHandler, faultHandler, token)) ;
			}
			return token;
		}
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
	}
}
	</xsl:template>
</xsl:stylesheet>