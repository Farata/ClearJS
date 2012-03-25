package com.farata.cleardatabuilder.extjs.validation.apt;

import com.farata.cdb.annotations.processor.CDBHQLAnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;

public class CDBAnnotationProcessor implements AnnotationProcessor {

	private JSFillChildrenMethodProcessor cxFillChildrenMethodProcessor;
	private JSFillMethodProcessor cxFillMethodProcessor;
	private JSGenerateStoreProcessor cxGenerateDataCollectionProcessor;
	private JSGenerateSampleProcessor cxGenerateMXMLSampleProcessor;
	private JSGetMethodProcessor cxGetMethodProcessor;
	private JSJPQLMethodProcessor cxJPQLMethodProcessor;
	private JSServiceProcessor cxServiceProcessor;
	private JSTransferInfoProcessor cxTransferInfoProcessor;
	private JSUpdateInfoProcessor cxUpdateInfoProcessor;
	private CDBHQLAnnotationProcessor hqlAnnotationProcessor;

	public CDBAnnotationProcessor(AnnotationProcessorEnvironment env) {
		_env = env;
		cxFillChildrenMethodProcessor = new JSFillChildrenMethodProcessor(_env);
		cxFillMethodProcessor = new JSFillMethodProcessor(_env);
		cxGenerateDataCollectionProcessor = new JSGenerateStoreProcessor(_env);
		cxGenerateMXMLSampleProcessor = new JSGenerateSampleProcessor(_env);
		cxGetMethodProcessor = new JSGetMethodProcessor(_env);
		cxJPQLMethodProcessor = new JSJPQLMethodProcessor(_env);
		cxServiceProcessor = new JSServiceProcessor(_env);
		cxTransferInfoProcessor = new JSTransferInfoProcessor(_env);
		cxUpdateInfoProcessor = new JSUpdateInfoProcessor(_env);
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
