package org.egov.user.web.contract;

import java.util.Date;
import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.user.abm.developer.contract.Developerdetail;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class DeveloperDetailResponse {
	@JsonProperty("responseInfo")
	ResponseInfo responseInfo;
    
	private Long id;
	private float currentVersion;
	private String createdBy;
	private Date createdDate;
	private String updateddBy;
	private Date updatedDate;
	@JsonProperty("devDetail")
	List<Developerdetail> devDetail;	
}
