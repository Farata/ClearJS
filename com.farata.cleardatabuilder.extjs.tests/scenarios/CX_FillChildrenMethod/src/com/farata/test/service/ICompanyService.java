package com.farata.test.service;

import java.util.List;

import com.farata.test.entity.Company;
import com.farata.test.entity.CompanyAssociate;
import com.farata.test.entity.Employee;

import clear.cdb.extjs.annotations.JSFillChildrenMethod;
import clear.cdb.extjs.annotations.JSGenerateStore;
import clear.cdb.extjs.annotations.JSGetMethod;
import clear.cdb.extjs.annotations.JSService;
import clear.cdb.extjs.annotations.JSTransferInfo;

@JSService 
public interface ICompanyService { 

	@JSGenerateStore  
	@JSFillChildrenMethod(parent = Company.class, property = "companyAssociates", transferInfo=@JSTransferInfo(type="com.farata.test.dto.CompanyAssociateDTO", mappedBy=CompanyAssociate.class))
	List<?> getAssociates1(Integer companyId);   
	
	@JSFillChildrenMethod(parent = Company.class, property = "companyAssociates")
	List<?> getAssociates2(Integer companyId);
	
	@JSFillChildrenMethod(parent = Company.class, property = "companyAssociates")
	List<CompanyAssociate> getAssociates3(Integer companyId);

	@JSFillChildrenMethod(parent = Company.class, property = "companyAssociates", transferInfo=@JSTransferInfo(type="com.farata.test.dto.CompanyAssociateDTO"))
	List<?> getAssociates4(Integer cmpanyId);  

	@JSGenerateStore
	@JSGetMethod(transferInfo=@JSTransferInfo(type="com.farata.test.dto.CompanyDTO", mappedBy=Company.class))
	Object getCompany(Integer companyId);
	
	@JSGenerateStore
	@JSGetMethod(transferInfo=@JSTransferInfo(type="com.farata.test.dto.EmployeeDTO", mappedBy=Employee.class))
	Object getEmployee(Integer emploeeId); 

}