package org.egov.user.abm.developer.contract;

import java.util.List;

import org.egov.user.domain.model.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddRemoveAuthoizedUsers {
	
	private int serialNumber;
	private String userName;
	private String gender;
	private String name;
	private boolean  active;
	private String type;
	private String password;
	private String tenantId;
	private String mobileNumber;
	private String email;
	private String uploadPanPdf;
	private String uploadAadharPdf;
	private String uploadDigitalSignaturePdf;
	private List<Role> roles;
	

}
