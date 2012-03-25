package com.farata.java_test.service;

import java.util.List;

import clear.cdb.extjs.annotations.CX_JSFillMethod;
import clear.cdb.extjs.annotations.CX_JSGenerateStore;
import clear.cdb.extjs.annotations.CX_JSService;

import com.farata.java_test.dto.CompanyDTO;

@CX_JSService
public interface ICompanyService {
	@CX_JSGenerateStore
	@CX_JSFillMethod(autoSyncEnabled = true)
	List<CompanyDTO> fill();
}
