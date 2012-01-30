<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mx="http://www.adobe.com/2006/mxml"
	xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	exclude-result-prefixes="xalan helper">
	<xsl:output method="xml" cdata-section-elements="mx:Script"
		indent="yes" xalan:indent-amount="4" />

	<xsl:template match="/" name="application.xsl">
		<xsl:param name="interfaceName" />
		<xsl:param name="fillParams" />
		<xsl:param name="methodName" />
		<xsl:param name="dtoName" />
		<xsl:variable name="fields" select="helper:getBeanProperties($dtoName)" />
		<xsl:text>&#10;</xsl:text>
		<mx:Application xmlns:mx="http://www.adobe.com/2006/mxml"
			creationComplete="onCreationComplete()">
			<mx:Panel title="{concat($interfaceName, '::', $methodName)}"
				width="800" height="100%">
				<mx:DataGrid id="dg" dataProvider="{{collection}}"
					editable="true" height="100%">
					<mx:columns>
						<xsl:for-each select="$fields/property">
							<xsl:choose>
								<xsl:when test="@type='date'">
									<mx:DataGridColumn editorDataField="selectedDate"
										itemEditor="mx.controls.DateField" dataField="{@name}"
										headerText="{@name}" editable="true" />
								</xsl:when>
								<xsl:otherwise>
									<mx:DataGridColumn dataField="{@name}"
										headerText="{@name}" editable="true" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</mx:columns>
				</mx:DataGrid>
				<mx:ControlBar width="100%">
					<mx:Button label="Fill" click="fill_onClick()" />
					<mx:Button label="Remove"
						click="collection.removeItemAt(dg.selectedIndex)" enabled="{{dg.selectedIndex != -1}}" />
					<mx:Button label="Add"
						click="addItemAt(Math.max(0,dg.selectedIndex+1)) " />
					<mx:Button label="Commit" click="collection.sync()"
						enabled="{{collection.commitRequired}}" />
					<mx:Label text="Deleted: {{collection.deletedCount}}" />
					<mx:Label text="Modified: {{collection.modifiedCount}}" />
				</mx:ControlBar>
			</mx:Panel>
			<xsl:element name="mx:String">
				<xsl:attribute name="id">destinationName</xsl:attribute>
				<xsl:value-of select="$interfaceName" />
			</xsl:element>
			<xsl:element name="mx:String">
				<xsl:attribute name="id">methodName</xsl:attribute>
				<xsl:value-of select="$methodName" />
			</xsl:element>

			<mx:Script>
				<xsl:text>

	import mx.logging.Log;
	import mx.logging.ILogger;
	private var logger:ILogger = Log.getLogger(destinationName + "." + methodName);
				
	import clear.collections.DataCollection;
	import mx.collections.ArrayCollection;
	import mx.controls.dataGridClasses.DataGridColumn;
	import mx.events.CollectionEvent;
	import mx.formatters.DateFormatter;
	
	import </xsl:text>
				<xsl:value-of select="$dtoName" />
				<xsl:text>;
	
    [Bindable]
	public var collection:DataCollection ;
	[Bindable]
	private var log : ArrayCollection;

	private function onCreationComplete() : void {
		collection = new DataCollection();
		collection.destination=destinationName;
		collection.fillMethod=methodName;
		collection.roundTripSync = true; 
		log = new ArrayCollection();
		collection.addEventListener( CollectionEvent.COLLECTION_CHANGE, logEvent);
		collection.addEventListener("fault", logEvent);
		fill_onClick();
	}
	private function fill_onClick():void {
		collection.fill(</xsl:text>
				<xsl:value-of select="concat($fillParams, '')" />
				<xsl:text>);
	}
	
	private function addItemAt(position:int):void	{
		var item:</xsl:text>
				<xsl:value-of select="concat($dtoName, '')" />
				<xsl:text> = new </xsl:text>
				<xsl:value-of select="concat($dtoName, '')" />
				<xsl:text>();
		collection.addItemAt(item, position);
		dg.selectedIndex = position;
	}
	
	private function logEvent(evt:Event):void {
		if (evt.type=="fault") {
			logger.error(evt["fault"]["faultString"]);
		} else {
			if (evt.type=="collectionChange") {
				logger.debug(evt["type"] + " " + evt["kind"]);
			} else {
				logger.debug(evt["type"]);
			}
		}
	}</xsl:text>
			</mx:Script>
		</mx:Application>
	</xsl:template>
</xsl:stylesheet>