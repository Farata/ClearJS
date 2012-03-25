package com.farata.demo.service;

import java.util.List;

import com.farata.demo.dto.CompanyAssociateDTO;
import com.farata.demo.entity.Company;
import com.farata.demo.entity.CompanyAssociate;

import clear.cdb.extjs.annotations.JSGenerateStore;
import clear.cdb.extjs.annotations.JSJPQLMethod;
import clear.cdb.extjs.annotations.JSService;
import clear.cdb.extjs.annotations.JSTransferInfo;
import clear.cdb.extjs.annotations.JSUpdateInfo;

@JSService
public interface CompanyService {

	@JSJPQLMethod(query = "select c from Company c")
	List<?> getCompaniesEmpty();

	@JSJPQLMethod(query = "select c from Company c", transferInfo = @JSTransferInfo(type = "com.farata.demo.dto.CompanyDTO"))
	List<?> getCompaniesTransferInfoType(); 

	@JSJPQLMethod(query = "select c from Company c", transferInfo = @JSTransferInfo(type = "com.farata.demo.dto.CompanyDTO", mappedBy = Company.class))
	List<?> getCompaniesTransferInfoTypeMappedBy();

	@JSJPQLMethod(query = "select c from Company c", transferInfo = @JSTransferInfo(type = "com.farata.demo.dto.CompanyDTO", mappedBy = Company.class), updateInfo = @JSUpdateInfo(updateEntity = Company.class))
	List<?> getCompaniesTransferInfoTypeUpdateEntity();

	@JSJPQLMethod(query = "select c from Company c", transferInfo = @JSTransferInfo(type = "com.farata.demo.dto.CompanyDTO", mappedBy = Company.class), updateInfo = @JSUpdateInfo(updateEntity = Company.class, updatablePropertyNames = "company,pk"))
	List<?> getCompaniesTransferInfoTypeUpdatablePropertyNames(); 

	@JSJPQLMethod(query = "select c from Company c", transferInfo = @JSTransferInfo(type = "com.farata.demo.dto.CompanyDTO", mappedBy = Company.class), updateInfo = @JSUpdateInfo(updateEntity = Company.class, keyPropertyNames="id"))
	List<?> getCompaniesTransferInfoTypeKeyPropertyNames();
	
	@JSGenerateStore
	@JSJPQLMethod(query = "select ca from CompanyAssociate ca", transferInfo = @JSTransferInfo(type = "com.farata.demo.dto.CompanyAssociateDTO", mappedBy = CompanyAssociate.class), updateInfo = @JSUpdateInfo(updateEntity = CompanyAssociate.class))
	List<?> getCompanyAssociates(Object companyId);
	
	@JSJPQLMethod(query = "select ca from CompanyAssociate ca")
	List<CompanyAssociateDTO> getCompanyAssociatesListTypeParameterAsDTO(Object companyId); 

	@JSJPQLMethod(query = "select ca from CompanyAssociate ca")
	List<CompanyAssociate> getCompanyAssociatesListTypeParameterAsEntity(Object companyId);  
}
