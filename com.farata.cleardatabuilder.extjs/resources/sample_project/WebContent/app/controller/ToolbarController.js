Ext.define('ssss.controller.ToolbarController', {
    extend: 'Ext.app.Controller',   
    stores: ['example.CompanyStore'],
    
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
    	var store = this.getExampleCompanyStore();
        store.load();
    },

    sync: function() {
    	var store = this.getExampleCompanyStore();
    	store.sync();
    }
});