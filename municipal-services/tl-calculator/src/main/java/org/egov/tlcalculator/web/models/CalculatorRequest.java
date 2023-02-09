package org.egov.tlcalculator.web.models;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tlcalculator.web.models.tradelicense.TradeLicense;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculatorRequest {



	@JsonProperty("totalLandSize")
	private String totalLandSize;

	@JsonProperty("potenialZone")
	private String potenialZone;

	@JsonProperty("purposeCode")
	private String purposeCode;
	
	@JsonProperty("applicationNumber")
	private String applicationNumber;	
	
	@JsonProperty("far")
	private String far;
	
}
