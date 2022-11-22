package org.egov.tl.web.models;


import java.util.List;

import lombok.Getter;
import lombok.Setter;	

@Getter
@Setter
public class AddRemoveAuthoizedUsers {

//	private int serialNumber;
	private String userName;
	private String name;	
	private String gender;
	private String active;
	private String type;
	private String password;
	private String tenantId;
	private String mobileNumber;
	private String uploadAadharPdf;
//	private String email;
	private String uploadPanPdf;
//	private String uploadAadharPdf;
	private String uploadDigitalSignaturePdf;
	//private List<Role> roles;
	private String emailId;
	private String dob;
	private String pan;

}
