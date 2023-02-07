package org.egov.tlcalculator.service;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PaymentCalculationResponse {
	
	private BigDecimal totalFeeComm;
	private BigDecimal totalFeeGH;
	private BigDecimal totalArea;

}
