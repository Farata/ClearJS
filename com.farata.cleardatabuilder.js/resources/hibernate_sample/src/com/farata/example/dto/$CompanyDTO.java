package com.farata.example.dto;
		
import com.farata.dto2extjs.annotations.*;

import java.io.Serializable;
import java.util.*;

@JSClass
public class $CompanyDTO  implements Serializable, IUID{

	private static final long serialVersionUID = 1L;
		
	protected java.lang.String company;
	
	public java.lang.String getCompany() {
		return company;
	}
	public void setCompany(java.lang.String value) {
		company = value;
	}
		
	protected java.lang.Integer id;
	
	public java.lang.Integer getId() {
		return id;
	}
	public void setId(java.lang.Integer value) {
		id = value;
	}		
}	
	