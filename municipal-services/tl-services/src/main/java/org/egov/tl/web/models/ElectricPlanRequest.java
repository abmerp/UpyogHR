package org.egov.tl.web.models;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


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

	
	private Long id;
	private boolean electricalInfra;
	private boolean elecricDistribution;
	private boolean electricalCapacity;
	private boolean switchingStation;
	private boolean loadSancation;
	private String environmentalClearance;
	private String autoCad;
	private String verifiedPlan;
	private String loiNumber;    
	private String selfCenteredDrawings;	       
	private String pdfFormat;	
	private String tenantID;
	private String applicationNumber;
	private String businessService;
	private String action;
	private String status;
	private String comment;
	@JsonProperty("assignee")
        private List<String> assignee = null;
	
	private AuditDetails auditDetails = null;
 
	public ElectricPlanRequest(ElectricPlan electricPlan) {
		this.id = electricPlan.getId();
		this.electricalInfra = electricPlan.isElectricalInfra();
		this.elecricDistribution = electricPlan.isElecricDistribution();
		this.switchingStation = electricPlan.isSwitchingStation();
		this.loadSancation = electricPlan.isLoadSancation();
		this.environmentalClearance = electricPlan.getEnvironmentalClearance();
		this.autoCad = electricPlan.getAutoCad();
		this.verifiedPlan = electricPlan.getVerifiedPlan();
		this.loiNumber = electricPlan.getLoiNumber();
		this.selfCenteredDrawings = electricPlan.getSelfCentredDrawing();
		this.tenantID = electricPlan.getTenantID();
		this.auditDetails = electricPlan.getAuditDetails();
		this.applicationNumber = electricPlan.getApplicationNumber();
		this.businessService = electricPlan.getBusinessService();
		this.action = electricPlan.getAction();
		this.status = electricPlan.getStatus();
		this.comment = electricPlan.getComment();
		this.pdfFormat = electricPlan.getPdfFormat();
		
	
	}
	
	public ElectricPlan toBuilder() {
		return ElectricPlan.builder().id(this.id)
				.electricalInfra(this.electricalInfra)
				.elecricDistribution(this.elecricDistribution)
				.switchingStation(this.switchingStation)
				.loadSancation(this.loadSancation)
				.environmentalClearance(this.environmentalClearance)
				.autoCad(this.autoCad)
				.verifiedPlan(this.verifiedPlan)
				.loiNumber(this.loiNumber)
				.selfCentredDrawing(this.selfCenteredDrawings)
				.tenantID(this.tenantID)
				.auditDetails(this.auditDetails)
				.applicationNumber(this.applicationNumber)
				.businessService(this.businessService)
				.action(this.action)
				.status(this.status)
				.comment(this.comment)
				.pdfFormat(this.pdfFormat).build();
	}
}
