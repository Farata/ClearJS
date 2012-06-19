Ext.define('**{app.name}**.view.AssociateList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.associatelist',
    //Effectively Ext JS will add 
    //store: 'ext-empty-store',
    title: 'Associates',
 
    disabled: true,
    forceFit: true,
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
        items: [{
            xtype: 'button',
            action: 'delete',
            text: 'Remove Associate',
            scope: this,
            disabled: true,
            iconCls: 'iconRemovePerson'
        }, {
            xtype: 'button',
            action: 'insert',
            text: 'Add Associate',
            scope: this,
            iconCls: 'iconAddPerson'
        }]
    }],

    columns: [{
        header: 'Id',
        dataIndex: 'id'
    }, {
        header: 'Company Id',
        dataIndex: 'companyId'
    }, {
        header: 'Associate Name',
        dataIndex: 'associateName',
        editor: 'textfield'
    }],

    listeners: {
        selectionchange: function(selModel) {
            var btnDelete = this.down('button[action="delete"]');

            if (selModel.hasSelection()) {
                btnDelete.enable(true);
            } else {
                btnDelete.disable(true);
            }
        }
    },
});
