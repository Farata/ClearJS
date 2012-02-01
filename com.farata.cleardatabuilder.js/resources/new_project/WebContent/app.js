Ext.Loader.setConfig({
	disableCaching: false,
	enabled: true,
	paths  : {
		**{project.name}**: 'app', Clear:'clear'
	}
});

Ext.require(
	['Ext.direct.Manager'],

	function() {
		var providerConfig = Clear.direct.providerConfig;
		providerConfig.enableBuffer = 0;
		var provider = Ext.Direct.addProvider( providerConfig);
		Djn.RemoteCallSupport.addCallValidation(provider);
		Djn.RemoteCallSupport.validateCalls = true;	


		Ext.application({
			name: '**{project.name}**',
			appFolder:'app',
			controllers: [],
            launch: function() {
            	Ext.create('Ext.container.Viewport', {
                    layout: 'fit',
                    items: [
                        {
                            xtype: ''
                        }
                    ]
                });
            }
		});		
	}
);


