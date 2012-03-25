/*

This file is part of Clear Components for Ext JS 4

Copyright (c) 2012 FarataSystems LLC

Contact:  info@faratasystems.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.

If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.

*/
/**
 * @author Ed Spencer
 * @class Ext.data.Batch
 *
 * <p>Provides a mechanism to run one or more {@link Ext.data.Operation operations} in a given order. Fires the 'operationcomplete' event
 * after the completion of each Operation, and the 'complete' event when all Operations have been successfully executed. Fires an 'exception'
 * event if any of the Operations encounter an exception.</p>
 *
 * <p>Usually these are only used internally by {@link Ext.data.proxy.Proxy} classes</p>
 *
 */
Ext.define('Clear.transaction.BatchManager', {
    mixins: {
        observable: 'Ext.util.Observable'
    },
    uses: [
    	'Clear.transaction.BatchMember',
    	'Clear.data.ChangeObject',
    	'Clear.data.Operation'
    ],

    /**
     * Creates new Batch object.
     * @param {Object} [config] Config object
     */
    constructor: function(config) {
        var me = this;

        me.addEvents(
          /**
           * @event complete
           * Fired when this batch have been completed
           * @param {Ext.data.Batch} batch The batch object
           * @param {Object} operation The last operation that was executed
           */
          'complete',

          /**
           * @event exception
           * Fired when this batch encountered an exception
           * @param {Ext.data.Batch} batch The batch object
           * @param {Object} operation The operation that encountered the exception
           */
          'exception'
        );

        me.mixins.observable.constructor.call(me, config);


        /**
         * Ordered array of store/priority pairs
         */
        me.registry = [];
        me.internalRegistry = [];
        me.batch = [];
        me.storeMap = null;
        me.stateMap = null;
        me.operations = null;
    },

 
    addStore: function(clearStore, priority) {
    	var registry = this.registry;
		registry.push({'store':clearStore, 'priority':priority});
    },
    
    createBatch: function () {
		var me=this,
			i,
		    batchMember,
		    records,
		    changeObjects = [],
		    store,
		    registry = me.registry,
		    _registry = me.internalRegistry = [];

		me.batch = [];
		me.operations = [];
		
		for ( i = registry.length-1; i>=0; i-- ) {
			me.registerWithChildren(registry[i]);
		}
		_registry.sort(this.sortOnPriority);
		
		for ( i=_registry.length-1; i>=0; i-- ) {
			store = _registry[i].store;
			//store.dispatchEvent(new DataCollectionEvent(DataCollectionEvent.COLLECTING_CHANGES));	
		}	
		
		for ( i=_registry.length-1; i>=0; i-- ) {
			store = _registry[i].store;
			records = store.getRemovedRecords();
			if (records.length > 0) {
				changeObjects = [];
				Ext.each(records, function(record) {	        						
					changeObjects.push(
						Ext.create('Clear.data.ChangeObject', 
							{	
								id: record.id,
								changedPropertyNames:[],
								newVersion: null,
								previousVersion: record.raw,
								state: 3
							}
						) 
				);	        	    			
				});				
				batchMember = Ext.create('Clear.transaction.BatchMember', {
					className: me.getDirectAction(store, 'destroy'),
					methodName: me.getDirectMethod(store, 'destroy'),
					parameters:[changeObjects],
					autoSyncSubtopic:Ext.isEmpty(store.autoSyncSubtopic)?store.proxy.readParamString:store.autoSyncSubtopic
				});
				me.batch.push(batchMember);
		    	me.operations.push(
		    		Ext.create('Clear.data.Operation', {
	                    action: 'destroy',
	                    'records': records,
	                    proxy: store
		    		})    
                );			
			}
		}
		for (i=0; i< _registry.length; i++ ) {
			store = _registry[i].store;
			records = store.getUpdatedRecords();
			if (records.length > 0) {
				changeObjects = [];
				Ext.each(records, function(record) {	    
	        		changeObjects.push(
	        			Ext.create('Clear.data.ChangeObject', 
	        				{	
    	        				id: record.id,
        	    				changedPropertyNames:Ext.Object.getKeys(record.modified),
    	    		    		newVersion: record.data,
        	    				previousVersion: record.raw,
        	    				state: 2
    		        	    }
	        			) 
		    		);	
				});


				batchMember = Ext.create('Clear.transaction.BatchMember', {
					className: me.getDirectAction(store, 'update'),
					methodName: me.getDirectMethod(store, 'update'),
					parameters:[changeObjects],
					autoSyncSubtopic:Ext.isEmpty(store.autoSyncSubtopic)?store.proxy.readParamString:store.autoSyncSubtopic
				});
				me.batch.push(batchMember);
		    	me.operations.push(
		    		Ext.create('Clear.data.Operation', {
	                    action: 'update',
	                    'records': records,
	                    proxy: store
		    		})    
	            );	
			}
		}
		
		for (i=0; i< _registry.length; i++ ) {
			store = _registry[i].store;
			records = store.getNewRecords();
			if (records.length > 0) {
				changeObjects = [];
				Ext.each(records, function(record) {	    
	        		changeObjects.push(
	        			Ext.create('Clear.data.ChangeObject', 
	        				{	
            					id: record.id,	        			
        	    				changedPropertyNames:Ext.Object.getKeys(record.modified),
    	    		    		newVersion: record.data,
        	    				previousVersion: null,
        	    				state: 1
    		        	    }
	        			) 
	        	);		
				});	    
				batchMember = Ext.create('Clear.transaction.BatchMember', {
					className: me.getDirectAction(store, 'create'),
					methodName: me.getDirectMethod(store, 'create'),
					parameters:[changeObjects],
					autoSyncSubtopic:Ext.isEmpty(store.autoSyncSubtopic)?store.proxy.readParamString:store.autoSyncSubtopic
				});
				me.batch.push(batchMember);
		    	me.operations.push(
		    		Ext.create('Clear.data.Operation', {
	                    action: 'create',
	                    'records': records,
	                    proxy: store
		    		})    
		        );
		    }
		}
		return me.batch;
	},
	
	sendBatch: function(batch)/*:AsyncToken*/ {
		var me = this,
		    key,
		    store,
		    storeMap = me.storeMap = Ext.create("Ext.util.HashMap"),
	//		stateMap = me.stateMap = Ext.create("Ext.util.HashMap"),
		    i,
			_registry = me.internalRegistry;
		    batchGatewayCallback = me.prepareBatchGatewayCallback(),
		    batch = batch || this.batch;
		
		if( _registry) {
			for (i=0; i< _registry.length; i++ ) {
				store = _registry[i].store;
				key =  this.getStoreKey(store);
				storeMap.add(key, store);
//				var token:AsyncToken = new AsyncToken(new RemotingMessage());
//				token.method = DataCollection.SYNC;
				// If datacollection is not going to clean it's state with returned or pushed changeObjects,
				// we clear in in advance. If problem happens, we will restoreState()
//				dataCollection.cx_internal::saveState(token);	
//				if (!dataCollection.autoSyncEnabled && !dataCollection.roundTripSync)
//					dataCollection.resetState();

//				stateMap[key] = token;
			}
		}
		
		Clear.direct.action.BatchGateway.execute(batch, batchGatewayCallback);
	},
	
	/*
	 * Returns class name of the direct action as string
	 */
	getDirectAction: function(store, actionType) {
		var action = store.proxy.getDirectAction(actionType);
		if (!action) 
			action = store.proxy.getDirectAction('read');
		return action;
	},
	/*
	 * Returns object with className and methodName properties
	 */
	getDirectMethod: function(store, actionType) {
		var method = store.proxy.getDirectMethod(actionType),
			suffix = '';
		if (!method) {
			method = store.proxy.getDirectMethod('read');
			switch (actionType) {
				case 'destroy': suffix = "_deleteItems"; break; 
				case 'update': suffix = "_updateItems"; break; 
				case 'create': suffix = "_insertItems"; break; 
			}
			method = method + suffix;
		}
		return method;
	},
	/*
	 * @private
	 */
	getStoreKey: function(store) {
		return this.getDirectAction(store, 'read') + "." +
			this.getDirectMethod(store, 'read') + "."  + 
    		store.proxy.readParamString;
	},    
	/*
	 * @private
	 */
	ignoreSubmethod: function (methodName) {
		var t = methodName.split(/_deleteItems|_insertItems|_updateItems/);
		return t[0];
	},
	/*
	 * @private
	 * Prepares callback to work on behalf of this instance despite the global window scope
	 */
	prepareBatchGatewayCallback: function() {
		var me = this;
		return function (result, remotingEvent) {
			if (remotingEvent.status)
				me.onResult(result);
			else
				me.onException(remotingEvent.message, remotingEvent.where);
		};	 	
	},
	/*@private
	 * 
	 */
	onResult: function (result) {
		 var me = this,
		 	 batchMember,	 
		 	 key, proxy,response, store;

		 for (var i=0; i< result.length; i++ ) {
			 batchMember = Ext.create("Clear.transaction.BatchMember", result[i]);
			 key = batchMember.className + "." + me.ignoreSubmethod(batchMember.methodName) + "." +  batchMember.autoSyncSubtopic;
		     store = me.storeMap.get(key);
		     if (store) {			    	 
		    	response = Ext.create('Ext.direct.RemotingEvent', {
		    		status: true,
		    		action:batchMember.className,
		    		method:batchMember.methodName,
		    		result:batchMember.result
		    	}); 
		    	proxy = store.getProxy();
		    	proxy.processResponse(true, me.operations[i], null, response, Ext.emptyFn, me);
		    	store.onProxyWrite(me.operations[i]);
		    	/*
		    	store.batchUpdateMode  operation complete
		    	store. 
		    	event :batch with .operations, operation
		    	who listens
		    	
		    		        	 * listeners = {
                scope: me,
                exception: me.onBatchException
            };

        if (me.batchUpdateMode == 'operation') {
            listeners.operationcomplete = me.onBatchOperationComplete;
        } else {
            listeners.complete = me.onBatchComplete;
        }
		    	*/
			 } else {
				console.log("Missing store for key: " + key);	
			 }			
		}
		me.fireEvent('complete'); 
	 		
	},
	/*@private
	 * 
	 */
	onException: function (message, where) {
		this.fireEvent('exception', message, where); 
	},
	/*
	 * @private
	 */     
    sortByPriority: function(a, b) {
		if (a.priority > b.priority) {
			return 1;
		} else if (a.priority < b.priority) {
			return -1;
		} else  {
			// == 
			return 0;
		}
    },
	/*
	 * @private
	 */
    registerWithChildren: function(registration) {
		var me = this,
			internalRegistry = me.internalRegistry,
			childRanking=[],
			store = registration.store;
		
	//if (store.syncRequired) {
		internalRegistry.push(registration);
		
		// Add to registry all modified children
		var modifiedItems = store.modifiedMap.getValues();
		modifiedItems.forEach(function(item) {
			if (item.associatedDirty) {
				var associations = item.associations;
				var count = associations.getCount();
				for (var i=0; i<count; i++) {
					var association = associations.getAt(i);
					var associatedStore;
					if (association.type=="hasMany") {
						associatedStore = item[association.storeName];
						if (associatedStore) {							
							//			childRanking =  item.childRanking; // that was array for all associations					
							me.registerWithChildren({
								'store': associatedStore, 
								'priority': registration.priority * 100 //+ childRanking[i] 
							});
						}
					}				
				}
			}
		});
		
		// Children collections of deleted hierarchical items get cascade-removeAll, so
		// they need to be added to registry too
		store.getRemovedRecords().forEach(function(item){	
	
			var associations = item.associations;
			var count = associations.getCount();
			for (var i=0; i<count; i++) {
				var association = associations.getAt(i);
				var associatedStore;
				if (association.type=="hasMany") {
					associatedStore = item[association.storeName];
					if (associatedStore) {						
						me.registerWithChildren({
							'store': associatedStore, 
							'priority': registration.priority + 1 
						});
					}
				}
			}				
		}, this);
	//}		
    }
  
});
