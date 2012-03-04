package com.farata.hibernate_test.dto;

import com.farata.dto2extjs.annotations.*;

@JSClass(kind=JSClassKind.EXT_JS)
public class AssociateDTO extends com.farata.hibernate_test.dto.gen._AssociateDTO {
	@JSManyToOne(foreignKey="companyId")
	public CompanyDTO company;
}		
	