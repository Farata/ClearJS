package com.farata.hibernate_test.service;

import java.util.List;

import com.farata.hibernate_test.entity.Company;
import com.farata.hibernate_test.entity.CompanyAssociate;
import clear.cdb.extjs.annotations.JSGenerateStore;
import clear.cdb.extjs.annotations.JSJPQLMethod;
import clear.cdb.extjs.annotations.JSService;
import clear.cdb.extjs.annotations.JSTransferInfo;
import clear.cdb.extjs.annotations.JSUpdateInfo;

@JSService
public interface ICompanyService {
	@JSGenerateStore
	@JSJPQLMethod(
			query="SELECT c FROM Company c",
			transferInfo=@JSTransferInfo(type="com.farata.hibernate_test.dto.CompanyDTO"),
			updateInfo=@JSUpdateInfo(updateEntity=Company.class, keyPropertyNames="id", updatablePropertyNames="company", autoSyncEnabled=true)
	)
	List<?> getCompanies();

	@JSGenerateStore
	@JSJPQLMethod(
			query="SELECT a.id, a.companyId, a.associate FROM CompanyAssociate a WHERE a.companyId=:companyId",
			transferInfo=@JSTransferInfo(type="com.farata.hibernate_test.dto.AssociateDTO"),
			updateInfo=@JSUpdateInfo(updateEntity=CompanyAssociate.class, keyPropertyNames="id", updatablePropertyNames="companyId,associate", autoSyncEnabled=true)
	)
	List<?> getAssociates(Integer companyId);
}
