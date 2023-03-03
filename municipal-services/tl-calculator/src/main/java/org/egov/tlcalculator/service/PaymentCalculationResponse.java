package org.egov.tlcalculator.service;

import java.math.BigDecimal;
import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCalculationResponse {
	 
    private ResponseInfo responseInfo;
    private List<FeesTypeCalculationDto> feesTypeCalculationDto;
    private BigDecimal totalFee;
//	public BigDecimal getTotalFee() {
//		BigDecimal scrutinyFee = new BigDecimal(0);
//		BigDecimal licenseFeeCharges = new BigDecimal(0);
//		for(FeesTypeCalculationDto feesTypeCalculationDto:feesTypeCalculationDto ) {
//			
//			scrutinyFee = scrutinyFee.add(feesTypeCalculationDto.getScrutinyFeeChargesCal());
//			licenseFeeCharges = licenseFeeCharges.add(feesTypeCalculationDto.getLicenseFeeChargesCal());
//		}
//		this.totalFee = scrutinyFee.add(licenseFeeCharges.multiply(new BigDecimal(0.25)));
//		
//	
//		return this.totalFee;
//	}

}
