package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DirectorsInformationMCA {
	@JsonProperty("contactNumber")
	private String contactNumber;
	@JsonProperty("din")
	private String din;
	@JsonProperty("name")
	private String name;

}
