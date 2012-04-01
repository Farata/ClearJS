package com.farata.java_test.service;

import java.util.List;

import com.farata.java_test.dto.AssociateDTO;

import clear.cdb.extjs.annotations.JSFillMethod;
import clear.cdb.extjs.annotations.JSGenerateStore;
import clear.cdb.extjs.annotations.JSService;

@JSService
public interface IAssociateService {
	@JSGenerateStore(storeType="com.farata.java_test.collections.AssociateCollection")
	@JSFillMethod(autoSyncEnabled = true)
	List<AssociateDTO> fill(Long companyId);
}
