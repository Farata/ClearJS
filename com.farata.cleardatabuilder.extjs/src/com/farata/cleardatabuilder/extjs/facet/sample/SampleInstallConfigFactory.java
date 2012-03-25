package com.farata.cleardatabuilder.extjs.facet.sample;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;

public class SampleInstallConfigFactory implements IActionConfigFactory {

	@Override
	public Object create() throws CoreException {
		return new SampleInstallConfig();
	}

}
