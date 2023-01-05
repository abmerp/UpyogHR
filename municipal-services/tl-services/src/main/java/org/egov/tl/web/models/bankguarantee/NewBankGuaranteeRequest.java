package org.egov.tl.web.models.bankguarantee;

import java.util.List;

import org.egov.tl.abm.newservices.entity.NewBankGuarantee;
import org.egov.tl.web.models.AuditDetails;

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
	private String memoNumber;
	private String typeOfBg;
	private String uploadBg;
	private String bankName;
	private String amountInFig;
	private String amountInWords;
	private String consentLetter;
	private String licenseApplied;
	private String validity;
	private String applicationNumber;
	private String tenantId;
	private String status;
	//TODO: add the below fields in NewBankGuarantee class-
	private Object additionalDetails;
	private AuditDetails auditDetails;
	private String workflowAction;
	private String workflowComment;
	private List<String> workflowAssignee;

	public NewBankGuaranteeRequest(NewBankGuarantee newBankGuarantee) {
		this.id = newBankGuarantee.getId();
		this.loiNumber = newBankGuarantee.getLoiNumber();
		this.memoNumber = newBankGuarantee.getMemoNumber();
		this.typeOfBg = newBankGuarantee.getTypeOfBg();
		this.uploadBg = newBankGuarantee.getUploadBg();
		this.bankName = newBankGuarantee.getBankName();
		this.amountInFig = newBankGuarantee.getAmountInFig();
		this.amountInWords = newBankGuarantee.getAmountInWords();
		this.consentLetter = newBankGuarantee.getConsentLetter();
		this.licenseApplied = newBankGuarantee.getLicenseApplied();
		this.validity = newBankGuarantee.getValidity();
		this.applicationNumber = newBankGuarantee.getApplicationNumber();
		this.status = newBankGuarantee.getStatus();
		this.tenantId = newBankGuarantee.getTenantId();
		this.additionalDetails = newBankGuarantee.getAdditionalDetails();
		this.auditDetails = newBankGuarantee.getAuditDetails();

	}
	
	
	public NewBankGuarantee toBuilder() {
		return NewBankGuarantee.builder().id(this.id)
				.loiNumber(this.loiNumber)
				.memoNumber(this.memoNumber)
				.typeOfBg(this.typeOfBg)
				.uploadBg(this.uploadBg)
				.bankName(this.bankName)
				.amountInFig(this.amountInFig)
				.amountInWords(this.amountInWords)
				.consentLetter(this.consentLetter)
				.licenseApplied(this.licenseApplied)
				.validity(this.validity)
				.applicationNumber(this.applicationNumber)
				.status(this.status)
				.tenantId(this.tenantId)
				.additionalDetails(this.additionalDetails)
				.auditDetails(this.auditDetails)
				.build();
	}
}
