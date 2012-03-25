package clear.cdb.extjs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Is used to generate collection-based CRUD code for data synchronization between a database and Flex UI. Should be
 * applied only to methods returning collections of entities or collections of DTO types.
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
  		<td><code>transferInfo</code></td><td>&#64;CX_TransferInfo</td><td>Optional</td><td>Allows to narrow the base type of the return collection to the DTO class that is dynamically created
 * from the result set metadata. It is applicable when Java return type of the method is a collection based on 
 * a wildcard.
 * <br>
 * <br>
 *  Sub-annotation <code>&#64;CX_TransferInfo</code> has the following parameters:
 * 		<li>type - String. Fully qualified class name of the dynamic return type;</li>
 * 		<li>mappedBy - Class. Entity type that will be globally narrowed down to this dynamic return type</li>
 * 		<li>generate - boolean (true). Flags to turn on generation of the dynamic DTO</li>
 * <br>
 * <br>
 * In it's complete form, i.e. <code>transferInfo=&#64;CX_TransferInfo(type="foo.dto.BarDTO", mappedBy="foo.entity.BarEntity")</code>
 * it defines a project-level mapping from entity to a newly created DTO. <br>You should use <code>mappedBy</code> only once for a DTO per project, be that in a <code>&#64;CXJPQLMethod</code>- or 
 * <code>&#64;CX_JSGetMethod</code>-based annotation to avoid conflicting re-mappings. Elsewhere use a shorter form :
 * <pre> 
 *   transferInfo=&#64;CX_TransferInfo(type="foo.dto.BarDTO")
 * </pre> 

 * Dynamic generation of the DTO in the context of code>&#64;CX_JSJPQLMethod</code> is based on the properties of the 
 * result set. When flag <code>generate</code> is set to false generation is omitted, this allows to
 * avoid conflicting redefinition of the DTO across the project. NOTE: NOT IMPLEMENTED YET
*  </td>
  	</tr>	
  	<tr>
  		<td><code>updateInfo</code></td><td>&#64;CX_UpdateInfo</td><td>Optional</td>
  		<td>Causes generation of update/insert/delete/sync methods in the service code. 
  		<br>
  		<br>Sub-annotation <code>&#64;CX_UpdateInfo</code> has the following parameters:
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
 * For associated properties, i.e. properties annotated with <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/BlazeDS/4/com/farata/dto2fx/annotations/FXOneToMany.html">&#64;FXOneToMany</a> 
 * and <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/BlazeDS/4/com/farata/dto2fx/annotations/FXManyToOne.html">&#64;FXManyToOne</a> 
 * generated implementation of the <i>fill_method</i> returns <code>null</code> with one exception in <code>&#64;FXManyToOne</code> case :
 * <li>a <code>&#64;FXOneToMany</code> annotated property is returned as <code>null</code> always and gets populated
 * on-demand, when the FlexUI code accesses the property first time; </li>
 * <li>a <code>&#64;FXManyToOne</code> property contains <code>null</code>,
 * unless it has been explicitly mentioned in the JPQL query</i>.
 * </p>
 *  <p>
 *  Example 1: <b>"Read-only" scenario with <code>fill</code> method returning entities</b>. The  generated service class will implement the <code>getCompanies</code> returning <code>List&lt;com.farata.test.entity.Company&gt;</code>. 
 *  Since there is no <code>updateInfo</code> the class will not have methods to sync the database with the changes originated from the client: 
 * <pre>
 * 	&#64;CX_JSJPQLMethod(
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
 * 	&#64;CX_JSJPQLMethod(
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
 *  Flex UI to invoke <code>sync</code> method of the DataCollection:
 * <pre>
 * 	&#64;CX_JSJPQLMethod(
 * 		query="SELECT a.id, a.associateName, a.company.id as companyId FROM CompanyAssociate a",
 * 		transferInfo=&#64;CX_TransferInfo(type="com.farata.test.dto.CompanyAssociateDTO", mappedBy=CompanyAssociate.class),
 * 		updateInfo=&#64;CX_UpdateInfo(updateEntity=CompanyAssociate.class)
 * 	)
 * 	List&lt;?&gt; getAssociates();
 * </pre>
 * </p>
 * <p>
 *  Example 4: <b>"Read-only" scenario that is reusing <code>CompanyAssociateDTO</code></b> type. The effective return type is a List of
 *  objects of <code>CompanyAssociateDTO</code> type that, according to <code>generate=false</code> is expected to be generated somewhere
 *  else in the project. 
 * <pre>
 * 	&#64;CX_JSJPQLMethod(
 * 		query="SELECT a.id, a.associateName, a.company.id as companyId FROM CompanyAssociate a",
 * 		transferInfo=&#64;CX_TransferInfo(type="com.farata.test.dto.CompanyAssociateDTO", generate=false)
 * 	)
 * 	List&lt;?&gt; getAssociates();
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CX_JSJPQLMethod {
	String query();
	CX_TransferInfo transferInfo() default @CX_TransferInfo(type="");
	CX_UpdateInfo updateInfo() default @CX_UpdateInfo(updateEntity=DEFAULT.class);
}