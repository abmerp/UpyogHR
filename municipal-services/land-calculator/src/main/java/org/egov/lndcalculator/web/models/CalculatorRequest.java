package org.egov.lndcalculator.web.models;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalculatorRequest {

	@JsonProperty("requestInfo")
	private RequestInfo requestInfo = null;

	@JsonProperty("totalLandSize")
	private String totalLandSize;

	@JsonProperty("potenialZone")
	private String potenialZone;

	@JsonProperty("purposeCode")
	private String purposeCode;
	@JsonProperty("applicationNumber")
	private String applicationNumber;	

}
