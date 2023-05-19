package org.egov.tl.abm.newservices.entity;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PerformaScruitny {
	private String id;
	private String applicationNumber;
	private String applicationStatus;
	private String userName;
	private String userId;
	private String designation;
	private String createdOn;
	private JsonNode additionalDetails;
}
