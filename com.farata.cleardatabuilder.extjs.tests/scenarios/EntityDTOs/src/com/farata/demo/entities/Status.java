package com.farata.demo.entities;

import java.io.Serializable;
import javax.persistence.*;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSClassKind;

import java.util.Set;


/**
 * The persistent class for the STATUS database table.
 * 
 */
@Entity
@JSClass(kind=JSClassKind.EXT_JS)
public class Status implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int statusid;

	private String statusname;

	//bi-directional many-to-one association to Storeorder
	@OneToMany(mappedBy="statusBean")
	private Set<Storeorder> storeorders;

    public Status() {
    }

	public int getStatusid() {
		return this.statusid;
	}

	public void setStatusid(int statusid) {
		this.statusid = statusid;
	}

	public String getStatusname() {
		return this.statusname;
	}

	public void setStatusname(String statusname) {
		this.statusname = statusname;
	}

	public Set<Storeorder> getStoreorders() {
		return this.storeorders;
	}

	public void setStoreorders(Set<Storeorder> storeorders) {
		this.storeorders = storeorders;
	}
	
}