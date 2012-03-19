package com.farata.hibernate_test.dto;
		
import com.farata.dto2extjs.annotations.*;

import clear.cdb.utils.SessionFactoryUtils;
import clear.data.IUID;

import java.io.Serializable;
import java.util.*;

import org.hibernate.Session;

@JSClass(kind=JSClassKind.EXT_JS, ignoreSuperclasses = { IUID.class })
public class $AssociateDTO  implements Serializable, IUID{

	private static final long serialVersionUID = 1L;
	
	private String uid;
	
	public String getUid() {
		if (uid == null) {
			uid = "" + id+"|";
		}
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
		
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
	