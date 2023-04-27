package org.egov.tl.abm.newservices.contract;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.newservices.entity.ApprovalStandardEntity;
import org.egov.tl.web.models.LicenseDetails;
import org.egov.tl.web.models.LicenseServiceRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class ApprovalStandardContract {
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	@JsonProperty("ApprovalStandardEntity")
	private ApprovalStandardEntity approvalStandardRequest;

}
