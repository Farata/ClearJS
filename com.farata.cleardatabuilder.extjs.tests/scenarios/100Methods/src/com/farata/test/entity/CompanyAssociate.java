package com.farata.test.entity;

import java.io.Serializable;
import javax.persistence.*;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSClassKind;
import com.farata.dto2extjs.annotations.JSIgnore;
import com.farata.dto2extjs.annotations.JSKeyColumn;
import com.farata.dto2extjs.annotations.JSManyToOne;


/**
 * The persistent class for the company_associate database table.
 * 
 */
@Entity
@Table(name="company_associate")
@JSClass(kind=JSClassKind.EXT_JS )
public class CompanyAssociate implements Serializable {
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
	private int id;

	private String associate;

	//bi-directional many-to-one association to Company
    @ManyToOne(fetch=FetchType.LAZY)
	private Company company;

    public CompanyAssociate() {
    }

    @JSKeyColumn
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAssociate() {
		return this.associate;
	}

	public void setAssociate(String associate) {
		this.associate = associate;
	}

	//@JSManyToOne(parent = "com.farata.test.entity.Company", property = "id")
	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
}