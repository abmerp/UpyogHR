package org.egov.tlcalculator.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
class FeesTypeCalculationDto {

	private String purpose;
	private BigDecimal scrutinyFeeChargesCal;

	public String getPurpose() {
		return purpose;
	}

	public BigDecimal getScrutinyFeeChargesCal() {
		if (scrutinyFeeChargesCal != null)
			return scrutinyFeeChargesCal.setScale(0, BigDecimal.ROUND_UP);
		else
			return scrutinyFeeChargesCal;
	}

	public BigDecimal getLicenseFeeChargesCal() {
		if (licenseFeeChargesCal != null)
			return licenseFeeChargesCal.setScale(0, BigDecimal.ROUND_UP);
		else

			return licenseFeeChargesCal;
	}

	public BigDecimal getConversionChargesCal() {
		if (conversionChargesCal != null)
			return conversionChargesCal.setScale(0, BigDecimal.ROUND_UP);
		else
			return conversionChargesCal;
	}

	public BigDecimal getExternalDevelopmentChargesCal() {
		if (externalDevelopmentChargesCal != null)
			return externalDevelopmentChargesCal.setScale(0, BigDecimal.ROUND_UP);
		else
			return externalDevelopmentChargesCal;
	}

	public BigDecimal getStateInfrastructureDevelopmentChargesCal() {
		if (stateInfrastructureDevelopmentChargesCal != null)
			return stateInfrastructureDevelopmentChargesCal.setScale(0, BigDecimal.ROUND_UP);
		else
			return stateInfrastructureDevelopmentChargesCal;
	}

	public BigDecimal getTotalFee() {
		if (totalFee != null)
			return totalFee.setScale(0, BigDecimal.ROUND_UP);
		else
			return totalFee;
	}

	public List<FeesTypeCalculationDto> getFeesTypeCalculationDto() {
		return feesTypeCalculationDto;
	}

	private BigDecimal licenseFeeChargesCal;
	private BigDecimal conversionChargesCal;
	private BigDecimal externalDevelopmentChargesCal;
	private BigDecimal stateInfrastructureDevelopmentChargesCal;
	private BigDecimal totalFee;
	private List<FeesTypeCalculationDto> feesTypeCalculationDto;

}
