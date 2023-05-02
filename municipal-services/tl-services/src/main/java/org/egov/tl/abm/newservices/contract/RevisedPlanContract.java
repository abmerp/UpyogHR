package org.egov.tl.abm.newservices.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.web.models.RevisedPlan;

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
public class RevisedPlanContract {
	
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	
    @JsonProperty("RevisedPlan")
	private List<RevisedPlan> revisedPlan;

}
