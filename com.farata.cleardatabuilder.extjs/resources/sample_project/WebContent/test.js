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

var Application = null;

Ext.onReady(function() {
    Application = Ext.create('Ext.app.Application', {
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
            jasmine.getEnv().addReporter(new jasmine.TrivialReporter());
            jasmine.getEnv().execute();
        }
    //,autoCreateViewport:true
    });
});

