package com.farata.test.service;

import java.util.List;

import com.farata.test.entity.Company;
import com.farata.test.entity.CompanyAssociate;

import clear.cdb.js.annotations.CX_JSGenerateStore;
import clear.cdb.js.annotations.CX_JSGenerateSample;
import clear.cdb.js.annotations.CX_JSJPQLMethod;
import clear.cdb.js.annotations.CX_JSService;
import clear.cdb.js.annotations.CX_UpdateInfo;
import clear.cdb.js.annotations.CX_TransferInfo;

@CX_JSService
public interface ICompanyService {
	@CX_JSGenerateSample
	@CX_JSGenerateStore()
	@CX_JSJPQLMethod(
			query="SELECT c FROM Company c",
			transferInfo=@CX_TransferInfo(type="com.farata.test.dto.CompanyDTO", mappedBy=Company.class),
			updateInfo=@CX_UpdateInfo(updateEntity=Company.class, autoSyncEnabled=true)
		)
		
		List<?> getCompanies();
	
	@CX_JSGenerateStore
	@CX_JSJPQLMethod(
			query="SELECT a FROM CompanyAssociate a WHERE a.company.id=:companyId",
			transferInfo=@CX_TransferInfo(type="com.farata.test.dto.CompanyAssociateDTO",mappedBy=CompanyAssociate.class),
			updateInfo=@CX_UpdateInfo(updateEntity=CompanyAssociate.class, autoSyncEnabled=true)
		)
		List<?> getAssociates(Integer companyId);
}
