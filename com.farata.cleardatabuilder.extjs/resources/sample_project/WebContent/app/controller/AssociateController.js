Ext.define('**{app.name}**.controller.AssociateController', {
    extend: 'Ext.app.Controller',
    stores: ['example.AssociateStore'],
    refs: [{
       ref: 'associatePanel',
       selector: 'associatelist'
    }],

    selectedCompany: null,

    init: function() {
        this.control({
            // These events come from the buttons of the ticketsPanel
            'associatelist button[action=insert]': {
                click: this.insertAssociate
            },
            'associatelist button[action=delete]': {
                click: this.deleteAssociate
            },

            // This event gets intercepted from the top panel
            'companylist': {
               companySelectionChange: this.setSelectedCompany
            }
        });
    },
   
    setSelectedCompany: function(selectedCompany) {
    	if (selectedCompany !== this.selectedCompany) {    		
    		var panel = this.getAssociatePanel(),
    		view = panel.getView(),
    		associateStore;
    		
    		if (selectedCompany === null) {
    			panel.disable();
     			associateStore = this.getExampleAssociateStore();
    		} else {
    			panel.enable();
    			associateStore = selectedCompany.getAssociates();
    		}
    		
    		view.bindStore(associateStore);
    		this.selectedCompany = selectedCompany;
    	}
    },

    insertAssociate: function(button) {
        var me = this,
            panel = me.getAssociatePanel(),
            view = panel.getView(),
            store = view.getStore(),
            
            associate = store.createModel({
                associateName: 'John Smith'
            });
        
        //Ext Model won't make phantom if id is part of the config
        associate.setId(store.getLocalIdentity());

        store.add(associate);
    },
   
    deleteAssociate: function(button) {
        var panel = this.getAssociatePanel(),
	        view = panel.getView(),
            store = view.getStore(),
            selectionModel = view.getSelectionModel(),
            selectedRecords = selectionModel.getSelection();
       
        store.remove(selectedRecords);
    }
});
