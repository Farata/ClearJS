package com.farata.demo.entities;

import java.io.Serializable;
import javax.persistence.*;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSClassKind;

import java.util.Set;


/**
 * The persistent class for the PRODUCT database table.
 * 
 */
@Entity
@JSClass(kind=JSClassKind.EXT_JS)
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int productid;

	private String description;

	private Double price;

	private String productname;

	//bi-directional many-to-one association to Orderline
	@OneToMany(mappedBy="productBean")
	private Set<Orderline> orderlines;

    public Product() {
    }

	public int getProductid() {
		return this.productid;
	}

	public void setProductid(int productid) {
		this.productid = productid;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return this.price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getProductname() {
		return this.productname;
	}

	public void setProductname(String productname) {
		this.productname = productname;
	}

	public Set<Orderline> getOrderlines() {
		return this.orderlines;
	}

	public void setOrderlines(Set<Orderline> orderlines) {
		this.orderlines = orderlines;
	}
	
}