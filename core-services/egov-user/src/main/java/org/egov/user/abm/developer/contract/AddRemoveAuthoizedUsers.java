package org.egov.user.abm.developer.contract;

import java.util.List;

import org.egov.user.domain.model.Document;
import org.egov.user.domain.model.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddRemoveAuthoizedUsers {

//	private int serialNumber;
	private String userName;
	private String name;
	private String gender;
	private boolean active;
	private String type;
	private String password;
	private String tenantId;
	private String mobileNumber;
	private List<Document> document;
//	private String email;
//	private String uploadPanPdf;
//	private String uploadAadharPdf;
//	private String uploadDigitalSignaturePdf;
	private List<Role> roles;
	private String emailId;
	private String dob;
	private String pan;

}
