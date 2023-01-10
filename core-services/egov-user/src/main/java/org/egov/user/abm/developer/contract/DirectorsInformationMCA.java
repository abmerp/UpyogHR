package org.egov.user.abm.developer.contract;

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
