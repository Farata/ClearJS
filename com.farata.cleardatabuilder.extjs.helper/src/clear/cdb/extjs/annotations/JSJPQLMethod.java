package clear.cdb.extjs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Is used to automatically generate complete set of Java/Hibernate and Ext JS classes
 * implementing CRUD data synchronization between a database and Ext JS UI; no manual coding
 * is required.
 * 
 * Should be applied only to methods returning collections of entities or collections of DTO types.
 * <p>
  <table class="innertable">  
  	<tr>
  		<th>Parameter</th><th>Type</th><th>Required</th><th>Description</th>
  	</tr>
  	<tr>
  		<td><code>query</code></td><td>String</td><td>Required</td><td>A JPQL query describing the result set to be fetched from the database by the <code>fill</code> method. The query can contain parameters of the annotated 
  		method prefixed with ":"</td>
  	</tr>	
  	<tr>
  		<td><code>transferInfo</code></td><td>&#64;JSTransferInfo</td><td>Optional</td><td>Allows to narrow the base type of the return collection to the DTO class that is dynamically created
 * from the result set metadata. It is applicable when Java return type of the method is a collection based on 
 * a wildcard.
 * <br>
 * <br>
 *  Sub-annotation <code>&#64;JSTransferInfo</code> has the following parameters:
 * 		<li>type - String. Fully qualified class name of the dynamic return type;</li>
 * 		<li>mappedBy - Class. Entity type that will be globally narrowed down to this dynamic return type</li>
 * 		<li>generate - boolean (true). Flags to turn on generation of the dynamic DTO</li>
 * <br>
 * <br>
 * In it's complete form, i.e. <code>transferInfo=&#64;JSTransferInfo(type="foo.dto.BarDTO", mappedBy="foo.entity.BarEntity")</code>
 * it defines a project-level mapping from entity to a newly created DTO. <br>You should use <code>mappedBy</code> only once for a DTO per project, be that in a <code>&#64;CXJPQLMethod</code>- or 
 * <code>&#64;JSGetMethod</code>-based annotation to avoid conflicting re-mappings. Elsewhere use a shorter form :
 * <pre> 
 *   transferInfo=&#64;JSTransferInfo(type="foo.dto.BarDTO")
 * </pre> 

 * Dynamic generation of the DTO in the context of code>&#64;JSJPQLMethod</code> is based on the properties of the 
 * result set. 
 * </td>
  	</tr>	
  	<tr>
  		<td><code>updateInfo</code></td><td>&#64;JSUpdateInfo</td><td>Optional</td>
  		<td>Causes generation of update/insert/delete/sync methods in the service code. 
  		<br>
  		<br>Sub-annotation <code>&#64;JSUpdateInfo</code> has the following parameters:
 * 		<li>updateEntity - Class. Entity class to use for update. If a query has more then one entity in it's FROM clause, only one entity can be updated;</li>
 * 		<li>keyPropertyNames - String. Optional, comma-separated list of properties used to identify original database records in the WHERE clause of the JPQL queries of the generated update/delete/insert 
 * methods. If omitted will default to the property annotated with <code>&#64;Id</code></li>
 * 		<li>updatablePropertyNames - String. Optional, comma-separated list of properties that are allowed to be updated. If omitted defaults to all
 * properties specified in <code>changedPropertyNames</code> of the <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/flex/4/clear/data/ChangeObject.html">ChangeObject</a> items sent by Flex
 * code to <code>sync</code> method of the generated service;</li>
 * 		<li>autoSyncEnabled - boolean. Optional. If set to <code>true</code> the generated service code will push incoming changes to all
 * destinations subscribed to the same result set. Default value is <code>false</code></li>
 *</td>
  	</tr>	
  </table>
 * </p>
 * <p><br>
 *<br>Parameter <code>updateInfo</code>, when set, triggers code generation of four extra methods with the following signatures:<br> 
  <pre>
  	public List&lt;ChangeObject&gt; [fill_method]_sync(List&lt;ChangeObject&gt;);
  	public List&lt;ChangeObject&gt; [fill_method]_deleteItems(List&lt;ChangeObject&gt;);
  	public List&lt;ChangeObject&gt; [fill_method]_updateItems(List&lt;ChangeObject&gt;);
  	public List&lt;ChangeObject&gt; [fill_method]_insertItems(List&lt;ChangeObject&gt;);
  </pre>,
  where [fill_method] stands for the name of the original annotated method of the interface
 * </p>
 * <p>
 * For associated properties, i.e. properties annotated with <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/java/extjs/com/farata/dto2extjs/annotations/JSOneToMany.html">&#64;JSOneToMany</a> 
 * and <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/java/extjs/com/farata/dto2extjs/annotations/JSManyToOne.html">&#64;JSManyToOne</a> 
 * generated implementation of the <i>fill_method</i> returns <code>null</code> with one exception in <code>&#64;JSManyToOne</code> case :
 * <li>a <code>&#64;JSOneToMany</code> annotated property is returned as <code>null</code> always and gets populated
 * on-demand, when the Ext JS code accesses the property first time; </li>
 * <li>a <code>&#64;JSManyToOne</code> property contains <code>null</code>,
 * unless it has been explicitly mentioned in the JPQL query</i>.
 * </p>
 *  <p>
 *  Example 1: <b>"Read-only" scenario with <code>fill</code> method returning entities</b>. The  generated service class will implement the <code>getCompanies</code> returning <code>List&lt;com.farata.test.entity.Company&gt;</code>. 
 *  Since there is no <code>updateInfo</code> the class will not have methods to sync the database with the changes originated from the client: 
 * <pre>
 * 	&#64;JSJPQLMethod(
 * 		query="SELECT c FROM Company c WHERE c.countryCode=:countryCode"
 * 	)
 * 	List&lt;com.farata.test.entity.Company&gt; getCompanies(String countryCode);
 *	</pre>
 *	</p>
 *  <p>
 *  Example 2: <b>"Read-only" scenario with <code>fill</code> method returning <code>java.util.Map</code></b>. Because the return type contains a wildcard
 *  the  generated implementation of the  <code>getCompanies</code> will be returning list of <code>java.util.Map</code> objects.
 *  Since there is no <code>updateInfo</code> the class will not contain methods to serve client's request to sync the database with the changes 
 *  originated from the client: 
 * <pre>
 * 	&#64;JSJPQLMethod(
 * 		query="SELECT c FROM Company c"
 * 	)
 * 	List&lt;?&gt; getCompanies();
 *	</pre>
 *	</p>
 *	<p>
 *  Example 3: <b>"Read-write" scenario with <code>fill</code> method returning dynamic DTO</b>. The factual return type is a List of
 *  dynamically generated <code>CompanyAssociateDTO</code> object, its properties are shaped by result set of the query:
 *  <code>id</code>, <code>associateName</code>, <code>companyId</code>. Due to <code>mappedBy</code>, nested references 
 *  to <code>CompanyAssociate</code> will be automatically replaced with <code>CompanyAssociateDTO</code>. Since <code>updateInfo</code> is provided, CDB will generate Java methods that enable
 *  Ext JS UI to invoke <code>sync</code> method of the store:
 * <pre>
 * 	&#64;JSJPQLMethod(
 * 		query="SELECT a.id, a.associateName, a.company.id as companyId FROM CompanyAssociate a",
 * 		transferInfo=&#64;JSTransferInfo(type="com.farata.test.dto.CompanyAssociateDTO", mappedBy=CompanyAssociate.class),
 * 		updateInfo=&#64;JSUpdateInfo(updateEntity=CompanyAssociate.class)
 * 	)
 * 	List&lt;?&gt; getAssociates();
 * </pre>
 * </p>

 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JSJPQLMethod {
	String query();
	JSTransferInfo transferInfo() default @JSTransferInfo(type="");
	JSUpdateInfo updateInfo() default @JSUpdateInfo(updateEntity=DEFAULT.class);
}