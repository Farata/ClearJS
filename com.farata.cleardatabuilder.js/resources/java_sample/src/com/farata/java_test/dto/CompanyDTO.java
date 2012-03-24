package com.farata.java_test.dto;
		


import java.io.Serializable;
import java.util.*;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSOneToMany;



@JSClass
public class CompanyDTO  implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	
	public java.lang.Integer id;
	public java.lang.String companyName;
	@JSOneToMany(foreignKey="companyId", getter="getAssociates")
	public List<AssociateDTO> associates;
	
	
	// Implementation of Cloneable is needed only for DataEngine mockup purposes.
	// We avoided constructor with arguments, to keep the essential part of the 
	// class cleaner.
	public Object clone( ) throws CloneNotSupportedException {
		CompanyDTO klon = new CompanyDTO();
		klon.id = id;
		klon.companyName = companyName;		
		return klon;
	}
					
}