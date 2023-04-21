package org.egov.tl.web.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ZonePlan {
	
	private String id;
	
	private String action;
	@JsonProperty("licenseNo")
	private String licenseNo;

	private String caseNumber;

	private String layoutPlan;

	private String anyotherDocument;
	
	private String amount;
	
	private String applicationNumber;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

	private JsonNode additionalDetails;

	private List<String> assignee;

	private String tenantId;

	private String businessService;

	private String comment;

	private String workflowCode;

	private String status;
	
	@JsonProperty("tcpApplicationNumber")
	private String tcpApplicationNumber;

	@JsonProperty("tcpCaseNumber")
	private String tcpCaseNumber;

	@JsonProperty("tcpDairyNumber")
	private String tcpDairyNumber;


}
