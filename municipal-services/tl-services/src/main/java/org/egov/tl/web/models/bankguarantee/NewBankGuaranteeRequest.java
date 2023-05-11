package org.egov.tl.web.models.bankguarantee;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.egov.tl.abm.newservices.entity.NewBankGuarantee;
import org.egov.tl.service.dao.LicenseServiceDao;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.Document;

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
public class NewBankGuaranteeRequest {

	private String id;
	private String loiNumber;
	private String bgNumber;
	private String typeOfBg;
	private String uploadBg;
	private String bankName;
	private BigDecimal amountInFig;
	private String amountInWords;
	//private String consentLetter;
	private String licenseApplied;
	private String validity;
	private String applicationNumber;
	private String tenantId;
	private String status;
	//TODO: add the below fields in NewBankGuarantee class-
	private Object additionalDetails;
	private AuditDetails auditDetails;
	private String action;
	private String comment;
	private List<String> assignee;
	private List<Document> wfDocuments;
	private LicenseServiceDao license;
	
	private String bankGuaranteeStatus;
	// fields for renew(extend)-
	private String licenceNumber;
	private Boolean hardcopySubmitted;
	// fields for release-
	private String fullCertificate;
	private String partialCertificate;
	private Map<String, String> additionalDocuments;
	
	private String hardcopySubmittedDocument;
	private String existingBgNumber;
	private Integer claimPeriod;
	private String originCountry;
	private String tcpSubmissionReceived;
	private String indianBankAdvisedCertificate;
	private String releaseBankGuarantee;
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
	
	
	    @JsonProperty("updateType")
	    private String updateType;
	
		@JsonProperty("releaseCertificate")
		private String releaseCertificate;
		
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
		
		@JsonProperty("anyOtherDocument")
		private String anyOtherDocument;
		
		@JsonProperty("anyOtherDocumentDescription")
		private String anyOtherDocumentDescription;
	
	/******************* realese bank gurentee  start**************************/

