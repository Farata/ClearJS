Ext.define('uuoo.controller.ToolbarController', {
    extend: 'Ext.app.Controller',   
    stores: ['uuoo.store.hibernate_test.CompanyStore'],
    
    init: function() {
        this.control({
        	'button[action=fill]': {
        		click: this.fill
        	},
        	'button[action=commit]': {
        		click: this.sync
        	}
        });       
    },
	
    fill: function() {
    	var store = this.getStore('uuoo.store.hibernate_test.CompanyStore');
        store.load( /*{ params: { 0: 'First', 1: 'Second' }}*/);
    },

    sync: function() {
    	var store = this.getStore('uuoo.store.hibernate_test.CompanyStore');
    	store.sync();
    }
});