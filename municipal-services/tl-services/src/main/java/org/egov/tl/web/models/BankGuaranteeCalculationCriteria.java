package org.egov.tl.web.models;

import lombok.Data;

import java.math.BigDecimal;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class BankGuaranteeCalculationCriteria {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo = null;
	
	private String tenantId;
	
	private String applicationNumber;

	private BigDecimal totalLandSize;

	private String potentialZone;

	private String purposeCode;

	private String loiNumber;

}
