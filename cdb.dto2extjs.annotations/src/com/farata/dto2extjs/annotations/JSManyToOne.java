package com.farata.dto2extjs.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Is used to maintain link between the auto-incremented primaryKey of the parent model and 
 * corresponding foreignKey of the child one.
 * <p>
 *  <table class="innertable">
 *   <tr>
 *   	<th>Parameter</th><th>Type</th><th>Required</th><th>Description</th>
 *   </tr>
 *   <tr>
 *   	<td><code>primaryKey</code></td><td>String</td><td>Optional</td>
 *   	<td>The name of the primaryKey property in the parent's class. Default value is <code>id</code>.
 *   	</td>
 *   </tr>
 *   <tr>
 *   	<td><code>foreignKey</code></td><td>String</td><td>Required</td>
 *   	<td>Name of a property in this model that is many-to-one related to the property you annotate
 *   	</td>
 *   </tr>
 *  </table>
 *  </p>  
 * <p>
 * The following snippet illustrates how a <code>departmentId</code> property in <code>EmployeeDTO</code> is many-to-one
 * mapped to the parent class <code>Department</code>. The primary key (of the parent) is, by default, <code>id</code>:
 * <pre>
 * &#64;JSClass
 * public class EmployeeDTO {
 *   .  .  .
 *	 public String departmentId;
 *	 &#64;JSManyToOne(foreignKey = "departmentId")
 *	 public DepartmentDTO department;
 * }
 * </pre>
 * </p>
 * <p>
 * A transaction inserting records into two PK-FK related tables may need a special treatment when the database autoincrements primary keys.
 * The problem is that auto-incremented values are available only upon <u>completion</u> of INSERT of the parent.
 * The very same values take place as foreign keys in the associated child record. And, to
 * adhere to referential constraints, application
 * need to properly set them <u>prior</u> to INSERT of the children.
 * </p>
 * <p>Clear Data Builder's technique to handle this requirement assumes that:<br> 
 * a) JavaScript code should use
 * locally unique negative values and assign them to both parent's PK and children FK fields.<br>
 * b) Java server code has to replace the negative FK values with the real ones available upon inserting the parent records.
 * </p>
 * <p>
 * For instance, if a company department has many employees, and <code>departmentDTO.id</code> is initialized by application to -1,
 * every one-to-many child <code>employeeDTO</code> should arrive to the server with <code>employeeDTO.departmentId</code> preset to -1 as well.
 * If there is another new <code>departmentDTO</code> in the same transaction batch it should come with PK=-2, and, it's children with
 * FK=-2, and so on.
 * </p>
 * <p>The following snippet of code illustrates the Java code that adheres to this technique:
 * <PRE>
 *  // Departments get inserted into the database
 * 	public void getDepartments_doCreate(ChangeObject changeObject) {
		DepartmentDTO dto = (DepartmentDTO) changeObject.getNewVersion();
		Object tempId = dto.getId();
		departmentMyBatisMapper.create(dto); 
		changeObject.addChangedPropertyName("id"); //PK got autoincremented
		Object primaryKey = dto.getId();
		IdExhange.put("clear.samples.dto.DepartmentDTO", "id", tempId, primaryKey);
	}
	.....
	// Employees get inserted into the database
 * 	public void getEmployees_doCreate(ChangeObject changeObject) {
		EmployeeDTO dto = (EmployeeDTO) changeObject.getNewVersion();
		Object tempId = dto.getId();
		Object foreignKey = IdExhange.get("clear.samples.dto.DepartmentDTO", "id");
		employeeMyBatisMapper.create(dto); 
		changeObject.addChangedPropertyName("id"); //PK got autoincremented
		Object primaryKey = dto.getId();
		IdExhange.put("clear.samples.dto.EmployeeDTO", "id", tempId,primaryKey);
	}
	</p>
 */ 
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD, ElementType.FIELD })
public @interface JSManyToOne {
	public abstract String primaryKey() default "id";
	public abstract String foreignKey();
}
