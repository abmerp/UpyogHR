package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicantType {
	@JsonProperty("licenceType")
	private String licenceType;
	@JsonProperty("developerType")
	private String developerType;
	@JsonProperty("licenceTypeSelected")
	private String licenceTypeSelected;
}
