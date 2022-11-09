package org.egov.lndcalculator.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeesTypeCalculationDto {

	private double scrutinyFeeChargesCal;
	private double licenseFeeChargesCal;
	private double conversionChargesCal;
	private double externalDevelopmentChargesCal;
	private double stateInfrastructureDevelopmentChargesCal;

}
