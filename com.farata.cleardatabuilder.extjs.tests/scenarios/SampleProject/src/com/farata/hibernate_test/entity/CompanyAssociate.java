package com.farata.hibernate_test.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the company_associate database table.
 * 
 */
@Entity
@Table(name="company_associate")
public class CompanyAssociate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private int id;

	private String associate;

	@Column(name="company_id")
	private int companyId;

    public CompanyAssociate() {
    }

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAssociate() {
		return this.associate;
	}

	public void setAssociate(String associate) {
		this.associate = associate;
	}

	public int getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

}