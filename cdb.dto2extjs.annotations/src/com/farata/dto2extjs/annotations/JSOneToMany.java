package com.farata.dto2extjs.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Is supplying metadata for retrieving the data associated to a model via <a href="http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.HasManyAssociation">HasMany association</a>.
 * Should be used only on a collection type property of the class marked as <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/java/ext/com/farata/dto2extjs/annotations/JSClass.html">&#64;JSClass</a>
 * <p>
 *  <table class="innertable">
 *   <tr>
 *   	<th>Parameter</th><th>Type</th><th>Required</th><th>Description</th>
 *   </tr>
 *    <tr>
 *   	<td><code>foreignKey</code></td><td>String</td><td>Required</td>
 *   	<td>Name of the foreign key field in the model class of records that populate the associated store.
 *     	</td>
 *   </tr>
 *    <tr>
 *   	<td><code>primaryKey</code></td><td>String</td><td>Optional</td>
 *   	<td>Name of the primary key field in the model. Default value is <code>id</code>.The value of this field
 *       will be passed as argument to the server side method retrieving the associated data. You can
 *       pass a comma-separated string - in this case values of these fields will be passed to the server method.
 *   	</td>
 *   </tr>
 *    <tr>
 *   	<td><code>getter</code></td><td>String</td><td>Optional</td>
 *   	<td>Name of the getter method to obtain the reference to the associated store. The default value is
 *       made by concatenating "get" and the capitalized name of the annotated property
 *      </td>
 *   </tr>
 *   <tr>
 *   	<td><code>storeType</code></td><td>String</td><td>Optional</td>
 *   	<td>Fully qualified class name of the store to populate with the associated data. Default value 
 *   corresponds to the model class of the stored records, i.e. the name of the
 *   store loaded with <code>AM.model.smart.User</code> is <code>AM.store.smart.UserStore</code>.
 *   Developer should make sure that the store class exists and is a <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/extjs/4/clear/data/DirectStore.html">Clear.data.DirectStore</a>; one way to do it is by using JSGenerateStore annotation.
 *   <p>ATTENTION! In the absence of <a href="http://www.cleartoolkit.com/dokuwiki/doku.php?id=clearwiki:40.clear_components_ext">Clear Components for ExtJS</a>
 *   (see below) this parameter has no effect, because out-of-the box <a href="http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.HasManyAssociation">HasManyAssociation</a>
 *   populates standard <a href="http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.Store">Ext.data.Store</a>
 *   </p>
 *      </td>
 *   </tr>
 *   <tr>
 *   	<td><code>storeConfig</code></td><td>String</td><td>Optional</td>
 *   	<td>JSON config to be applied to the store created by <code>HasManyAssociation</code>.
 *   This config will be applied in all use cases: with Clear Component for ExtJS or without them.
 *      </td>
 *   </tr>
 *   <tr>
 *   	<td><code>autoLoad</code></td><td>Boolean</td><td>Optional</td>
 *   	<td>Starts automatic load of the associated store on first invocation of the getter Method. Default value is true.
 *   	</td>
 *   </tr>
 *   <tr>
 *   	<td><code>ranking</code></td><td>int</td><td>Optional. Not implemented in this version.</td>
 *   	<td>0-based relative order of insertion for multiple "hasMany"-associated children of the same record. 
 * <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/extjs/4/clear/transaction/BatchManager.html">BatchManager</a>
 * sorts all inserts in the order of priority (deletes are sorted in the opposite order and go before inserts) and for each hasMany-based  store
 * it assigns priority as <code>parentPriority + 100 + ranking</code> (parentPriority for the root store in the transaction is 0).
 * <p>This way, by setting <code>ranking</code> in
 * the range of 0-99, developer can control relative order of inserts (and deletes) within the child stores of the same record. Default value is 0, which
 * means random relative order of inserts and deletes of the stores associated with the same record</p>
  <p>ATTENTION! In the absence of <a href="http://www.cleartoolkit.com/dokuwiki/doku.php?id=clearwiki:40.clear_components_ext">Clear Components for ExtJS</a>
 *   (see below) this parameter has no effect, because by design <a href="http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.Store">Ext.data.Store</a>
 *   does not support transactional sync() of the associated data as well as it does not support transactional sync of one
 *   <code>Ext.data.Store</code>.  Out-of-the-box batching of ExtJS assumes that inserts, deletes and updates done to one store require three HTTP requests
 *   to the server to get settled.
 *   </p>
 *      	</td>
 *   </tr>
 *  </table>
 *  </p> 
 *  <p> The following example developer provided both both <code>storeType</code> and <code>storeConfig</code>.
 *  As a result all relevant settings of <code>TicketStore</core> or it's ancestors will be overriden
 *  by the corresponding values of <code>storeConfig</code>. To access the associated store developer would
 *  use  the default getter - <code>user.getTickets()</code>. By default <code>autoLoad</code> is <code>true</code>
 *  so first call to <code>user.getTickets()</code> returns the reference to the store <b>and, in parallel,</b> start
 *  the load:  
 *
