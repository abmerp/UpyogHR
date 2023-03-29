package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RenewalLicenseSPC {
	
	@JsonProperty("spcDateOfApprovalOfLayoutPlan")
	private String spcDateOfApprovalOfLayoutPlan;
	
	@JsonProperty("spcAreaInAcres")
	private String spcAreaInAcres;
	
	@JsonProperty("spcPartCompletion")
	private String spcPartCompletion;
	
	@JsonProperty("spcDocumentUploadPartCompletion")
	private String spcDocumentUploadPartCompletion;
	
	@JsonProperty("spcTotalAreaCompletePercentage")
	private String spcTotalAreaCompletePercentage;

}
