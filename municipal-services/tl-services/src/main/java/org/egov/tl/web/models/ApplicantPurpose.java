package org.egov.tl.web.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter	
public class ApplicantPurpose {

	private String purposeDd;
	private String potential;
	private String district;
	private String state;
	@JsonProperty("AppliedLandDetails")
	List<AppliedLandDetails> appliedLandDetails;

}
