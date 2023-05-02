package org.egov.tl.web.models;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;

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
public class RealeseBankGurenteeResponse {
	
	@JsonProperty("ResponseInfo")
	private RequestInfo requestInfo = null;
	
	@JsonProperty("releaseBankGuarantee")
	private List<?> releaseBankGuarantee;
	
	@JsonProperty("message")
	private String message;
	
	@JsonProperty("status")
	private boolean status;


}
