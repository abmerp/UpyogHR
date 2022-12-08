package org.egov.land.abm.models;


import org.egov.land.abm.newservices.entity.RenewBankGuarantee;

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

	private Long id;
	private String licenceNumber;
	private String extendBy;
	
	public RenewBankGuaranteeRequest(RenewBankGuarantee renewBankGuarantee) {
		this.id = renewBankGuarantee.getId();
		this.licenceNumber = renewBankGuarantee.getLicenceNumber();
		this.extendBy = renewBankGuarantee.getExtendBy();
	}
	
	public RenewBankGuarantee toBuilder() {
		return RenewBankGuarantee.builder().id(this.id)
				.licenceNumber(this.licenceNumber)
				.extendBy(this.extendBy).build();
	}
	
}
