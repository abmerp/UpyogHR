package org.egov.tl.abm.newservices.contract;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.web.models.bankguarantee.RenewBankGuaranteeRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RenewBankGuaranteeContract {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo = null;
	
	
	@JsonProperty("RenewBankGuaranteeRequest")
	RenewBankGuaranteeRequest renewBankGuaranteeRequest;
}
