Ext.Loader.setConfig({
    disableCaching: false,
    enabled: true,
    paths: {
    	**{app.name}**: 'app',
    	Clear: 'clear'
    }
});

// Load Direct Manager synchronously 
Ext.syncRequire('Ext.direct.Manager');

// Add Clear and application providers. 
// (Needs Ext.direct.Manager to be loaded)
function addProvider(providerConfig) {
    var provider;
    // 5 min for Java-side debugging. Might be appropriate for production as well.
    providerConfig.timeout = 0.5 * 60 * 1000; 
    provider = Ext.Direct.addProvider( providerConfig);
    // VERY IMPORTANT: this is for debugging purposes, set validateCalls to false if this causes problems
    Djn.RemoteCallSupport.addCallValidation(provider);
    Djn.RemoteCallSupport.validateCalls = true;
}

Ext.direct.Manager.on('exception', function () {
	var i=5;	
 },this
);


addProvider(Clear.direct.REMOTING_API);

// Launch the application 
Ext.application({
    name: '**{app.name}**',
    requires: ['Clear.override.ExtJSOverrider'],
    controllers: [
      'ToolbarController', 
      'CompanyListController', 
      'AssociateListController'
    ],
    autoCreateViewport: true
});
