package org.egov.land.abm.models;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.land.abm.newservices.entity.RenewBankGuarantee;

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
public class RenewBankGuaranteeResponse {

	private ResponseInfo responseInfo = null;

	private List<RenewBankGuarantee> renewBankGuarantee;
}
