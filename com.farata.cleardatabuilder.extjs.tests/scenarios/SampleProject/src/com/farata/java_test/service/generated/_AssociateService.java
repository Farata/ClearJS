package com.farata.java_test.service.generated;

import java.util.*;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.*;

import clear.transaction.identity.PropertyRack;
import clear.utils.MessagingUtils;
import clear.cdb.utils.SessionFactoryUtils;

import flex.data.*;
import flex.data.messages.DataMessage;

public class _AssociateService implements com.farata.java_test.service.IAssociateService {
		
	public java.util.List<com.farata.java_test.dto.AssociateDTO> fill(java.lang.Long companyId) {
		return null;
	}
		
 	public List<ChangeObject> fill_sync(List<ChangeObject> items) throws Exception {
 		fill_deleteItems(items);
 		fill_updateItems(items);
 		fill_insertItems(items);
			
		MessagingUtils.pushChanges("com.farata.java_test.IAssociate.fill", items);
			
 		return items;
 	}

	public List<ChangeObject> fill_insertItems(List<ChangeObject> items) throws Exception {
        List<ChangeObject> list = new ArrayList<ChangeObject>();
        for (ChangeObject changeObject:items) {
            if(changeObject.isCreate()) {
            	fill_doCreate(changeObject);
            	list.add(changeObject);
            }	       	
        }
			
		MessagingUtils.pushChanges("com.farata.java_test.IAssociate.fill", list);
			
		return list;
	} 	

	public List<ChangeObject> fill_updateItems(List<ChangeObject> items) throws Exception {
        List<ChangeObject> list = new ArrayList<ChangeObject>();
        for (ChangeObject changeObject:items) {
            if(changeObject.isUpdate()) {
            	fill_doUpdate(changeObject);
            	list.add(changeObject);
            }	       	
        }
			
		MessagingUtils.pushChanges("com.farata.java_test.IAssociate.fill", list);
			
		return list;
	} 	

	public List<ChangeObject> fill_deleteItems(List<ChangeObject> items) throws Exception {
        List<ChangeObject> list = new ArrayList<ChangeObject>();
        for (ChangeObject changeObject:items) {
            if(changeObject.isDelete()) {
            	fill_doDelete(changeObject);
            	list.add(changeObject);
            }	       	
        }
			
		MessagingUtils.pushChanges("com.farata.java_test.IAssociate.fill", list);
			
		return list;
	} 	

	protected void fill_doCreate(ChangeObject changeObject) {
	}
	
	protected void fill_doUpdate(ChangeObject changeObject) {
	}

	protected void fill_doDelete(ChangeObject changeObject) {
	}
		
}
	