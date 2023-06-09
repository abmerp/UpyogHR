package org.egov.tl.web.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

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
	

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("developerServiceCode")
	private String developerServiceCode;
	
	@JsonProperty("applicationNumber")
	private String applicationNumber;
	
	@JsonProperty("licenseNumber")
	private String licenseNumber;
	
	@JsonProperty("isDraft")
	private String isDraft;  // 0-draft,1 -not draft
	
	@JsonProperty("paymentType")
	private int paymentType;  // 1-partial payment,2- full payment   //new 
	
	@JsonProperty("applicationStatus")
	private int applicationStatus; //  1- pending,2-partial paied,3-full paied    // new
	
	@JsonProperty("paidAmount")
	private String paidAmount;	

	@JsonProperty("isFullPaymentDone")
	private boolean isFullPaymentDone;
	
	@JsonProperty("areaInAcres")
	private String areaInAcres;
	
	@JsonProperty("totalChangeBeneficialCharge")
	private String totalChangeBeneficialCharge;
	
	@JsonProperty("developerId")
	private long developerId;
	
	@JsonProperty("createdDate")
	private String createdDate;
	
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
	
	@JsonProperty("tranactionId")
	private String tranactionId;
	
	@JsonProperty("workFlowCode")
	private String workFlowCode = null;
	
	@JsonProperty("auditDetails")
	AuditDetails auditDetails=null;
	
	@JsonProperty("createdTime")
	private long createdTime;
	
	@JsonProperty("newAdditionalDetails")
	private JsonNode newAdditionalDetails = null;
	
	@JsonProperty("tenantId")
	private String tenantId;
	
	@JsonProperty("businessService")
	private String businessService;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("action")
	private String action;
	
	@JsonProperty("assignee")
	private List<String> assignee;
	
	@JsonProperty("tcpDairyNumber")
	private String tcpDairyNumber; 
	
	@JsonProperty("tcpApplicationNumber")
	private String tcpApplicationNumber; 
	
	@JsonProperty("tcpCaseNumber")
	private String tcpCaseNumber; 
	
	

	
}
