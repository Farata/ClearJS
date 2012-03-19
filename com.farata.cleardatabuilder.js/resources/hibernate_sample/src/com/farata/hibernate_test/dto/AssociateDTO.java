package com.farata.hibernate_test.dto;

import com.farata.dto2extjs.annotations.*;

@JSClass(kind=JSClassKind.EXT_JS)
public class AssociateDTO extends com.farata.hibernate_test.dto.$AssociateDTO {
	
	private CompanyDTO company;

	@JSManyToOne(foreignKey="companyId")
	public CompanyDTO getCompany() {
		return company;
	}

	public void setCompany(CompanyDTO company) {
		this.company = company;
	}
}		
	