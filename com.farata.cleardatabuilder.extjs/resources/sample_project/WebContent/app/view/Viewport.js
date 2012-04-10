Ext.define('**{app.name}**.view.Viewport' ,{
    extend: 'Ext.container.Viewport', 
    requires: [
        '**{app.name}**.view.CompanyList',
        '**{app.name}**.view.AssociateList'
    ],
 
    layout: {
		type:	'vbox',
		align: 'stretch'
    },

    items: [{
        border: false,
        html: 'You can load companies\' list from server, add/remove/modify companies info and sync back to the server.<br>' +
        	  'Highlight a company to remove it or view/edit company\'s associates. Columns  <i>id</i> and <i>companyId</i> are not editable.'
    }, {
        	xtype: 'toolbar',
        	items: [
            { itemId: 'btn_fill', text: 'Load', action: 'fill', iconCls:'iconLargeLoad' },
            { itemId: 'btn_commit', text: 'Sync', action: 'commit', disabled: true, iconCls:'iconLargeSync' }
        ]
    }, {
        xtype: 'companylist',
        flex: 5
    }, {
        xtype: 'splitter',
            height: 5,
            style: {
                background: '#002366'
            }   	
    }, {
        xtype: 'associatelist',
            flex:5,
			border: false
    }],
 			
    initComponent: function () {
    	this.callParent(arguments);
    	this.bindComponents();
    },
    
    bindComponents: function () {
    	var btnCommit = this.down('#btn_commit'),
            panel = this.down('companylist'),
            store = panel.getStore();
    	
    	btnCommit.disable(true);
    	if (store) {
	    	store.on('commitRequiredChange', function(store, commitRequired){
                if (commitRequired) {
	    			btnCommit.enable(true);
                } else {
	    			btnCommit.disable(true);
                }
	    	});
    	}	  	
    }     
});