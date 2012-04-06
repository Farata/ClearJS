package com.farata.example.dto;
		
import com.farata.dto2extjs.annotations.*;

import clear.cdb.utils.SessionFactoryUtils;

import java.io.Serializable;
import java.util.*;

import org.hibernate.Session;

@JSClass
public class $AssociateDTO  implements Serializable{

	private static final long serialVersionUID = 1L;	
		
	protected java.lang.Integer id;
	
	public java.lang.Integer getId() {
		return id;
	}
	public void setId(java.lang.Integer value) {
		id = value;
	}
		
	protected java.lang.Integer companyId;
	
	public java.lang.Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(java.lang.Integer value) {
		companyId = value;
	}
		
	protected java.lang.String associateName;
	
	public java.lang.String getAssociateName() {
		return associateName;
	}
	public void setAssociateName(java.lang.String value) {
		associateName = value;
	}
		
}	
	