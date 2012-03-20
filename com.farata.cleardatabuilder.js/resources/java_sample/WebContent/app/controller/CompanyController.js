Ext.define('**{app.name}**.controller.CompanyController', {
    extend: 'Ext.app.Controller',
    stores: ['**{app.name}**.store.java_test.CompanyStore'],
    refs: [{
       ref: 'companyPanel',
       selector: 'companylist'
    }],
    
    constructor: function () {
        this.callParent(arguments); 	
    },
    
    init: function() {    	
        this.control({
            'companylist button[action=insert]': {
                click: this.insertCompany
            },
            'companylist button[action=delete]': {
                click: this.deleteCompanies
            }
        });        
    },

    insertCompany: function() {
        var me = this,
            store = this.getStore('**{app.name}**.store.java_test.CompanyStore'),
            company = store.createModel({
        	  companyName: "New Company"
            });
        
        //Ext Model won't make phantom if id is part of the config
        company.setId(store.getLocalIdentity());
        
        store.add(company);
    },

    deleteCompanies: function() {
        var store = this.getStore('**{app.name}**.store.java_test.CompanyStore'),
            panel = this.getCompanyPanel(),
            view = panel.getView(),
            selectionModel = view.getSelectionModel(),
            selectedRecords = selectionModel.getSelection();

        store.remove(selectedRecords);
    }
});
