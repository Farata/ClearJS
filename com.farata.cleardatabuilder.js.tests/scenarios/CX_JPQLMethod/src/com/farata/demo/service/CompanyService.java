package com.farata.demo.service;

import java.util.List;

import com.farata.demo.dto.CompanyAssociateDTO;
import com.farata.demo.entity.Company;
import com.farata.demo.entity.CompanyAssociate;

import clear.cdb.js.annotations.CX_JSGenerateStore;
import clear.cdb.js.annotations.CX_JSJPQLMethod;
import clear.cdb.js.annotations.CX_JSService;
import clear.cdb.js.annotations.CX_TransferInfo;
import clear.cdb.js.annotations.CX_UpdateInfo;

@CX_JSService
public interface CompanyService {

	@CX_JSJPQLMethod(query = "select c from Company c")
	List<?> getCompaniesEmpty();

	@CX_JSJPQLMethod(query = "select c from Company c", transferInfo = @CX_TransferInfo(type = "com.farata.demo.dto.CompanyDTO"))
	List<?> getCompaniesTransferInfoType(); 

	@CX_JSJPQLMethod(query = "select c from Company c", transferInfo = @CX_TransferInfo(type = "com.farata.demo.dto.CompanyDTO", mappedBy = Company.class))
	List<?> getCompaniesTransferInfoTypeMappedBy();

	@CX_JSJPQLMethod(query = "select c from Company c", transferInfo = @CX_TransferInfo(type = "com.farata.demo.dto.CompanyDTO", mappedBy = Company.class), updateInfo = @CX_UpdateInfo(updateEntity = Company.class))
	List<?> getCompaniesTransferInfoTypeUpdateEntity();

	@CX_JSJPQLMethod(query = "select c from Company c", transferInfo = @CX_TransferInfo(type = "com.farata.demo.dto.CompanyDTO", mappedBy = Company.class), updateInfo = @CX_UpdateInfo(updateEntity = Company.class, updatablePropertyNames = "company,pk"))
	List<?> getCompaniesTransferInfoTypeUpdatablePropertyNames(); 

	@CX_JSJPQLMethod(query = "select c from Company c", transferInfo = @CX_TransferInfo(type = "com.farata.demo.dto.CompanyDTO", mappedBy = Company.class), updateInfo = @CX_UpdateInfo(updateEntity = Company.class, keyPropertyNames="id"))
	List<?> getCompaniesTransferInfoTypeKeyPropertyNames();
	
	@CX_JSGenerateStore
	@CX_JSJPQLMethod(query = "select ca from CompanyAssociate ca", transferInfo = @CX_TransferInfo(type = "com.farata.demo.dto.CompanyAssociateDTO", mappedBy = CompanyAssociate.class), updateInfo = @CX_UpdateInfo(updateEntity = CompanyAssociate.class))
	List<?> getCompanyAssociates(Object companyId);
	
	@CX_JSJPQLMethod(query = "select ca from CompanyAssociate ca")
	List<CompanyAssociateDTO> getCompanyAssociatesListTypeParameterAsDTO(Object companyId); 

	@CX_JSJPQLMethod(query = "select ca from CompanyAssociate ca")
	List<CompanyAssociate> getCompanyAssociatesListTypeParameterAsEntity(Object companyId);  
}
