package org.egov.land.abm.contract;

import org.egov.common.contract.request.RequestInfo;
import org.egov.land.abm.models.ReplaceBankGuaranteeRequest;
import org.egov.land.abm.newservices.entity.ReplaceBankGuarantee;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplaceBankGuaranteeContract {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo = null;
	
	@JsonProperty("ReplaceBankGuaranteeRequest")
	private ReplaceBankGuaranteeRequest replaceBankGuaranteeRequest;
}
