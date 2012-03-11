package com.farata.hibernate_test.dto;

import java.util.List;

import com.farata.dto2extjs.annotations.*;

@JSClass(kind=JSClassKind.EXT_JS)
public class CompanyDTO extends com.farata.hibernate_test.dto.$CompanyDTO {
	
	private List<AssociateDTO> companyAssociates;

	@JSOneToMany(foreignKey="companyId", getter="getAssociates")
	public List<AssociateDTO> getCompanyAssociates() {
		return companyAssociates;
	}

	public void setCompanyAssociates(List<AssociateDTO> companyAssociates) {
		this.companyAssociates = companyAssociates;
	}

	
}		
	