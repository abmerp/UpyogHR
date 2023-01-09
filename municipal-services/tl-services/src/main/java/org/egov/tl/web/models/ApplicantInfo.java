package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicantInfo {
	@JsonProperty("devDetail")
	private Developerdetail devDetail;
	@JsonProperty("authorizedDeveloper")
	private String authorizedDeveloper;
	@JsonProperty("authorizedPerson")
	private String authorizedPerson;
	@JsonProperty("authorizedmobile")
	private String authorizedmobile;
	@JsonProperty("alternatemobile")
	private String alternatemobile;
	@JsonProperty("authorizedEmail")
	private String authorizedEmail;
	@JsonProperty("authorizedPan")
	private String authorizedPan;
	@JsonProperty("authorizedAddress")
	private String authorizedAddress;
	@JsonProperty("village")
	private String village;
	@JsonProperty("authorizedPinCode")
	private Integer authorizedPinCode;
	@JsonProperty("tehsil")
	private String tehsil;
	@JsonProperty("district")
	private String district;
	@JsonProperty("state")
	private String state;
	@JsonProperty("status")
	private String status;
	@JsonProperty("")
	private String LC;
	@JsonProperty("address")
	private String address;
	@JsonProperty("permanentAddress")
	private String permanentAddress;
	@JsonProperty("notSigned")
	private String notSigned;
	@JsonProperty("email")
	private String email;
	@JsonProperty("authorized")
	private String authorized;
	@JsonProperty("developerName")
	private String developerName;
	@JsonProperty("developerAddress")
	private String developerAddress;
	@JsonProperty("developerEmail")
	private String developerEmail;
	@JsonProperty("developerType")
	private String developerType;
	@JsonProperty("developerCinNo")
	private String developerCinNo;
	@JsonProperty("directorDinNo")
	private String directorDinNo;
	@JsonProperty("directorName")
	private String directorName;
	@JsonProperty("directorContactNumber")
	private String directorContactNumber;
	@JsonProperty("directorDoc")
	private String directorDoc;
	@JsonProperty("shareholdingName")
	private String shareholdingName;
	@JsonProperty("shareholdingDesignition")
	private String shareholdingDesignition;
	@JsonProperty("shareholdingPercentage")
	private String shareholdingPercentage;
	@JsonProperty("shareholdingDoc")
	private String shareholdingDoc;
	@JsonProperty("authorizedName")
	private String authorizedName;
	@JsonProperty("authorizedMobile")
	private String authorizedMobile;


}
