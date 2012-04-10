package com.farata.example.service;

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

	@Override
	public void getCompanies_doCreate(ChangeObject changeObject) {
		System.out.println("doCreate method");
		CompanyDTO dto =  (CompanyDTO)deserializeObject((Map<String, String>)changeObject.getNewVersion(), CompanyDTO.class);					

		
		if ((dto.getId() == null) || (dto.getId() <= 0)) {
			Object oldId = dto.getId();
			dto.setId(dataEngine.getMaxCompanyId() + 1);	
			changeObject.addChangedPropertyName("id");
			
			IdentityRack.setIdentity(CompanyDTO.class.getName(), "id", oldId, dto.getId());		
		}

		dataEngine.addCompany(dto);
		changeObject.setNewVersion(dto);
	}

	@Override
	public void getCompanies_doUpdate(ChangeObject changeObject) {
		
		System.out.println("doUpdate method executing");
		CompanyDTO newVersion =  (CompanyDTO)deserializeObject((Map<String, String>)changeObject.getNewVersion(), CompanyDTO.class);
		CompanyDTO previousVersion =  (CompanyDTO)deserializeObject((Map<String, String>)changeObject.getPreviousVersion(), CompanyDTO.class);
		
		dataEngine.updateCompany(previousVersion, newVersion, changeObject.getChangedPropertyNames() );
	}

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