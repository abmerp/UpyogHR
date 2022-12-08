package org.egov.land.abm.newservices.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.egov.common.contract.request.RequestInfo;
import org.egov.land.abm.models.RenewBankGuaranteeRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="eg_replace_bank_guarantee")
public class ReplaceBankGuarantee {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="license_number")
	private String licenseNumber;
	
	@Column(name="new_memo_number")
	private String newMemoNumber;
	
	@Column(name="bank_name")
	private String bankName;
	
	@Column(name="validity")
	private String validity;
	
	@Column(name="amount")
	private String amount;
	
	
}
