package com.farata.demo.services;

import java.util.List;

import com.farata.demo.entities.Status;

import clear.cdb.extjs.annotations.JSGenerateStore;
import clear.cdb.extjs.annotations.JSGenerateSample;
import clear.cdb.extjs.annotations.JSJPQLMethod;
import clear.cdb.extjs.annotations.JSService;
import clear.cdb.extjs.annotations.JSTransferInfo;

@JSService
public interface TestService1 {
	@JSGenerateSample
	@JSGenerateStore
	@JSJPQLMethod(query = "SELECT st, st.statusid, ord FROM Status st JOIN st.storeorders ord", transferInfo=@JSTransferInfo(type="com.farata.demo.dto.StatusDTO")) 
	List<?> getStatusesList ();
}
