//
//Plug in Clear Components for Ext JS:
//

Ext.Loader.setConfig({
	disableCaching: false,
	enabled: true,
	paths  : {
		MyApp: 'app', Clear:'clear'
	}
});
	
Ext.require('Clear.override.ExtJSOverrider');

//
// Plug in DJN Direct:
//

// Load direct Manager, wait till it's loaded and then only add Provider.
// Once we add provider the actions will become visible under Clear.action
// namespace. It is importat that it happens before application is launched.

// (Note that Clear.direct gets set in direct/ServerConfig.js, which is 
// loaded by index.html prior to app.js). 

Ext.require([
        'Ext.direct.Manager'
    ],

	function() {	
		//PREPARE DIRECT ACTIONS		
		var providerConfig = Clear.direct.REMOTING_API;
		providerConfig.enableBuffer = 0;
		var provider = Ext.Direct.addProvider( providerConfig);
		//DJN NOTE: this is for debugging purposes, set validateCalls to false if this causes problems
		Djn.RemoteCallSupport.addCallValidation(provider);
		Djn.RemoteCallSupport.validateCalls = true;
		
		// --- Start your application when ALL is done 
		Ext.application({
			name: 'MyApp',
			controllers: [
              'MainController'
            ],
            autoCreateViewport:true
		});			
	}
);