package com.farata.java_test.service;

import java.util.List;

import clear.cdb.extjs.annotations.JSFillMethod;
import clear.cdb.extjs.annotations.JSGenerateStore;
import clear.cdb.extjs.annotations.JSService;

import com.farata.java_test.dto.CompanyDTO;

@JSService
public interface ICompanyService {
	@JSGenerateStore
	@JSFillMethod(autoSyncEnabled = true)
	List<CompanyDTO> fill();
}
