package com.engage.entitlement.service;

import java.util.List;

import clear.cdb.extjs.annotations.CX_JSGenerateStore;
import clear.cdb.extjs.annotations.CX_JSJPQLMethod;
import clear.cdb.extjs.annotations.CX_JSService;
import clear.cdb.extjs.annotations.CX_TransferInfo;
import clear.cdb.extjs.annotations.CX_UpdateInfo;

import com.engage.entitlement.entity.UserEntity;

@CX_JSService
public interface IUserService {

    @CX_JSGenerateStore
	@CX_JSJPQLMethod(
	 		query="SELECT u.usrId, u.firstName, u.lastName, u.emailId, u.loginId, u.pwd"
	            + ",u.selfDesc, u.title, u.photoFileUri, u.primaryCommunityId, u.isSponsor"
	            + ",u.addressLine1, u.addressLine2, u.city, u.state, u.isoCountryCode"
					+ ",u.postalCode"
	            + ",u.contactNo, u.trialEndDate, u.isUnsubscribedByusr, u.isNonBilling"
	            + ",u.isEmailDisplay, u.isContactnoDisplay, u.isTempPwdFlag, u.isEmailSendNotify"
	            + ",u.pwdExpDate, u.emailFormatFlag, u.isKeepmeLoggedIn, u.lastViewedTreeId"
				+ ",u.lastViewedTreeDate, u.userStatus, u.createdBy, u.createdDate, u.updatedBy"
				+ ",u.updatedDate, u.termsAcceptedDate"
				+ ",u.unsubscribedDate, u.unsubscribedReason, u.unsubscribedConfirmationNo"
				+ ",( SELECT MIN(r1.roleId)"
				+ "  FROM UserRoleEntity ur1, RoleEntity r1"
				+ "  WHERE ur1.usrId=u.usrId AND r1.roleName='CA'"
				+ "    AND ur1.roleId=r1.roleId"
				+ ") as communityAdminId"
				+ ",( SELECT MIN(r2.roleId)"
				+ "  FROM UserRoleEntity ur2, RoleEntity r2"
				+ "  WHERE ur2.usrId=u.usrId AND r2.roleName='Part'"
				+ "    AND ur2.roleId=r2.roleId"
				+ ") as participantId"
				+ ",( SELECT MIN(r3.roleId)"
				+ "  FROM UserRoleEntity ur3, RoleEntity r3"
				+ "  WHERE ur3.usrId=u.usrId AND r3.roleName='Host'"
				+ "    AND ur3.roleId=r3.roleId"
				+ ") as hostId"
				+ " FROM UserEntity u WHERE usrId=:id",
		transferInfo=@CX_TransferInfo(
				type="com.engage.entitlement.dto.UserEntityDTO", 
				mappedBy=UserEntity.class),
		updateInfo=@CX_UpdateInfo(updateEntity=UserEntity.class, autoSyncEnabled=true)
	) 
    List<?> getById(Long id); 
}