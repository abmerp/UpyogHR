package org.egov.tl.web.models;

import java.util.List;

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
public class ComplianceRequest {
	private String id;
	@JsonProperty("loiNumber")
	private String loiNumber;
	@JsonProperty("tcpApplicationNumber")
	private String tcpAapplicationNumber;
	@JsonProperty("businessService")
	private String businessService;
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	@JsonProperty("additionalDetails")
	private Object additionalDetails = null;
	@JsonProperty("Compliance")
	private Compliance compliance;
}
