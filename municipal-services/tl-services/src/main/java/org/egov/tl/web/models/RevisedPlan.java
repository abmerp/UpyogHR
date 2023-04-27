package org.egov.tl.web.models;

import java.math.BigDecimal;
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
public class RevisedPlan {
	private String id;

	private String licenseNo;

	private String existingArea;

	private String areaPlanning;

	private String anyOtherFeature;

	private BigDecimal amount;

	private String reasonRevision;

	private String earlierApprovedlayoutPlan;
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;
	private String applicationNumber;

	private String tenantId;

	private String action;

	private String status;

	private String businessService;

	private String comment;

	private String workflowCode;

	private JsonNode additionalDetails;
	private JsonNode newAdditionalDetails;
	private List<String> assignee;

	@JsonProperty("wfDocuments")
	private List<Document> wfDocuments;

	private JsonNode feesCharges;

	private JsonNode feesResult;
	@JsonProperty("ReviseLayoutPlan")
	private ReviseLayoutPlan ReviseLayoutPlan;

	@JsonProperty("tcpApplicationNumber")
	private String tcpApplicationNumber;

	@JsonProperty("tcpCaseNumber")
	private String tcpCaseNumber;

	@JsonProperty("tcpDairyNumber")
	private String tcpDairyNumber;

}
