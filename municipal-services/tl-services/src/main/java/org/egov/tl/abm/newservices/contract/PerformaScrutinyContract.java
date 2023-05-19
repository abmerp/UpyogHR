package org.egov.tl.abm.newservices.contract;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.newservices.entity.PerformaScruitny;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.JsonAdapter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter 
@AllArgsConstructor
@NoArgsConstructor
public class PerformaScrutinyContract {
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	@JsonProperty("PerformaScruitny")
	private PerformaScruitny performaScruitny;

}
