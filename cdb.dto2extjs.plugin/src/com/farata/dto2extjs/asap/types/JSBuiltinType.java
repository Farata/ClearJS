/**
 * Copyright (c) 2009 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
package com.farata.dto2extjs.asap.types;

import com.farata.dto2extjs.annotations.JSClassKind;

public enum JSBuiltinType implements IJSType {
	AUTO {
		@Override public String id() { return "Ext.data.Types.AUTO"; }
	},
	BOOLEAN {
		@Override public String id() { return "Ext.data.Types.BOOLEAN"; }
	},
	STRING {
		@Override public String id() { return "Ext.data.Types.STRING"; }
	},
	INTEGER {
		@Override public String id() { return "Ext.data.Types.INTEGER"; }
	},
	FLOAT {
		@Override public String id() { return "Ext.data.Types.FLOAT"; }
	}, 
	NUMBER {
		@Override public String id() { return "Ext.data.Types.NUMBER"; }
	},
	DATE {
		@Override public String id() { return "Ext.data.Types.DATE"; }
	};
	/*XML, Object,*/
	public String id() { return name(); }
	public JSClassKind classKind() { return null; }
	public boolean isContainer() { return false; }
	public boolean isEnum() { return false; }
	public IJSType contentType() { throw new UnsupportedOperationException(); }
}
