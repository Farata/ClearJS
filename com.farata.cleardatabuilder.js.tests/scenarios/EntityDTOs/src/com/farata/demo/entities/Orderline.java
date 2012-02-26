package com.farata.demo.entities;

import java.io.Serializable;
import javax.persistence.*;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSClassKind;


/**
 * The persistent class for the ORDERLINE database table.
 * 
 */
@Entity
@JSClass(kind=JSClassKind.EXT_JS)
public class Orderline implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int orderlineid;

	private int quantity;

	//bi-directional many-to-one association to Product
    @ManyToOne
	@JoinColumn(name="PRODUCT")
	private Product productBean;

	//bi-directional many-to-one association to Storeorder
    @ManyToOne
	@JoinColumn(name="STOREORDER")
	private Storeorder storeorderBean;

    public Orderline() {
    }

	public int getOrderlineid() {
		return this.orderlineid;
	}

	public void setOrderlineid(int orderlineid) {
		this.orderlineid = orderlineid;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Product getProductBean() {
		return this.productBean;
	}

	public void setProductBean(Product productBean) {
		this.productBean = productBean;
	}
	
	public Storeorder getStoreorderBean() {
		return this.storeorderBean;
	}

	public void setStoreorderBean(Storeorder storeorderBean) {
		this.storeorderBean = storeorderBean;
	}
	
}