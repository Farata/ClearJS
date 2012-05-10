package com.farata.example.dto;

import java.util.List;

import com.farata.dto2extjs.annotations.*;

@JSClass
public class CompanyDTO  extends com.farata.example.dto.$CompanyDTO{
	private List<AssociateDTO> associates;

	@JSOneToMany(foreignKey="companyId", getter="getAssociates")
	public List<AssociateDTO> getAssociates() {
		return associates;
	}

	public void setAssociates(List<AssociateDTO> associates) {
		this.associates = associates;
	}
}		
	