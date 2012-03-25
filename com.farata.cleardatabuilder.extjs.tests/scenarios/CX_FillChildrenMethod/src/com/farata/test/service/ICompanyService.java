package com.farata.test.service;

import java.util.List;

import com.farata.test.entity.Company;
import com.farata.test.entity.CompanyAssociate;
import com.farata.test.entity.Employee;

import clear.cdb.extjs.annotations.CX_JSFillChildrenMethod;
import clear.cdb.extjs.annotations.CX_JSGenerateStore;
import clear.cdb.extjs.annotations.CX_JSGetMethod;
import clear.cdb.extjs.annotations.CX_JSService;
import clear.cdb.extjs.annotations.CX_TransferInfo;

@CX_JSService 
public interface ICompanyService { 

	@CX_JSGenerateStore  
	@CX_JSFillChildrenMethod(parent = Company.class, property = "companyAssociates", transferInfo=@CX_TransferInfo(type="com.farata.test.dto.CompanyAssociateDTO", mappedBy=CompanyAssociate.class))
	List<?> getAssociates1(Integer companyId);   
	
	@CX_JSFillChildrenMethod(parent = Company.class, property = "companyAssociates")
	List<?> getAssociates2(Integer companyId);
	
	@CX_JSFillChildrenMethod(parent = Company.class, property = "companyAssociates")
	List<CompanyAssociate> getAssociates3(Integer companyId);

	@CX_JSFillChildrenMethod(parent = Company.class, property = "companyAssociates", transferInfo=@CX_TransferInfo(type="com.farata.test.dto.CompanyAssociateDTO"))
	List<?> getAssociates4(Integer cmpanyId);  

	@CX_JSGenerateStore
	@CX_JSGetMethod(transferInfo=@CX_TransferInfo(type="com.farata.test.dto.CompanyDTO", mappedBy=Company.class))
	Object getCompany(Integer companyId);
	
	@CX_JSGenerateStore
	@CX_JSGetMethod(transferInfo=@CX_TransferInfo(type="com.farata.test.dto.EmployeeDTO", mappedBy=Employee.class))
	Object getEmployee(Integer emploeeId); 

}