package com.farata.cleardatabuilder.extjs.facet.common;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;

public class CommonInstallConfigFactory implements IActionConfigFactory {

	@Override
	public Object create() throws CoreException {
		return new CommonInstallConfig();
	}

}
