package org.egov.tl.web.models.bankguarantee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankGuaranteeSearchCriteria {

	@JsonProperty("applicationNumber")
	private List<String> applicationNumber;

	@JsonProperty("loiNumber")
	private String loiNumber;

	@JsonProperty("typeOfBg")
	private String typeOfBg;
	
	@JsonProperty("bgNumber")
	private String bgNumber;
	
	@JsonProperty("existingBgNumber")
	private String existingBgNumber;
	
	@JsonProperty("bankName")
	private String bankName;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("status")
	private List<String> status;

	@JsonProperty("ids")
	private List<String> ids;

	@JsonProperty("fromDate")
	private Long fromDate = null;

	@JsonProperty("toDate")
	private Long toDate = null;

	@JsonProperty("offset")
	private Integer offset;

	@JsonProperty("limit")
	private Integer limit;
	@JsonProperty("licenceNumber")
	private String licenceNumber;
}
