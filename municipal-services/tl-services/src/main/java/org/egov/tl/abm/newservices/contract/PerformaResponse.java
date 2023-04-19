package org.egov.tl.abm.newservices.contract;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.tl.web.models.Performa;

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
public class PerformaResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("Performa")
	private List<Performa> performa;

}
