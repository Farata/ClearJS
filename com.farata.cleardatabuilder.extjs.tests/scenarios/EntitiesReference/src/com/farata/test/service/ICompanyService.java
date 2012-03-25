package com.farata.test.service;

import java.util.List;

import com.farata.test.entity.Company;
import com.farata.test.entity.CompanyAssociate;

import clear.cdb.extjs.annotations.JSGenerateStore;
import clear.cdb.extjs.annotations.JSGenerateSample;
import clear.cdb.extjs.annotations.JSJPQLMethod;
import clear.cdb.extjs.annotations.JSService;
import clear.cdb.extjs.annotations.JSTransferInfo;
import clear.cdb.extjs.annotations.JSUpdateInfo;

@JSService
public interface ICompanyService {

	@JSGenerateStore
	@JSGenerateSample
	@JSJPQLMethod(query = "SELECT c FROM Company c", 
			updateInfo=@JSUpdateInfo(updateEntity = Company.class, autoSyncEnabled = true))
	
	List<?> getCompanies();

	@JSGenerateStore
	@JSGenerateSample
	@JSJPQLMethod(query = "SELECT a FROM CompanyAssociate a WHERE a.company.id=:company",
			updateInfo=@JSUpdateInfo(updateEntity = CompanyAssociate.class, autoSyncEnabled = true))
	
	List getAssociates(Integer company);
}
