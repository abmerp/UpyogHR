package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StatusOfCommunitySite {
	
	private String socsType;
	private String socsArea;
	private boolean socsBuldingPlan;
	private boolean socsOCGrantzed;
}
