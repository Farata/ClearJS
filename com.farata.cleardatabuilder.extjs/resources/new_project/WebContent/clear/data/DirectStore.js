/*

This file is part of Ext JS 4

Copyright (c) 2011 Sencha Inc

Contact:  http://www.sencha.com/contact

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.

If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.

*/
/**
 * Small helper class to create an {@link Ext.data.Store} configured with an {@link Ext.data.proxy.Direct}
 * and {@link Ext.data.reader.Json} to make interacting with an {@link Ext.direct.Manager} server-side
 * {@link Ext.direct.Provider Provider} easier. To create a different proxy/reader combination create a basic
 * {@link Ext.data.Store} configured as needed.
 *
 * **Note:** Although they are not listed, this class inherits all of the config options of:
 *
 * - **{@link Ext.data.Store Store}**
 *
 * - **{@link Ext.data.reader.Json JsonReader}**
 *
 *   - **{@link Ext.data.reader.Json#root root}**
 *   - **{@link Ext.data.reader.Json#idProperty idProperty}**
 *   - **{@link Ext.data.reader.Json#totalProperty totalProperty}**
 *
 * - **{@link Ext.data.proxy.Direct DirectProxy}**
 *
 *   - **{@link Ext.data.proxy.Direct#directFn directFn}**
 *   - **{@link Ext.data.proxy.Direct#paramOrder paramOrder}**
 *   - **{@link Ext.data.proxy.Direct#paramsAsHash paramsAsHash}**
 *
 */
