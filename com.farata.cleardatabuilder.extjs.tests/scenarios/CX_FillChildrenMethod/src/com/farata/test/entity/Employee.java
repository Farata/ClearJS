package com.farata.test.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSClassKind;

@Entity
@JSClass(kind=JSClassKind.EXT_JS)
public class Employee {
	@Id
	private Integer id;
	private String name;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
