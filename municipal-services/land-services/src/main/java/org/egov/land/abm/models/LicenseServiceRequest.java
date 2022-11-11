package org.egov.land.abm.models;


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
	
	@JsonProperty("LicenseServiceDao")
	private LicenseServiceRequestInfo newServiceInfo = null;
}
