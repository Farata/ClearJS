package com.engage.entitlement.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.engage.common.EngageConstants;
import com.engage.utils.DTOUtil;
import com.engage.utils.EntityUtil;


/**
 * The persistent class for the CORE_USER_DTL database table.
 * 
 */
@Entity
@Table(name="CORE_USER_DTL")
public class UserEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final Long SYSTEM_USER_ID = 1L;
	
	@Id
	@SequenceGenerator(name="CORE_USER_MST_ROLEID_GENERATOR", sequenceName="LEGEND_ID_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CORE_USER_MST_ROLEID_GENERATOR")
	@Column(name="USR_ID")
	private Long usrId;

	@Column(name="ADDRESS_LINE1")
	private String addressLine1;

	@Column(name="ADDRESS_LINE2")
	private String addressLine2;

	private String city;

	@Column(name="CONTACT_NO")
	private String contactNo;

	@ManyToOne
	@JoinColumn(name="CREATED_BY")
	private UserEntity createdBy;

	@Column(name="CREATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name="EMAIL_FORMAT_FLAG")
	private String emailFormatFlag;

	@Column(name="EMAIL_ID")
	private String emailId;

	@Column(name="FIRST_NAME")
	private String firstName;

	@Column(name="IS_CONTACTNO_DISPLAY")
	private String isContactnoDisplay;

	@Column(name="IS_EMAIL_DISPLAY")
	private String isEmailDisplay;

	@Column(name="IS_EMAIL_SEND_NOTIFY")
	private String isEmailSendNotify;

	@Column(name="IS_KEEPME_LOGGED_IN")
	private String isKeepmeLoggedIn;

	@Column(name="IS_NON_BILLING")
	private String isNonBilling;

	@Column(name="IS_SPONSOR")
	private String isSponsor;

	@Column(name="IS_TEMP_PWD_FLAG")
	private Long isTempPwdFlag;

	@Column(name="IS_UNSUBSCRIBED_BYUSR")
	private String isUnsubscribedByusr;

	@Column(name="ISO_COUNTRY_CODE")
	private String isoCountryCode;

	@Column(name="LAST_NAME")
	private String lastName;

	@Column(name="LAST_VIEWED_TREE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastViewedTreeDate;

	@Column(name="LAST_VIEWED_TREE_ID")
	private Long lastViewedTreeId;

	@Column(name="LOGIN_ID")
	private String loginId;

	@Column(name="PHOTO_FILE_URI")
	private String photoFileUri;

	@Column(name="PRIMARY_COMMUNITY_ID")
	private Long primaryCommunityId;

	private String pwd;

	@Column(name="PWD_EXP_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date pwdExpDate;

	@Column(name="SELF_DESC")
	private String selfDesc;

	private String state;

	private String title;

	@Column(name="TRIAL_END_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date trialEndDate;

	@Column(name="UPDATED_BY")
	private Long updatedBy;

	@Column(name="UPDATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

	@Column(name="USER_STATUS")
	private String userStatus;

	@Column(name="UNSUBSCRIBED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date unsubscribedDate;

	@Column(name="TERMS_ACCEPTED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date termsAcceptedDate;

	@Column(name="UNSUBSCRIBE_REASON")
	private String unsubscribedReason;

	@Column(name="UNSUBSCRIBE_CONFIRMATION_NO")
	private String unsubscribedConfirmationNo;

	@Column(name="POSTAL_CODE")
	private String postalCode;

	public UserEntity() {
    	firstName = " ";
    	lastName = " ";
    	emailId = " ";
    	isSponsor = "N";
    	addressLine1 = " ";
    	isoCountryCode = " ";
    	isContactnoDisplay = "N";
    	isEmailDisplay = "N";
    	isEmailSendNotify = "N";
    	isKeepmeLoggedIn = "N";
    	isNonBilling = "Y";
    	isTempPwdFlag = 1L;
    	isUnsubscribedByusr = "N";
    	primaryCommunityId = 1L;
    	lastViewedTreeId = 1L;
    	createdBy = null;
    	updatedBy =1L;
    	createdDate = new Date();
    	updatedDate = new Date();
    	pwdExpDate = new Date();
    	emailFormatFlag = "P";
    	userStatus = "I";

    }

	public String toString() {
		return DTOUtil.toString(this);
	}
	
	public Long getUsrId() {
		return this.usrId;
	}

	public void setUsrId(Long usrId) {
		this.usrId = usrId;
	}

	public String getAddressLine1() {
		return this.addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return this.addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getContactNo() {
		return this.contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public UserEntity getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(UserEntity createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getEmailFormatFlag() {
		return this.emailFormatFlag;
	}

	public void setEmailFormatFlag(String emailFormatFlag) {
		this.emailFormatFlag = emailFormatFlag;
	}

	public String getEmailId() {
		return this.emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getIsContactnoDisplay() {
		return this.isContactnoDisplay;
	}

	public void setIsContactnoDisplay(String isContactnoDisplay) {
		this.isContactnoDisplay = isContactnoDisplay;
	}

	public String getIsEmailDisplay() {
		return this.isEmailDisplay;
	}

	public void setIsEmailDisplay(String isEmailDisplay) {
		this.isEmailDisplay = isEmailDisplay;
	}

	public String getIsEmailSendNotify() {
		return this.isEmailSendNotify;
	}

	public void setIsEmailSendNotify(String isEmailSendNotify) {
		this.isEmailSendNotify = isEmailSendNotify;
	}

	public String getIsKeepmeLoggedIn() {
		return this.isKeepmeLoggedIn;
	}

	public void setIsKeepmeLoggedIn(String isKeepmeLoggedIn) {
		this.isKeepmeLoggedIn = isKeepmeLoggedIn;
	}

	public String getIsNonBilling() {
		return this.isNonBilling;
	}

	public void setIsNonBilling(String isNonBilling) {
		this.isNonBilling = isNonBilling;
	}

	public String getIsSponsor() {
		return this.isSponsor;
	}

	public void setIsSponsor(String isSponsor) {
		this.isSponsor = isSponsor;
	}

	public Long getIsTempPwdFlag() {
		return this.isTempPwdFlag;
	}

	public void setIsTempPwdFlag(Long isTempPwdFlag) {
		this.isTempPwdFlag = isTempPwdFlag;
	}

	public String getIsUnsubscribedByusr() {
		return this.isUnsubscribedByusr;
	}

	public void setIsUnsubscribedByusr(String isUnsubscribedByusr) {
		this.isUnsubscribedByusr = isUnsubscribedByusr;
	}

	public String getIsoCountryCode() {
		return this.isoCountryCode;
	}

	public void setIsoCountryCode(String isoCountryCode) {
		this.isoCountryCode = isoCountryCode;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getLastViewedTreeDate() {
		return this.lastViewedTreeDate;
	}

	public void setLastViewedTreeDate(Date lastViewedTreeDate) {
		this.lastViewedTreeDate = lastViewedTreeDate;
	}

	public Long getLastViewedTreeId() {
		return this.lastViewedTreeId;
	}

	public void setLastViewedTreeId(Long lastViewedTreeId) {
		this.lastViewedTreeId = lastViewedTreeId;
	}

	public String getLoginId() {
		return this.loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPhotoFileUri() {
		return this.photoFileUri;
	}

	public void setPhotoFileUri(String photoFileUri) {
		this.photoFileUri = photoFileUri;
	}

	public Long getPrimaryCommunityId() {
		return this.primaryCommunityId;
	}

	public void setPrimaryCommunityId(Long primaryCommunityId) {
		this.primaryCommunityId = primaryCommunityId;
	}

	public String getPwd() {
		return this.pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public Date getPwdExpDate() {
		return this.pwdExpDate;
	}

	public void setPwdExpDate(Date pwdExpDate) {
		this.pwdExpDate = pwdExpDate;
	}

	public String getSelfDesc() {
		return this.selfDesc;
	}

	public void setSelfDesc(String selfDesc) {
		this.selfDesc = selfDesc;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getTrialEndDate() {
		return this.trialEndDate;
	}

	public void setTrialEndDate(Date trialEndDate) {
		this.trialEndDate = trialEndDate;
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

	public String getUserStatus() {
		return this.userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public Date getUnsubscribedDate() {
		return unsubscribedDate;
	}

	public void setUnsubscribedDate(Date unsubscribedDate) {
		this.unsubscribedDate = unsubscribedDate;
	}

	public String getUnsubscribedReason() {
		return unsubscribedReason;
	}

	public void setUnsubscribedReason(String unsubscribedReason) {
		this.unsubscribedReason = unsubscribedReason;
	}

	public String getUnsubscribedConfirmationNo() {
		return unsubscribedConfirmationNo;
	}

	public void setUnsubscribedConfirmationNo(String unsubscribedConfirmationNo) {
		this.unsubscribedConfirmationNo = unsubscribedConfirmationNo;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	// Transient properties

	@Transient
	public boolean isNonBilling() {
		return EntityUtil.getBooleanValue(this.isNonBilling);
	}

	public void setNonBilling(boolean value) {
		this.isNonBilling = EntityUtil.getBooleanValue(value);
	}

	@Transient
	public boolean isEmailFormatHtml() {
		return getEmailFormatFlag() != null 
		&& getEmailFormatFlag().equals(EngageConstants.EMAIL_FORMAT_HTML); 
	}
	
	@Transient
	public boolean isContactNoDisplay() {
		return EntityUtil.getBooleanValue(getIsContactnoDisplay()); 
	}

	public Date getTermsAcceptedDate() {
		return termsAcceptedDate;
	}

	public void setTermsAcceptedDate(Date termsAcceptedDate) {
		this.termsAcceptedDate = termsAcceptedDate;
	}

}