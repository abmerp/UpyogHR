package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;

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
public class RevisedPlanRequest {
	
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	
    @JsonProperty("RevisedPlan")
	private RevisedPlan revisedPlan;

}
