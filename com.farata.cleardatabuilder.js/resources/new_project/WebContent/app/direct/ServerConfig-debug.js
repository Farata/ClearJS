/**********************************************************************
 * 
 * Code generated automatically by DirectJNgine
 * Copyright (c) 2009, Pedro Agulló Soliveres
 * 
 * DO NOT MODIFY MANUALLY!!
 * 
 **********************************************************************/

Ext.namespace( '**{app.name}**.direct.config');
Ext.namespace( '**{app.name}**.direct.action');

**{app.name}**.direct.config.PROVIDER_BASE_URL=window.location.protocol + '//' + window.location.host + '/' + (window.location.pathname.split('/').length>2 ? window.location.pathname.split('/')[1]+ '/' : '')  + 'djn/directprovider';

**{app.name}**.direct.config.POLLING_URLS = {
}

**{app.name}**.direct.config.REMOTING_API = {
  url: **{app.name}**.direct.config.PROVIDER_BASE_URL,
  type: 'remoting',
  namespace: **{app.name}**.direct.action,
  actions: {
    CompanyAction: [
      {
        name: 'getCompanies_insertItems'/*(java.util.List) => java.util.List */,
        len: 1,
        formHandler: false
      },
      {
        name: 'getCompanies_deleteItems'/*(java.util.List) => java.util.List */,
        len: 1,
        formHandler: false
      },
      {
        name: 'getCompanies_updateItems'/*(java.util.List) => java.util.List */,
        len: 1,
        formHandler: false
      },
      {
        name: 'getCompanies'/*() => java.util.List */,
        len: 0,
        formHandler: false
      }
    ],
    CompanyAssociateAction: [
      {
        name: 'getCompanyAssociates_deleteItems'/*(java.util.List) => java.util.List */,
        len: 1,
        formHandler: false
      },
      {
        name: 'getCompanyAssociates_updateItems'/*(java.util.List) => java.util.List */,
        len: 1,
        formHandler: false
      },
      {
        name: 'getCompanyAssociates'/*(Integer) => java.util.List */,
        len: 1,
        formHandler: false
      },
      {
        name: 'getCompanyAssociates_insertItems'/*(java.util.List) => java.util.List */,
        len: 1,
        formHandler: false
      }
    ]
  }
}

