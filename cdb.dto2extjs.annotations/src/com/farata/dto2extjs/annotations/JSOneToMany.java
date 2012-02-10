package com.farata.dto2extjs.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * Is used to generate JavaScript code for remote lazy load of the collection property; can also be used to
 * control relative order of inserts/deletes between children of the same DTO during BatchService batch preparation.
 * <p>
 *  <table class="innertable">
 *   <tr>
 *   	<th>Parameter</th><th>Type</th><th>Required</th><th>Description</th>
 *   </tr>
 *   <tr>
 *   	<td><code>fillArguments</code></td><td>String</td><td>Optional</td>
 *   	<td>A comma-separated string of arguments that have to 
 * be used to <code>fill()</code> the lazy collection. Default value is <code>id</code>
 *   	</td>
 *   </tr>
 *   <tr>
 *   	<td><code>collectionType</code></td><td>String</td><td>Optional</td>
 *   	<td>Fully qualified name of the subclass of the 
 * <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/flex/4/clear/collections/DataCollection.html">DataCollection</a>
 * that has to be created and lazy-loaded on the JavaScript side
 *   	</td>
 *   </tr>
 *   <tr>
 *   	<td><code>ranking</code></td><td>int</td><td>Optional</td>
 *   	<td>0-based relative order of insertion within nested data collections of this DTO. 
 * <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/flex/4/clear/transactions/BatchService.html">BatchService</a>
 * sorts all inserts in the order of priority (deletes are sorted in the opposite order and go before inserts) and for each nested data
 * collection it assigns priority as <code>parentPriority + 100 + ranking</code>. Accordingly, by setting <code>ranking</code> in
 * the range of 0-99, developer can control relative order of inserts (and deletes) within the children of the DTO. Default value is 0, which
 * means random relative order of inserts and deletes of the child data collections
 *   	</td>
 *   </tr>
 *  </table>
 *  </p>  
 * <p>Using <code>&#64;JSOneToMany</code> annotation on a Java getter that returns a collection  
 * results in the JavaScript getter that implements remote lazy load of the peer collection. In the example below
 * property <code>getCompanyAssociates</code> is annotated with <code>&#64;JSOneToMany</code>, indicating which
 * collection should be created and filled on the JavaScript side:
 * <pre>
 * 	&#64;JSOneToMany(fillArguments="id", collectionType="com.farata.test.collections.CompanyAssociateCollection")
 *	public Set<CompanyAssociate> getCompanyAssociates() {
 *		return this.companyAssociates;
 *	}
 * </pre>
 * Accordingly, the generated JavaScript class will contain the following code: 
 *  <pre>
 *  // One to many property "companyAssociates"; collection of com.farata.test.entity.CompanyAssociate 
 *  private var _companyAssociates:com.farata.test.collections.CompanyAssociateCollection;
 *   
 *  [Transient][Bindable(event="propertyChange")]
 *  public function get companyAssociates():mx.collections.ICollectionView {
 *    if (_companyAssociates==null) {
 *    	_companyAssociates = new com.farata.test.collections.CompanyAssociateCollection();
 *     	.  .  .
 *     		_companyAssociates.fill(id);
 *    }	
 *    return _companyAssociates;
 *  }
 * </pre>
 * 
 * <p>Annotation <code>&#64;JSOneToMany</code> is not related to <code>&#64;OneToMany</code> JPQL annotation or any JPQL annotation at all.
 * However, the class containing the annotation should be, of itself, annotated with the <code>&#64;JSClass</code>.
 * </p>

 * <p>If you omit the <code>collectionType</code> the default value is determined from the base name of the
 * Java collection type: class name is computed as <i>base name + "Collection"</i> and package name is computed by replacing
 * the nearest folder with "collections". Thus, if a base collection type is 
 * <code>com.farata.test.bean.CompanyAssociate</code> the default value of the <code>collectionType</code> becomes
 * <code>com.farata.test.collections.CompanyAssociateCollection</code>.
 * </p>
 * 
 * <p>Due to asynchronous nature of the lazy loading, access of collections that are pending completion of the load may result 
 * in the <a href="http://help.adobe.com/en_US/FlashPlatform/reference/actionscript/3/mx/collections/errors/ItemPendingError.html">ItemPendingError</a>. In this case application code should piggy back on ItemPendingError to register a responder that 
 * should be put in effect when the fill() completes.
 * </p>
 * <p>The following example assumes that <code>companyAssociates</code> is a child collection of some 
 * <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/flex/4/clear/collections/dto/IHierarchicalDTO.html">IHierarchicalDTO</a>.
 * Accordingly, to access the first item of the <code>companyAssociates</code> you can write the code similar to this one:
 * </p>
 * <pre>
import mx.collections.ItemResponder;
import mx.collections.errors.ItemPendingError;
import mx.rpc.events.ResultEvent;
import mx.rpc.events.FaultEvent;
 
 try {
    // If we collection has been pre-retrieved, 
    // we can display the first item right away
 
    trace(parentDTO.companyAssociates.getItemAt(0).toString());	
 
 } catch (ipe:ItemPendingError) {
    // Otherwise, let's add responder...			 
    ipe.addResponder(
        new ItemResponder(
            resultHandler,
            faultHandler,
            {collectionName:"companyAssociates"}
        )
    );
 }
 private function resultHandler(event:ResultEvent, token:Object=null):void {
    // ... and display the first item of the collection when it is ready
    trace(parentDTO.companyAssociates.getItemAt(0).toString()); 
 }
 private function faultHandler(event:FaultEvent, token:Object=null):void {
    trace (token.collectionName);   //displays "companyAssociates", etc.
    trace(event.fault.faultString);						
 }
</pre>
* <p>
* Note that MX List and MX DataGrid controls have built in support for handling ItemPendingError: if you use parentDTO.companyAssociates 
* as <code>dataProvider</code> for these controls there is not need in explicit IPE coding, since corresponding responders are 
* added by the Flex framework automatically.
* </p>
* <p>With Spark controls, handling of the ItemPendingError has been delegated to AsyncListView. You should not use use lazy
*  loaded DataCollection as a direct dataProvider for DataGroup and it's descendants like Spark List. Instead you should wrap an
*  instance of AsyncListView around lazy loaded DataCollection. For instance, you could code a snippet similar to:
*  </p>
<pre>
&lt;s:List&gt; 
   &lt;s:dataProvider&gt;
     &lt;mx:AsyncListView list="{parentDTO.companyAssociates}" 
        createPendingItemFunction="{pendingItemFunction}" 
        createFailedItemFunction="{failedItemFunction}"/&gt; 
   &lt;/s:dataProvider&gt;
&lt;/s:List&gt;
</pre>
<p>Then, assuming the default (toString()) item renderer for the List, you could provide the following <code>pendingItemFunction</code>:
</p>
<pre>
private function pendindItemFunction(index:int, ipe:ItemPendingError):Object { 
            return "Pending associate " + index + " ..."; 
}
</pre>
 */ 
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD, ElementType.FIELD })
public @interface JSOneToMany {
	public enum SyncType { BATCH, HIERARCHY }
	public Class<? extends Collection<?>> collectionType() default defaultCollection.class;
	public String fillArguments() default "";
	public SyncType sync() default SyncType.BATCH;
	public int ranking() default 0;

	abstract public static class defaultCollection implements Collection<Object> {}
}
