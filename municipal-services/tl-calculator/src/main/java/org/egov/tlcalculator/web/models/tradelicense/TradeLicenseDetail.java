package org.egov.tlcalculator.web.models.tradelicense;

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

import org.egov.tlcalculator.web.models.AuditDetails;
import org.egov.tlcalculator.web.models.Document;
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
public class TradeLicenseDetail   {

	@JsonProperty("id")
	@SafeHtml
	@Size(max = 64)
	private String id;

	@JsonProperty("owners")

	private List<OwnerInfo> owners = new ArrayList<>();

	/**
	 * License can be created from different channels
	 */
	public enum ChannelEnum {
		COUNTER("COUNTER"),

		CITIZEN("CITIZEN"),

		DATAENTRY("DATAENTRY");

		private String value;

		ChannelEnum(String value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public String toString() {
			return String.valueOf(value);
		}

		@JsonCreator
		public static ChannelEnum fromValue(String text) {
			for (ChannelEnum b : ChannelEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}
	}

	@JsonProperty("channel")
	private ChannelEnum channel = null;

	@Size(max = 64)
	@SafeHtml
	@JsonProperty("tradeType")
	private String tradeType = null;

	@JsonProperty("applicationDocuments")
	@Valid
	private List<Document> applicationDocuments = null;

	@JsonProperty("verificationDocuments")
	@Valid
	private List<Document> verificationDocuments = null;

	@JsonProperty("additionalDetail")
	private JsonNode additionalDetail = null;

	@JsonProperty("currentVersion")
	private float currentVersion;

	@JsonProperty("scrutinyFeeCharges")
	private BigDecimal scrutinyFeeCharges;
	
	@JsonProperty("conversionCharges")
	private BigDecimal conversionCharges;
	@JsonProperty("externalDevelopmentCharges")
	private BigDecimal externalDevelopmentCharges;
	@JsonProperty("stateInfrastructureDevelopmentCharges")
	private BigDecimal stateInfrastructureDevelopmentCharges;
	@JsonProperty("licenseFeeCharges")
	private BigDecimal licenseFeeCharges;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

	public TradeLicenseDetail addOwnersItem(OwnerInfo ownersItem) {
		if (this.owners == null)
			this.owners = new ArrayList<>();
		if (!this.owners.contains(ownersItem))
			this.owners.add(ownersItem);
		return this;
	}

	public TradeLicenseDetail addApplicationDocumentsItem(Document applicationDocumentsItem) {
		if (this.applicationDocuments == null) {
			this.applicationDocuments = new ArrayList<>();
		}
		if (!this.applicationDocuments.contains(applicationDocumentsItem))
			this.applicationDocuments.add(applicationDocumentsItem);
		return this;
	}

	public TradeLicenseDetail addVerificationDocumentsItem(Document verificationDocumentsItem) {
		if (this.verificationDocuments == null) {
			this.verificationDocuments = new ArrayList<>();
		}
		if (!this.verificationDocuments.contains(verificationDocumentsItem))
			this.verificationDocuments.add(verificationDocumentsItem);
		return this;
	}

}