	public NewBankGuaranteeRequest(NewBankGuarantee newBankGuarantee) {
		this.id = newBankGuarantee.getId();
		this.loiNumber = newBankGuarantee.getLoiNumber();
		this.bgNumber = newBankGuarantee.getBgNumber();
		this.typeOfBg = newBankGuarantee.getTypeOfBg();
		this.uploadBg = newBankGuarantee.getUploadBg();
		this.bankName = newBankGuarantee.getBankName();
		this.amountInFig = newBankGuarantee.getAmountInFig();
		this.amountInWords = newBankGuarantee.getAmountInWords();
		//this.consentLetter = newBankGuarantee.getConsentLetter();
		this.licenseApplied = newBankGuarantee.getLicenseApplied();
		this.validity = newBankGuarantee.getValidity();
		this.applicationNumber = newBankGuarantee.getApplicationNumber();
		this.status = newBankGuarantee.getStatus();
		this.tenantId = newBankGuarantee.getTenantId();
		this.additionalDetails = newBankGuarantee.getAdditionalDetails();
		this.auditDetails = newBankGuarantee.getAuditDetails();
		this.license = newBankGuarantee.getLicense();
		this.bankGuaranteeStatus = newBankGuarantee.getBankGuaranteeStatus();
		this.licenceNumber = newBankGuarantee.getLicenceNumber();
		this.hardcopySubmitted = newBankGuarantee.getHardcopySubmitted();
		this.fullCertificate = newBankGuarantee.getFullCertificate();
		this.partialCertificate = newBankGuarantee.getPartialCertificate();
		this.additionalDocuments = newBankGuarantee.getAdditionalDocuments();
		this.hardcopySubmittedDocument = newBankGuarantee.getHardcopySubmittedDocument();
		this.existingBgNumber = newBankGuarantee.getExistingBgNumber();
		this.claimPeriod = newBankGuarantee.getClaimPeriod();
		this.originCountry = newBankGuarantee.getOriginCountry();
		this.tcpSubmissionReceived = newBankGuarantee.getTcpSubmissionReceived();
		this.indianBankAdvisedCertificate = newBankGuarantee.getIndianBankAdvisedCertificate();
		this.releaseBankGuarantee = newBankGuarantee.getReleaseBankGuarantee();
		this.businessService = newBankGuarantee.getBusinessService();
		
		this.releaseCertificate=newBankGuarantee.getReleaseCertificate();
		this.bankGuaranteeReplacedWith=newBankGuarantee.getBankGuaranteeReplacedWith();
		this.reasonForReplacement=newBankGuarantee.getReasonForReplacement();
		this.applicationCerficifate=newBankGuarantee.getApplicationCerficifate();
		this.applicationCerficifateDescription=newBankGuarantee.getApplicationCerficifateDescription();
		this.completionCertificate=newBankGuarantee.getCompletionCertificate();
		this.completionCertificateDescription=newBankGuarantee.getCompletionCertificateDescription();
		this.anyOtherDocument=newBankGuarantee.getAnyOtherDocument();
		this.anyOtherDocumentDescription=newBankGuarantee.getAnyOtherDocumentDescription();
		
		/*
		this.mortgageKhasraDetails = newBankGuarantee.getMortgageKhasraDetails();
		this.totalKhasraAreaToMortgage = newBankGuarantee.getTotalKhasraAreaToMortgage();
		this.mortgagePlotDetails = newBankGuarantee.getMortgagePlotDetails();
		this.totalPlotAreaToMortgage = newBankGuarantee.getTotalPlotAreaToMortgage();
		this.mortgageLayoutPlan = newBankGuarantee.getMortgageLayoutPlan();
		this.mortgageDeed = newBankGuarantee.getMortgageDeed();
		this.mortgageLandScheduleAndPlotNumbersDoc = newBankGuarantee.getMortgageLandScheduleAndPlotNumbersDoc();
		this.mortgageDeedAfterBPApproval = newBankGuarantee.getMortgageDeedAfterBPApproval();
		*/

	}
	
	
	public NewBankGuarantee toBuilder() {
		return NewBankGuarantee.builder().id(this.id)
				.loiNumber(this.loiNumber)
				.bgNumber(this.bgNumber)
				.typeOfBg(this.typeOfBg)
				.uploadBg(this.uploadBg)
				.bankName(this.bankName)
				.amountInFig(this.amountInFig)
				.amountInWords(this.amountInWords)
				//.consentLetter(this.consentLetter)
				.licenseApplied(this.licenseApplied)
				.validity(this.validity)
				.applicationNumber(this.applicationNumber)
				.status(this.status)
				.tenantId(this.tenantId)
				.additionalDetails(this.additionalDetails)
				.auditDetails(this.auditDetails)
				.license(this.license)
				.bankGuaranteeStatus(this.bankGuaranteeStatus)
				.licenceNumber(this.licenceNumber)
				.hardcopySubmitted(this.hardcopySubmitted)
				.fullCertificate(this.fullCertificate)
				.partialCertificate(this.partialCertificate)
				.additionalDocuments(this.additionalDocuments)
				.hardcopySubmittedDocument(this.hardcopySubmittedDocument)
				.existingBgNumber(this.existingBgNumber)
				.claimPeriod(this.claimPeriod)
				.originCountry(this.originCountry)
				.tcpSubmissionReceived(this.tcpSubmissionReceived)
				.indianBankAdvisedCertificate(this.indianBankAdvisedCertificate)
				.releaseBankGuarantee(this.releaseBankGuarantee)
				.businessService(this.businessService)
				
				.releaseCertificate(this.releaseCertificate)
				.bankGuaranteeReplacedWith(this.bankGuaranteeReplacedWith)
				.reasonForReplacement(this.reasonForReplacement)
				.applicationCerficifate(this.applicationCerficifate)
				.applicationCerficifateDescription(this.applicationCerficifateDescription)
				.completionCertificate(this.completionCertificate)
				.completionCertificateDescription(this.completionCertificateDescription)
				.anyOtherDocument(this.anyOtherDocument)
				.anyOtherDocumentDescription(this.anyOtherDocumentDescription)

				/*
				.mortgageKhasraDetails(this.mortgageKhasraDetails)
				.totalKhasraAreaToMortgage(this.totalKhasraAreaToMortgage)
				.mortgagePlotDetails(this.mortgagePlotDetails)
				.totalPlotAreaToMortgage(this.totalPlotAreaToMortgage)
				.mortgageLayoutPlan(this.mortgageLayoutPlan)
				.mortgageDeed(this.mortgageDeed)
				.mortgageLandScheduleAndPlotNumbersDoc(this.mortgageLandScheduleAndPlotNumbersDoc)
				.mortgageDeedAfterBPApproval(this.mortgageDeedAfterBPApproval)
				*/
				
				.build();
	}
}
