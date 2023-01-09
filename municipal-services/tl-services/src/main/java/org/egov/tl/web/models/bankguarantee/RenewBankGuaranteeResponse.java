package org.egov.tl.web.models.bankguarantee;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.tl.abm.newservices.entity.RenewBankGuarantee;

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
