package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppliedLandDetails {

	@JsonProperty("rowid")
	private String rowid;
	@JsonProperty("tehsil")
	private String tehsil;

	@JsonProperty("revenueEstate")
	private String revenueEstate;

	@JsonProperty("mustil")
	private String mustil;

	@JsonProperty("consolidationType")
	private String consolidationType;

	@JsonProperty("sarsai")
	private String sarsai;

	@JsonProperty("kanal")
	private String kanal;

	@JsonProperty("marla")
	private String marla;

	@JsonProperty("bigha")
	private String bigha;

	@JsonProperty("biswansi")
	private String biswansi;

	@JsonProperty("biswa")
	private String biswa;

	@JsonProperty("landOwner")
	private String landOwner;

	@JsonProperty("collaboration")
	private String collaboration;

	@JsonProperty("developerCompany")
	private String developerCompany;

	@JsonProperty("agreementValidFrom")
	private String agreementValidFrom;

	@JsonProperty("validitydate")
	private String agreementValidTo;

	@JsonProperty("agreementIrrevocialble")
	private String agreementIrrevocialble;

	@JsonProperty("authSignature")
	private String authSignature;

	@JsonProperty("nameAuthSign")
	private String nameAuthSign;

	@JsonProperty("registeringAuthority")
	private String registeringAuthority;

	@JsonProperty("registeringAuthorityDoc")
	private Document registeringAuthorityDoc;

}