Ext.define('Clear.data.DirectStore', {
    /* Begin Definitions */
    
    extend: 'Ext.data.Store',
    
    alias: 'store.clear',

    requires: [
               'Clear.data.proxy.DirectProxy',
               'Ext.util.HashMap',
               'Ext.window.MessageBox'
              ],    
    uses: ['Clear.transaction.BatchManager'],
        
    /**
     * @property {Boolean} commitRequired
	 *  Flag denoting that records loaded into this directStore have been added, removed or modified. 
	 *  Flag is cleared to false upon successfull {@link #sync}
     */
    commitRequired: false,
    
    /**
     * @property {Ext.util.HashMap} modifiedMap
	 * Collection of modified records
	 * @private 
     */
    modifiedMap: null,

    /* End Definitions */

    /**
     * Creates the store with the proxy pre-defined as Clear.data.proxy.Direct
     * @param {Object} config (optional) Config object
     */
    
    constructor : function(config){

        // CHANGE: out-of-the-box ExtJS 4.0.7 does not account
    	// for storeConfig vs. instance config and, in particular
    	// when instantiated in MVC will not communicate
    	// "Direct" related information to the proxy

    	var defaultConfig = {};
    	Ext.copyTo(defaultConfig, this, "paramOrder,paramsAsHash,directFn,api,simpleSortMode", true);    	
    	config = config || {};
    	Ext.applyIf(config, defaultConfig);
    	
        config.api = config.api || {};     	
        
        if (!config.proxy) {
            var proxyAttributes = {
                type: 'clear'
            };
            Ext.copyTo(proxyAttributes, config, 'paramOrder,paramsAsHash,directFn,api,simpleSortMode');
            Ext.copyTo(proxyAttributes.reader, config, 'totalProperty,root,idProperty');
            // Constructor of the AbstractStore will use setProxy() to make the proxy via Ext.createByAlias()
            config.proxy = proxyAttributes;
        }
        
        this.callParent([config]); 
        this.addEvents(
                /**
                 * @event exception
                 * Fired when this batch encountered an exception
                 * @param {Ext.data.Batch} batch The batch object
                 * @param {Object} operation The operation that encountered the exception
                 */
                'exception',
                /**
                 * @event commitRequiredChange
                 * Fired when 'commitRequired' is changing the value
                 * @param {String} property  The name of the property
                 * @param {Object} newValue  New value of the property
                 * @param {Object} oldValue  Old value of the property
                 */
                'commitRequiredChange'
        );
        this.resetState();
        this.on({
        	load: 	this.onLoad,
        	add: 	this.onAddRecord,
        	remove: this.onRemoveRecord,
        	update: this.onUpdateRecord,
        	write:	this.updateCommitRequired,
        	read:	this.updateCommitRequired
        });
    },
        
    //private
    clearData: function() {
        this.callParent();
        this.resetState();
    },
    
	getLocalIdentity: function () {
		var min=0;

		this.each(function(record){
			if (record.get(record.idProperty) < min) 
				min = record.get(record.idProperty);
		});
		return Math.min(0,min-1); 
	}
    ,
    setProxy: function(proxy) {
    	var me = this;
    	me.callParent([proxy]);
    	me.proxy.addListener('exception',me.onProxyException);
    },

    getChanges: function() {
    	  	var me        = this,
            changes   = {},
            toCreate  = me.getNewRecords(),
            toUpdate  = me.getUpdatedRecords(),
            toDestroy = me.getRemovedRecords();


        if (toCreate.length > 0) {
            changes.create = toCreate;
        }

        if (toUpdate.length > 0) {
            changes.update = toUpdate;
        }

        if (toDestroy.length > 0) {
            changes.destroy = toDestroy;
        }
        
        return changes;
    },
    
    updateCommitRequired: function() {    	 	
    	this.setCommitRequired((this.modifiedMap.getCount() + this.removed.length) > 0);
    },
    
    setCommitRequired: function(newValue) {
		if (newValue !== this.commitRequired) {
			this.commitRequired = newValue;
			this.fireEvent("commitRequiredChange", this, newValue, this.commitRequired);
		}   	
    },
    
    sync: function(deep) {
        var me        = this,
			changes = me.getChanges(),
			batchManager;
        	//Changes based purely on the top store will carry nothing
            // event when child (associated) stores have been modified.
        	//This is a design limitation of the beforesync event.
	        if (me.fireEvent('beforesync', changes) !== false) {
	        	
	        	batchManager = Ext.create('Clear.transaction.BatchManager');
	        	batchManager.addStore(me, 0);
	        	batchManager.createBatch();
	        	batchManager.sendBatch();
	            //JSinternal::saveState(act);
				//if (!autoSyncEnabled && !roundTripSync)
				//	resetState();
	        }        
    }, 
    
    /*
     * @private
     * Resets 'extra' state management counters and flags added by DirectStore
     * Not to be used as public method, b/c the real state is dispersed in records
     * 
     */
    resetState: function() {
        this.commitRequired = false;
        this.modifiedMap = new Ext.util.HashMap();
        //this.removed = [];// this is a redundancy, after store.onDestroyRecords()
    },


    onAddRecord: function(store, items) {

	   	Ext.each(items, function(item, index, value){
	   		if(item.isValid()) {
	   			this.modifiedMap.add(item, item);
	   		}
	   	}, this);
		this.updateCommitRequired();
    },
    
    /**
     * @private
     * Attached as the 'complete' event listener to a proxy's Batch object. Iterates over the batch operations
     * and updates the Store's internal data MixedCollection.
     */
    
    onBatchComplete: function(batch, operation) {
        this.callParent([batch, operation]);
        this.resetState();
    },
    
    onBatchException: function(message, where) {  		
        Ext.MessageBox.alert( "Batch Failed", Ext.String.format("{0}/n{1}", message, where));	
    },
    
    onLoad: function(store, records, successful, error){
    	  if (successful) {
    		  console.log( "onLoad", Ext.String.format("Store {0} loaded {1} records", store.storeId, records.length));
    	  } else {
    		  Ext.MessageBox.alert( error.message, Ext.String.format("{0}::{1} failed: {2}", error.action, error.method, error.where));      		  
    	  }
    },
    
    onProxyException: function (proxy, response, operation) {
    	var error = operation.error;
        Ext.MessageBox.alert( error.message, Ext.String.format("{0}::{1} failed: {2}", error.action, error.method, error.where));	
    	
    },

    onRemoveRecord: function (store, item) {
      var associations;

      if (item.phantom!==true) {			
    	  if (this.modifiedMap.remove(item) ) {
			associations = item.associatons;
			associations.each(function(association) {
				var associatedStore;
				if (association.type=="hasMany") {
					associatedStore = item[association.storeName];
					if (associatedStore) {
						associatedStore.removeAll();
					} 
				}        					
			}, this);  		
    	  }
		}
		this.updateCommitRequired();		
    },
    
    onUpdateRecord: function (store, item) {
		var me=this, property;
		
		//	if (item is IUID && pce.property == "uid") return true;

		// Item might have reverted to original value or be not changed at all
		// but associations can be dirty
		item.computeAssociatedDirty();
		
		if (me.modifiedMap.get(item)) {
			//Remove item that does not belond to modifiedMap
			if (item.isValid()) {
				if ( item.dirty!==true && item.associatedDirty!==true) {
					me.modifiedMap.remove(item);
				} 
			} else {
				me.modifiedMap.remove(item);	 
			}		
		} else {
			//Add item that belongs to the modifiedMap
			if (item.isValid()) {
				if ( item.dirty || item.associatedDirty) {
					//To allow update server with the client-based records,
					//we 'backdate' create missing 'raw':
					if (item.raw === undefined) {
						item.raw = Ext.apply({}, item[item.persistenceProperty]);
						Ext.apply(item.raw, item.modified);
					}
						
					me.modifiedMap.add(item, item);
				} 											
			}
		}
		this.updateCommitRequired();
    }

});
