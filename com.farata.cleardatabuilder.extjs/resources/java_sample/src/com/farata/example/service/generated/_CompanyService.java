	
package com.farata.example.service.generated;

import java.util.*;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.*;

import clear.transaction.*;
import clear.transaction.identity.IdentityRack;
import clear.cdb.utils.SessionFactoryUtils;
import clear.data.ChangeObject;

import com.google.gson.Gson;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;

		
public class _CompanyService implements _ICompanyService {
			
	@DirectMethod
	public java.util.List<com.farata.example.dto.CompanyDTO> getCompanies() {
		return null;
	}
		
 	@DirectMethod
	public List<ChangeObject> getCompanies_insertItems(List<ChangeObject> items) throws Exception {
        List<ChangeObject> list = new ArrayList<ChangeObject>();
    	Iterator<ChangeObject> iterator = items.iterator();
		while (iterator.hasNext()) {
			clear.data.ChangeObject co = (clear.data.ChangeObject)deserializeObject((Map<String, String>)iterator.next(),clear.data.ChangeObject.class);
            if(co.isCreate()) {
            	getCompanies_doCreate(co);
            	list.add(co);
            }	       	
        }					
		return list;				
	} 	

	@DirectMethod
	public List<ChangeObject> getCompanies_updateItems(List<ChangeObject> items) throws Exception {
        List<ChangeObject> list = new ArrayList<ChangeObject>();
        
    	Iterator<ChangeObject> iterator = items.iterator();
		while (iterator.hasNext()) {
			clear.data.ChangeObject co = (clear.data.ChangeObject)deserializeObject((Map<String, String>)iterator.next(),clear.data.ChangeObject.class);
			if(co.isUpdate()) {
				getCompanies_doUpdate(co);
				list.add(co);
			}	       	
		}						
		return list;		
	} 	

	@DirectMethod
	public List<ChangeObject> getCompanies_deleteItems(List<ChangeObject> items) throws Exception {
		List<ChangeObject> list = new ArrayList<ChangeObject>();
		Iterator<ChangeObject> iterator = items.iterator();
		while (iterator.hasNext()) {
			clear.data.ChangeObject co = (clear.data.ChangeObject)deserializeObject((Map<String, String>)iterator.next(),clear.data.ChangeObject.class);
			if (co.isDelete()) {
            	getCompanies_doDelete(co);
            	list.add(co);
            }	       	
        }					
		return list;
	} 	

	public void getCompanies_doCreate(ChangeObject changeObject) {
	}
	
	public void getCompanies_doUpdate(ChangeObject changeObject) {
	}

	public void getCompanies_doDelete(ChangeObject changeObject) {
	}
		
	protected Object deserializeObject(Map<String, String> map, Class<?> clazz){
		Gson gson = new Gson();
		String jsonItem = gson.toJson(map);
		return gson.fromJson(jsonItem, clazz);
	}
}
	