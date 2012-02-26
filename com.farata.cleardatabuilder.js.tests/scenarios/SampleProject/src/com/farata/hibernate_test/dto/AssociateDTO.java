package com.farata.hibernate_test.dto;

import com.farata.dto2extjs.annotations.*;

@JSClass(kind = JSClassKind.EXT_JS)
public class AssociateDTO extends com.farata.hibernate_test.dto.gen._AssociateDTO {

	@JSManyToOne(parent = "com.farata.hibernate_test.dto.CompanyDTO", property = "id")
	public Integer getCompanyId() {
		return super.getCompanyId();
	}

	public void setCompanyId(Integer value) {
		super.setCompanyId(value);
	}
}
