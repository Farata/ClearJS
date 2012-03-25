package com.farata.demo.entity;

import java.io.Serializable;
import javax.persistence.*;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSClassKind;

import java.util.Set;


/**
 * The persistent class for the company database table.
 * 
 */
@Entity
@Table(name="company")
@JSClass(kind=JSClassKind.EXT_JS)
public class Company implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	private String company;

	private Integer pk;

	//bi-directional many-to-one association to CompanyAssociate
	@OneToMany(mappedBy="company")
	private Set<CompanyAssociate> companyAssociates;

    public Company() {
    }

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

	public Integer getPk() {
		return this.pk;
	}

	public void setPk(Integer pk) {
		this.pk = pk;
	}

	public Set<CompanyAssociate> getCompanyAssociates() {
		return this.companyAssociates;
	}

	public void setCompanyAssociates(Set<CompanyAssociate> companyAssociates) {
		this.companyAssociates = companyAssociates;
	}
	
}