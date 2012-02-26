package com.farata.demo.entities;

import java.io.Serializable;
import javax.persistence.*;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSClassKind;

import java.sql.Timestamp;
import java.util.Set;


/**
 * The persistent class for the STOREORDER database table.
 * 
 */
@Entity
@JSClass(kind=JSClassKind.EXT_JS)
public class Storeorder implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int ordernumber;

	private boolean expressshipping;

	private boolean gift;

	private String giftnote;

	private Timestamp orderdate;

	private Timestamp shipdate;

	//bi-directional many-to-one association to Orderline
	@OneToMany(mappedBy="storeorderBean")
	private Set<Orderline> orderlines;

	//bi-directional many-to-one association to Status
    @ManyToOne
	@JoinColumn(name="STATUS")
	private Status statusBean;

    public Storeorder() {
    }

	public int getOrdernumber() {
		return this.ordernumber;
	}

	public void setOrdernumber(int ordernumber) {
		this.ordernumber = ordernumber;
	}

	public boolean getExpressshipping() {
		return this.expressshipping;
	}

	public void setExpressshipping(boolean expressshipping) {
		this.expressshipping = expressshipping;
	}

	public boolean getGift() {
		return this.gift;
	}

	public void setGift(boolean gift) {
		this.gift = gift;
	}

	public String getGiftnote() {
		return this.giftnote;
	}

	public void setGiftnote(String giftnote) {
		this.giftnote = giftnote;
	}

	public Timestamp getOrderdate() {
		return this.orderdate;
	}

	public void setOrderdate(Timestamp orderdate) {
		this.orderdate = orderdate;
	}

	public Timestamp getShipdate() {
		return this.shipdate;
	}

	public void setShipdate(Timestamp shipdate) {
		this.shipdate = shipdate;
	}

	public Set<Orderline> getOrderlines() {
		return this.orderlines;
	}

	public void setOrderlines(Set<Orderline> orderlines) {
		this.orderlines = orderlines;
	}
	
	public Status getStatusBean() {
		return this.statusBean;
	}

	public void setStatusBean(Status statusBean) {
		this.statusBean = statusBean;
	}
	
}