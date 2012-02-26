package com.farata.test.service;

import java.util.List;

import com.farata.test.entity.Company;
import com.farata.test.entity.CompanyAssociate;

import clear.cdb.js.annotations.CX_JSGenerateStore;
import clear.cdb.js.annotations.CX_JSGetMethod;
import clear.cdb.js.annotations.CX_JSService;
import clear.cdb.js.annotations.CX_TransferInfo;

@CX_JSService
public interface ICompanyService {

	@CX_JSGenerateStore
	@CX_JSGetMethod(transferInfo=@CX_TransferInfo(type="com.farata.test.dto.CompanyAssociateDTO", mappedBy=CompanyAssociate.class))
	Object getAssociate1(Integer associateId);  
	
	@CX_JSGetMethod(fillChildren=true, sync=true, transferInfo=@CX_TransferInfo(type="com.farata.test.dto.CompanyDTO", mappedBy=Company.class))
	Object getCompany1(Integer companyId);
	
	@CX_JSGetMethod(transferInfo=@CX_TransferInfo(type="com.farata.test.dto.CompanyAssociateDTO"), sync=true)
	Object getAssociate2(Integer associateId);  
	
	@CX_JSGetMethod(fillChildren=true)
	Company getCompany2(Integer companyId); 
	
}