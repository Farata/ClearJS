package com.farata.example.service;

import java.util.List;

import clear.cdb.extjs.annotations.JSFillMethod;
import clear.cdb.extjs.annotations.JSGenerateSample;
import clear.cdb.extjs.annotations.JSGenerateStore;
import clear.cdb.extjs.annotations.JSService;

import com.farata.example.dto.CompanyDTO;

@JSService
public interface ICompanyService {
	@JSGenerateSample
	@JSGenerateStore
	@JSFillMethod(autoSyncEnabled = true)
	List<CompanyDTO> getCompanies();
}
