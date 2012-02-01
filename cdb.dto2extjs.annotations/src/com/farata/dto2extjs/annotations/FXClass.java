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
 * Main annotation to generate the Java's class counterpart in ActionScript for the purpose of two-way marshalling in BlazeDS
 * <p>
 *  <table class="innertable">
 *   <tr>
 *   	<th>Parameter</th><th>Type</th><th>Required</th><th>Description</th>
 *   </tr>
 *   <tr>
 *   	<td><code>value</code></td><td>String</td><td>Optional</td>
 *   	<td>A fully qualified name of the ActionScript class to generate. This is a default parameter.
 *    When omitted, the class and package names mirror class and package name of the Java source.
 *   	</td>
 *   </tr>
 *   <tr>
 *   	<td><code>kind</code></td><td>FXClassKind</td><td>Optional</td>
 *   	<td>Assign to <code>FXClassKind.REMOTE</code> to generate class with meta tag [RemoteClass], assign to
 *      <code>FXClassKind.MANAGED</code> to generate class with meta tag [Managed]. The default is to generate Remote Flex classes unless a class is
annotated with the <code>&#64;javax.jpa.Entity</code>, in which case CDB generates managed classes. To ensure FXClassKind.REMOTE setting for entities you may
set project level APT parameter <code>-Acom.faratasystems.dto2extjs.default-class-kind=remote</code>.
 *   	</td>
 *   </tr>
 *   <tr>
 *   	<td><code>ignoreSuperClasses</code></td><td>Class<?>[]</td><td>Optional</td>
 *   	<td>Array of classes and  interfaces in the inheritance chain of the given class that should be exempted from
 *   translation into corresponding ActionScript classed; by design every ancestor or interface implemented by a &#64;FXClass class must also
 *   be an &#64;FXClass class and gets translated into its ActionScript's counterpart</td> 
 *   </tr>
 *  </table>
 *  <p>Location of the generated ActionScript files is determined by APT parameter <code>-Acom.faratasystems.dto2extjs.output</code>
that you can inspect or modify via <i>Project Properties->Java Compiler->Annotation Processing->Processor Options</i>.  
The output directory always contains two ActionScript classes per one Java source class: 
<li> a class  <code>original_package_name.<b>generated</b>._OriginalClassName</code> with public properties of the original Java class</li>
<li> a descendant of the above class - <code>original_package_name.OriginalClassName</code> for hand-finishing by developers.</li>
Every time you save the Java class annotated with <code>&#64;FXClass</code> the <b>generated</b> ActionScript class is regenerated from scratch.
The descendant class is not getting changed, preserving manual changes intact.
 * </p>
 * <p>
 * In the following example Java class <code>com.farata.test.dto.EmployeeDTO</code> is annotated by <code>&#64;FXClass</code>
 * <pre>
 * package com.farata.test.dto;
 * import com.farata.dto2extjs.annotations.FXClass;
 * 	&#64;FXClass(kind=FXClassKind.REMOTE)
 * 
 * 	public class EmployeeDTO {
 * 		private String firstName;
 * 		public	String lastName;
 * 		public String getFirstName() {
 * 			return firstName;
 * 		}
 * 		public void setFirstName(String firstName) {
 * 			this.firstName = firstName;
 * 		}
 * }
 * </pre>
 * </p>  
 * <p>
 * One of the generated ActionScript classes is shown below. Aside of carrying the <code>[RemoteClass]</code> metatag it delegates
 * all code to the <u>second generated class</u> - <code>_EmployeeDTO</code>, located in the "generated" subfolder:
 * </p>
 * <pre>
 * package com.farata.test.dto {
 *	import com.farata.test.dto.generated._EmployeeDTO
 *	[RemoteClass(alias="com.farata.test.dto.EmployeeDTO")]
 *	public class Employee extends _Employee {
 *		public function Employee():void {
 *			super();
 *		}
 *	}
 * }
 *</pre>
 * <p>
 * Importantly, this ActionScript class gets generated only once. On the contrary, super class <code>_EmployeeDTO</code>, is regenerated
 * on every modification of the Java source. This way developers can add functionality to the super class without intervening
 * with the code generation process: all changes go to the superclass.
 * </p>
 * <p>
 * Generated ActionScript classes carry public properties for every Java public variable or get/set pair which
 * has not been excluded by <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/BlazeDS/4/com/farata/dto2extjs/annotations/FXIgnore.html">&#64;FXIgnore</a>
 * annotation.
 * </p>
 *   
 */ 
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FXClass {
	String value() default "";
	FXClassKind kind() default FXClassKind.DEFAULT;
	Class<?>[] ignoreSuperclasses() default {};
}
