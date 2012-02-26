package com.engage.entitlement.entity;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.SQLDelete;

import com.engage.utils.DTOUtil;

import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the CORE_ROLE_MST database table.
 * 
 */
@Entity
@Table(name="CORE_ROLE_MST")
@FilterDef(name = "isActive")
@Filter(name = "isActive", condition = "IS_ACTIVE = 'Y'")
@SQLDelete(sql = "UPDATE CORE_ROLE_MST SET IS_ACTIVE='N' WHERE ROLE_ID=?")
public class RoleEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CORE_ROLE_MST_ROLEID_GENERATOR", sequenceName="ROLE_ID_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CORE_ROLE_MST_ROLEID_GENERATOR")
	@Column(name="ROLE_ID")
	private Long roleId;

	@Column(name="CREATED_BY")
	private Long createdBy;

	@Column(name="CREATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name="IS_ACTIVE")
	private String isActive;

	@Column(name="ROLE_DESC")
	private String roleDesc;

	@Column(name="ROLE_NAME")
	private String roleName;

	@Column(name="UPDATED_BY")
	private Long updatedBy;

	@Column(name="UPDATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

    public RoleEntity() {
    }

	public String toString() {
		return DTOUtil.toString(this);
	}

	public Long getRoleId() {
		return this.roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getIsActive() {
		return this.isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getRoleDesc() {
		return this.roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Long getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

}