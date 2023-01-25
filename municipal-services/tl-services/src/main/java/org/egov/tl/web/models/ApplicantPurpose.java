package org.egov.tl.web.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter	
public class ApplicantPurpose {

	@JsonProperty("purpose")
	private String purpose;
	
	@JsonProperty("state")
	private String state;
	
	@JsonProperty("totalArea")
	private String totalArea;
	
	
	@JsonProperty("AppliedLandDetails")
	List<AppliedLandDetails> appliedLandDetails;

}
