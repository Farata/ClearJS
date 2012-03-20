Ext.define('**{app.name}**.controller.ToolbarController', {
    extend: 'Ext.app.Controller',   
    stores: ['**{app.name}**.store.java_test.CompanyStore'],
    
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
    	var store = this.getStore('**{app.name}**.store.java_test.CompanyStore');
        store.load( /*{ params: { 0: 'First', 1: 'Second' }}*/);
    },

    sync: function() {
    	var store = this.getStore('**{app.name}**.store.java_test.CompanyStore');
    	store.sync();
    }
});