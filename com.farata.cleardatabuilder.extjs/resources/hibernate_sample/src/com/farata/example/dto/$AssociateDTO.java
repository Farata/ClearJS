package com.farata.example.dto;
		
import com.farata.dto2extjs.annotations.*;
import java.io.Serializable;
import java.util.*;

@JSClass
public class $AssociateDTO  implements Serializable {

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
		
	protected java.lang.String associate;
	
	public java.lang.String getAssociate() {
		return associate;
	}
	public void setAssociate(java.lang.String value) {
		associate = value;
	}
		
}	
	