package org.egov.land.abm.models;

import org.egov.land.abm.newservices.entity.NewBankGuarantee;

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

	private Long id;
	private String loiNumber;
	private String memoNumber;
	private String typeOfBg;
	private String bankName;
	private String amount;
	private String amountInWords;
	private String validity;

	public NewBankGuaranteeRequest(NewBankGuarantee newBankGuarantee) {
		this.id = newBankGuarantee.getId();
		this.loiNumber = newBankGuarantee.getLoiNumber();
		this.memoNumber = newBankGuarantee.getMemoNumber();
		this.typeOfBg = newBankGuarantee.getTypeOfBg();
		this.bankName = newBankGuarantee.getBankName();
		this.amount = newBankGuarantee.getAmount();
		this.amountInWords = newBankGuarantee.getAmountInWords();
		this.validity = newBankGuarantee.getValidity();

	}
	
	
	public NewBankGuarantee toBuilder() {
		return NewBankGuarantee.builder().id(this.id)
				.loiNumber(this.loiNumber)
				.memoNumber(this.memoNumber)
				.typeOfBg(this.typeOfBg)
				.bankName(this.bankName)
				.amount(this.amount)
				.amountInWords(this.amountInWords)
				.validity(this.validity).build();
	}
}
