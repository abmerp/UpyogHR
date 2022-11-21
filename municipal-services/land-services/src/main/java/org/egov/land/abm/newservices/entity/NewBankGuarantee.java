package org.egov.land.abm.newservices.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


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
@Entity(name="eg_new_bank_guarantee")
public class NewBankGuarantee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "loi_number", length = 100)
	private String loiNumber;
	
	@Column(name = "memo_number", length = 100)
	private String memoNumber;
	
	@Column(name = "type_of_bg", length = 100)
	private String typeOfBg;
	
	@Column(name = "bank_name", length = 100)
	private String bankName;
	
	@Column(name = "amount", length = 100)
	private String amount;
	
	@Column(name = "amount_in_words", length = 200)
	private String amountInWords;

	@Column(name = "validity", length = 100)
	private String validity;
}
