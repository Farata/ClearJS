package com.farata.hibernate_test.dto;

import java.util.List;

import com.farata.dto2extjs.annotations.*;

@JSClass(kind=JSClassKind.EXT_JS)
public class CompanyDTO extends com.farata.hibernate_test.dto.gen._CompanyDTO {
	@JSOneToMany(foreignKey="companyId", getter="getAssociates")
	public List<AssociateDTO> companyAssociates;
}		
	