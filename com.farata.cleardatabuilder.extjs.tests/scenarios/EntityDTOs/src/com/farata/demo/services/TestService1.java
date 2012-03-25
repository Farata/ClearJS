package com.farata.demo.services;

import java.util.List;

import com.farata.demo.entities.Status;

import clear.cdb.extjs.annotations.CX_JSGenerateStore;
import clear.cdb.extjs.annotations.CX_JSGenerateSample;
import clear.cdb.extjs.annotations.CX_JSJPQLMethod;
import clear.cdb.extjs.annotations.CX_JSService;
import clear.cdb.extjs.annotations.CX_TransferInfo;

@CX_JSService
public interface TestService1 {
	@CX_JSGenerateSample
	@CX_JSGenerateStore
	@CX_JSJPQLMethod(query = "SELECT st, st.statusid, ord FROM Status st JOIN st.storeorders ord", transferInfo=@CX_TransferInfo(type="com.farata.demo.dto.StatusDTO")) 
	List<?> getStatusesList ();
}
