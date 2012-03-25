package com.farata.example.entity;

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

	@Column(name="associate")
	private String associateName;

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

	public String getAssociateName() {
		return this.associateName;
	}

	public void setAssociateName(String associateName) {
		this.associateName = associateName;
	}

	public int getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

}