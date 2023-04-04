package org.egov.tl.web.models;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.egov.tl.web.models.Accessory;
import org.egov.tl.web.models.Address;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.Document;
import org.egov.tl.web.models.OwnerInfo;
import org.egov.tl.web.models.TradeUnit;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * A Object holds the basic data for a Trade License
 */
@ApiModel(description = "A Object holds the basic data for a Trade License")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-09-18T17:06:11.263+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RenewalLicenseDetail {

	@JsonProperty("id")
	@SafeHtml
	@Size(max = 64)
	private String id;
	
	@JsonProperty("renewalType")
	private String renewalType;
	
	@JsonProperty("renewllicenseId")
	private String renewllicenseId;

	@JsonProperty("additionalDetail")
	private JsonNode additionalDetail = null;

	@JsonProperty("currentVersion")
	private float currentVersion;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	
	@JsonProperty("renewalAmount")
	private String renewalAmount;
	
	@JsonProperty("penaltyPayable")
	private String penaltyPayable;

}
