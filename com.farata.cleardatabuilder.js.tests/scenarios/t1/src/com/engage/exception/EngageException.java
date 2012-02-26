package com.engage.exception;

import java.util.HashMap;
import java.util.Map;

public class EngageException extends RuntimeException {

	// ----------------------------------------------------------------------
	//
	// Class Constants
	//
	// ----------------------------------------------------------------------

	private static final String[] ERRORS_LIST = {
		// Declared in RFD
		"ERR01","Please enter required field",
		"ERR02","The Email Address has an invalid format or character",
		"ERR03","Your Email ID is invalid. Please try again.",
		"ERR04","The password is not valid.  Passwords must be entered using the correct case.  Make sure that the Caps Lock is in the correct position.",
		"ERR05","The password could not be changed because: the New Password contains an invalid character; the New Password is less than six (6) characters; the New Password and Confirm New Password do not match",
		"ERR06","Confirm Email ID does not match Email ID",
		"ERR07","Please select a discussion restriction",
		"ERR08","You have made the discussion restricted. Please invite participants to the discussion",
		"ERR09","Please select participants you wish to appear in the participant panel",
		"ERR10","The characters you entered did not match the image verification. Please try again.",
		"ERR11","No results found",
		"ERR12","Discussion is offline",
		"ERR13","Please pick a date",
		"ERR14","File could not be located",
		"ERR15","The file size exceeds the community’s maximum allowable size of [size amount]MB",
		"ERR16","From date must be earlier than or equal to today’s date",
		"ERR17","From date must be earlier than or equal to \"To date\"",
		"ERR18","The Email ID matches an existing Email ID. Please use a different Email ID",
		
		// Our custom errors
		"ERR00","Entitlements Engine rules violation",
		"ERR1000","System error",
		"ERR1001","Upload photo exception",
		"ERR1002","Email sending problem"
	
	};

	private static final Map<String, String> ERRORS = new HashMap<String, String>();

	// ----------------------------------------------------------------------
	//
	// Class Methods
	//
	// ----------------------------------------------------------------------

	static {
		for (int i = 0; i < ERRORS_LIST.length; i += 2) {
			ERRORS.put(ERRORS_LIST[i], ERRORS_LIST[i + 1]);
		}
	}

	private static String getMessageByCode(String code) {
		String msg = ERRORS.get(code);
		return msg != null ? msg : "Error with unknown error code: " + code;
	}

	// ----------------------------------------------------------------------
	//
	// Constructor
	//
	// ----------------------------------------------------------------------

	public EngageException(Throwable e) {
		super(getMessageByCode("ERR1000") + " " + e.getMessage());
		this.code = "ERR1000";
	}

	public EngageException(String code) {
		super(getMessageByCode(code));
		this.code = code;
	}

	public EngageException(String code, String addMsg) {
		super(getMessageByCode(code) + " " + addMsg);
		this.code = code;
	}

	// ----------------------------------------------------------------------
	//
	// Overriden Properties
	//
	// ----------------------------------------------------------------------

	// ----------------------------------------------------------------------
	//
	// Properties
	//
	// ----------------------------------------------------------------------

	private String code;

	// ----------------------------------------------------------------------
	//
	// Overriden Methods
	//
	// ----------------------------------------------------------------------

	// ----------------------------------------------------------------------
	//
	// Methods
	//
	// ----------------------------------------------------------------------

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
