package com.farata.java_test.service;

import java.util.List;

import com.farata.java_test.dto.AssociateDTO;

import clear.cdb.js.annotations.CX_JSFillMethod;
import clear.cdb.js.annotations.CX_JSGenerateStore;
import clear.cdb.js.annotations.CX_JSService;

@CX_JSService
public interface IAssociateService {
	@CX_JSGenerateStore(collectionType="com.farata.java_test.collections.AssociateCollection")
	@CX_JSFillMethod(autoSyncEnabled = true)
	List<AssociateDTO> fill(Long companyId);
}
