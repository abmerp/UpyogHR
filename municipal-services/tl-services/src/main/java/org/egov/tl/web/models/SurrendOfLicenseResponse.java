package org.egov.tl.web.models;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

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
public class SurrendOfLicenseResponse {
	
	 private ResponseInfo responseInfo = null;
		private List<SurrendOfLicense> surrendOfLicense;

}
