package com.farata.example.service;

import java.util.List;
import java.util.Map;

import clear.transaction.identity.IdentityRack;

import com.farata.example.data.DataEngine;
import com.farata.example.dto.AssociateDTO;
import com.farata.example.dto.CompanyDTO;
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
		System.out.println("doCreate method");

		AssociateDTO dto =  (AssociateDTO)deserializeObject((Map<String, String>)changeObject.getNewVersion(), AssociateDTO.class);			
		
		if (dto.getCompanyId() <=0) {
			Integer parentCompanyId = (Integer)IdentityRack.getIdentity(CompanyDTO.class.getName(), "id", dto.getCompanyId());
			dto.setCompanyId(parentCompanyId);
		}
		
		
		if ((dto.getId() == null) || (dto.getId() <= 0)) {
			dto.setId(dataEngine.getMaxCompanyAssociateId() + 1);	
			changeObject.addChangedPropertyName("id");
		}
		
		dataEngine.addAssociate(dto);
		changeObject.setNewVersion(dto);

	}

	@Override
	public void getAssociates_doUpdate(ChangeObject changeObject) {
		System.out.println("doUpdate method");
		AssociateDTO newVersion =  (AssociateDTO)deserializeObject((Map<String, String>)changeObject.getNewVersion(), AssociateDTO.class);
		AssociateDTO previousVersion =  (AssociateDTO)deserializeObject((Map<String, String>)changeObject.getPreviousVersion(), AssociateDTO.class);

		dataEngine.updateAssociate(previousVersion, newVersion, changeObject.getChangedPropertyNames() );
		
	}

	@Override
	public void getAssociates_doDelete(ChangeObject changeObject) {
		System.out.print("doDelete method ");
		AssociateDTO dto =  (AssociateDTO)deserializeObject((Map<String, String>)changeObject.getPreviousVersion(), AssociateDTO.class);			
		AssociateDTO removed = dataEngine.removeAssociate(dto);
		if (removed != null) {
			System.out.println("removed: " + removed);
		}
	}
}
