package org.egov.user.web.contract;

import java.util.Date;

import org.egov.common.contract.request.RequestInfo;
import org.egov.user.abm.developer.contract.Developerdetail;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;	

@Setter
@Getter
@Builder	
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	private Long id;
	private float currentVersion;
	private String createdBy;
	private Date createdDate;
	private String updateddBy;
	private Date updatedDate;
	private String pageName;
	
	@JsonProperty("devDetail")
	private Developerdetail devDetail;
	
}
