package org.egov.tl.web.models;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.egov.tl.web.models.RenewalLicense.ApplicationTypeEnum;
import org.egov.tl.web.models.RenewalLicense.LicenseTypeEnum;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//@Entity
//@Table(name="eg_tl_release_bank_guarantee")
//@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReleaseBankGuarantee {
	
//	@JsonProperty("id")
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private int id;

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("bankGuaranteeId")
	private String bankGuaranteeId;
	
	@JsonProperty("bankGuaranteeNumber")
	private String bankGuaranteeNumber;
	
	@JsonProperty("bankGuaranteeIssueDate")
	private String bankGuaranteeIssueDate;
	
	@JsonProperty("expiryDate")
	private String expiryDate;
	
	@JsonProperty("claimExpiryDate")
	private String claimExpiryDate;
	
	@JsonProperty("bgAmount")
	private String bgAmount;
	
	@JsonProperty("release")
	private String release;
	
	@JsonProperty("bankGuaranteeReplacedWith")
	private String bankGuaranteeReplacedWith;
	
	@JsonProperty("reasonForReplacement")
	private String reasonForReplacement;

	@JsonProperty("applicationCerficifate")
	private String applicationCerficifate;
	
	@JsonProperty("applicationCerficifateDescription")
	private String applicationCerficifateDescription;
	
	@JsonProperty("completionCertificate")
	private String completionCertificate;
	
	@JsonProperty("completionCertificateDescription")
	private String completionCertificateDescription;
	
	@JsonProperty("anyOtherDocument ")
	private String anyOtherDocument;
	
	@JsonProperty("anyOtherDocumentDescription")
	private String anyOtherDocumentDescription;
	
	@JsonProperty("createdAt")
	private Timestamp createdAt;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails; 
	
	@JsonProperty("applicationStatus")
	private int applicationStatus;
	
	
	
	

}
