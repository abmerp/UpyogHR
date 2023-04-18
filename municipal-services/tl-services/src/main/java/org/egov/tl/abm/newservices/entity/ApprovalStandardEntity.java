package org.egov.tl.abm.newservices.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

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

public class ApprovalStandardEntity {

	@JsonProperty("licenseNo")
	private String licenseNo;
	@JsonProperty("plan")
	private String plan;
	@JsonProperty("otherDocument")
	private String otherDocument;
	@JsonProperty("amount")
	private BigDecimal amount;
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	@JsonProperty("applicationNumber")
	private String applicationNumber;
	@JsonProperty("tenantId")
	private String tenantId;
	@JsonProperty("id")
	private String id;
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

	@JsonProperty("tcpApplicationNumber")
	private String tcpApplicationNumber;

	@JsonProperty("tcpCaseNumber")
	private String tcpCaseNumber;

	@JsonProperty("tcpDairyNumber")
	private String tcpDairyNumber;
	
	private JsonNode additionalDetails = null;
}
