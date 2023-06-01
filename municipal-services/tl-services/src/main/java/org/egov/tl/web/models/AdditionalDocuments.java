package org.egov.tl.web.models;

import java.util.List;

import org.egov.tl.web.models.AuditDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalDocuments {
	@JsonProperty("id")
	private String id;

	@JsonProperty("licenceNumber")
	private String licenceNumber;
	
//	@JsonProperty("type")
//	private String type;
	@JsonProperty("userName")
	private String userName;
	@JsonProperty("developerName")
	private String developerName;
	@JsonProperty("businessService")
	private String businessService;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	@JsonProperty("additionalDetails")
	private Object additionalDetails = null;
	@JsonProperty("DocumentsDetails")
	private List<DocumentsDetails> documentsDetails;

}
