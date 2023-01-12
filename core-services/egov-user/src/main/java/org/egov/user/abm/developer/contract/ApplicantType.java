package org.egov.user.abm.developer.contract;



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
}
