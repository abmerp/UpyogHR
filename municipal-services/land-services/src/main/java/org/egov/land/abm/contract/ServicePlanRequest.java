package org.egov.land.abm.contract;

import org.egov.land.abm.newservices.entity.ServicePlan;

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
	
	public ServicePlanRequest(ServicePlan servicePlan) {
		
		this.loiNumber = servicePlan.getLoiNumber();
		this.undertaking = servicePlan.isUndertaking();
		this.selfCertifiedDrawingsFromCharetedEng = servicePlan.isSelfCertifiedDrawingsFromCharetedEng();
		this.selfCertifiedDrawingFromEmpaneledDoc = servicePlan.getSelfCertifiedDrawingFromEmpaneledDoc();
		this.environmentalClearance = servicePlan.getEnvironmentalClearance();
		this.shapeFileAsPerTemplate = servicePlan.getShapeFileAsPerTemplate();
		this.autoCadFile = servicePlan.getAutoCadFile();
		this.certifieadCopyOfThePlan = servicePlan.getCertifieadCopyOfThePlan();
	}
	
	
	public ServicePlan toBuilder() {
		return ServicePlan.builder().loiNumber(this.loiNumber)
														.undertaking(this.undertaking)
														.selfCertifiedDrawingsFromCharetedEng(this.selfCertifiedDrawingsFromCharetedEng)
														.selfCertifiedDrawingFromEmpaneledDoc(this.selfCertifiedDrawingFromEmpaneledDoc)
														.environmentalClearance(this.environmentalClearance)
														.shapeFileAsPerTemplate(this.shapeFileAsPerTemplate)
														.autoCadFile(this.autoCadFile)
														.certifieadCopyOfThePlan(this.certifieadCopyOfThePlan).build();
	}
}
