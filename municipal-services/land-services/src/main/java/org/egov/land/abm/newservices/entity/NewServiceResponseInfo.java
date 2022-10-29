package org.egov.land.abm.newservices.entity;


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
public class NewServiceResponseInfo {

	@JsonProperty("RequestInfo")
	private ResponseInfo responseInfo = null;
	
	@JsonProperty("NewServiceInfo")
	private List<NewServiceInfo> newServiceInfo = null;
}
