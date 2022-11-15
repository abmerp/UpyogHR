package org.egov.tlcalculator.web.models;

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
public class NewServiceInfoData {

	@JsonProperty("ver")
	private float ver;
	
	@JsonProperty("ApplicantInfo")
	private ApplicantInfo applicantInfo;
	
	@JsonProperty("ApplicantPurpose")
	private ApplicantPurpose applicantPurpose;
	
	@JsonProperty("LandSchedule")
	private LandSchedule landSchedule;
	
	@JsonProperty("DetailsofAppliedLand")
	private DetailsofAppliedLand detailsofAppliedLand;
	
	@JsonProperty("FeesAndCharges")
	private FeesAndCharges feesAndCharges;
	
	
}
