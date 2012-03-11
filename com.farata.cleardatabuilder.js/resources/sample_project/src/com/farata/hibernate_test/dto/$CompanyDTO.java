package com.farata.hibernate_test.dto;
		
import com.farata.dto2extjs.annotations.*;

import clear.cdb.utils.SessionFactoryUtils;
import clear.data.IUID;

import java.io.Serializable;
import java.util.*;

import org.hibernate.Session;

@JSClass(kind=JSClassKind.EXT_JS, ignoreSuperclasses = { IUID.class })
public class $CompanyDTO  implements Serializable, IUID{

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
	