package com.farata.cleardatabuilder.js.validation.apt;

import com.farata.cdb.annotations.processor.CDBHQLAnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;

public class CDBAnnotationProcessor implements AnnotationProcessor {

	private CX_JSFillChildrenMethodProcessor cxFillChildrenMethodProcessor;
	private CX_JSFillMethodProcessor cxFillMethodProcessor;
	private CX_JSGenerateStoreProcessor cxGenerateDataCollectionProcessor;
	private CX_JSGenerateSampleProcessor cxGenerateMXMLSampleProcessor;
	private CX_JSGetMethodProcessor cxGetMethodProcessor;
	private CX_JSJPQLMethodProcessor cxJPQLMethodProcessor;
	private CX_JSServiceProcessor cxServiceProcessor;
	private CX_TransferInfoProcessor cxTransferInfoProcessor;
	private CX_UpdateInfoProcessor cxUpdateInfoProcessor;
	private CDBHQLAnnotationProcessor hqlAnnotationProcessor;

	public CDBAnnotationProcessor(AnnotationProcessorEnvironment env) {
		_env = env;
		cxFillChildrenMethodProcessor = new CX_JSFillChildrenMethodProcessor(_env);
		cxFillMethodProcessor = new CX_JSFillMethodProcessor(_env);
		cxGenerateDataCollectionProcessor = new CX_JSGenerateStoreProcessor(_env);
		cxGenerateMXMLSampleProcessor = new CX_JSGenerateSampleProcessor(_env);
		cxGetMethodProcessor = new CX_JSGetMethodProcessor(_env);
		cxJPQLMethodProcessor = new CX_JSJPQLMethodProcessor(_env);
		cxServiceProcessor = new CX_JSServiceProcessor(_env);
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
