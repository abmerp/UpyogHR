package org.egov.tl.web.models;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.tl.abm.newservices.contract.AdditionalDocumentResponse;

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
public class AdditionalDocumentReportResponse {
	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo = null;

	@JsonProperty("AdditionalDocumentReport")
	private List<AdditionalDocumentReport> additionalDocumentReport;
}
