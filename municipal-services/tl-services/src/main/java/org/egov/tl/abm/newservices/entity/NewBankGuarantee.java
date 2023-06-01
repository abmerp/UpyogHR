package org.egov.tl.abm.newservices.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.egov.tl.service.dao.LicenseServiceDao;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.bankguarantee.MortgageKhasraDetails;
import org.egov.tl.web.models.bankguarantee.MortgagePlotDetails;
import org.egov.tl.web.models.bankguarantee.NewBankGuaranteeRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@Entity(name="eg_new_bank_guarantee")
public class NewBankGuarantee {

	//@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;
	
	//@Column(name = "loi_number", length = 100)
	private String loiNumber;
	
	//@Column(name = "memo_number", length = 100)
	//private String memoNumber;
	private String bgNumber;
	
	//@Column(name = "type_of_bg", length = 100)
	private String typeOfBg;
	
	//@Column(name = "upload_Bg", length = 100)
	private String uploadBg;
	
	//@Column(name = "bank_name", length = 100)
	private String bankName;
	
	//@Column(name = "amount_In_Fig", length = 100)
	private BigDecimal amountInFig;
	
	//@Column(name = "amount_in_words", length = 200)
	private String amountInWords;
	
	//@Column(name = "consent_Letter", length = 200)
	//private String consentLetter;
	
	//@Column(name = "license_Applied", length = 200)
	private String licenseApplied;


	//@Column(name = "validity", length = 100)
	private String validity;
	
	//@Column(name = "application_number", length = 32)
	private String applicationNumber;
	
	//@Column(name = "tenantId", length = 16)
	private String tenantId;
	
	//@Column(name = "status", length = 100)
	private String status;
	
	private String bankGuaranteeStatus;
	
	private Object additionalDetails;
	private AuditDetails auditDetails;
	private LicenseServiceDao license;

	//fields for renew(extend)-
	//@Column(name = "licence_number", length = 200)
	private String licenceNumber;
	
	//@Column(name = "hard_copy_Submitted", length = 200)
	private Boolean hardcopySubmitted;
	
	private String hardcopySubmittedDocument;
	
	private String existingBgNumber;
	
	private Integer claimPeriod;
	
	private String originCountry;
	
	private String tcpSubmissionReceived;
	
	private String indianBankAdvisedCertificate;
	
	private String releaseBankGuarantee;
	
	//fields for release-
	//@Column(name="full_Certificate")
	private String fullCertificate;
	
	//@Column(name="partial_Certificate")
	private String partialCertificate;
	
	private Map<String,String> additionalDocuments;
	private List<NewBankGuaranteeRequest> auditEntries = null;
	
	/*
	// mortgage related fields-
	List<MortgageKhasraDetails> mortgageKhasraDetails;
	BigDecimal totalKhasraAreaToMortgage;
	List<MortgagePlotDetails> mortgagePlotDetails;
	BigDecimal totalPlotAreaToMortgage;
	// Documents for mortgage-
	private String mortgageLayoutPlan;
	private String mortgageDeed;
	private String mortgageLandScheduleAndPlotNumbersDoc;
	private String mortgageDeedAfterBPApproval;
	*/
	
	private String businessService;
	private String workflowCode = businessService;
	
	

	
	/******************* realese bank gurentee  start**************************/
		private String release;
		private String bankGuaranteeReplacedWith;
		private String reasonForReplacement;
		private String applicationCerficifate;
		private String applicationCerficifateDescription;
		private String completionCertificate;
		private String completionCertificateDescription;
		private String anyOtherDocument;
		private String anyOtherDocumentDescription;	
	
	/******************* realese bank gurentee  start**************************/
		

	/******************* extend bank gurentee  start**************************/
		private String dateOfAmendment;
		private String amendmentExpiryDate;
		private String amendmentClaimExpiryDate;
		private String issuingBank;	
		private String bankGurenteeCertificate;
		private String bankGurenteeCertificateDescription;
		
	/******************* extend bank gurentee  end**************************/
		

}
