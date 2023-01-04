package org.egov.tl.web.models;

import java.util.List;

import org.egov.tl.web.models.ServicePlan;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	private String loiNumber;
	private boolean undertaking;
	private boolean selfCertifiedDrawingsFromCharetedEng;
	private String selfCertifiedDrawingFromEmpaneledDoc;
	private String environmentalClearance;
	private String shapeFileAsPerTemplate;
	private String autoCadFile;
	private String certifieadCopyOfThePlan;
	private List<String> assignee;
	private String action;
	private String status;
	private String businessService;
	private String comment;
	private String tenantID;
	private String applicationNumber;
	
    private AuditDetails auditDetails = null;
	
	public ServicePlanRequest(ServicePlan servicePlan) {
		
		this.loiNumber = servicePlan.getLoiNumber();
		this.undertaking = servicePlan.isUndertaking();
		this.selfCertifiedDrawingsFromCharetedEng = servicePlan.isSelfCertifiedDrawingsFromCharetedEng();
		this.selfCertifiedDrawingFromEmpaneledDoc = servicePlan.getSelfCertifiedDrawingFromEmpaneledDoc();
		this.environmentalClearance = servicePlan.getEnvironmentalClearance();
		this.shapeFileAsPerTemplate = servicePlan.getShapeFileAsPerTemplate();
		this.autoCadFile = servicePlan.getAutoCadFile();
		this.certifieadCopyOfThePlan = servicePlan.getCertifieadCopyOfThePlan();
//		this.assignee = servicePlan.getAssignee();
		this.action = servicePlan.getAction();
		this.status = servicePlan.getStatus();
		this.businessService = servicePlan.getBusinessService();
		this.comment = servicePlan.getComment();
		this.tenantID = servicePlan.getTenantID();
		this.applicationNumber = servicePlan.getApplicationNumber();
		this.auditDetails = servicePlan.getAuditDetails();
	}
	
	
	public ServicePlan toBuilder() {
		return ServicePlan.builder().loiNumber(this.loiNumber)
														.undertaking(this.undertaking)
														.selfCertifiedDrawingsFromCharetedEng(this.selfCertifiedDrawingsFromCharetedEng)
														.selfCertifiedDrawingFromEmpaneledDoc(this.selfCertifiedDrawingFromEmpaneledDoc)
														.environmentalClearance(this.environmentalClearance)
														.shapeFileAsPerTemplate(this.shapeFileAsPerTemplate)
														.autoCadFile(this.autoCadFile)
														.certifieadCopyOfThePlan(this.certifieadCopyOfThePlan)
//														.assignee(this.assignee)
														.action(this.action)
														.status(this.status)
														.businessService(this.businessService)
														.comment(this.comment)
														.tenantID(this.tenantID)
														.applicationNumber(this.applicationNumber)
														.auditDetails(this.auditDetails)
														.build();
	}
}
