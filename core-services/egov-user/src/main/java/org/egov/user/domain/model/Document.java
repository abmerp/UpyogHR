package org.egov.user.domain.model;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-09-18T17:06:11.263+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Document {

	@Size(max = 64)
	@SafeHtml
	@JsonProperty("id")
	private String id;

	@JsonProperty("active")
	private Boolean active;

	@Size(max = 64)
	@SafeHtml
	@JsonProperty("tenantId")
	private String tenantId = null;

	@Size(max = 64)
	@SafeHtml
	@JsonProperty("documentType")
	private String documentType = null;

	@Size(max = 64)
	@SafeHtml
	@JsonProperty("fileStoreId")
	private String fileStoreId = null;

	@Size(max = 64)
	@SafeHtml
	@JsonProperty("documentUid")
	private String documentUid;


}
