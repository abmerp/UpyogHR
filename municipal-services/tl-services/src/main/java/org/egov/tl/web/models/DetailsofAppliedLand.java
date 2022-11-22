package org.egov.tl.web.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailsofAppliedLand {
	
	@JsonProperty("dgpsDetails")
	private List<GISDeatils> dgps;
	
	@JsonProperty("DetailsAppliedLandPlot")
	private DetailsAppliedLandPlot detailsAppliedLandPlot;
	
	@JsonProperty("DetailsAppliedLandDdjay")
	private DetailsAppliedLandDdjay detailsAppliedLandDdjay;
	
	@JsonProperty("DetailsAppliedLandIndustrial")
	private DetailsAppliedLandIndustrial detailsAppliedLandIndustrial;
	
	@JsonProperty("DetailsAppliedLandResidential")
	private DetailsAppliedLandResidential detailsAppliedLandResidential;
	
	@JsonProperty("DetailsAppliedLandNILP")
	private DetailsAppliedLandNILP detailsAppliedLandNILP;
	
	@JsonProperty("DetailsAppliedLand")
	private DetailsAppliedLand detailsAppliedLand;

	
	
}
