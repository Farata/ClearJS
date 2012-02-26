package com.farata.java_test.dto;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import flex.messaging.util.UUIDUtils;
import clear.data.IUID;

import com.farata.dto2extjs.annotations.*;

@JSClass(ignoreSuperclasses = { IUID.class })
public class AssociateDTO implements Serializable, IUID {

	private static final long serialVersionUID = 1L;

	private String uid;
	private Long id;
	private Long companyId;
	private String associate;

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
	@JSManyToOne(parent = "com.farata.java_test.dto.CompanyDTO", property = "id")
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long value) {
		this.companyId = value;
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