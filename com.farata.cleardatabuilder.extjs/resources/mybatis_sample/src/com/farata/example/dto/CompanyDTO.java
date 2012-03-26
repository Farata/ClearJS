package com.farata.example.dto;		
import java.io.Serializable;
import java.util.*;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSOneToMany;

@JSClass
public class CompanyDTO  implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	
	public java.lang.Integer id;
	public java.lang.String companyName;
	@JSOneToMany(foreignKey="companyId", getter="getAssociates")
	public List<AssociateDTO> associates;
						
}