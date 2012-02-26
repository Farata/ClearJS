package com.farata.java_test.dto;

import java.io.Serializable;

import java.util.List;

import flex.messaging.util.UUIDUtils;
import clear.data.IUID;

import com.farata.dto2extjs.annotations.*;
import com.farata.dto2extjs.annotations.JSOneToMany.SyncType;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@JSClass(ignoreSuperclasses = { IUID.class })
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

	@JSOneToMany(collectionType = "com.farata.java_test.collections.AssociateCollection", fillArguments = "id", sync = SyncType.BATCH)
	public List<AssociateDTO> getAssociates() {
		return associates;
	}

	public void setAssociates(List<AssociateDTO> associates) {
		this.associates = associates;
	}

	/**
	 * Obligatory implementation of IUID
	 */
	@JSIgnore
	public String getUid() {
		if (uid == null) {
			uid = "" + id;
			if (uid.equals(""))
				uid = UUIDUtils.createUUID(false);
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