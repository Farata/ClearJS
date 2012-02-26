package com.farata.test.entity;

import java.io.Serializable;
import javax.persistence.*;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSClassKind;
import com.farata.dto2extjs.annotations.JSIgnore;
import com.farata.dto2extjs.annotations.JSKeyColumn;
import com.farata.dto2extjs.annotations.JSOneToMany;

import java.util.List;


/**
 * The persistent class for the company database table.
 * 
 */
@Entity
@Table(name="company")
@JSClass(kind=JSClassKind.EXT_JS) 
public class Company implements Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	private String uid;
	@JSIgnore
	public String getUid() {
		if (uid == null) {
			uid = "" + id;
		}
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	private String company;

	//bi-directional many-to-one association to CompanyAssociate
	@OneToMany(mappedBy="company", fetch=FetchType.LAZY)
	private List<CompanyAssociate> companyAssociates;
	
    public Company() {
    }

    @JSKeyColumn
    public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCompany() {
		return this.company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	@JSOneToMany(fillArguments="id")
	public List<CompanyAssociate> getCompanyAssociates() {
		return this.companyAssociates;
	}

	public void setCompanyAssociates(List<CompanyAssociate> companyAssociates) {
		this.companyAssociates = companyAssociates;
	}
	
}