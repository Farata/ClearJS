package com.farata.example.service;

import clear.data.ChangeObject;
import clear.transaction.identity.IdentityRack;
import com.farata.example.dto.CompanyDTO;
import com.farata.example.mapper.CompanyMapper;
import com.farata.example.service.generated._CompanyService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service("com.farata.example.service.CompanyService")
public class CompanyService extends _CompanyService {
	
	@Autowired
	private CompanyMapper companyMapper;	
	
	@Override
	public List<CompanyDTO> getCompanies() {
		List<CompanyDTO> companyList = companyMapper.getCompanies();
		System.out.println("getCompanies method has returned " + companyList.size() + " CompanyDTO records");
		return companyList;
	}
	
	@Override
	public void getCompanies_doCreate(ChangeObject changeObject) {
		System.out.println("doCreate method");
		CompanyDTO dto =  (CompanyDTO)deserializeObject((Map<String, String>)changeObject.getNewVersion(), CompanyDTO.class);							
		Object oldId = dto.id;
		companyMapper.create(dto);
		if (oldId != dto.id) {
			changeObject.addChangedPropertyName("id");
			IdentityRack.setIdentity(CompanyDTO.class.getName(), "id", oldId, dto.id);		
		}

		changeObject.setNewVersion(dto);
	}

	@Override
	public void getCompanies_doUpdate(ChangeObject changeObject) {	
		System.out.println("doUpdate method executing");
		CompanyDTO newVersion =  (CompanyDTO)deserializeObject((Map<String, String>)changeObject.getNewVersion(), CompanyDTO.class);		
		if (newVersion != null) {
			companyMapper.update(newVersion);
		}		
	}

	@Override
	public void getCompanies_doDelete(ChangeObject changeObject) {
		System.out.print("doDelete method ");		
		CompanyDTO dto =  (CompanyDTO)deserializeObject((Map<String, String>)changeObject.getPreviousVersion(), CompanyDTO.class);
		if (dto != null) {
			companyMapper.delete(dto);
			System.out.println("removed: " + dto);
		}
	}	
}