package com.farata.demo.entity;

import java.io.Serializable;
import javax.persistence.*;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSClassKind;


/**
 * The persistent class for the company_associate database table.
 * 
 */
@Entity
@Table(name="company_associate")
@JSClass(kind=JSClassKind.EXT_JS)
public class CompanyAssociate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	private String associate;

	//bi-directional many-to-one association to Company
    @ManyToOne
	private Company company;

    public CompanyAssociate() {
    }

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAssociate() {
		return this.associate;
	}

	public void setAssociate(String associate) {
		this.associate = associate;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
}