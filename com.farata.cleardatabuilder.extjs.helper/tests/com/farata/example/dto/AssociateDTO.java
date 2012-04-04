package com.farata.example.dto;

import com.farata.dto2extjs.annotations.*;

@JSClass
public class AssociateDTO extends com.farata.example.dto.$AssociateDTO {
	
	private CompanyDTO company;

	@JSManyToOne(foreignKey="companyId")
	public CompanyDTO getCompany() {
		return company;
	}

	public void setCompany(CompanyDTO company) {
		this.company = company;
	}
}		
	