package org.egov.tl.abm.newservices.contract;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.tl.web.models.ComplianceRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplianceResponse {
	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo = null;
	@JsonProperty("ComplianceRequest")
	private List<ComplianceRequest> complianceRequest;
}
