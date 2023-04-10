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
public class SurrendOfLicenseRequest {
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	
    @JsonProperty("SurrendOfLicense")
	private List<SurrendOfLicense> surrendOfLicense;

}
