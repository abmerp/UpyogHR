package org.egov.tl.abm.newservices.contract;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.web.models.Performa;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerformaContract {
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	@JsonProperty("Performa")
	private List<Performa> performa;

}
