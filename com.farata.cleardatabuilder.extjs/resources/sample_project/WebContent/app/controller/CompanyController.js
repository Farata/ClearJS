Ext.define('**{app.name}**.controller.CompanyController', {
    extend: 'Ext.app.Controller',
    stores: ['example.CompanyStore'],
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
            store = this.getExampleCompanyStore(),
            company = store.createModel({
        	  companyName: "New Company"
            });
        
        store.add(company);
    },

    deleteCompanies: function() {
        var store = this.getExampleCompanyStore(),
            panel = this.getCompanyPanel(),
            view = panel.getView(),
            selectionModel = view.getSelectionModel(),
            selectedRecords = selectionModel.getSelection();

        store.remove(selectedRecords);
    }
});
