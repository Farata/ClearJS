package com.farata.example.dto;
		
import com.farata.dto2extjs.annotations.*;

import clear.cdb.utils.SessionFactoryUtils;

import java.io.Serializable;
import java.util.*;

import org.hibernate.Session;

@JSClass
public class $CompanyDTO  implements Serializable{

	private static final long serialVersionUID = 1L;	
		
	protected java.lang.String companyName;
	
	public java.lang.String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(java.lang.String value) {
		companyName = value;
	}
		
	protected java.lang.Integer id;
	
	public java.lang.Integer getId() {
		return id;
	}
	public void setId(java.lang.Integer value) {
		id = value;
	}
		
}	
	