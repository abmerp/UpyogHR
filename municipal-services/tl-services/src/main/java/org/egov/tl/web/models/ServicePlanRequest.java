package org.egov.tl.web.models;

import java.util.List;

import org.egov.tl.web.models.ServicePlan;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicePlanRequest {

	@JsonProperty("id")
	private String id;

	@JsonProperty("loiNumber")
	private String loiNumber;

	@JsonProperty("undertaking")
	private String undertaking;

	@JsonProperty("selfCertifiedDrawingsFromCharetedEng")
	private String selfCertifiedDrawingsFromCharetedEng;

	@JsonProperty("selfCertifiedDrawingFromEmpaneledDoc")
	private String selfCertifiedDrawingFromEmpaneledDoc;

	@JsonProperty("environmentalClearance")
	private String environmentalClearance;

	@JsonProperty("shapeFileAsPerTemplate")
	private String shapeFileAsPerTemplate;

	@JsonProperty("autoCadFile")
	private String autoCadFile;

	@JsonProperty("certifieadCopyOfThePlan")
	private String certifieadCopyOfThePlan;

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

	@JsonProperty("tenantID")
	private String tenantID;

	@JsonProperty("applicationNumber")
	private String applicationNumber;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

	@JsonProperty("workflowCode")
	private String workflowCode = null;

	@JsonProperty("wfDocuments")
	private List<Document> wfDocuments;

	@JsonProperty("additionalDetails")
	private Object additionalDetails = null;

	@JsonProperty("devName")
	private String devName = null;

	@JsonProperty("developmentPlan")
	private String developmentPlan = null;

	@JsonProperty("purpose")
	private String purpose = null;

	@JsonProperty("totalArea")
	private String totalArea = null;

	@JsonProperty("layoutPlan")
	private String layoutPlan = null;

	@JsonProperty("revisedLayout")
	private String revisedLayout = null;

	@JsonProperty("demarcation")
	private String demarcation = null;

	@JsonProperty("demarcationgis")
	private String demarcationgis = null;

	@JsonProperty("layoutExcel")
	private String layoutExcel = null;

	@JsonProperty("anyOtherdoc")
	private String anyOtherdoc = null;

	@JsonProperty("externalAgency")
	private JsonNode externalAgency = null;

	@JsonProperty("tcpApplicationNumber")
	private String tcpApplicationNumber;

	@JsonProperty("tcpCaseNumber")
	private String tcpCaseNumber;

	@JsonProperty("tcpDairyNumber")
	private String tcpDairyNumber;

	

//	public ServicePlanRequest(ServicePlan servicePlan) {
//		
//		this.loiNumber = servicePlan.getLoiNumber();
//		this.undertaking = servicePlan.isUndertaking();
//		this.selfCertifiedDrawingsFromCharetedEng = servicePlan.isSelfCertifiedDrawingsFromCharetedEng();
//		this.selfCertifiedDrawingFromEmpaneledDoc = servicePlan.getSelfCertifiedDrawingFromEmpaneledDoc();
//		this.environmentalClearance = servicePlan.getEnvironmentalClearance();
//		this.shapeFileAsPerTemplate = servicePlan.getShapeFileAsPerTemplate();
//		this.autoCadFile = servicePlan.getAutoCadFile();
//		this.certifieadCopyOfThePlan = servicePlan.getCertifieadCopyOfThePlan();
////		this.assignee = servicePlan.getAssignee();
//		this.action = servicePlan.getAction();
//		this.status = servicePlan.getStatus();
//		this.businessService = servicePlan.getBusinessService();
//		this.comment = servicePlan.getComment();
//		this.tenantID = servicePlan.getTenantID();
//		this.applicationNumber = servicePlan.getApplicationNumber();
//		this.auditDetails = servicePlan.getAuditDetails();
//	}

//	public ServicePlan toBuilder() {
//		return ServicePlan.builder().loiNumber(this.loiNumber)
//														.undertaking(this.undertaking)
//														.selfCertifiedDrawingsFromCharetedEng(this.selfCertifiedDrawingsFromCharetedEng)
//														.selfCertifiedDrawingFromEmpaneledDoc(this.selfCertifiedDrawingFromEmpaneledDoc)
//														.environmentalClearance(this.environmentalClearance)
//														.shapeFileAsPerTemplate(this.shapeFileAsPerTemplate)
//														.autoCadFile(this.autoCadFile)
//														.certifieadCopyOfThePlan(this.certifieadCopyOfThePlan)
////														.assignee(this.assignee)
//														.action(this.action)
//														.status(this.status)
//														.businessService(this.businessService)
//														.comment(this.comment)
//														.tenantID(this.tenantID)
//														.applicationNumber(this.applicationNumber)
//														.auditDetails(this.auditDetails)
//														.build();
//	}
}
