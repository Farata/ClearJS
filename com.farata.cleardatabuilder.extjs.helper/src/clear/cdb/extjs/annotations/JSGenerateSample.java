package clear.cdb.extjs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Generates sample GridPanel-based CRUD application. 
 * <p>This annotation results in two files: <code>sampleApp.js</code> and <code>controller/SampleControlles.js</code>
 * located in <code><project-root>/WebContent/samples/<interface-package>/<method-name></code> folder, where
 * <code>interface-package</code> and <code>method-name</code> correspond to the class and method annotated
 * with &#64;JSGenerateSample.
 * </p>
 * 
 * @parameter defaultArguments 
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JSGenerateSample {

	String defaultArguments() default "";
}
