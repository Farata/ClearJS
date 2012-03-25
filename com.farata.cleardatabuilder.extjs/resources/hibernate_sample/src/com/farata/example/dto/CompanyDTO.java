package com.farata.example.dto;

import java.util.List;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSOneToMany;

@JSClass
public class CompanyDTO extends $CompanyDTO {

	private List<AssociateDTO> companyAssociates;
	
	@JSOneToMany(foreignKey="companyId", getter="getAssociates")
	public List<AssociateDTO> getCompanyAssociates() {
		return companyAssociates;
	}
	
	public void setCompanyAssociates(List<AssociateDTO> companyAssociates) {
		this.companyAssociates = companyAssociates;
	}
}