package org.egov.lndcalculator.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailsofAppliedLand {

	private String dgps;
	
	@JsonProperty("DetailsAppliedLandData1")
	private DetailsAppliedLandData1 detailsAppliedLandData1;
	
	@JsonProperty("DetailsAppliedLandDdjay2")
	private DetailsAppliedLandDdjay2 detailsAppliedLandDdjay2;
	
	@JsonProperty("DetailsAppliedLandIndustrial3")
	private DetailsAppliedLandIndustrial3 detailsAppliedLandIndustrial3;
	
	@JsonProperty("DetailsAppliedLandResidential4")
	private DetailsAppliedLandResidential4 detailsAppliedLandResidential4;
	
	@JsonProperty("DetailsAppliedLandNpnl5")
	private DetailsAppliedLandNpnl5 detailsAppliedLandNpnl5;
	
	@JsonProperty("DetailsAppliedLand6")
	private DetailsAppliedLand6 detailsAppliedLand6;

	
	
}
