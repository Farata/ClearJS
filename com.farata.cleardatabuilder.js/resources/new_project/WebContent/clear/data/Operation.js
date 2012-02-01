/*

This file is part of Ext JS 4

Copyright (c) 2011 Sencha Inc

Contact:  http://www.sencha.com/contact

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.

If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.

*/
/**
 * @author Ed Spencer
 *
 * Represents a single read or write operation performed by a {@link Ext.data.proxy.Proxy Proxy}. Operation objects are
 * used to enable communication between Stores and Proxies. Application developers should rarely need to interact with
 * Operation objects directly.
 *
 * Several Operations can be batched together in a {@link Ext.data.Batch batch}.
 */
Ext.define('Clear.data.Operation', {
 
	extend: 'Ext.data.Operation',
	
	proxy: null,

    /**
     * This method is called to commit data to this instance's records given the records in
     * the server response. This is followed by calling {@link Ext.data.Model#commit} on all
     * those records (for 'create' and 'update' actions).
     *
     * Based on property {@link #actionSkipSyncRe}, if the value of {@link #action} is 'destroy', any server records are ignored and the
     * {@link Ext.data.Model#commit} method is not called.
     *
     * @param {Ext.data.Model[]} changeObjects An array of {@link Clear.data.ChangeObject} objects returned by
     * the server.
     * @markdown
     */
    commitRecords: function (changeObjects) {
        var me = this,
            mc, index, clientRecords, changeObject, clientRec, serverRec;

        if (!me.actionSkipSyncRe.test(me.action)) {
            clientRecords = me.records;

            if (clientRecords && clientRecords.length) {
                mc = Ext.create('Ext.util.MixedCollection', true, function(r) {return r.id;});
                mc.addAll(clientRecords);

                for (index = changeObjects ? changeObjects.length : 0; index--; ) {
                    changeObject = changeObjects[index];              
                    clientRec = mc.get(changeObject.id);
                    // Notice that now we compare by ID of the record rather then by the "id" of the data of the record
                    // This helps identify records where data id has been modified/autoincremented by the server
                    // Goind forward with PUSH we will need to add second level search by comparing the data id
                    if (clientRec) {
                        serverRec = changeObject.newVersion;
                        clientRec.beginEdit();
                        clientRec.set(serverRec.data);
                        clientRec.endEdit(true);
                    }
                }

                if (me.actionCommitRecordsRe.test(me.action)) {
                    for (index = clientRecords.length; index--; ) {
                        clientRecords[index].commit();
                    }
                }
            }
        }
    },
    
    /**
     * Returns an array of Ext.data.Model instances as set by the Proxy.
     * For read-action operations - that's resultSet.records;
     * for create-, update-, destroy-action  operations - that's the already modified records
     * @return {Ext.data.Model[]} Any loaded Records
     */
    getRecords: function() {
    	var result = this.records;
    	if (this.action=="read") {
	        var resultSet = this.getResultSet();
	        if (resultSet !== undefined )
	        	result = resultSet.records;
    	} 
    	return result;
    },
 
});
