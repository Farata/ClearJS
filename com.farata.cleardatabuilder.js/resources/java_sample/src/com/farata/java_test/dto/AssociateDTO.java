package com.farata.java_test.dto;

import java.io.Serializable;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSManyToOne;

@JSClass
public class AssociateDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	//mandatory attributes:
	public Integer id;
	public String associateName;
	public Integer companyId;
	
	//many-to-one reference:
	@JSManyToOne(foreignKey="companyId")
	public CompanyDTO company;

	// Implementation of Cloneable is needed only for DataEngine mockup purposes.
	// We avoided constructor with arguments, to keep the essential part of the 
	// class cleaner.
	public Object clone( ) throws CloneNotSupportedException {
		AssociateDTO klon = new AssociateDTO();
		klon.id = id;
		klon.associateName = associateName;
		klon.companyId = companyId;
		klon.company = company;
		return klon;
	}
}
