package org.egov.tl.web.models;


import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;


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
public class RenewalLicenseResponse {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo = null;
	
	@JsonProperty("renewalLicenseRequest")
	private List<RenewalLicenseRequestDetail> renewalLicenseRequest;
	
	@JsonProperty("message")
	private String message;
	
	@JsonProperty("status")
	private boolean status;
}
