package org.egov.tl.web.models;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

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
public class Transfer {

	private String id;

	private String licenseNo;

	private String selectType;

	private String araeInAcres;

	private JsonNode additionalDetails = null;
	private JsonNode transferOfLicenceDetail;
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	@JsonProperty("applicationNumber")
	private String applicationNumber;
	@JsonProperty("tenantId")
	private String tenantId;
	@JsonProperty("assignee")
	private List<String> assignee;

	@JsonProperty("action")
	private String action;

	@JsonProperty("status")
	private String status;

	@JsonProperty("businessService")
	private String businessService;

	@JsonProperty("comment")
	private String comment;

	@JsonProperty("workflowCode")
	private String workflowCode = null;

	@JsonProperty("wfDocuments")
	private List<Document> wfDocuments;
	@JsonProperty("TransferOfLicence")
	private TransferOfLicence transferOfLicence;
	@JsonProperty("tcpApplicationNumber")
	private String tcpApplicationNumber;

	@JsonProperty("tcpCaseNumber")
	private String tcpCaseNumber;

	@JsonProperty("tcpDairyNumber")
	private String tcpDairyNumber;

}
