package com.engage.entitlement.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.SQLDelete;

import com.engage.utils.DTOUtil;


/**
 * The persistent class for the CORE_COMMUNITY_MST database table.
 * 
 */
@Entity
@Table(name="CORE_COMMUNITY_MST")
@FilterDef(name = "isActive")
@Filter(name = "isActive", condition = "IS_ACTIVE = 'Y'")
@SQLDelete(sql = "UPDATE CORE_COMMUNITY_MST SET IS_ACTIVE='N' WHERE COMMUNITY_ID=?")
public class CommunityEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CORE_COMMUNITY_MST_COMMUNITYID_GENERATOR", sequenceName="COMMUNITY_ID_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CORE_COMMUNITY_MST_COMMUNITYID_GENERATOR")
	@Column(name="COMMUNITY_ID")
	private Long communityId;

	@Column(name="COMMUNITY_CODE")
	private String communityCode;

	@Column(name="COMMUNITY_LOCATION")
	private String communityLocation;

	@Column(name="COMMUNITY_NAME")
	private String communityName;

	@Column(name="CREATED_BY")
	private Long createdBy;

	@Column(name="CREATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name="IS_ACTIVE")
	private String isActive;

	@Column(name="SPONSOR_BANNER_FILE_URI")
	private String sponsorBannerFileUri;

	@Column(name="SPONSOR_NAME")
	private String sponsorName;

	@Column(name="UPDATED_BY")
	private Long updatedBy;

	@Column(name="UPDATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

	@Column(name="SPONSOR_ADVERTISEMENT_URI")
	private String sponsorAdvertisementUri;
	
	@Column(name="COMMUNITY_URI")
	private String communityUri;

	@Column(name="PROFILE_ID")
	private Long profileId;

	public CommunityEntity() {
    }

	public String toString() {
		return DTOUtil.toString(this);
	}

	public Long getCommunityId() {
		return this.communityId;
	}

	public void setCommunityId(Long communityId) {
		this.communityId = communityId;
	}

	public String getCommunityCode() {
		return this.communityCode;
	}

	public void setCommunityCode(String communityCode) {
		this.communityCode = communityCode;
	}

	public String getCommunityLocation() {
		return this.communityLocation;
	}

	public void setCommunityLocation(String communityLocation) {
		this.communityLocation = communityLocation;
	}

	public String getCommunityName() {
		return this.communityName;
	}

	public void setCommunityName(String communityName) {
		this.communityName = communityName;
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

	public String getSponsorBannerFileUri() {
		return this.sponsorBannerFileUri;
	}

	public void setSponsorBannerFileUri(String sponsorBannerFileUri) {
		this.sponsorBannerFileUri = sponsorBannerFileUri;
	}

	public String getSponsorName() {
		return this.sponsorName;
	}

	public void setSponsorName(String sponsorName) {
		this.sponsorName = sponsorName;
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

	public String getSponsorAdvertisementUri() {
		return sponsorAdvertisementUri;
	}

	public void setSponsorAdvertisementUri(String sponsorAdvertisementUri) {
		this.sponsorAdvertisementUri = sponsorAdvertisementUri;
	}

	public String getCommunityUri() {
		return communityUri;
	}

	public void setCommunityUri(String communityUri) {
		this.communityUri = communityUri;
	}

	public Long getProfileId() {
		return profileId;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}

}