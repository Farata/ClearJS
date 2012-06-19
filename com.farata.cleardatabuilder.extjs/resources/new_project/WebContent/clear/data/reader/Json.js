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
	
	/**
	 * This method wraps functionality from Ext.data.reader.Reader and, additionally,
	 * checks for node to be not null. The later is important in the context of
	 * deserialization of the ChangeObject, where either newVersion or previousVersion
	 * can be null
	 * @private
	 */
	deserializeRecord: function(node, id) {
		var me = this,
			convertedValues,
			record=null,		
			Model = me.model;
		
		if (node) {		    	  	
	        // Create a record with an empty data object.
	        // Populate that data object by extracting and converting field values from raw data
	        record = new Model(undefined, me.getId(node), node, convertedValues = {});
	        // If the server did not include an id in the response data, the Model constructor will mark the record as phantom.
	        // We  need to set phantom to false here because records created from a server response using a reader by definition are not phantom records.
	        record.phantom = false;
	        // record generated function to extract all fields at once
	        me.convertRecordData(convertedValues, node, record);
	        if (id) {
	        	record.id = id;
	        }
		}
        return record;
    	
	},
		
	deserializeChangeObject: function(object) {
		var me = this;
 		var changeObject = Ext.create('Clear.data.ChangeObject', object);
			
		if (changeObject) { 			
			changeObject.newVersion = me.deserializeRecord(changeObject.newVersion, changeObject.id);
			changeObject.previousVersion = me.deserializeRecord(changeObject.previousVersion, changeObject.id);
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
                	record = me.deserializeRecord(node);
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