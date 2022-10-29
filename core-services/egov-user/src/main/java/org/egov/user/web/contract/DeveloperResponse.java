package org.egov.user.web.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.user.abm.developer.contract.DevDetail;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
public class DeveloperResponse {
	@JsonProperty("responseInfo")
	ResponseInfo responseInfo;

	private Long id;
	private float currentVersion;
	private String createdBy;
	private Date createdDate;
	private String updateddBy;
	private Date updatedDate;
	@JsonProperty("devDetail")
	DevDetail devDetail;
}
