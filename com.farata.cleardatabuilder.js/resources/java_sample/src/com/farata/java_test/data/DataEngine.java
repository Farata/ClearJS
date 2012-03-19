package com.farata.java_test.data;import java.util.ArrayList;import java.util.Collections;import java.util.Comparator;import java.util.HashMap;import java.util.Iterator;import java.util.List;import java.util.Map;import java.util.NoSuchElementException;
import com.farata.java_test.dto.AssociateDTO;import com.farata.java_test.dto.CompanyDTO;
public class DataEngine {
	private DataEngine() {		initializeCompanies();		initializeCompanyAssociates();	}
	
	private static DataEngine _instance;
	public static DataEngine getInstance(){
		if (_instance==null) {
			_instance = new DataEngine();
		}
		return _instance;
	}
	/*
	 * companyList is rudimentary entity: only single thread is supposed to access it,
	 * it serves only for illustration of the ChangeObject mechanics
	 */
	private  ArrayList<CompanyDTO> companyList;

	@SuppressWarnings("serial")
	private  void initializeCompanies() {
		companyList = new ArrayList<CompanyDTO>() {
			public boolean add(CompanyDTO dto) {
				if ((dto==null) || (dto.getId()==null)) return false; 
				ArrayList<AssociateDTO> associateList = new ArrayList<AssociateDTO>();
				if (companyAssociateMap == null) {
					companyAssociateMap = new HashMap<Long, ArrayList<AssociateDTO>>();
				}
				companyAssociateMap.put(dto.getId(), associateList);				
				return super.add(dto);
			}
			public CompanyDTO remove(int index) {
				CompanyDTO dto = super.remove(index);
				if (dto.getId()!=null) {
					companyAssociateMap.remove(dto.getId());
				}
				return dto;
			}
		};
		CompanyDTO dto = new CompanyDTO();
		dto.setCompanyName("FarataSystems");
		dto.setId(new Long(1));
		companyList.add(dto);
		dto = new CompanyDTO();
		dto.setCompanyName("Adobe");
		dto.setId(new Long(2));
		companyList.add(dto);
	}
	
	public List<CompanyDTO> getCompanyList() {
		return companyList;
	}
	
	public CompanyDTO findCompany(CompanyDTO dto) {
		CompanyDTO companyDTO = null;
	
		for (int i=0; i< companyList.size(); i++) {
			CompanyDTO listDTO = companyList.get(i) ;
			if (listDTO.getId().compareTo(dto.getId())==0) {
				companyDTO = listDTO; 
				break;
			}	
		}
		return companyDTO;
	}
	
	public CompanyDTO removeCompany(CompanyDTO dto) {
		CompanyDTO removed = null;

		for (int i=0; i< companyList.size(); i++) {
			CompanyDTO listDTO = companyList.get(i) ;
			if (listDTO.getId().compareTo(dto.getId())==0) {
				removed = companyList.remove(i);
				break;
			}	
		}
		return removed;
	}
	public long getMaxCompanyId() {
		long maxId=0;

		try {
			CompanyDTO max = (CompanyDTO)Collections.max(companyList, new CompanyIdComparator());
			maxId = max.getId();
    	} catch (NoSuchElementException nsee) { 
    		// default to 0  
    	}
    	return maxId;
	}
	class CompanyIdComparator implements Comparator<CompanyDTO> {
		@Override
		public int compare(CompanyDTO o1, CompanyDTO o2) {
			return o1.getId().compareTo(o2.getId());
		}
	}
	/*
	 * companyAssociateMap is rudimentary support of one to many company-associate
	 * relationship, only single thread is supposed to access it,
	 * it serves only for illustration of the ChangeObject mechanics
	 */
	private  Map<Long, ArrayList<AssociateDTO>> companyAssociateMap;
	private void initializeCompanyAssociates() {
		//Farata Associates
		ArrayList<AssociateDTO> associateList = companyAssociateMap.get(new Long(1));
		Long companyId = new Long(1);
		AssociateDTO dto = new AssociateDTO();
		dto.setAssociate("Yakov Fain");
		dto.setCompanyId(companyId);
		dto.setId(new Long(1));
		associateList.add(dto);
			dto = new AssociateDTO();		dto.setAssociate("Anatole Tartakovsky");
		dto.setCompanyId(companyId);
		associateList.add(dto);
		dto.setId(new Long(2));		
		dto = new AssociateDTO();
		dto.setAssociate("Victor Rasputnis");
		dto.setCompanyId(companyId);
		dto.setId(new Long(3));
		associateList.add(dto);
		
		//Flex Team Associates
		associateList = companyAssociateMap.get(new Long(2));
		companyId = new Long(2);
		
		dto = new AssociateDTO();
		dto.setAssociate("Ely Greenfield");
		dto.setCompanyId(companyId);
		dto.setId(new Long(4));
		associateList.add(dto);
		dto = new AssociateDTO();
		dto.setAssociate("Alex Harui");
		dto.setCompanyId(companyId);
		dto.setId(new Long(5));
		associateList.add(dto);
		dto = new AssociateDTO();
		dto.setAssociate("Matt Chotin");
		dto.setCompanyId(companyId);
		dto.setId(new Long(6));	
		associateList.add(dto);
	}
	public List<AssociateDTO> getAssociateList(Long companyId) {
		if (companyId==null) throw new RuntimeException("companyId can not be null");
		
		List<AssociateDTO> associateList = companyAssociateMap.get(companyId);
		if (associateList==null) associateList = new ArrayList<AssociateDTO>();
		return associateList;		
	}
	
	public AssociateDTO findAssociate(AssociateDTO dto) {
		AssociateDTO targetDTO=null;		Long companyId = dto.getCompanyId();
		if (companyId == null) throw new RuntimeException("companyId can not be null");
		List<AssociateDTO> associateList = companyAssociateMap.get(companyId);
		if (associateList != null) {	        for (AssociateDTO listDTO: associateList) {	        	if (listDTO.getId().compareTo(dto.getId())==0) {	        		targetDTO = listDTO; 
	        		break;
	        	}	
	        }
		}
		return targetDTO;
	}
	
	public AssociateDTO removeAssociate(AssociateDTO dto) {
		AssociateDTO removedDTO=null;		Long companyId = dto.getCompanyId();
		if (companyId == null) throw new RuntimeException("companyId can not be null");
		List<AssociateDTO> associateList = companyAssociateMap.get(companyId);		if (associateList!=null) {			for (AssociateDTO listDTO: associateList) {								if (listDTO.getId().compareTo(dto.getId())==0) {					associateList.remove(listDTO);					removedDTO = listDTO; 					break;				}				}		}		return removedDTO;	}
	@SuppressWarnings("unchecked")	public long getMaxAssociateId () {		long maxId=0;	    Iterator it = companyAssociateMap.entrySet().iterator();
	    while (it.hasNext()) {	        Map.Entry pairs = (Map.Entry)it.next();	        ArrayList<AssociateDTO> partialList = (ArrayList<AssociateDTO>) pairs.getValue();	        try {		        AssociateDTO maxDTO = (AssociateDTO)Collections.max(partialList, new AssociateIdComparator());		        if (maxDTO.getId() > maxId) maxId = maxDTO.getId();	    	} catch (NoSuchElementException nsee) { 	    		// default to 0  	    	}	    }	    return maxId;	}
	class AssociateIdComparator implements Comparator<AssociateDTO> {
		@Override
		public int compare(AssociateDTO o1, AssociateDTO o2) {
			return o1.getId().compareTo(o2.getId());
		}
	}
}