package com.farata.example.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import clear.transaction.identity.IdentityRack;

import com.farata.example.data.DataEngine;
import com.farata.example.dto.AssociateDTO;
import com.farata.example.service.generated._AssociateService;

import clear.data.ChangeObject;

public class AssociateService extends _AssociateService {
	
	DataEngine dataEngine = DataEngine.getInstance();

	@Override
	public List<AssociateDTO> getAssociates(Integer companyId) {
		List<AssociateDTO> associateList = dataEngine
				.getAssociateList(companyId);

		System.out.println("getAssociates method has returned " + associateList.size()
				+ " AssociateDTO records");
		return associateList;
	}
	
	@Override
	public void getAssociates_doCreate(ChangeObject changeObject) {		
		
		// This sample code illustrates how to extract data from the newly
		// created object.
		// Please replace it with your own code.
		
		System.out.println("doCreate method");

		AssociateDTO dto =  (AssociateDTO)deserializeObject((Map<String, String>)changeObject.getNewVersion(), AssociateDTO.class);			
		
		if (dto.companyId <=0) {
			Integer parentCompanyId = (Integer)IdentityRack.getIdentity("com.farata.example.dto.CompanyDTO", "id", dto.id);
			dto.companyId = parentCompanyId;
		}
		
		Integer companyId = dto.companyId;
		List<AssociateDTO> associateList = dataEngine.getAssociateList(companyId);
		
		if ((dto.id == null) || (dto.id <= 0)) {
			dto.id = dataEngine.getMaxCompanyAssociateId() + 1;	
			changeObject.addChangedPropertyName("id");
		}
		
		associateList.add(dto);
		changeObject.setNewVersion(dto);

	}

	@Override
	public void getAssociates_doUpdate(ChangeObject changeObject) {
		// This sample code illustrates how to extract data from the updated
		// object.
		// Please replace it with your own code.

		System.out.println("doUpdate method");
		AssociateDTO newVersion =  (AssociateDTO)deserializeObject((Map<String, String>)changeObject.getNewVersion(), AssociateDTO.class);
		AssociateDTO previousVersion =  (AssociateDTO)deserializeObject((Map<String, String>)changeObject.getPreviousVersion(), AssociateDTO.class);

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

	@Override
	public void getAssociates_doDelete(ChangeObject changeObject) {
		// This sample code illustrates how to extract data from the deleted
		// object.
		// Please replace it with your own code.

		System.out.print("doDelete method ");
		AssociateDTO dto =  (AssociateDTO)deserializeObject((Map<String, String>)changeObject.getPreviousVersion(), AssociateDTO.class);			
		AssociateDTO removed = dataEngine.removeAssociate(dto);
		if (removed != null) {
			System.out.println("removed: " + removed);
		}
	}
}
