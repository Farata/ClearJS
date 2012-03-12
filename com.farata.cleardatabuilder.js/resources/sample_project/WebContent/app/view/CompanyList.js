Ext.define('**{app.name}**.view.CompanyList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.companylist',
    requires: ['Ext.grid.plugin.CellEditing'],

    title: 'Companies',
    store: '**{app.name}**.store.hibernate_test.CompanyStore',
    minWidth: 400,
    minHeight:140,
    plugins: [{
        ptype: 'cellediting',
        clicksToEdit: 1

    }],

    selModel: {
        allowDeselect: true
    },

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        defaults: {
           width: 70 
        },
        items: [{
            xtype: 'button',
            itemId: 'btn_delete',
            action: 'delete',
            text: 'Remove',
            disabled: true
        }, {
            xtype: 'button',
            itemId: 'btn_insert',
            action: 'insert',
            text: 'Add'
        }]
    }],

    columns: [{
        header: 'Id',
        dataIndex: 'id',
        flex: 1
    }, {
        header: 'Company Name',
        dataIndex: 'company',
        flex: 1,
        editor: {
            xtype: 'textfield',
            allowBlank: false
        }
    }],

    initComponent: function() {
    	this.callParent(arguments);

    	var view = this.getView(),
            selectionModel = view.getSelectionModel();
            selectedCompany = null;

        this.addEvents({
            /**
             * @event companySelectionChange
             * Fires after selection of the company has been changed.
             * @param {**{app.name}**.store.hibernate_test.CompanyModel} selectedCompany
             */
            companySelectionChange: true
        });


        selectionModel.on('selectionChange', function (selectionModel, selectedModels, options) {
        	var selectedCompany = null;
            if (selectedModels.length > 0) {
            	selectedCompany = selectedModels[0];
            };
            this.fireEvent('companySelectionChange', selectedCompany);
        }, this); 

        this.bindComponents();
    },

    bindComponents: function () {
        var view = this.getView(),
            selectionModel = view.getSelectionModel(),
            btnDelete = this.down('#btn_delete');

        selectionModel.on('selectionchange', function(selectionModel, selectedRecords) {
            if (selectionModel.hasSelection()) {
                btnDelete.enable(true);
            } else {
                btnDelete.disable(true);
            }
        });
    }
});
