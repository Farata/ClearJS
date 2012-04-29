/**********************************************************************
 * 
 * Code generated automatically by DirectJNgine
 * Copyright (c) 2009, Pedro Agulló Soliveres
 * 
 * DO NOT MODIFY MANUALLY!!
 * 
 **********************************************************************/

Ext.namespace( 'AM.direct.config');
Ext.namespace( 'AM.direct.action');

AM.direct.config.PROVIDER_BASE_URL=window.location.protocol + '//' + window.location.host + '/' + (window.location.pathname.split('/').length>2 ? window.location.pathname.split('/')[1]+ '/' : '')  + 'djn/directprovider';

AM.direct.config.POLLING_URLS = {
}

AM.direct.config.REMOTING_API = {
  url: AM.direct.config.PROVIDER_BASE_URL,
  type: 'remoting',
  namespace: AM.direct.action,
  actions: {
    UserAction: [
      {
        name: 'loadUsers_insertItems'/*(java.util.List) => java.util.List */,
        len: 1,
        formHandler: false
      },
      {
        name: 'loadUsers_deleteItems'/*(java.util.List) => java.util.List */,
        len: 1,
        formHandler: false
      },
      {
        name: 'loadUsers'/*(String, String) => java.util.List */,
        len: 2,
        formHandler: false
      },
      {
        name: 'loadUsers_updateItems'/*(java.util.List) => java.util.List */,
        len: 1,
        formHandler: false
      },
      {
        name: 'loadUsers_sync'/*(java.util.List) => java.util.List */,
        len: 1,
        formHandler: false
      }
    ],
    TicketAction: [
      {
        name: 'getTickets_sync'/*(java.util.List) => java.util.List */,
        len: 1,
        formHandler: false
      },
      {
        name: 'getTickets_insertItems'/*(java.util.List) => java.util.List */,
        len: 1,
        formHandler: false
      },
      {
        name: 'getTickets_updateItems'/*(java.util.List) => java.util.List */,
        len: 1,
        formHandler: false
      },
      {
        name: 'getTickets_deleteItems'/*(java.util.List) => java.util.List */,
        len: 1,
        formHandler: false
      },
      {
        name: 'getTickets'/*(String) => java.util.List */,
        len: 1,
        formHandler: false
      }
    ]
  }
}

