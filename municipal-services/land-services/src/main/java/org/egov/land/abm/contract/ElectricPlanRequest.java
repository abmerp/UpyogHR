package org.egov.land.abm.contract;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.egov.land.abm.newservices.entity.ElectricPlan;

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
				.selfCentredDrawing(this.selfCenteredDrawings).build();
	}
}
