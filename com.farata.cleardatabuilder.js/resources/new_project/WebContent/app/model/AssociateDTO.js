Ext.define('CDB.model.AssociateDTO',{
	extend:'Ext.data.Model',
	fields:['id','companyId','uid','associate'],
	belongsTo: 'CDB.model.CompanyDTO'
});