package clear.cdb.extjs.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Is used to generate descendants of the  DataCollection with preset <code>destination</code> and <code>fillMethod</code>. 
 * <p>
 * By default, the package and the name of the generated collection class is derived from the package and the type of the method's transfer type: 
 * <li>if transfer type is "com.farata.test.anyname.Company" , generated collection will be "com.farata.test.collections.CompanyCollection"</li> 
 * <li>if transfer type is "Company" , generated collection will be "collections.CompanyCollection"</li>
 * </p>
 * <p>Transfer type gets determined in the following order:
 * <li>if return type of the annotated method (collection is assumed) is not raw and does not contain a wildcard then the base type of the collection becomes the
 * transfer type;</li>
 * <li>if the method is annotated with <code>&#64;JSJPQLMethod</code> annotation with the <code>transferInfo</code> parameter, the <code>type</code> subparameter of
 * the <code>transferInfo</code> becomes the transfer type;</li>
 * <li>if none of the above conditions are met, transfer type remains unknown</li>
 * </p> 
 * <p>If transfer type is not known, developer has to explicitly provide the value of the <code>collectionType</code> parameter, otherwise no collection
 * will be generated at all. A special care has to be taken of the related &#64;FXOneToMany annotations preceding getters of such transfer type: developer has to
 * explicitly provide the same value of the <code>collectionType</code> there as well.  
 * </p>
 * <p>
 *  Example 1: When the transfer type is known, it is recommended to rely on default naming of the generated collection. Here, based on the 
 *  known transfer type - com.farata.test.entity.Company - code generator will create com.farata.test.collections.CompanyCollection:
 * <pre>
 * 	&#64;JSGenerateStore
 * 	&#64;JSJPQLMethod(
 * 		query="SELECT c FROM Company c"
 * 	)
 * 	List&lt;com.farata.test.entity.Company&gt; getCompanies();
 * </pre>
 * </p>
 * <p>
 * 	Example 2: When the transfer type is not known, <code>collectionType</code> has to be provided by the developer explicitly:
 *  <pre>
 * 	&#64;JSGenerateStore(collectionType="com.farata.collections.MyCompanyCollection")
 * 	&#64;JSJPQLMethod(
 * 		query="SELECT c FROM Company c"
 * 	)
 * 	List&lt;?&gt; getCompanies();
* </pre>
 * </p>
 * <p>
 * 	Example 3: In relation with Example 2, <code>&#64;FXOneToMany</code> annotation of the <code>companyAssociates</code> property 
 * in the <code>com.farata.test.entity.Company</code> class has to explicitly specify the <code>collectionType</code> to match
 * that of the related <code>&#64;JSGenerateStore</code> annotation:
 *  <pre>
 * 	&#64;FXOneToMany(fillArguments="id", collectionType="com.farata.collections.MyCompanyCollection")
 * 	public Set<CompanyAssociate> getCompanyAssociates() {
 *		return this.companyAssociates;
 * 	}
 *
 * 	public void setCompanyAssociates(Set<CompanyAssociate> companyAssociates) {
 *		this.companyAssociates = companyAssociates;
 * 	}
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
	String collectionType() default "";
}
