/*

This file is part of Ext JS 4

Copyright (c) 2011 Sencha Inc

Contact:  http://www.sencha.com/contact

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.

If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.

*/
/**
 * This class is used to send requests to the server using {@link Ext.direct.Manager Ext.Direct}. When a
 * request is made, the transport mechanism is handed off to the appropriate
 * {@link Ext.direct.RemotingProvider Provider} to complete the call.
 *
 * # Specifying the function
 *
 * This proxy expects a Direct remoting method to be passed in order to be able to complete requests.
 * This can be done by specifying the {@link #directFn} configuration. This will use the same direct
 * method for all requests. Alternatively, you can provide an {@link #api} configuration. This
 * allows you to specify a different remoting method for each CRUD action.
 *
 * # Parameters
 *
 * This proxy provides options to help configure which parameters will be sent to the server.
 * By specifying the {@link #paramsAsHash} option, it will send an object literal containing each
 * of the passed parameters. The {@link #paramOrder} option can be used to specify the order in which
 * the remoting method parameters are passed.
 *
 * # Example Usage
 *
 *     Ext.define('User', {
 *         extend: 'Ext.data.Model',
 *         fields: ['firstName', 'lastName'],
 *         proxy: {
 *             type: 'direct',
 *             directFn: MyApp.getUsers,
 *             paramOrder: 'id' // Tells the proxy to pass the id as the first parameter to the remoting method.
 *         }
 *     });
 *     User.load(1);
 */
Ext.define('Clear.data.proxy.DirectProxy', {
    /* Begin Definitions */

    extend: 'Ext.data.proxy.Direct',

    alternateClassName: 'Clear.data.DirectProxy',
    alias: 'proxy.clear',

    defaultReaderType: 'clear',
    defaultWriterType: 'clear',
    /**
     * @cfg {Number} timeout
     * The number of milliseconds to wait for a response. Set to 300000 milliseconds (5 minutes) vs. original 30 sec
     */
    timeout : 300000,
    
    requires: [
       'Ext.direct.Manager',
       'Clear.data.writer.Json',
       'Clear.data.reader.Json'
    ],
    
    /* End Definitions */
    
    constructor: function(config){

    	config = config || {};
    	config.api = config.api || {};
    	config.api.read = config.api.read || config.directFn;
    	if (!config.api.read) {
			Ext.Error.raise( "Direct Proxy can not be constructed when neither directFn nor api.read can be resolved");
    	}
    	
        this.addEvents(
            /**
             * @event
             * Fires when the server returns the result
             * @param {Ext.data.proxy.Proxy} this
             * @param {Ext.data.Request} result The result that was sent
             * @param {Ext.data.Operation} operation The operation that triggered the request
             */
            'result'
        );
        this.callParent(arguments);
		this.setWriter('clear');
    },
    
	/*
	 * Returns name of the remote action
	 * @param {String} local action type: a choice from 'create', 'read', 'update', 'destroy'
	 */
	getDirectAction: function(actionType) {
		try {
			var directCfg = this.api[actionType].directCfg;
			return directCfg.action;
		} catch (e) {
		}	
	},
	
	/*
	 * Returns name of the remote method
	 * @param {String} local action type: a choice from 'create', 'read', 'update', 'destroy'
	 */
	getDirectMethod: function(actionType) {
		
		try {
			var directCfg = this.api[actionType].directCfg;
			return directCfg.method.name;
		} catch (e) {
		}	
	},

    // They had only message and ultimately error coming to the DataStore load event was flat string
    setException: function(operation, response) {
        operation.setException({action:response.action, method:response.method, message:response.message, where:response.where});
    },
    

    processResponse: function(success, operation, request, response, callback, scope){
        var me = this,
            reader,
            result;

        if (success === true) {
            reader = me.getReader();
            result = reader.read(me.extractResponseData(response));

            if (result.success !== false) {
                //see comment in buildRequest for why we include the response object here
                Ext.apply(operation, {
                    response: response,
                    resultSet: result
                });
                // We added this
                if (me.fireEvent('result', this, result, operation)) {                	
                	operation.commitRecords(result.records);
                }
                operation.setCompleted();
                operation.setSuccessful();
            } else {
                operation.setException(result.message);
                me.fireEvent('exception', this, response, operation);
            }
        } else {
            me.setException(operation, response);
            me.fireEvent('exception', this, response, operation);
        }

        //this callback is the one that was passed to the 'read' or 'write' function above
        if (typeof callback == 'function') {
            callback.call(scope || me, operation);
        }

        me.afterRequest(request, success);
    }

});




