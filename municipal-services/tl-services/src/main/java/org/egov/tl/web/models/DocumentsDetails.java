package org.egov.tl.web.models;

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
	@JsonProperty("Date")
	private String Date;

	@JsonProperty("document")
	private String document;

}
