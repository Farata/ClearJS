package com.farata.java_test.dto;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import clear.data.IUID;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSClassKind;
import com.farata.dto2extjs.annotations.JSKeyColumn;
import com.farata.dto2extjs.annotations.JSManyToOne;
import com.farata.hibernate_test.dto.CompanyDTO;

@JSClass(kind=JSClassKind.EXT_JS, ignoreSuperclasses = { IUID.class })
public class AssociateDTO implements Serializable, IUID {

	private static final long serialVersionUID = 1L;

	private String uid;
	private Long id;
	private Long companyId;
	private String associate;

	public String getUid() {
		if (uid == null) {
			uid = "" + id+"|";
		}
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * id property.
	 * 
	 * If you keep property variable 'public', your annotations have to be on
	 * the getter
	 */
	@Id
	@GeneratedValue
	@JSKeyColumn
	public Long getId() {
		return id;
	}

	public void setId(Long value) {
		this.id = value;
	}

	/**
	 * companyId property
	 * 
	 * If you keep property variable 'public', your annotations have to be on
	 * the getter
	 */
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long value) {
		this.companyId = value;
	}

	private CompanyDTO company;

	@JSManyToOne(foreignKey="companyId")
	public CompanyDTO getCompany() {
		return company;
	}

	public void setCompany(CompanyDTO company) {
		this.company = company;
	}
	
	/**
	 * associate property
	 */
	public String getAssociate() {
		return associate;
	}

	public void setAssociate(java.lang.String _associate) {
		this.associate = _associate;
	}

	/**
	 * Optional, just to simplify debugging output in Associate.java
	 */
	public String toString() {
		return "AssociateDTO{id:" + id + ",companyId:" + companyId
				+ ",associate:'" + associate + "'}";
	}
}