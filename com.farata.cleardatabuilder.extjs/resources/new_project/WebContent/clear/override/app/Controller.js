Ext.define('Clear.override.app.Controller', {
    override: 'Ext.app.Controller',
    //4.2 Override
    statics: {
        getGetterName: function (name, kindUpper) {
            var fn = 'get',
                parts = name.split('.'),
                numParts = parts.length,
                index,
                pos;

            // Handle namespaced class names. E.g. feed.Add becomes getFeedAddView etc.
            for (index = 0; index < numParts; index++) {
                fn += Ext.String.capitalize(parts[index]);
            }

            // --- Prevent StoreStore-like suffixes ---
            pos = fn.length - kindUpper.length;
            if (pos <= 0 || fn.substring(pos) !== kindUpper) {
                fn += kindUpper;
            }

            return fn;
        }
    },
    //4.0.X Override prior to 4.2
	createGetters: function(type, refs) {
		type = Ext.String.capitalize(type);
		Ext.Array.each(refs, function(ref) {
				var fn = 'get',
					parts = ref.split('.'),
					pos;

				// Handle namespaced class names. E.g. feed.Add becomes getFeedAddView etc.
				Ext.Array.each(parts, function(part) {
				    fn += Ext.String.capitalize(part);
				});
				
				// --- Prevent StoreStore-like suffixes --- 
				pos = fn.length - type.length;
				if (pos<=0 || fn.substring(pos)!==type) {        	
					fn += type;
				}
				
				if (!this[fn]) {
				    this[fn] = Ext.Function.pass(this['get' + type], [ref], this);
				}
				// Execute it right away
				this[fn](ref);
		},this);
	}
});