/**
 * This file is part of the Clear Components for Ext JS 4.
 * 
 * Copyright (c) 2012 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
/**
 * @author Victor Rasputnis
 * 
 * Subclass of {@link Ext.data.reader.Json Ext.data.reader.Json} that enables de-serialization
 * of JSON server response for {@link Clear.data.ChangeObject ChangeObject} structures in addition
 * to de-serialization of the models as per standard JsonReader. 
 * This class is used by {@link Clear.data.DirectProxy Clear.data.DirectProxy} and normally would not be used directly.
 */
Ext.define('Clear.data.reader.Json', {
    extend: 'Ext.data.reader.Json',
    alternateClassName: 'Clear.data.JsonReader',
    alias: 'reader.clear',
    constructor: function (config) {
        this.callParent(arguments);
		// Unfortunately, define function from the data.Reader leaves nullResultSet undefined
	    // This leads to exception while processing the null return from a remote action
		this.nullResultSet = Ext.create('Ext.data.ResultSet', {
	            total  : 0,
	            count  : 0,
	            records: [],
	            success: true
	    });	
	},
	
	deserializeModel: function(object, id) {
		var me = this,
			values,
			model=null,
			Model = me.model;
		if (object) {
			values = me.extractValues(object);
	    	model = new Model(values, me.getId(object), object);
	    	if (id) {
	    		model.id = id;
	    	}
		}
    	return model;
	},
		
	deserializeChangeObject: function(object) {
		var me = this;
 		var changeObject = Ext.create('Clear.data.ChangeObject', object);
			
		if (changeObject) { 			
			changeObject.newVersion = me.deserializeModel(changeObject.newVersion, changeObject.id);
			changeObject.previousVersion = me.deserializeModel(changeObject.previousVersion, changeObject.id);
		}

        return changeObject;
	},
	    
}, function() {
    // Here we patch the extractData() method in the ancestory tree:
    
    Ext.data.Reader.override ({
    	extractData : function(root) {
            var me = this,
                records = [],
                i       = 0,
                length  = root.length,
                node, record;
                
            if (!root.length && Ext.isObject(root)) {
                root = [root];
                length = 1;
            }

            for (; i < length; i++) {
                node   = root[i];
                if (node.__type__ == 'changeObject') {
                	records.push(me.deserializeChangeObject(node));
                } else {   
                	// Otherwise, follow the standard logic for item
                	record = me.deserializeModel(node);
                	records.push(record);            	
                	if (me.implicitIncludes) {
                		me.readAssociated(record, node);
                	}
                }
            }
            return records;
        }
    	
    });
});   