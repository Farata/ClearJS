package com.farata.demo.dto.gen;
		
import com.farata.dto2extjs.annotations.*;

import clear.cdb.utils.SessionFactoryUtils;
import clear.data.IUID;
import flex.messaging.util.UUIDUtils;

import java.io.Serializable;
import java.util.*;

import org.hibernate.Session;

@JSClass(kind=JSClassKind.EXT_JS, ignoreSuperclasses = { IUID.class })
public class _CompanyAssociateDTO  implements Serializable, IUID{

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
		
	protected java.lang.String associate;
	
	public java.lang.String getAssociate() {
		return associate;
	}
	public void setAssociate(java.lang.String value) {
		associate = value;
	}
		
	protected com.farata.demo.dto.gen._CompanyDTO company;

	@JSManyToOne(parent = "com.farata.demo.dto.gen._CompanyDTO", property = "id")			
				
	public com.farata.demo.dto.gen._CompanyDTO getCompany() {
		return company;
	}
	public void setCompany(com.farata.demo.dto.gen._CompanyDTO value) {
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
		
	public com.farata.demo.entity.CompanyAssociate toEntity() {	
		Session session = SessionFactoryUtils.getCurrentSession();
		com.farata.demo.entity.CompanyAssociate entity = (com.farata.demo.entity.CompanyAssociate) session.get(com.farata.demo.entity.CompanyAssociate.class, id);
		return entity;
	}
	
}	
	