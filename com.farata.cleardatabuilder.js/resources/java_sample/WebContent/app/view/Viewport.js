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
        html: '"Fill" data from server, interactively modify, then "Commit" to persist changes.'
            + 'Use "Reset Data" to reinsert the original data tuples. <br><br>'
            + 'NOTE: only "Company Name" and "associate name" columns are editable'
    }, {
        	xtype: 'toolbar',
        	items: [
            { itemId: 'btn_fill', text: 'Fill', action: 'fill' },
    	        {	itemId:'btn_test', text:'Test'},
            { itemId: 'btn_commit', text: 'Commit', action: 'commit', disabled: true }
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