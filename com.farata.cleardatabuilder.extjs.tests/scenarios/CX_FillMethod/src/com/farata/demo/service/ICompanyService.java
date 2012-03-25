package com.farata.demo.service;

import java.util.List;

import com.farata.demo.dto.CompanyDTO;

import clear.cdb.extjs.annotations.CX_JSFillMethod;
import clear.cdb.extjs.annotations.CX_JSService;

@CX_JSService
public interface ICompanyService {
	@CX_JSFillMethod
	List<?> getCompaniesEmpty();
	
	@CX_JSFillMethod(sync=false)
	List<CompanyDTO> getCompaniesSyncFalse();

	@CX_JSFillMethod(autoSyncEnabled=true)
	List<CompanyDTO> getCompaniesAutoSyncTrue();

	@CX_JSFillMethod(sync=false, autoSyncEnabled=true)
	List<?> getCompaniesSyncFalseAutoSyncTrue();
}