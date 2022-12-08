package org.egov.land.abm.models;

import javax.persistence.Column;

import org.egov.land.abm.newservices.entity.ReleaseBankGuarantee;

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
public class ReleaseBankGuaranteeRequest {

	private Long id;
	private String licenseNumber;
	private String idcOrEdc;
	private String fullOrPartial;
	
	public ReleaseBankGuaranteeRequest(ReleaseBankGuarantee releaseBankGuarantee) {
		
		this.id = releaseBankGuarantee.getId();
		this.licenseNumber = releaseBankGuarantee.getLicenseNumber();
		this.idcOrEdc = releaseBankGuarantee.getIdcOrEdc();
		this.fullOrPartial = releaseBankGuarantee.getFullOrPartial();
	}
	
	public ReleaseBankGuarantee toBuilder() {
		return ReleaseBankGuarantee.builder().id(this.id)
				.licenseNumber(this.licenseNumber)
				.idcOrEdc(this.idcOrEdc)
				.fullOrPartial(this.fullOrPartial).build();
	}
}
