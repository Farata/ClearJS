package clear.cdb.js.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Is used to generate item-based CRUD code for data synchronization between a Java class and Flex UI, driven
 * by the item id. Should be applied only to methods returning entity or DTO type.
 * <p>
 *  <table class="innertable">
  
  	<tr>
  		<th>Parameter</th><th>Type</th><th>Required</th><th>Description</th>
  	</tr>
  	<tr>
  		<td><code>transferInfo</code></td><td>&#64;CX_TransferInfo</td><td>Optional</td><td>Allows to define or re-use the DTO class that is dynamically created
 * from the entity. Applicable when the return type of the annotated method is an Object.
 * <br>
 * <br>
 *  Sub-annotation <code>&#64;CX_TransferInfo</code> has the following parameters:
 * 		<li>type - String. Fully qualified class name of the dynamic return type;</li>
 * 		<li>mappedBy - Class. Entity class that the dynamically-generated DTO should be based on</li>
 * 		<li>generate - boolean (true). Flags to turn on dynamic generation of the DTO.</li>
 * <br>
 * In it's complete form, i.e. <code>transferInfo=&#64;CX_TransferInfo(type="foo.dto.BarDTO", mappedBy="foo.entity.BarEntity")</code>
 * it defines a project-level mapping from an entity to the dynamically generated DTO. <br>You should use <code>mappedBy</code> only once for a DTO per project, be that in a <code>&#64;CXJPQLMethod</code>- or 
 * <code>&#64;CX_JSGetMethod</code>-based annotation to avoid conflicting re-mappings. Elsewhere use a shorter form  :
 * <pre> transferInfo=&#64;CX_TransferInfo(type="foo.dto.BarDTO")</pre>

 * Dynamic generation of the DTO in the context of code>&#64;CX_JSGetMethod</code> is based on the properties of the 
 * underlying entity. When flag <code>generate</code> is set to false generation is omitted, this allows to
 * avoid conflicting redefinition of the DTO across the project. NOTE: NOT IMPLEMENTED YET
 *  </td>
  	</tr>	
  	 <tr>
  		<td><code>sync</code></td><td>Boolean</td><td>Optional</td><td>If set to <code>true</code> directs the code-generation script to support updates from the
 * Flex UI, in addition to pulling the data to Flex UI from Java. Default value is <code>true</code></td>
  	</tr>	
  	 <tr>
  		<td><code>fillChildren</code></td><td>Boolean</td><td>Optional</td><td>If set to <code>true</code> the DTO returned by the generated "get" method contains nested
  		child collection for each one-to-many property. Default value is <code>false</code> and associate collections are returned as <code>null</code> to support
  		remote-lazy-load on demand.</td>
  	</tr>
  </table>
</p>  
 * <p>
 * Annotation <code>&#64;CX_JSGetMethod</code> supports single item approach for Flex/Java data synchronization.
 * </p>
 * Original method signature, herein referred to as [get_method], is supposed to have id of the entity as its single argument.
 * The type of the return value - [return_type] - should be the entity type to return or Object. Using Object in combination with
 * <code>&#64;CX_JSGetMethod</code> results in returning a dynamically generated DTO type.
 * When annotation parameter <code>sync</code> is <code>true</code>, code generation script 
 * creates additional three methods:
 * </p>
 * <pre>
 * 	public [return_type] [get_method]_create([return_type]);
 * 	public [return_type] [get_method]_update([return_type]);
 * 	public [return_type] [get_method]_delete([return_type]);
 * </pre>
 * <p>
 * For associated properties of the item, i.e. properties annotated with <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/BlazeDS/4/com/farata/dto2fx/annotations/FXOneToMany.html">&#64;FXOneToMany</a> 
 * and <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/BlazeDS/4/com/farata/dto2fx/annotations/FXManyToOne.html">&#64;FXManyToOne</a> 
 * generated implementation of the <i>get_method</i> returns <code>null</code>. In case of  <code>&#64;FXOneToMany</code>, a DataCollection corresponding to
 * the annotated property gets populated on-demand, when the FlexUI code accesses the property first time.  
 * </p>
 *  <p>
 *  Example 1: <b>"Read-only" scenario returning entity</b>. The argument to the method is the id of the
 *  entity. Generated service class will acquire instance of the <code>Company</code> from the Hibernate session.
 *  <pre>
 * 	&#64;CX_JSGetMethod
 * 	com.farata.test.entity.Company getCompany(Integer companyId);
 *	</pre>
 *	</p> 
 *  <p>
 *  Example 2: <b>"Read-write" scenario returning entity</b>. The argument to the method is the id of the
 *  entity. Because of the <code>sync=true</code> generated service class will contain three extra methods: 
 *  <code>getCompany_create</code>, <code>getCompany_update</code> and <code>getCompany_delete</code> - all
 *  expecting a single argument of type <code>com.farata.test.entity.Company</code>.
 * <pre>
 * 	&#64;CX_JSGetMethod(sync=true)
 * 	com.farata.test.entity.Company getCompany(Integer companyId);
 *	</pre>
 *	</p> 
 *  Example 3: <b>"Read-only" scenario returning dynamic DTO</b>. The argument to the method is the id of the
 *  entity. Presence of full-featured <code>&#64;CX_TransferInfo</code> annotation with specified <code>mappedBy</code>
 *  value makes CDB define a new project-level data type - <code>com.farata.test.dto.CompanyAssociateDTO</code>.
 * <pre>
 * 	&#64;CX_JSGetMethod(transferInfo=&#64;CX_TransferInfo(type="com.farata.test.dto.CompanyAssociateDTO", mappedBy=CompanyAssociate.class))
 * 	Object getAssociate(Integer companyId);
 *	</pre>
 *	</p> 
 *	</p> 
 *  Example 4: <b>"Read-write" scenario utilizing existing com.farata.test.dto.CompanyAssociateDTO type</b>. The argument to the method is the id of the
 *  entity. Presence of short <code>generate=false</code> makes CDB skip generation of the use dynamic DTO expecting it
 *  to be generated by some other 
 *  <a href="http://help.faratasystems.com/htdocs/en_US/cleartoolkit/reference/BlazeDS/4/clear/cdb/annotations/CX_JSJPQLMethod.html">&#64;CX_JSJPQLMethod</a>
 *   or <code>&#64;CX_JSGetMethod</code> annotation within
 *  this project. Because of the <code>sync=true</code> generated service class will contain three extra methods: 
 *  <code>getAssociate_create</code>, <code>getAssociate_update</code> and <code>getAssociate_delete</code> - all
 *  expecting a single argument of type <code>com.farata.test.dto.CompanyAssociateDTO</code>.
 * <pre>
 * 	&#64;CX_JSGetMethod(transferInfo=&#64;CX_TransferInfo(type="com.farata.test.dto.CompanyAssociateDTO", generate=false), sync=true)
 * 	Object getAssociate(Integer associateId); 
 *	</pre>
 *	</p> 
 *
 */



  

 

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface CX_JSGetMethod {
	CX_TransferInfo transferInfo() default @CX_TransferInfo(type="");

	boolean sync() default true;
	
	boolean fillChildren() default false;
}