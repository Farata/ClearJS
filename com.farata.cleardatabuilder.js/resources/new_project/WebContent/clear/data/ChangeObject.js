Ext.define('Clear.data.ChangeObject', {
    extend: 'Ext.Base',
    alternateClassName: 'ChangeObject',
    changedPropertyNames: null, 
    newVersion: null,
    previousVersion: null,
    id: null,
    state: 0,
    fields: ['changedPropertyNames', 'newVersion', 'previousVersion', 'state'],
    statics: {
    	stateType: {CREATE:1, UPDATE:2, DELETE:3}
    },
    constructor: function (config) {
    	var me = this;
    	Ext.apply(me, config);
    	me.callParent([config]);
    }
});

