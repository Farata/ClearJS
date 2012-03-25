package com.farata.demo.service;

import java.util.List;

import com.farata.demo.dto.CompanyDTO;

import clear.cdb.extjs.annotations.JSFillMethod;
import clear.cdb.extjs.annotations.JSService;

@JSService
public interface ICompanyService {
	@JSFillMethod
	List<?> getCompaniesEmpty();
	
	@JSFillMethod(sync=false)
	List<CompanyDTO> getCompaniesSyncFalse();

	@JSFillMethod(autoSyncEnabled=true)
	List<CompanyDTO> getCompaniesAutoSyncTrue();

	@JSFillMethod(sync=false, autoSyncEnabled=true)
	List<?> getCompaniesSyncFalseAutoSyncTrue();
}