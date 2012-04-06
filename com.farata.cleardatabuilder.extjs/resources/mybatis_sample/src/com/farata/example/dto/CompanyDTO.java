package com.farata.example.dto;		
import java.io.Serializable;
import java.util.*;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSOneToMany;

@JSClass
public class CompanyDTO  implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	
	private java.lang.Integer id;
	private java.lang.String companyName;
	
	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	public java.lang.String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(java.lang.String companyName) {
		this.companyName = companyName;
	}

	@JSOneToMany(foreignKey="companyId", getter="getAssociates")
	public List<AssociateDTO> associates;
}