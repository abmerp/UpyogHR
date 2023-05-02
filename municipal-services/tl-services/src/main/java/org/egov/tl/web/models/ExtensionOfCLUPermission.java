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
public class ExtensionOfCLUPermission {

	private String id;

	private String action;

	private String licenseNo;

	private String caseNo;

	private String applicationNumber;

	private String naturePurpose;

	private String totalAreaSq;

	private String cluDate;

	private String expiryClu;

	private String stageConstruction;

	private String applicantName;

	private String mobile;

	private String emailAddress;

	private String address;

	private String village;

	private String tehsil;

	private String pinCode;

	private String reasonDelay;

	private String buildingPlanApprovalStatus;

	private String zoningPlanApprovalDate;

	private String dateOfSanctionBuildingPlan;

	private String appliedFirstTime;

	private String uploadbrIIIfileUrl;

	private String cluPermissionLetterfileUrl;

	private String uploadPhotographsfileUrl;

	private String receiptApplicationfileUrl;

	private String uploadBuildingPlanfileUrl;

	private String indemnityBondfileUrl;

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
	
	private JsonNode newAdditionalDetails;

}
