Ext.Loader.setConfig({
    disableCaching: false,
    enabled: true,
    paths: {
        Clear: 'clear',
        **{app.name}**: 'app'
    }
});

Ext.syncRequire('**{app.name}**.init.InitDirect');

// Launch the application 
Ext.application({
    name: '**{app.name}**',
    requires: [
        'Clear.override.ExtJSOverrider'
    ],
    controllers: [
        'ToolbarController',
        'CompanyListController',
        'AssociateController'
    ],
    autoCreateViewport: true
});