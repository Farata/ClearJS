Ext.define('CDB.model.CompanyDTO',{
	extend:'Ext.data.Model',
	fields:['id','company'],
	hasMany:'CDB.model.AssociateDTO'
});