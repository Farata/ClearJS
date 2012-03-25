package com.farata.example.service;

import java.util.List;

import javax.annotation.Generated;

import com.farata.example.entity.Company;
import com.farata.example.entity.CompanyAssociate;
import clear.cdb.extjs.annotations.JSGenerateStore;
import clear.cdb.extjs.annotations.JSGenerateSample;
import clear.cdb.extjs.annotations.JSJPQLMethod;
import clear.cdb.extjs.annotations.JSService;
import clear.cdb.extjs.annotations.JSTransferInfo;
import clear.cdb.extjs.annotations.JSUpdateInfo;

@JSService
public interface ICompanyService {
	@JSGenerateSample
	@JSGenerateStore
	@JSJPQLMethod(
			query="SELECT c FROM Company c",
			transferInfo=@JSTransferInfo(type="com.farata.example.dto.CompanyDTO"),
			updateInfo=@JSUpdateInfo(updateEntity=Company.class, keyPropertyNames="id", updatablePropertyNames="companyName", autoSyncEnabled=true)
	)
	List<?> getCompanies();

	@JSGenerateSample
	@JSGenerateStore
	@JSJPQLMethod(
			query="SELECT a.id, a.companyId, a.associateName FROM CompanyAssociate a WHERE a.companyId=:companyId",
			transferInfo=@JSTransferInfo(type="com.farata.example.dto.AssociateDTO"),
			updateInfo=@JSUpdateInfo(updateEntity=CompanyAssociate.class, keyPropertyNames="id", updatablePropertyNames="companyId,associateName", autoSyncEnabled=true)
	)
	List<?> getAssociates(Integer companyId);
}
