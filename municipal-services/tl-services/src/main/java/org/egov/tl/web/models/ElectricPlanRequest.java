package org.egov.tl.web.models;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
public class ElectricPlanRequest {

	@JsonProperty("id")
	private String id;
	@JsonProperty("electricInfra")
	private String electricInfra;
	@JsonProperty("electricDistribution")
	private String electricDistribution;
	@JsonProperty("electricalCapacity")
	private String electricalCapacity;
	@JsonProperty("switchingStation")
	private String switchingStation;
	@JsonProperty("LoadSancation")
	private String LoadSancation;
	@JsonProperty("environmentalClearance")
	private String environmentalClearance;
	@JsonProperty("autoCad")
	private String autoCad;
	@JsonProperty("verifiedPlan")
	private String verifiedPlan;
	@JsonProperty("loiNumber")
	private String loiNumber;
	@JsonProperty("selfCenteredDrawings")
	private String selfCenteredDrawings;
	@JsonProperty("pdfFormat")
	private String pdfFormat;
	@JsonProperty("tenantID")
	private String tenantID;
	@JsonProperty("applicationNumber")
	private String applicationNumber;
	@JsonProperty("businessService")
	private String businessService;
	@JsonProperty("action")
	private String action;
	@JsonProperty("status")
	private String status;
	@JsonProperty("comment")
	private String comment;
	@JsonProperty("assignee")
	private List<String> assignee = null;
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
	@JsonProperty("tcpApplicationNumber")
	private String tcpApplicationNumber;

	@JsonProperty("tcpCaseNumber")
	private String tcpCaseNumber;

	@JsonProperty("tcpDairyNumber")
	private String tcpDairyNumber;

	
 
//	public ElectricPlanRequest(ElectricPlan electricPlan) {
//		this.id = electricPlan.getId();
//		this.electricalInfra = electricPlan.getElectricalInfra();
//		this.elecricDistribution = electricPlan.getElecricDistribution();
//		this.switchingStation = electricPlan.getSwitchingStation();
//		this.loadSancation = electricPlan.getLoadSancation();
//		this.environmentalClearance = electricPlan.getEnvironmentalClearance();
//		this.autoCad = electricPlan.getAutoCad();
//		this.verifiedPlan = electricPlan.getVerifiedPlan();
//		this.loiNumber = electricPlan.getLoiNumber();
//		this.selfCenteredDrawings = electricPlan.getSelfCentredDrawing();
//		this.tenantID = electricPlan.getTenantID();
//		this.auditDetails = electricPlan.getAuditDetails();
//		this.applicationNumber = electricPlan.getApplicationNumber();
//		this.businessService = electricPlan.getBusinessService();
//		this.action = electricPlan.getAction();
//		this.status = electricPlan.getStatus();
//		this.comment = electricPlan.getComment();
//		this.pdfFormat = electricPlan.getPdfFormat();
//		
//	
//	}
	
//	public ElectricPlan toBuilder() {
//		return ElectricPlan.builder().id(this.id)
//				.electricalInfra(this.electricalInfra)
//				.elecricDistribution(this.elecricDistribution)
//				.switchingStation(this.switchingStation)
//				.loadSancation(this.loadSancation)
//				.environmentalClearance(this.environmentalClearance)
//				.autoCad(this.autoCad)
//				.verifiedPlan(this.verifiedPlan)
//				.loiNumber(this.loiNumber)
//				.selfCentredDrawing(this.selfCenteredDrawings)
//				.tenantID(this.tenantID)
//				.auditDetails(this.auditDetails)
//				.applicationNumber(this.applicationNumber)
//				.businessService(this.businessService)
//				.action(this.action)
//				.status(this.status)
//				.comment(this.comment)
//				.pdfFormat(this.pdfFormat).build();
//	}
}
