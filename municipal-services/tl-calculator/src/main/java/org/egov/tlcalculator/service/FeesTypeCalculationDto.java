package org.egov.tlcalculator.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class FeesTypeCalculationDto {

	private String purpose;
	private BigDecimal scrutinyFeeChargesCal;
	private BigDecimal licenseFeeChargesCal;
	private BigDecimal conversionChargesCal;
	private BigDecimal externalDevelopmentChargesCal;
	private BigDecimal stateInfrastructureDevelopmentChargesCal;
	private List<FeesTypeCalculationDto> feesTypeCalculationDto;

}
