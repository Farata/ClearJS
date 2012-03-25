package com.farata.example.service;

import java.util.List;

import javax.annotation.Generated;

import com.farata.example.entity.Company;
import com.farata.example.entity.CompanyAssociate;
import clear.cdb.extjs.annotations.CX_JSGenerateStore;
import clear.cdb.extjs.annotations.CX_JSGenerateSample;
import clear.cdb.extjs.annotations.CX_JSJPQLMethod;
import clear.cdb.extjs.annotations.CX_JSService;
import clear.cdb.extjs.annotations.CX_TransferInfo;
import clear.cdb.extjs.annotations.CX_UpdateInfo;

@CX_JSService
public interface ICompanyService {
	@CX_JSGenerateSample
	@CX_JSGenerateStore
	@CX_JSJPQLMethod(
			query="SELECT c FROM Company c",
			transferInfo=@CX_TransferInfo(type="com.farata.example.dto.CompanyDTO"),
			updateInfo=@CX_UpdateInfo(updateEntity=Company.class, keyPropertyNames="id", updatablePropertyNames="companyName", autoSyncEnabled=true)
	)
	List<?> getCompanies();

	@CX_JSGenerateSample
	@CX_JSGenerateStore
	@CX_JSJPQLMethod(
			query="SELECT a.id, a.companyId, a.associateName FROM CompanyAssociate a WHERE a.companyId=:companyId",
			transferInfo=@CX_TransferInfo(type="com.farata.example.dto.AssociateDTO"),
			updateInfo=@CX_UpdateInfo(updateEntity=CompanyAssociate.class, keyPropertyNames="id", updatablePropertyNames="companyId,associateName", autoSyncEnabled=true)
	)
	List<?> getAssociates(Integer companyId);
}
