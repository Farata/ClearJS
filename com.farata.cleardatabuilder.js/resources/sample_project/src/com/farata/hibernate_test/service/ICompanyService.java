package com.farata.hibernate_test.service;

import java.util.List;

import javax.annotation.Generated;

import com.farata.hibernate_test.entity.Company;
import com.farata.hibernate_test.entity.CompanyAssociate;
import clear.cdb.js.annotations.CX_JSGenerateStore;
import clear.cdb.js.annotations.CX_JSGenerateSample;
import clear.cdb.js.annotations.CX_JSJPQLMethod;
import clear.cdb.js.annotations.CX_JSService;
import clear.cdb.js.annotations.CX_TransferInfo;
import clear.cdb.js.annotations.CX_UpdateInfo;

@CX_JSService
public interface ICompanyService {
	@CX_JSGenerateSample
	@CX_JSGenerateStore
	@CX_JSJPQLMethod(
			query="SELECT c FROM Company c",
			transferInfo=@CX_TransferInfo(type="com.farata.hibernate_test.dto.CompanyDTO"),
			updateInfo=@CX_UpdateInfo(updateEntity=Company.class, keyPropertyNames="id", updatablePropertyNames="company", autoSyncEnabled=true)
	)
	List<?> getCompanies();

	@CX_JSGenerateSample
	@CX_JSGenerateStore
	@CX_JSJPQLMethod(
			query="SELECT a.id, a.companyId, a.associate FROM CompanyAssociate a WHERE a.companyId=:companyId",
			transferInfo=@CX_TransferInfo(type="com.farata.hibernate_test.dto.AssociateDTO"),
			updateInfo=@CX_UpdateInfo(updateEntity=CompanyAssociate.class, keyPropertyNames="id", updatablePropertyNames="companyId,associate", autoSyncEnabled=true)
	)
	List<?> getAssociates(Integer companyId);
}
