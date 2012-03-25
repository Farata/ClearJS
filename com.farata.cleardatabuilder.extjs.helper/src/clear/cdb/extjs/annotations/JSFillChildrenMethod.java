package clear.cdb.extjs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JSFillChildrenMethod {
	JSTransferInfo transferInfo() default @JSTransferInfo(type="");
	Class<?> parent();
	String property();
}
