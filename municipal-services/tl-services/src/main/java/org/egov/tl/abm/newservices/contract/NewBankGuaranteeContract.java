package org.egov.tl.abm.newservices.contract;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.web.models.bankguarantee.NewBankGuaranteeRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewBankGuaranteeContract {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo = null;
	
	
	@JsonProperty("NewBankGuaranteeRequest")
	List<NewBankGuaranteeRequest> newBankGuaranteeRequest;
}
