package com.farata.example.service;

import java.util.List;

import com.farata.example.dto.AssociateDTO;

import clear.cdb.extjs.annotations.JSFillMethod;
import clear.cdb.extjs.annotations.JSGenerateSample;
import clear.cdb.extjs.annotations.JSGenerateStore;
import clear.cdb.extjs.annotations.JSService;

@JSService
public interface IAssociateService {
	@JSGenerateSample
	@JSGenerateStore
	@JSFillMethod(autoSyncEnabled = true)
	List<AssociateDTO> getAssociates(Integer companyId);
}
