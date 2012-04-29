Ext.define('Clear.override.data.Model', {
    requires: [
       'Ext.data.Model'               
    ]
}, function () {
	// Here we add the computeAssociatedDirty() method of the Model 
    Ext.data.Model.override({
	    computeAssociatedDirty: function() {
	    	var me = this,
        	associations = me.associations,
    		association,
    		associatedStore,
    		i,
    		count = associations.getCount(),
    		associatedDirty = false;
        	
        	for (i=0; i<count; i++) {
        		association = associations.getAt(i);        				
        		if (association.type=="hasMany") {
        			associatedStore = me[association.storeName];
        			if (associatedStore && associatedStore.commitRequired) {
        				associatedDirty = true;
        				break;
        			}
        		}        					
        	}
        	if (associatedDirty !== me.associatedDirty) {
        		me.associatedDirty = associatedDirty;
        	} 
        	return associatedDirty;
        }
    });
} );