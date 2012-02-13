package clear.cdb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Generates GridTest CRUD Flex app with the DataGrid and DataCollection communicating with current annotated method.
 * 
 * @parameter defaultFillArguments Comma separated values to be hardcoded as values of the collection.fill(arg1, arg2,...)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CX_GenerateMXMLSample {

	String defaultFillArguments() default "";
}
