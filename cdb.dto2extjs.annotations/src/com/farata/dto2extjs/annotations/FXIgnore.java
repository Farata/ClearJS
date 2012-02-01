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
 * Is used to restrain or eliminate the property in the ActionScript counterpart of the Java class annotated with &#64;FXClass.
 * <p>When a property is implemented as a public variable, the annotation <code>&#64;FXIgnore</code> is applied to the very public variable.
 * If a getter and, optionally, setter is present, the annotation must be applied to the getter or setter.
 * </p>
 * <p>When <code>&#64;FXIgnore</p> is applied to the variable or to the getter it will eliminate the corresponding ActionScript
 * property completely. If it is applied to the setter, the corresponding ActionScript property will be generated <i>read-only</i>.
 * The later might be useful for annotating interfaces. 
 * </p>
 * <p>Here is an example of the Java DTO interface that marks &#64;FXIgnore-s all setters:
 * * <pre>
 * package com.farata.test.dto;
 * import com.farata.dto2extjs.annotations.FXClass;
 * import com.farata.dto2extjs.annotations.FXIgnore;
 * &#64;FXClass(kind=FXClassKind.REMOTE)
 * public interface IEmployee {
 * 		public String getFirstName( );
 * 		&#64;FXIgnore
 * 		public void setFirstName(String firstName);
 *
 * 		public String getLastName();
 * 		&#64;FXIgnore
 * 		public void setLastName(String lastName);
 * 	
 * 		public Double getSalary();
 * 		&#64;FXIgnore
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
public @interface FXIgnore {
	final public static class any {}
}
