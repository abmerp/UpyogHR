package org.egov.land.abm.contract;

import org.egov.common.contract.request.RequestInfo;
import org.egov.land.abm.models.ReleaseBankGuaranteeRequest;
import org.egov.land.abm.newservices.entity.ReleaseBankGuarantee;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseBankGuaranteeContract {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo = null;
	
	@JsonProperty("ReleaseBankGuarantee")
	private ReleaseBankGuaranteeRequest releaseBankGuaranteeRequest;
	
}