<PRE>package clear.dto;

import java.util.List;
import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSOneToMany;

&#64;JSClass
public class UserDTO {
	public String id;
	public String name;
	
	&#64;JSOneToMany(
			foreignKey="userId",
			storeType="AM.store.clear.TicketStore",
			storeConfig="{"+        	
	         	"paramOrder:['0'],"+   
	        	"api: {"+
	        		"create  : Clear.action.TicketAction.getTickets_insertItems,"+
	        		"read    : Clear.action.TicketAction.getTickets,"+
	        		"update  : Clear.action.TicketAction.getTickets_updateItems,"+
	        		"destroy : Clear.action.TicketAction.getTickets_deleteItems"+
	        	"}"+
	        "}")
	public List<TicketDTO> tickets;
}
</PRE>
 *  </p>
 * <p><b>Downloading and Using DTO2ExtJS Annotation Processor in Eclipse Plugin</b></p>
 *  <p>
 *  <li>Copy into your <code>eclipse/plugins folder the jar downloaded from
 *  <a href="http://www.cleartoolkit.com/downloads/plugins/extjs/dto2extjs/com.farata.dto2extjs.asap_4.6.0.jar">http://www.cleartoolkit.com/downloads/plugins/extjs/dto2extjs/com.farata.dto2extjs.asap_4.6.0.jar</a></li>
 *  <li>Copy into <code>WebContent/lib</code> folder of your Dynamic Web Project annotations jar downloaded from
 *  <a href="http://www.cleartoolkit.com/downloads/plugins/extjs/dto2extjs/com.farata.dto2extjs.annotations.jar">http://www.cleartoolkit.com/downloads/plugins/extjs/dto2extjs/com.farata.dto2extjs.annotations.jar</a></li>
 * </p>
 *  <p><b>Downloading and Using Clear Components for Ext JS </b></p>
 *  <p>
 *  To take full advantage of &#64;JSOneToMany you need to use <a href="http://www.cleartoolkit.com/dokuwiki/doku.php?id=clearwiki:40.clear_components_ext">Clear Components for ExtJS</a>.
 *  In this case you get:
 *  <li> automatic injection of reference to the parent record to all "many" associated records on load;</li>
 *  <li> automatic batching of associated changes during <a href="http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.AbstractStore-method-sync">sync()</a>
 *  of the parent store, i.e. "deep sync".</li>
 *  </p>
 *  <p>
 *  To plug in Clear Components for Ext JS to your Ext JS MVC application copy the contents of ClearJS/src into the web root
 *  of your application and make sure that the main application script starts similar to the following:
 <PRE>// app.js 
 Ext.Loader.setConfig({
	disableCaching: false,
	enabled: true,
	paths  : {
		MyApp: 'app', Clear:'clear'
	}
});
	
Ext.require('Clear.patch.ExtJSPatch');
</PRE>
 *  </p> 
 */ 
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD, ElementType.FIELD })
public @interface JSOneToMany {
	public enum SyncType { BATCH, HIERARCHY }
	
	public String foreignKey();
	public String primaryKey() default "id";
	public String getter() default "";
	public String storeType() default "";
	public String storeConfig() default "";
	public boolean autoLoad() default true;
	public SyncType sync() default SyncType.BATCH;
	public int ranking() default 0;
}
