package org.egov.tl.abm.newservices.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.egov.tl.web.models.AuditDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ApprovalStandardEntity {

	@JsonProperty("licenseNo")
	private String licenseNo;
	@JsonProperty("plan")
	private String plan;
	@JsonProperty("otherDocument")
	private String otherDocument;
	@JsonProperty("amount")
	private BigDecimal amount;
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
}
