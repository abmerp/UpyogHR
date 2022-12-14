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
	@JsonProperty("potential")
	private String potential;
	@JsonProperty("district")
	private String district;
	@JsonProperty("state")
	private String state;
	
	
	@JsonProperty("AppliedLandDetails")
	List<AppliedLandDetails> appliedLandDetails;

}
