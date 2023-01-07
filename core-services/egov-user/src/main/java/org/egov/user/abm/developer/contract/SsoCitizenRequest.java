package org.egov.user.abm.developer.contract;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SsoCitizenRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	@JsonProperty("SsoCitizen")
	private SsoCitizen SsoCitizen;

}
