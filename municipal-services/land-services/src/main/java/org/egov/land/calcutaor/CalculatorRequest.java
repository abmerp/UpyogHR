package org.egov.land.calcutaor;

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

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo = null;

	@JsonProperty("totalLandSize")
	private String totalLandSize;

	@JsonProperty("potenialZone")
	private String potenialZone;

	@JsonProperty("purposeCode")
	private String purposeCode;

}
