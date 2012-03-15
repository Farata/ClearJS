Ext.define('**{app.name}**.init.InitDirect' ,{
	requires: ['Ext.direct.Manager']
}, function () {

	function addProvider(providerConfig) {
	    var provider;
	    // 5 min for Java-side debugging. Might be appropriate for production as well.
	    providerConfig.timeout = 0.5 * 60 * 1000; 
	    provider = Ext.Direct.addProvider( providerConfig);
	    // VERY IMPORTANT: this is for debugging purposes, set validateCalls to false if this causes problems
	    Djn.RemoteCallSupport.addCallValidation(provider);
	    Djn.RemoteCallSupport.validateCalls = true;
	}

	addProvider(Clear.direct.config.REMOTING_API);
	addProvider(**{app.name}**.direct.config.REMOTING_API);
});

