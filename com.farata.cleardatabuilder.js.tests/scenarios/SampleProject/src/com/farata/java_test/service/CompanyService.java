package com.farata.java_test.service;

import java.lang.reflect.Field;
import java.util.List;

import clear.transaction.identity.PropertyRack;
import com.farata.java_test.data.DataEngine;
import com.farata.java_test.dto.CompanyDTO;
import com.farata.java_test.service.generated._CompanyService;

import flex.data.ChangeObject;


public class CompanyService extends _CompanyService {
	
	DataEngine dataEngine = DataEngine.getInstance();
	
	public List<CompanyDTO> fill() {
		List<CompanyDTO> companyList = dataEngine.getCompanyList();
		System.out.println("fill method has returned " + companyList.size() + " CompanyDTO records");
		return companyList;
	}
	
	// This method, with arbitrary name, illustrates how to insert new data
	//  marked as changeObject.isCreate() 
	
	public void fill_doCreate(ChangeObject changeObject) {

		CompanyDTO dto = (CompanyDTO) changeObject.getNewVersion();

		System.out.println("doCreate method adding new object:");
		System.out.println(dto);
		
		//Check "autoincrement" field  - id  -and,  when null, set it to max+1
		//Please note that Farata translator must be plugged in to AMF channel
		// in order to guarantee ActionsScript NaN will come as "null", otherwise
		// default BlazeDS/LCDS behavior would deliver it as 0:
		if ((dto.getId() == null) || (dto.getId() <= 0)) {
			Object oldId = dto.getId();
			dto.setId(dataEngine.getMaxCompanyId() + 1);	
			changeObject.addChangedPropertyName("id");
			
			PropertyRack.setEntity("com.farata.test.dto.CompanyDTO", "id", oldId, dto.getId());		
		}

		dataEngine.getCompanyList().add(dto);
	}

	// This method, with arbitrary name, illustrates how to update data marked 
	//  as changeObject.isUpdate() utilizing array of changedPropertyNames
	
	public void fill_doUpdate(ChangeObject changeObject) {
		System.out.println("doUpdate method executing");
		CompanyDTO newVersion = (CompanyDTO)changeObject.getNewVersion();
		CompanyDTO previousVersion = (CompanyDTO)changeObject.getPreviousVersion();
		
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
	
	public void fill_doDelete(ChangeObject changeObject) {

		System.out.print("doDelete method ");
		
		CompanyDTO dto = (CompanyDTO) changeObject.getPreviousVersion();
		CompanyDTO removed = dataEngine.removeCompany(dto);
		if (removed != null) {
			System.out.println("removed: " + removed);
		}
	}	
}