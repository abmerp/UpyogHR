package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Performa {
	@JsonProperty("id")
	private String id;
	@JsonProperty("applicationNumber")
	private String applicationNumber;
	@JsonProperty("businessService")
	private String businessService;
	@JsonProperty("tenantId")
	private String tenantId;
	@JsonProperty("additionalDetail")
	private JsonNode additionalDetail = null;
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
}
