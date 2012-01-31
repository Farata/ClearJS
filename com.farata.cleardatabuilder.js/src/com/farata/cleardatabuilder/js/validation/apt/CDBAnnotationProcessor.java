package com.farata.cleardatabuilder.js.validation.apt;

import com.farata.cdb.annotations.processor.CDBHQLAnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;

public class CDBAnnotationProcessor implements AnnotationProcessor {

	private CX_FillChildrenMethodProcessor cxFillChildrenMethodProcessor;
	private CX_FillMethodProcessor cxFillMethodProcessor;
	private CX_GenerateDataCollectionProcessor cxGenerateDataCollectionProcessor;
	private CX_GenerateMXMLSampleProcessor cxGenerateMXMLSampleProcessor;
	private CX_GetMethodProcessor cxGetMethodProcessor;
	private CX_JPQLMethodProcessor cxJPQLMethodProcessor;
	private CX_ServiceProcessor cxServiceProcessor;
	private CX_TransferInfoProcessor cxTransferInfoProcessor;
	private CX_UpdateInfoProcessor cxUpdateInfoProcessor;
	private CDBHQLAnnotationProcessor hqlAnnotationProcessor;

	public CDBAnnotationProcessor(AnnotationProcessorEnvironment env) {
		_env = env;
		cxFillChildrenMethodProcessor = new CX_FillChildrenMethodProcessor(_env);
		cxFillMethodProcessor = new CX_FillMethodProcessor(_env);
		cxGenerateDataCollectionProcessor = new CX_GenerateDataCollectionProcessor(_env);
		cxGenerateMXMLSampleProcessor = new CX_GenerateMXMLSampleProcessor(_env);
		cxGetMethodProcessor = new CX_GetMethodProcessor(_env);
		cxJPQLMethodProcessor = new CX_JPQLMethodProcessor(_env);
		cxServiceProcessor = new CX_ServiceProcessor(_env);
		cxTransferInfoProcessor = new CX_TransferInfoProcessor(_env);
		cxUpdateInfoProcessor = new CX_UpdateInfoProcessor(_env);
		hqlAnnotationProcessor = (CDBHQLAnnotationProcessor) new CDBHQLAnnotationProcessor().getProcessorFor(null, _env);
	}

	public void process() {
		cxFillChildrenMethodProcessor.process();
		cxFillMethodProcessor.process();
		cxGenerateDataCollectionProcessor.process();
		cxGenerateMXMLSampleProcessor.process();
		cxGetMethodProcessor.process();
		cxJPQLMethodProcessor.process();
		cxServiceProcessor.process();
		cxTransferInfoProcessor.process();
		cxUpdateInfoProcessor.process();
		hqlAnnotationProcessor.process();
	}

	private AnnotationProcessorEnvironment _env;

}
