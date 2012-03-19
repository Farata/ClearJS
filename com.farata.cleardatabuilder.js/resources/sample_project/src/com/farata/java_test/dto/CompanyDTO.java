package com.farata.java_test.dto;

import java.io.Serializable;

import java.util.List;

import clear.data.IUID;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSClassKind;
import com.farata.dto2extjs.annotations.JSKeyColumn;
import com.farata.dto2extjs.annotations.JSOneToMany;
import com.farata.hibernate_test.dto.AssociateDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@JSClass(kind=JSClassKind.EXT_JS, ignoreSuperclasses = { IUID.class })
public class CompanyDTO implements Serializable, IUID {

	private static final long serialVersionUID = 1L;

	private String uid;
	private Long id;
	private String company;
	private List<AssociateDTO> associates;

	@Id
	@GeneratedValue
	@JSKeyColumn
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	private List<AssociateDTO> companyAssociates;

	
	@JSOneToMany(foreignKey="companyId", getter="getAssociates")
	public List<AssociateDTO> getAssociates() {
		return associates;
	}

	public void setAssociates(List<AssociateDTO> associates) {
		this.associates = associates;
	}

	/**
	 * Obligatory implementation of IUID
	 */
	public String getUid() {
		if (uid == null) {
			uid = "" + id+"|";
		}
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	/*
	 * Business domain properties
	 */

	/**
	 * Optional, just to simplify debugging output
	 */
	public String toString() {
		return "CompanyDTO{id:" + id + ",company:'" + company + "'}";
	}
}