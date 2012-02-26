package com.farata.demo.dto;

import com.farata.dto2extjs.annotations.*;

import clear.cdb.utils.SessionFactoryUtils;
import clear.data.IUID;

import java.io.Serializable;

import org.hibernate.Session;

@JSClass(kind=JSClassKind.EXT_JS, ignoreSuperclasses = { IUID.class })
public class CompanyDTO  implements Serializable, IUID{

	private static final long serialVersionUID = 1L;
	
	private String uid;
	@JSIgnore
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
	@JSKeyColumn	
	public java.lang.Integer getId() {
		return id;
	}
	public void setId(java.lang.Integer value) {
		id = value;
	}
		
	protected java.lang.Integer pk;
	
	public java.lang.Integer getPk() {
		return pk;
	}
	public void setPk(java.lang.Integer value) {
		pk = value;
	}
}	