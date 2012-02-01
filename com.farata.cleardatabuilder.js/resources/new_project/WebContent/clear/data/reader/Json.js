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
    var ExtDataReader = Ext.ClassManager.get("Ext.data.Reader");
    
    ExtDataReader.prototype.extractData =  function(root) {
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
    };
});   