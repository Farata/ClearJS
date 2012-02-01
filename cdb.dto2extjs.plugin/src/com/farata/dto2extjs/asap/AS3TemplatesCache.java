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

import javax.xml.transform.Templates;

class AS3TemplatesCache {
	final private static TemplatesLoader MANAGED_CLASS 
		= new TemplatesLoader("/xslt/as3-managed-class.xslt");
	final private static TemplatesLoader REMOTE_CLASS 
		= new TemplatesLoader("/xslt/as3-remote-class.xslt");
	final private static TemplatesLoader INTERFACE 
		= new TemplatesLoader("/xslt/as3-interface.xslt");
	final private static TemplatesLoader ENUM_FARATA 
		= new TemplatesLoader("/xslt/as3-enum-farata.xslt");
	final private static TemplatesLoader ENUM_ADOBE 
		= new TemplatesLoader("/xslt/as3-enum-adobe.xslt");

	
	static Templates as3ManagedClass() { return MANAGED_CLASS.load(); }
	static Templates as3RemoteClass() { return REMOTE_CLASS.load(); }	
	static Templates as3Interface() { return INTERFACE.load(); }	
	static Templates as3EnumFarata() { return ENUM_FARATA.load(); }	
	static Templates as3EnumAdobe() { return ENUM_ADOBE.load(); }
	
	private static class TemplatesLoader {
		final private String templatesUri;
		private Templates templates;
		
		TemplatesLoader(final String templatesUri) { this.templatesUri = templatesUri; }
		
		synchronized Templates load() {
			if (null == templates)
				templates = XsltOperation.loadTemplates(templatesUri);
			return templates;
		}
	}
}

