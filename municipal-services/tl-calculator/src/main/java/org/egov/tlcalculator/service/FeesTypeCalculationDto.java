package org.egov.tlcalculator.service;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class FeesTypeCalculationDto {
	private double totalFee;
	private double scrutinyFeeChargesCal;
	private double licenseFeeChargesCal;
	private double conversionChargesCal;
	private double externalDevelopmentChargesCal;
	private double stateInfrastructureDevelopmentChargesCal;

	public double getTotalFee() {
		this.totalFee = this.scrutinyFeeChargesCal + this.conversionChargesCal + this.licenseFeeChargesCal
				+ this.externalDevelopmentChargesCal + this.stateInfrastructureDevelopmentChargesCal;
		return this.totalFee;
	}

}
