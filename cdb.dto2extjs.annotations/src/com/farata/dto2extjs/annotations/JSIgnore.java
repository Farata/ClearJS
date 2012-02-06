/**
 * Copyright (c) 2009 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
package com.farata.dto2extjs.annotations;

import java.lang.annotation.Documented;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;


/**
 * Is used to restrain or eliminate the property in the JavaScript counterpart of the Java class annotated with &#64;JSClass.
 * <p>When a property is implemented as a public variable, the annotation <code>&#64;JSIgnore</code> is applied to the very public variable.
 * If a getter and, optionally, setter is present, the annotation must be applied to the getter or setter.
 * </p>
 * <p>When <code>&#64;JSIgnore</p> is applied to the variable or to the getter it will eliminate the corresponding JavaScript
 * property completely. If it is applied to the setter, the corresponding JavaScript property will be generated <i>read-only</i>.
 * The later might be useful for annotating interfaces. 
 * </p>
 * <p>Here is an example of the Java DTO interface that marks &#64;JSIgnore-s all setters:
 * * <pre>
 * package com.farata.test.dto;
 * import com.farata.dto2extjs.annotations.JSClass;
 * import com.farata.dto2extjs.annotations.JSIgnore;
 * &#64;JSClass(kind=JSClassKind.EXT_JS)
 * public interface IEmployee {
 * 		public String getFirstName( );
 * 		&#64;JSIgnore
 * 		public void setFirstName(String firstName);
 *
 * 		public String getLastName();
 * 		&#64;JSIgnore
 * 		public void setLastName(String lastName);
 * 	
 * 		public Double getSalary();
 * 		&#64;JSIgnore
 * 		public void setSalary(Double salary);
 * }
 * </pre>
 * As always with ClearDataBuilder code generation, there will be two output files, and the re-generated one - 
 * <code>com.farata.test.dto.generated._IEmployee</code> - will look the following way:
 * <pre>
 * package com.farata.test.dto.generated {
 * import flash.events.IEventDispatcher;
 * import mx.core.IUID;
 * [ExcludeClass]
 * public interface _IEmployee extends flash.events.IEventDispatcher, mx.core.IUID {
 * 		function get firstName():String;
 * 		function get lastName():String;
 * 		function get salary():Number;
 * }
 * }
 * </pre>
 * </p>
 */ 
@Documented

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface JSIgnore {
	final public static class any {}
}
