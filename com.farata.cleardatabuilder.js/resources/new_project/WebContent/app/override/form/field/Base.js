Ext.define('**{app.name}**.override.form.field.Base', {
    override: 'Ext.form.field.Base',

    initComponent : function() {
        this.callOverridden(arguments);
        this.enableBubble('change');
    },

    getBubbleTarget: function() {
        var me = this;
        if (me.form === undefined) {
            me.form = me.up('form');
        }

        return me.form;
    }
});
