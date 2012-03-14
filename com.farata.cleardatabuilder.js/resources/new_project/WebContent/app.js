Ext.Loader.setConfig({
    disableCaching: false,
    enabled: true,
    paths: {
    	**{app.name}**: 'app',
    	Clear: 'clear'
    }
});

Ext.syncRequire('**{app.name}**.init.InitDirect');

// Launch the application 
Ext.application({
    name: '**{app.name}**',
    requires: [
        'Clear.override.ExtJSOverrider',
        '**{app.name}**.override.ExtJSOverrider'
    ],
    controllers: [
      'ToolbarController',
      'CompanyListController',
      'AssociateController'
    ],
    autoCreateViewport: true
});