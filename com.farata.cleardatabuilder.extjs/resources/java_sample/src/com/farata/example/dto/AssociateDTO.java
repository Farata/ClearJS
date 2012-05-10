package com.farata.example.dto;

import java.io.Serializable;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSManyToOne;

@JSClass
public class AssociateDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	//mandatory attributes:
	private Integer id;
	private String associateName;
	private Integer companyId;
	private CompanyDTO company;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAssociateName() {
		return associateName;
	}

	public void setAssociateName(String associateName) {
		this.associateName = associateName;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	//many-to-one reference:
	@JSManyToOne(foreignKey="companyId")
	public CompanyDTO getCompany() {
		return company;
	}

	public void setCompany(CompanyDTO company) {
		this.company = company;
	}
}
