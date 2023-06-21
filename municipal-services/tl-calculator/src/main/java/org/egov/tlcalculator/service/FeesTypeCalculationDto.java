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

	public BigDecimal getTotalScruitnyFee() {
		if (totalScruitnyFee != null)
			return totalScruitnyFee.setScale(0, BigDecimal.ROUND_UP);
		else
			return totalScruitnyFee;
	}

	public BigDecimal getTotalLicenceFee() {
		if (totalLicenceFee != null)
			return totalLicenceFee.setScale(0, BigDecimal.ROUND_UP);
		else
			return totalLicenceFee;
	}

	private BigDecimal licenseFeeChargesCal;
	private BigDecimal conversionChargesCal;
	private BigDecimal externalDevelopmentChargesCal;
	private BigDecimal stateInfrastructureDevelopmentChargesCal;
	private BigDecimal totalFee;
	private List<FeesTypeCalculationDto> feesTypeCalculationDto;
	private BigDecimal totalScruitnyFee;
	private BigDecimal totalLicenceFee;
	private String scrutinyFormula;
	private String conversionFormula;
	private String stateInfraFormula;
	private String licenceFormula;
	private String edcFormula;

	public String getScrutinyFormula() {
		return scrutinyFormula;
	}

	public String getConversionFormula() {
		return conversionFormula;
	}

	public String getStateInfraFormula() {
		return stateInfraFormula;
	}

	public String getLicenceFormula() {
		return licenceFormula;
	}

	public String getEdcFormula() {
		return edcFormula;
	}

}
