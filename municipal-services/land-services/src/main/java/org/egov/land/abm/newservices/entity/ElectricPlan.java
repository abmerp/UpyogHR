package org.egov.land.abm.newservices.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name="eg_electric_plan")
public class ElectricPlan {

	@Id
	private Long id;
	private boolean electricalInfra;
	private boolean elecricDistribution;
	private boolean electricalCapacity;
	private boolean switchingStation;
	private boolean loadSancation;
	private String environmentalClearance;
	private String autoCad;
	private String verifiedPlan;
	
}
