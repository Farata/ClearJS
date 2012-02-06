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

class JSTemplatesCache {
	final private static TemplatesLoader CLASSIC_JS_CLASS 
		= new TemplatesLoader("/xslt/js-classic-js-class.xslt");
	final private static TemplatesLoader EXT_JS__CLASS 
		= new TemplatesLoader("/xslt/js-ext-js-class.xslt");
	final private static TemplatesLoader INTERFACE 
		= new TemplatesLoader("/xslt/js-interface.xslt");
	final private static TemplatesLoader ENUM_OBJECTS 
		= new TemplatesLoader("/xslt/js-enum-objects.xslt");
	final private static TemplatesLoader ENUM_STRINGS 
		= new TemplatesLoader("/xslt/js-enum-strings.xslt");

	
	static Templates jsClassicJSClass() { return CLASSIC_JS_CLASS.load(); }
	static Templates jsExtJSClass() { return EXT_JS__CLASS.load(); }	
	static Templates jsInterface() { return INTERFACE.load(); }	
	static Templates jsEnumObjects() { return ENUM_OBJECTS.load(); }	
	static Templates jsEnumString() { return ENUM_STRINGS.load(); }
	
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

