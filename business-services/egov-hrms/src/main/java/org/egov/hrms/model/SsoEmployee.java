package org.egov.hrms.model;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SsoEmployee {
	private String applicantName;
	private String mobileNumber;
	private String uid;
	private String userName;
	private String email;
	private String rtnUrl;
	private String ssoDashboardURL;
	private String tokenId;
	private String designationID;
	private String designation;
	private String officeID;
	private String officeName;
}
