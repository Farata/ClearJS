package clear.cdb.extjs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface CX_UpdateInfo {
	Class<?> updateEntity();

	String keyPropertyNames() default "";

	String updatablePropertyNames() default "";

	boolean autoSyncEnabled() default false;
}
