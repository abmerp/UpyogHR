package org.egov.tl.web.models;

import java.util.Date;
import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LicenseServiceResponseInfo {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo = null;
	
	@JsonProperty("id")
	private Long id;
	
	@JsonProperty("currentVersion")
	private float currentVersion;
	
	@JsonProperty("createdBy")
	private String createdBy;
	
	@JsonProperty("createdDate")
	private Date createdDate;
	
	@JsonProperty("updateddBy")
	private String updateddBy;
	
	@JsonProperty("updatedDate")
	private Date updatedDate;
	
	@JsonProperty("applicationStatus")
	private String applicationStatus;
	
	@JsonProperty("applicationNumber")
	private String applicationNumber;
	
	@JsonProperty("tcpApplicationNumber")
	private String tcpApplicationNumber;
	
	@JsonProperty("businessService")
	private String businessService;
	
	@JsonProperty("dairyNumber")
	private String dairyNumber;
	
	@JsonProperty("caseNumber")
	private String caseNumber;
	
	@JsonProperty("workFlowCode")
	private String workFlowCode;
	
	@JsonProperty("loiNumber")
	private String loiNumber;
	
	@JsonProperty("tcpLoiNumber")
	private String tcpLoiNumber;
	
	@JsonProperty("applicationDate")
	private String applicationDate;
	
	@JsonProperty("LicenseDetails")
	private List<LicenseDetails> newServiceInfoData;
	
	@JsonProperty("edc")
	private String edc;
	
	@JsonProperty("idw")
	private String idw;

}
