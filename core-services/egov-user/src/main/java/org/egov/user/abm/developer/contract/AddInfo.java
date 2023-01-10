	package org.egov.user.abm.developer.contract;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddInfo {

	@JsonProperty("csr_Number")
	private String csr_Number;

	@JsonProperty("llp_Number")
	private String llp_Number;

	@JsonProperty("showDevTypeFields")
	private String showDevTypeFields;

	@JsonProperty("name")
	private String name;

	@JsonProperty("mobileNumberUser")
	private String mobileNumberUser;

	@JsonProperty("cin_Number")
	private String cin_Number;

	@JsonProperty("companyName")
	private String companyName;

	@JsonProperty("incorporationDate")
	private String incorporationDate;

	@JsonProperty("registeredAddress")
	private String registeredAddress;

	@JsonProperty("email")
	private String email;

	@JsonProperty("registeredContactNo")
	private String registeredContactNo;

	@JsonProperty("gst_Number")
	private String gst_Number;

	@JsonProperty("DirectorsInformation")
	private List<DirectorsInformation> directorsInformation;

	@JsonProperty("shareHoldingPatterens")
	private List<ShareholdingPattens> shareHoldingPatterens;

	@JsonProperty("existingColonizer")
	private String existingColonizer;

	@JsonProperty("existingColonizerData")
	private ExistingColonizerData existingColonizerData;

	@JsonProperty("othersDetails")
	private List<Other> othersDetails;

	@JsonProperty("emailId")
	private String emailId;
	
	@JsonProperty("uploadDigitalSignaturePdf")
	private String uploadDigitalSignaturePdf;
	
	@JsonProperty("uploadBoardResolution")
	private String uploadBoardResolution;
	
	@JsonProperty("isUndertaken")
	private Boolean isUndertaken;
	
	@JsonProperty("existingDirectors")
	private String existingDirectors;
	
	@JsonProperty("DirectorsInformationMCA")
	private List<DirectorsInformationMCA> directorsInformationMCA;
	

}
