package org.egov.land.abm.models;

import javax.persistence.Column;

import org.egov.land.abm.newservices.entity.ReplaceBankGuarantee;

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
public class ReplaceBankGuaranteeRequest {

	
	private Long id;
	private String licenseNumber;
	private String newMemoNumber;
	private String bankName;
	private String validity;
	private String amount;
	
	public ReplaceBankGuaranteeRequest(ReplaceBankGuarantee replaceBankGuarantee) {
		this.id = replaceBankGuarantee.getId();
		this.licenseNumber = replaceBankGuarantee.getLicenseNumber();
		this.newMemoNumber = replaceBankGuarantee.getNewMemoNumber();
		this.bankName = replaceBankGuarantee.getBankName();
		this.validity = replaceBankGuarantee.getValidity();
		this.amount = replaceBankGuarantee.getAmount();
	}
	
	public ReplaceBankGuarantee toBuilder() {
		return ReplaceBankGuarantee.builder().id(this.id)
				.licenseNumber(this.licenseNumber)
				.newMemoNumber(this.newMemoNumber)
				.bankName(this.bankName)
				.validity(this.validity)
				.amount(this.amount).build();
	}
	
}
