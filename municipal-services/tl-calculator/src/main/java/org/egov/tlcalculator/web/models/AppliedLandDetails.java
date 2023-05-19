package org.egov.tlcalculator.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppliedLandDetails {

	@JsonProperty("district")
	private District district;

	@JsonProperty("potential")
	private String potential;

	@JsonProperty("zone")
	private String zone;

	@JsonProperty("developmentPlan")
	private DevelopmentPlan developmentPlan;

	@JsonProperty("sector")
	private String sector;

	private String isChange;

	@JsonProperty("rowid")
	private String rowid;
	@JsonProperty("tehsil")
	private Tehsil tehsil;

	@JsonProperty("revenueEstate")
	private RevenueEstate revenueEstate;

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

	@JsonProperty("hadbastNo")
	private String hadbastNo;

	@JsonProperty("bigha")
	private String bigha;

	@JsonProperty("biswansi")
	private String biswansi;

	@JsonProperty("biswa")
	private String biswa;

	@JsonProperty("landOwner")
	private String landOwner;

	@JsonProperty("landOwnerRegistry")
	private String landOwnerRegistry;

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
	private String registeringAuthorityDoc;

	@JsonProperty("khewats")
	private String khewats;

	@JsonProperty("consolidatedTotal")
	private String consolidatedTotal;

	@JsonProperty("nonConsolidatedTotal")
	private String nonConsolidatedTotal;

	@JsonProperty("editKhewats")
	private String editKhewats;

	@JsonProperty("editRectangleNo")
	private String editRectangleNo;

	@JsonProperty("typeLand")
	private TypeLand typeLand;

	@JsonProperty("rectangleNo")
	private String rectangleNo;

	@JsonProperty("nonConsolidationType")
	private String nonConsolidationType;

	@JsonProperty("landOwnerSPAGPADoc")
	private String landOwnerSPAGPADoc;

	@JsonProperty("developerSPAGPADoc")
	private String developerSPAGPADoc;

	@JsonProperty("collaboratorAgreementDocument")
	private String collaboratorAgreementDocument;
	@JsonProperty("acquistionStatus")
	private String acquistionStatus;
}
