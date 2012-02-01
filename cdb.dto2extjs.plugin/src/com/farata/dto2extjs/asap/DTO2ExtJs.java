/**
 * Copyright (c) 2009 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
package com.farata.dto2extjs.asap;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.farata.dto2extjs.asap.env.AptEnvironmentInspector;
import com.farata.dto2extjs.asap.env.EclipseEnvironmentInspector;

public class DTO2ExtJs extends Plugin {
	//The shared instance.
	private static DTO2ExtJs plugin;
	
	/**
	 * The constructor.
	 */
	public DTO2ExtJs() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override public void start(BundleContext context) throws Exception {
		super.start(context);
		AS3AnnotationProcessorFactory.INSPECTOR = EclipseEnvironmentInspector.INSTANCE;
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override public void stop(BundleContext context) throws Exception {
		AS3AnnotationProcessorFactory.INSPECTOR = AptEnvironmentInspector.INSTANCE;		
		super.stop(context);
		plugin = null;
	}
	
	/**
	 * Returns the shared instance.
	 */
	public static DTO2ExtJs getDefault() { return plugin; }
}
