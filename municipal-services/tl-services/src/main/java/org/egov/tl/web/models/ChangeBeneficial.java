package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class ChangeBeneficial {
	

	@JsonIgnore
	@JsonProperty("id")
	private String id;
	

	@JsonProperty("developerServiceCode")
	private String developerServiceCode;
	
	@JsonProperty("applicationNumber")
	private String applicationNumber;
	

//	@JsonProperty("totalAmount")
//	private double totalAmount;
	
	@JsonProperty("paidAmount")
	private double paidAmount;
	
//	@JsonProperty("paymentStatus")
//	private double paymentStatus;
//	
//	@JsonProperty("tranactionId")
//	private String tranactionId;
//	

	@JsonProperty("areaInAcres")
	private String areaInAcres;
	
	@JsonProperty("paid_beneficial_change_amount")
	private String paid_beneficial_change_amount;
	
	@JsonProperty("developerId")
	private long developerId;
	
	@JsonProperty("nocDocument")
	private String nocDocument;
	
	@JsonProperty("consentLetterFromNewEntryDoc")
	private String consentLetterFromNewEntryDoc;
	
	@JsonProperty("JustificationForSuchRequestDoc")
	private String JustificationForSuchRequestDoc;
	
	@JsonProperty("sideEffectForExtstingDeveloperDoc")
	private String sideEffectForExtstingDeveloperDoc;
	
	@JsonProperty("detailsOfAppliedAreaDoc")
	private String detailsOfAppliedAreaDoc;
	
	@JsonProperty("undertackingPayBalanceDoc")
	private String undertackingPayBalanceDoc;
	
	@JsonProperty("authorizedSignatoryOEDDoc")
	private String authorizedSignatoryOEDDoc;  // existing developer
	
	@JsonProperty("authorizedSignatoryNEDoc")
	private String authorizedSignatoryNEDoc; // new Entity
	

}
