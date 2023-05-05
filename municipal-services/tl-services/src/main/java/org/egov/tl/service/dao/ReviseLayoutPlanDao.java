package org.egov.tl.service.dao;

import java.math.BigDecimal;
import java.util.List;

import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.Document;
import org.egov.tl.web.models.ExixtingAreaDetails;
import org.egov.tl.web.models.ReviseLayoutPlan;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviseLayoutPlanDao {

	private String id;

	private String licenseNo;

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

	@JsonProperty("tcpApplicationNumber")
	private String tcpApplicationNumber;

	@JsonProperty("tcpCaseNumber")
	private String tcpCaseNumber;

	@JsonProperty("tcpDairyNumber")
	private String tcpDairyNumber;

}
