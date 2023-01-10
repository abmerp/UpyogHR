package org.egov.tl.web.models.bankguarantee;

import java.util.List;

import org.egov.tl.abm.newservices.entity.RenewBankGuarantee;
import org.egov.tl.service.dao.LicenseServiceDao;
import org.egov.tl.web.models.AuditDetails;

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
public class RenewBankGuaranteeRequest {

	private String id;
	private String licenceNumber;
	private String amountInFig;
	private String amountInWords;
	private String extendedTime;
	private String bankName;
	private String memoNumber;
	private String hardcopySubmitted;
	private String uploadBg;
	private String validity;
	private String consentLetter;
	private String applicationNumber;
	private String tenantId;
	private String status;
	private Object additionalDetails;
	private AuditDetails auditDetails;
	private String workflowAction;
	private String workflowComment;
	private List<String> workflowAssignee;
	private LicenseServiceDao license;

	
	public RenewBankGuaranteeRequest(RenewBankGuarantee renewBankGuarantee) {
		this.id = renewBankGuarantee.getId();
		this.licenceNumber = renewBankGuarantee.getLicenceNumber();
		this.amountInFig = renewBankGuarantee.getAmountInFig();
		this.amountInWords = renewBankGuarantee.getAmountInWords();
		this.extendedTime = renewBankGuarantee.getExtendedTime();
		this.bankName = renewBankGuarantee.getBankName();
		this.memoNumber = renewBankGuarantee.getMemoNumber();
		this.hardcopySubmitted = renewBankGuarantee.getHardcopySubmitted();
		this.uploadBg = renewBankGuarantee.getUploadBg();
		this.validity = renewBankGuarantee.getValidity();
		this.consentLetter = renewBankGuarantee.getConsentLetter();
		
	}
	
	public RenewBankGuarantee toBuilder() {
		return RenewBankGuarantee.builder().id(this.id)
				.licenceNumber(this.licenceNumber)
				.amountInFig(this.amountInFig)
				.amountInWords(this.amountInWords)
				.extendedTime(this.extendedTime)
				.bankName(this.bankName)
				.memoNumber(this.memoNumber)
				.hardcopySubmitted(this.hardcopySubmitted)
				.uploadBg(this.uploadBg)
				.validity(this.validity)
				.consentLetter(this.consentLetter).build();
	}
	
}
