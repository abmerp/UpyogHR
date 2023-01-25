package org.egov.tl.web.models;

import org.egov.common.contract.response.ResponseInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuranteeCalculatorResponse {

	private ResponseInfo responseInfo;
	private String bankGuaranteeForEDC;
	private String bankGuaranteeForIDW;

}
