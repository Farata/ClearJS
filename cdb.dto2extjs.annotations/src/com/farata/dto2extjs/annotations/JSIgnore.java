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
 * Is preventing translation of the Java property into a corresponding Ext JS model field.
 * Should be used only on a property of the class annotated with <a href="http://help.faratasystems.com/en_US/cleartoolkit/reference/java/extjs/com/farata/dto2extjs/annotations/JSClass.html">&#64;JSClass</a>.
 * <p>When <code>&#64;JSIgnore</p> is applied to the variable or to the getter it will eliminate the corresponding field
 * property completely. If it is applied to the setter, the corresponding field will be implemented as <i>read-only</i>.
 * </p>
 * <p>If a property is implemented as Java setter/getter pair the annotation should be placed on the getter even
 * if the backing variable is public (see example below)</p>
 * </p>
 * <p>Here is an example of the Java DTO interface that &#64;JSIgnore-s all setters:
 * * <pre>
 * package com.farata.test.dto;
 * import com.farata.dto2extjs.annotations.JSClass;
 * import com.farata.dto2extjs.annotations.JSIgnore;
 * &#64;JSClass(kind=JSClassKind.EXT_JS)
package clear.dto;

import java.util.Date;
import java.util.List;


import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSIgnore;
import com.farata.dto2extjs.annotations.JSOneToMany;

&#64;JSClass
public class UserDTO {
	public String id;
	&#64;JSIgnore
	public Double salary;

	private Date dob;
	&#64;JSIgnore
	public Date getDob() {
		return dob;
	}
	public void setDob(Date value) {
		dob = value;
	}

	@JSOneToMany(storeType="helpdesk.Tickets", foreignKey="userId")
	public List<TicketDTO> tickets;
	
}

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
