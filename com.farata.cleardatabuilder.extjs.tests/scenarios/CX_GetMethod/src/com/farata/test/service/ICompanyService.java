package com.farata.test.service;

import java.util.List;

import com.farata.test.entity.Company;
import com.farata.test.entity.CompanyAssociate;

import clear.cdb.extjs.annotations.JSGenerateStore;
import clear.cdb.extjs.annotations.JSGetMethod;
import clear.cdb.extjs.annotations.JSService;
import clear.cdb.extjs.annotations.JSTransferInfo;

@JSService
public interface ICompanyService {

	@JSGenerateStore
	@JSGetMethod(transferInfo=@JSTransferInfo(type="com.farata.test.dto.CompanyAssociateDTO", mappedBy=CompanyAssociate.class))
	Object getAssociate1(Integer associateId);  
	
	@JSGetMethod(fillChildren=true, sync=true, transferInfo=@JSTransferInfo(type="com.farata.test.dto.CompanyDTO", mappedBy=Company.class))
	Object getCompany1(Integer companyId);
	
	@JSGetMethod(transferInfo=@JSTransferInfo(type="com.farata.test.dto.CompanyAssociateDTO"), sync=true)
	Object getAssociate2(Integer associateId);  
	
	@JSGetMethod(fillChildren=true)
	Company getCompany2(Integer companyId); 
	
}