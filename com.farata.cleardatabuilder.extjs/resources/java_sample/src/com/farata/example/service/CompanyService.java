package com.farata.example.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import clear.transaction.identity.IdentityRack;

import com.farata.example.data.DataEngine;
import com.farata.example.dto.CompanyDTO;
import com.farata.example.service.generated._CompanyService;

import clear.data.ChangeObject;

public class CompanyService extends _CompanyService {
	
	DataEngine dataEngine = DataEngine.getInstance();
	
	@Override
	public List<CompanyDTO> getCompanies() {
		List<CompanyDTO> companyList = dataEngine.getCompanyList();
		System.out.println("getCompanies method has returned " + companyList.size() + " CompanyDTO records");
		return companyList;
	}
	
	// This method, with arbitrary name, illustrates how to insert new data
	//  marked as changeObject.isCreate() 
	
	@Override
	public void getCompanies_doCreate(ChangeObject changeObject) {
		System.out.println("doCreate method");
		CompanyDTO dto =  (CompanyDTO)deserializeObject((Map<String, String>)changeObject.getNewVersion(), CompanyDTO.class);					

		
		if ((dto.id == null) || (dto.id <= 0)) {
			Object oldId = dto.id;
			dto.id = dataEngine.getMaxCompanyId() + 1;	
			changeObject.addChangedPropertyName("id");
			
			IdentityRack.setIdentity("com.farata.example.dto.CompanyDTO", "id", oldId, dto.id);		
		}

		dataEngine.getCompanyList().add(dto);
		changeObject.setNewVersion(dto);
	}

	// This method, with arbitrary name, illustrates how to update data marked 
	//  as changeObject.isUpdate() utilizing array of changedPropertyNames
	@Override
	public void getCompanies_doUpdate(ChangeObject changeObject) {
		
		System.out.println("doUpdate method executing");
		CompanyDTO newVersion =  (CompanyDTO)deserializeObject((Map<String, String>)changeObject.getNewVersion(), CompanyDTO.class);
		CompanyDTO previousVersion =  (CompanyDTO)deserializeObject((Map<String, String>)changeObject.getPreviousVersion(), CompanyDTO.class);
		
		CompanyDTO originalDTO = dataEngine.findCompany(previousVersion);
		if (originalDTO != null) {
			String[] changedPropertyNames = changeObject.getChangedPropertyNames();
			Class<?extends CompanyDTO> clazz = CompanyDTO.class;
			for (String propertyName: changedPropertyNames) {
				try {
				    Field field = clazz.getField(propertyName);
				    Object originalValue = field.get(originalDTO);
				    Object newValue = field.get(newVersion);
				    field.set(originalDTO, newValue);
					System.out.println("Changed: " + propertyName + ", Original Value: "
							+ originalValue + ", New Value: " + newValue);				    
				} catch (Exception e) {
					new RuntimeException("Failed updating  property '" + propertyName +"'"  );
				}
			}
		}	
	}

	// This method, with arbitrary name, illustrates how to delete data marked 
	//  as changeObject.isDelete().
	@Override
	public void getCompanies_doDelete(ChangeObject changeObject) {

		System.out.print("doDelete method ");
		
		CompanyDTO dto =  (CompanyDTO)deserializeObject((Map<String, String>)changeObject.getPreviousVersion(), CompanyDTO.class);
		CompanyDTO removed = dataEngine.removeCompany(dto);
		if (removed != null) {
			System.out.println("removed: " + removed);
		}
	}	
}