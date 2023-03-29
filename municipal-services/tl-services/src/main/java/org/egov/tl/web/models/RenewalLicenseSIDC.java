package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RenewalLicenseSIDC {
	//SIDC--Internal Development Cost
	@JsonProperty("sidcHead")
	private String sidcHead;
	
	@JsonProperty("sidcPrinciple")
	private String sidcPrinciple;
	
	@JsonProperty("sidcInterest")
	private String sidcInterest;
	
	@JsonProperty("sidcPenalInterest")
	private String sidcPenalInterest;
	
	@JsonProperty("sidcTotal")
	private String sidcTotal;
	
	@JsonProperty("sidcRemarks")
	private String sidcRemarks;

}
