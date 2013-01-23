Ext.define('**{app.name}**.view.Viewport' ,{
    extend: 'Ext.container.Viewport',
 
    layout: {
		type:	'vbox',
		align: 'center',
    },
    items: [
           {xtype:'component', itemId:'topSpacer', flex:1}, 
           {xtype:'button', action:'hello', text:'"Hello" button', height: 100},
           {xtype:'component', itemId:'bottomSpacer', flex:1} 
    ]   
});