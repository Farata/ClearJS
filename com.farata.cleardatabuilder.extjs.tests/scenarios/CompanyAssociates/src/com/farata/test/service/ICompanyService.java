package com.farata.test.service;

import java.util.List;

import com.farata.test.entity.Company;
import com.farata.test.entity.CompanyAssociate;

import clear.cdb.extjs.annotations.JSGenerateStore;
import clear.cdb.extjs.annotations.JSGenerateSample;
import clear.cdb.extjs.annotations.JSJPQLMethod;
import clear.cdb.extjs.annotations.JSService;
import clear.cdb.extjs.annotations.JSUpdateInfo;
import clear.cdb.extjs.annotations.JSTransferInfo;

@JSService
public interface ICompanyService {
	@JSGenerateSample
	@JSGenerateStore()
	@JSJPQLMethod(
			query="SELECT c FROM Company c",
			transferInfo=@JSTransferInfo(type="com.farata.test.dto.CompanyDTO", mappedBy=Company.class),
			updateInfo=@JSUpdateInfo(updateEntity=Company.class, autoSyncEnabled=true)
		)
		
		List<?> getCompanies();
	
	@JSGenerateStore
	@JSJPQLMethod(
			query="SELECT a FROM CompanyAssociate a WHERE a.company.id=:companyId",
			transferInfo=@JSTransferInfo(type="com.farata.test.dto.CompanyAssociateDTO",mappedBy=CompanyAssociate.class),
			updateInfo=@JSUpdateInfo(updateEntity=CompanyAssociate.class, autoSyncEnabled=true)
		)
		List<?> getAssociates(Integer companyId);
}
