package com.farata.hibernate_test.service;

import java.util.List;

import com.farata.hibernate_test.entity.Company;
import com.farata.hibernate_test.entity.CompanyAssociate;
import clear.cdb.annotations.CX_GenerateDataCollection;
import clear.cdb.annotations.CX_JPQLMethod;
import clear.cdb.annotations.CX_Service;
import clear.cdb.annotations.CX_TransferInfo;
import clear.cdb.annotations.CX_UpdateInfo;

@CX_Service
public interface ICompanyService {
	@CX_GenerateDataCollection
	@CX_JPQLMethod(
			query="SELECT c FROM Company c",
			transferInfo=@CX_TransferInfo(type="com.farata.hibernate_test.dto.CompanyDTO"),
			updateInfo=@CX_UpdateInfo(updateEntity=Company.class, keyPropertyNames="id", updatablePropertyNames="company", autoSyncEnabled=true)
	)
	List<?> getCompanies();

	@CX_GenerateDataCollection
	@CX_JPQLMethod(
			query="SELECT a.id, a.companyId, a.associate FROM CompanyAssociate a WHERE a.companyId=:companyId",
			transferInfo=@CX_TransferInfo(type="com.farata.hibernate_test.dto.AssociateDTO"),
			updateInfo=@CX_UpdateInfo(updateEntity=CompanyAssociate.class, keyPropertyNames="id", updatablePropertyNames="companyId,associate", autoSyncEnabled=true)
	)
	List<?> getAssociates(Integer companyId);
}
