package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicantInfo {

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
	@JsonProperty("authorizedEmail")
	private String authorizedEmail;
	@JsonProperty("authorizedPan")
	private String authorizedPan;
	@JsonProperty("authorizedAddress")
	private String authorizedAddress;

}
