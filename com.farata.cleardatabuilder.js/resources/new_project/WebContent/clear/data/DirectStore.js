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
               'Ext.data.HasManyAssociation'
              ],    
    uses: ['Clear.transaction.BatchManager'],
    
    /* End Definitions */

    /**
     * Creates the store with the proxy pre-defined as Clear.data.proxy.Direct
     * @param {Object} config (optional) Config object
     */
    constructor : function(config){


    	config = config || {};
    	// Fighting with controller-based instantiation that results in loosing _direct_ information at the store proxy level
    	Ext.copyTo(config, this, "paramOrder,paramsAsHash,directFn,api,simpleSortMode", true);
    	
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
                'exception'
              );

        this.on('load', 
            	function(store, groupers, successful, operation, options ) {
            	    	
            	}
        );
        
    },
    setProxy: function(proxy) {
    	var me = this;
    	me.callParent([proxy]);
    	me.proxy.addListener('exception',me.onProxyException);
    },
    onProxyException: function (proxy, response, operation) {
    	var error = operation.error;
        Ext.MessageBox.alert( error.message, Ext.String.format("{0}::{1} failed: {2}", error.action, error.method, error.where));	
    	
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
    
    sync: function(deep) {
        var me        = this,
			changes = me.getChanges(),
			batchManager;

	        if (me.fireEvent('beforesync', changes) !== false) {
	        	batchManager = Ext.create('Clear.transaction.BatchManager', {
	        		listeners: me.getBatchListeners() || {}
	        	});
	        	batchManager.addStore(me, 0);
	        	batchManager.createBatch();
	        	batchManager.sendBatch();
	            //cx_internal::saveState(act);
				//if (!autoSyncEnabled && !roundTripSync)
				//	resetState();
	        }        
    }, 

    /**
     * @private
     * Attached as the 'complete' event listener to a proxy's Batch object. Iterates over the batch operations
     * and updates the Store's internal data MixedCollection.
     */
    
    onBatchComplete: function(batch, operation) {
        var me = this,
            operations = batch.operations,
            length = operations.length,
            i;

        me.suspendEvents();

        for (i = 0; i < length; i++) {
            me.onProxyWrite(operations[i]);
        }

        me.resumeEvents();

        me.fireEvent('datachanged', me);
    },
    

     onBatchException: function(message, where) {  		
        Ext.MessageBox.alert( "Batch Failed", Ext.String.format("{0}/n{1}", message, where));	
    },
    
    load: function(options) {
        var me = this;
        return me.callParent([options]);
    },
    


}, function () {
	
	// Here we patch the createStore() method of the HasManyAssociation
	// for we need the nested stores to be of clear.data.DirectStore type
    var HasManyAssociation = Ext.ClassManager.get("Ext.data.HasManyAssociation");

    HasManyAssociation.prototype.createStore =  function() {
	        var that            = this,
	            associatedModel = that.associatedModel,
	            storeName       = that.storeName,
	            foreignKey      = that.foreignKey,
	            primaryKey      = that.primaryKey,
	            filterProperty  = that.filterProperty,
	            autoLoad        = that.autoLoad,
	            storeConfig     = that.storeConfig || {},
	            storeClassName	= that.storeClassName || 'Clear.data.DirectStore';
	        
	        return function() {
	            var me = this,
	                config, filter,
	                modelDefaults = {},
	                params={};
	      
	                
	            if (me[storeName] === undefined) {
	                if (filterProperty) {
	                    filter = {
	                        property  : filterProperty,
	                        value     : me.get(filterProperty),
	                        exactMatch: true
	                    };
	                    params[filterProperty] = me.get(filterProperty);
	                } else {
	                    filter = {
	                        property  : foreignKey,
	                        value     : me.get(primaryKey),
	                        exactMatch: true
	                    };
	                    params[foreignKey] = me.get(primaryKey);
	                }
	                
	                modelDefaults[foreignKey] = me.get(primaryKey);
	                
	                config = Ext.apply({}, storeConfig, {
	                    model        : associatedModel,
	                    filters      : [filter],
	                    remoteFilter : false,
	                    modelDefaults: modelDefaults
	                });
	       
	                //Was:  me[storeName] = Ext.create('Ext.data.Store', config);
	                me[storeName] = Ext.create(storeClassName, config);
	                if (autoLoad) {
                        //Added 	                	
	                    me[storeName].load({'params':params});
	                }
	            }
	            
	            return me[storeName];
	        };
	    };
});
