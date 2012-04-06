package com.farata.example.dto;

import java.util.List;

import com.farata.dto2extjs.annotations.*;

@JSClass
public class CompanyDTO  extends com.farata.example.dto.$CompanyDTO{
	@JSOneToMany(foreignKey="companyId", getter="getAssociates")
	public List<AssociateDTO> associates;
}		
	