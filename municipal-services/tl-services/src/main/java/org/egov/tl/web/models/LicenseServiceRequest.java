package org.egov.tl.web.models;

import java.util.Date;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LicenseServiceRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo = null;

	private Long id;

	private float currentVersion;

	private String applicationNumber;
	private String pageName;
	@JsonProperty("action")
	private String action;
	
	@JsonProperty("ApplicationStatus")
	private String applicationStatus;
	@JsonProperty("LicenseDetails")
	private LicenseDetails licenseDetails;
}
