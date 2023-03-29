package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RenewalLicenseEDC {
//EDC--External Development
	@JsonProperty("edcHead")
	private String edcHead;
	
	@JsonProperty("edcPrinciple")
	private String edcPrinciple;
	
	@JsonProperty("edcInterest")
	private String edcInterest;
	
	@JsonProperty("edcPenalInterest")
	private String edcPenalInterest;
	
	@JsonProperty("edcTotal")
	private String edcTotal;
	
	@JsonProperty("edcRemarks")
	private String edcRemarks;
	

}
