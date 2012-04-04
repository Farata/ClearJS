package com.farata.example.service;

import java.util.List;
import java.util.Map;
import clear.data.ChangeObject;
import clear.transaction.identity.IdentityRack;
import com.farata.example.dto.AssociateDTO;
import com.farata.example.dto.CompanyDTO;
import com.farata.example.mapper.AssociateMapper;
import com.farata.example.service.generated._AssociateService;

import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service("com.farata.example.service.AssociateService")
public class AssociateService extends _AssociateService {
	@Autowired
	private AssociateMapper associateMapper;
	@Override
	public List<AssociateDTO> getAssociates(Integer companyId) {
		List<AssociateDTO> associateList = associateMapper.getAssociates(companyId);

		System.out.println("getAssociates method has returned " + associateList.size()
				+ " AssociateDTO records");
		return associateList;
	}
	
	@Override
	public void getAssociates_doCreate(ChangeObject changeObject) {				
		AssociateDTO dto =  (AssociateDTO)deserializeObject((Map<String, String>)changeObject.getNewVersion(), AssociateDTO.class);			
			
		if (dto.companyId <=0) {
			Integer parentCompanyId = (Integer)IdentityRack.getIdentity(CompanyDTO.class.getName(), "id", dto.id);
			dto.companyId = parentCompanyId;
		}
		
		if ((dto.id == null) || (dto.id <= 0)) {
			associateMapper.create(dto);
			changeObject.addChangedPropertyName("id");
		}

		changeObject.setNewVersion(dto);
	}

	@Override
	public void getAssociates_doUpdate(ChangeObject changeObject) {
		AssociateDTO newVersion =  (AssociateDTO)deserializeObject((Map<String, String>)changeObject.getNewVersion(), AssociateDTO.class);
		if (newVersion != null) {
			associateMapper.update(newVersion);
		}
	}

	@Override
	public void getAssociates_doDelete(ChangeObject changeObject) {
		AssociateDTO dto =  (AssociateDTO)deserializeObject((Map<String, String>)changeObject.getPreviousVersion(), AssociateDTO.class);			
		if (dto != null) {
			associateMapper.delete(dto);
		}
	}
}
