package clear.cdb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CX_FillChildrenMethod {
	CX_TransferInfo transferInfo() default @CX_TransferInfo(type="");
	Class<?> parent();
	String property();
}
