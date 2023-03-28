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
	
	@JsonProperty("cbApplicationNumber")
	private String cbApplicationNumber;

	@JsonProperty("developerServiceCode")
	private String developerServiceCode;
	
	@JsonProperty("applicationNumber")
	private String applicationNumber;
	
	@JsonProperty("isDraft")
	private String isDraft;
	
	@JsonProperty("workFlowCode")
	private String workFlowCode;

	@JsonProperty("paymentType")
	private int paymentType;  // 1-partial payment,2- full payment   //new 
	
	@JsonProperty("applicationStatus")
	private int applicationStatus; //  1- pending,2-partial paied,3-full paied    // new
	
	@JsonProperty("paidAmount")
	private String paidAmount;	

	@JsonProperty("areaInAcres")
	private String areaInAcres;
	
	@JsonProperty("paid_beneficial_change_amount")
	private String paid_beneficial_change_amount;
	
	@JsonProperty("developerId")
	private long developerId;
	
	
	
	@JsonProperty("noObjectionCertificate")
	private String noObjectionCertificate;
	
	@JsonProperty("consentLetter")
	private String consentLetter;
	
	@JsonProperty("justificationCertificate")
	private String justificationCertificate;
	
	@JsonProperty("thirdPartyRightsCertificate")
	private String thirdPartyRightsCertificate;
	
	@JsonProperty("jointDevelopmentCertificate")
	private String jointDevelopmentCertificate;
	
	@JsonProperty("aministrativeChargeCertificate")
	private String aministrativeChargeCertificate;
	
	@JsonProperty("boardResolutionExisting")
	private String boardResolutionExisting;  // existing developer
	
	@JsonProperty("boardResolutionNewEntity")
	private String boardResolutionNewEntity; // new Entity
		
	
	@JsonProperty("shareholdingPatternCertificate")
	private String shareholdingPatternCertificate; 
		
	@JsonProperty("reraRegistrationCertificate")
	private String reraRegistrationCertificate; 
	
	@JsonProperty("fiancialCapacityCertificate")
	private String fiancialCapacityCertificate; 
	
	

}
