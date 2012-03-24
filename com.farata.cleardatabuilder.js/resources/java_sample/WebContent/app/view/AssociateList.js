Ext.define('**{app.name}**.view.AssociateList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.associatelist',

    title: 'Associates',
    store: 'example.AssociateStore',
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
        defaults: {
            width: 70,
            margin: '0 5px 0 0',
            cls: 'PanelDocketBtnsClass'
        },
        items: [{
            xtype: 'button',
            action: 'delete',
            text: 'Remove',
            scope: this,
            disabled: true
        }, {
            xtype: 'button',
            action: 'insert',
            text: 'Add',
            scope: this
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
