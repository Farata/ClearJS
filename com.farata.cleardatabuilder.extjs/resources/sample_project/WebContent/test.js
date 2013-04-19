Ext.Loader.setConfig({
    disableCaching: false,
    enabled: true,
    paths: {
    	Clear: 'clear',
    	Test: 'test',
    	**{app.name}**:'app'	
    }
});

Ext.syncRequire('**{app.name}**.init.InitDirect');

Ext.application({
    name: '**{app.name}**',
    requires: [
        'Clear.override.ExtJSOverrider',
        'Test.spec.AllSpecs'
    ],
    controllers: [
        'ToolbarController',
        'CompanyController',
        'AssociateController'
    ],
	launch: function() {
		var jasmineEnv = jasmine.getEnv();
		jasmineEnv.application = this;
	    jasmineEnv.addReporter(new jasmine.TrivialReporter());
	    jasmineEnv.execute();
	}
});
