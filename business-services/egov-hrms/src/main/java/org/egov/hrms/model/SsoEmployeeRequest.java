package org.egov.hrms.model;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SsoEmployeeRequest {
	 @JsonProperty("RequestInfo")
	 private RequestInfo requestInfo;
	 @JsonProperty("SsoEmployee")
	 private SsoEmployee ssoEmployee;


}
