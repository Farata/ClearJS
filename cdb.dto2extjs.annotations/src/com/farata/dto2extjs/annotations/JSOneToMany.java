package com.farata.dto2extjs.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Is supplying metadata for retrieving the data associated to a model via <a href="http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.HasManyAssociation">HasMany association</a>.
 * Should be used only on a collection type property of the class marked as <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/java/ext/com/farata/dto2extjs/annotations/JSClass.html">&#64;JSClass</a>
 * Requires use of ClearComponents for ExtJS, see below.
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
 *   	<td>Fully qualified name of the store class to populate with the associated data. Default value is
 *   derived from the name of the model class for store records, i.e. the default expected name of the
 *   store carrying <code>AM.model.smart.User</code> records is <code>AM.store.smart.UserStore</code>.
 *   Developer must ensure that the corresponding store class exists. It is recommended that an instance
 *   of <code>Clear.data.DirectStore</code> is used (see below).
 *      </td>
 *   </tr>
 *   <tr>
 *   	<td><code>autoLoad</code></td><td>Boolean</td><td>Optional</td>
 *   	<td>Starts automatic load of the associated store on first invokation of the getter Method. Default value is true.
 *   	</td>
 *   </tr>
 *   <tr>
 *   	<td><code>ranking</code></td><td>int</td><td>Optional. Not implemented in this version.</td>
 *   	<td>0-based relative order of insertion between multiple "hasMany" of the same record. 
 * <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/extjs/4/clear/transaction/BatchManager.html">BatchManager</a>
 * sorts all inserts in the order of priority (deletes are sorted in the opposite order and go before inserts) and for each hasMany-based  store
 * it assigns priority as <code>parentPriority + 100 + ranking</code> (parentPriority for the root store in the transaction is 0).
 * <p>This way, by setting <code>ranking</code> in
 * the range of 0-99, developer can control relative order of inserts (and deletes) within the child stores of the same record. Default value is 0, which
 * means random relative order of inserts and deletes of the stores associated with the same record</p>
 *   	</td>
 *   </tr>
 *  </table>
 *  </p> 
 *  <p><b>Downloading and Using Clear Components for Ext JS </b><br>
 *  
 *  To take full advantage of &#64;JSOneToMany you need to use Clear Components for ExtJS. Below is the SVN repo of Clear Components for Ext JS: 
 *  <li><a href="http://cleartoolkit.svn.sourceforge.net/viewvc/cleartoolkit/HTML5/ExtJS/4/ClearJS/">http://cleartoolkit.svn.sourceforge.net/viewvc/cleartoolkit/HTML5/ExtJS/4/ClearJS/</a></li>
 *  
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
	public boolean autoLoad() default true;
	public SyncType sync() default SyncType.BATCH;
	public int ranking() default 0;
}
