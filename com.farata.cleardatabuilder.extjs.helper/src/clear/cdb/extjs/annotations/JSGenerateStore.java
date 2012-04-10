package clear.cdb.extjs.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Is used to generate descendants of the  <code>Clear.data.DirectStore</code> with specific <code>model</code> and <code>api</code> config properties. 
 * <p>
 * By default, package and name of the generated store class is derived from the transfer type (see below) of the method by adding a <code>Store</code> suffix.
 * For instance, if transfer type is <code>com.farata.example.dto.CompanyDTO</code> , generated store will be <code>store.com.farata.example.CompanyStore</code>
 * You can change the naming schema of the generated store by playing with the value of 
 * the APT parameter <code>com.faratasystems.cdbjs.store.class-name-transformer</code> located in <code>.settings/org.eclipse.apt.core.prefs</code>:
 * <table class="innertable" width="100%">
 *   <tr>
 *   	<th>Setting</th><th>Java Class Name</th><th>Model Name</th>
 *   </tr>
 *   <tr>
 *   	<td><code>MyApp.store</code></td><td>com.farata.dto.UserDTO</td><td>MyApp.store.com.farata.dto.UserDTO</td>
 *   </tr>
 *   <tr>
 *   	<td><code>MyApp.store.$1$3<<^com.farata.((\w+\.)*)dto.(\w+)DTO$</code></td><td><code>com.farata.example.dto.UserDTO</code></td><td><code>MyApp.store.example.User</code></td>
 *   </tr>
 *   <tr>
 *   	<td><code>MyApp.store.$1$3Store<<^com.farata.((\w+\.)*)dto.(\w+)DTO$</code></td><td><code>com.farata.example.dto.UserDTO</code></td><td><code>MyApp.store.example.UserStore</code></td>
 *   </tr>
 *  </table>
 * </>
 * </p>
 * <p>Transfer type gets determined in the following order:
 * <li>if return type of the annotated method is not raw and does not contain a wildcard then the base type of the collection becomes the
 * transfer type;</li>
 * <li>if the method is annotated with <code>&#64;JSJPQLMethod</code> annotation with the <code>transferInfo</code> parameter, the <code>type</code> subparameter of
 * the <code>transferInfo</code> becomes the transfer type;</li>
 * <li>if none of the above conditions are met, transfer type remains unknown</li>
 * </p> 
 * <p>If transfer type is not known, developer has to explicitly provide the value of the <code>storeType</code> parameter, otherwise no collection
 * will be generated at all. A special care has to be taken of the related &#64;JSOneToMany annotations preceding getters of such transfer type: developer has to
 * explicitly provide the same value of the <code>storeType</code> there as well.  
 * </p>
 * <p>
 *  Example 1: When the transfer type is known, it is recommended to rely on default naming of the generated store. Here, based on the 
 *  known transfer type - <code>com.farata.test.entity.Company</code> - code generator will create <code>store.com.farata.test.entity.CompanyStore</code>:
 * <pre>
 * 	&#64;JSGenerateStore
 * 	&#64;JSJPQLMethod(
 * 		query="SELECT c FROM Company c"
 * 	)
 * 	List&lt;com.farata.test.entity.Company&gt; getCompanies();
 * </pre>
 * </p>
 * <p>
 * 	Example 2: When the transfer type is not known, <code>storeType</code> has to be provided by the developer explicitly:
 *  <pre>
 * 	&#64;JSGenerateStore(storeType="store.com.farata.CompanyStore")
 * 	&#64;JSJPQLMethod(
 * 		query="SELECT c FROM Company c"
 * 	)
 * 	List&lt;?&gt; getCompanies();
* </pre>
 * </p>
 * <p>
 * 	Example 3: In relation with Example 2, <code>&#64;JSOneToMany</code> annotation of the <code>companyAssociates</code> property 
 * in the <code>com.farata.test.entity.Company</code> class has to explicitly specify the <code>storeType</code> to match
 * that of the related <code>&#64;JSGenerateStore</code> annotation:
 *  <pre>
 * 	&#64;JSOneToMany(foreignKey="companyId", storeType="store.com.farata.CompanyStore")
 * 	public List<CompanyAssociate> companyAssociates;
 * </pre>
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JSGenerateStore {
	/**
	 * Fully qualified name of the DataCollection descendant, i.e. "com.farata.test.collections.CompanyCollection".
	 * @return
	 */
	String storeType() default "";
}
