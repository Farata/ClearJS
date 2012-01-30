package com.farata.cleardatabuilder.js.facet.sample;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;

public class SampleInstallConfigFactory implements IActionConfigFactory {

	@Override
	public Object create() throws CoreException {
		return new SampleInstallConfig();
	}

}
