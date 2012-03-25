/*

This file is part of Clear-ExtJS for Ext JS 4

Copyright (c) 2011 Farata Systems LLC

Licensed under The MIT License
Re-distributions of files must retain the above copyright notice.

@license http://www.opensource.org/licenses/mit-license.php The MIT License

*/

/**
 * This class is used by {@link Clear.transaction.BatchService BatchService to facilitates sequential invoke of several remote methods without round-trips to the client. 
 * Request sent to the server and response from the server are both contain array of BatchMember instances.
 */
Ext.define('Clear.transaction.BatchMember', {
    /* Begin Definitions */
    alias: 'batchMember',
    
	className:'',
	methodName:'',
	parameters:[],
	autoSyncSubtopic:'',
	result:{},

    /* End Definitions */
    
    
    constructor: function(config) {
    	Ext.apply(this,config); 
    	this.toString = function() {
			var me = this;
			return [
				"clear.transaction.BatchMember {",
				" \nclassName: ", me.className,
				" \nmethodName: ", me.methodName,
				" \nparameters: ", me.parameters,
				" \nautoSyncSubtopic: ", me.autoSyncSubtopic,
				"\n}"
			].join(""); 
		};     
    }
});




