package org.egov.land.abm.models;


import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.land.abm.newservices.entity.LicenseServiceDao;

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
public class LicenseServiceResponse {

	@JsonProperty("RequestInfo")
	private ResponseInfo responseInfo = null;
	
	@JsonProperty("LicenseServiceDao")
	private List<LicenseServiceDao> newServiceInfo = null;
}
