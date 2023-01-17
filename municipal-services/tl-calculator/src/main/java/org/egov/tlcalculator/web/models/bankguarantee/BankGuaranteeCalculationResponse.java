package org.egov.tlcalculator.web.models.bankguarantee;

import java.math.BigDecimal;

import org.egov.common.contract.response.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BankGuaranteeCalculationResponse {

	private ResponseInfo responseInfo = null;
	private BigDecimal bankGuaranteeForEDC;
	private BigDecimal bankGuaranteeForIDW;
}
