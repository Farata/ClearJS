package com.engage.entitlement.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.engage.utils.DTOUtil;

/**
 * The persistent class for the CORE_USER_ROLE_MAP database table.
 * 
 */
@Entity
@Table(name="CORE_USER_ROLE_MAP")
public class UserRoleEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CORE_USER_ROLE_GENERATOR", sequenceName="LEGEND_ID_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CORE_USER_ROLE_GENERATOR")
	@Column(name="USR_ROLE_COMM_ID")
	private Long id;

	@Column(name="ROLE_ID")
	private Long roleId;

	@Column(name="COMMUNITY_ID")
	private Long communityId;

	@Column(name="USR_ID")
	private Long usrId;

	@Column(name="CREATED_BY")
	private Long createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATE")
	private Date createdDate;

    public UserRoleEntity() {
    }

	public String toString() {
		return DTOUtil.toString(this);
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getCommunityId() {
		return communityId;
	}

	public void setCommunityId(Long communityId) {
		this.communityId = communityId;
	}

	public Long getUsrId() {
		return usrId;
	}

	public void setUsrId(Long usrId) {
		this.usrId = usrId;
	}

}