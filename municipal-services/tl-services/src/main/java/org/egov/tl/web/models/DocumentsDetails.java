package org.egov.tl.web.models;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentsDetails {
	@JsonProperty("documentName")
	private String documentName;

	private Long createdDate;

	@JsonProperty("document")
	private String document;
	@JsonProperty("applicationSection")
	private String applicationSection;

}
