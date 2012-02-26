package com.farata.java_test.service;

import java.lang.reflect.Field;
import java.util.List;

import clear.transaction.identity.PropertyRack;
import com.farata.java_test.data.DataEngine;
import com.farata.java_test.dto.AssociateDTO;
import com.farata.java_test.service.generated._AssociateService;

import flex.data.ChangeObject;

public class AssociateService extends _AssociateService {
	
	DataEngine dataEngine = DataEngine.getInstance();

	public List<AssociateDTO> fill(Long companyId) {
		List<AssociateDTO> associateList = dataEngine
				.getAssociateList(companyId);

		System.out.println("fill method has returned " + associateList.size()
				+ " AssociateDTO records");
		return associateList;
	}
	
	public void fill_doCreate(ChangeObject changeObject) {
		// This sample code illustrates how to extract data from the newly
		// created object.
		// Please replace it with your own code.
		
		AssociateDTO dto = (AssociateDTO) changeObject.getNewVersion();

		System.out.println("doCreate method adding new object:");
		System.out.println(dto);
		
		Long parentCompanyId = (Long)PropertyRack.getEntity("com.farata.test.dto.CompanyDTO", "id", dto.getCompanyId());
		dto.setCompanyId(parentCompanyId);

		
		Long companyId = dto.getCompanyId();
		List<AssociateDTO> associateList = dataEngine.getAssociateList(companyId);
		
		//Check "autoincrement" field  - id  - and,  when null, set it to max+1
		//Please note that Farata translator must be plugged in to AMF channel
		// in order to guarantee ActionsScript NaN will come as "null", otherwise
		// default BlazeDS/LCDS behavior would deliver it as 0:
		
		if ((dto.getId() == null) || (dto.getId() <= 0)) {
			dto.setId(dataEngine.getMaxAssociateId() + 1);	
			changeObject.addChangedPropertyName("id");
		}
		
		associateList.add(dto);
	}

	public void fill_doUpdate(ChangeObject changeObject) {
		// This sample code illustrates how to extract data from the updated
		// object.
		// Please replace it with your own code.

		System.out.println("doUpdate method executing");
		AssociateDTO newVersion = (AssociateDTO)changeObject.getNewVersion();
		AssociateDTO previousVersion = (AssociateDTO)changeObject.getPreviousVersion();
		
		AssociateDTO originalDTO = dataEngine.findAssociate(previousVersion);
		if (originalDTO != null) {

			String[] changedPropertyNames = changeObject.getChangedPropertyNames();
			Class<?extends AssociateDTO> clazz = AssociateDTO.class;
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

	public void fill_doDelete(ChangeObject changeObject) {
		// This sample code illustrates how to extract data from the deleted
		// object.
		// Please replace it with your own code.

		System.out.print("doDelete method ");
		
		AssociateDTO dto = (AssociateDTO) changeObject.getPreviousVersion();
		AssociateDTO removed = dataEngine.removeAssociate(dto);
		if (removed != null) {
			System.out.println("removed: " + removed);
		}
	}
}
