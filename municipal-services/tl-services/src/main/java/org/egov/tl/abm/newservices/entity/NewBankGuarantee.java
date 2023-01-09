package org.egov.tl.abm.newservices.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.egov.tl.service.dao.LicenseServiceDao;
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
//@Entity(name="eg_new_bank_guarantee")
public class NewBankGuarantee {

	//@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;
	
	//@Column(name = "loi_number", length = 100)
	private String loiNumber;
	
	//@Column(name = "memo_number", length = 100)
	private String memoNumber;
	
	//@Column(name = "type_of_bg", length = 100)
	private String typeOfBg;
	
	//@Column(name = "upload_Bg", length = 100)
	private String uploadBg;
	
	//@Column(name = "bank_name", length = 100)
	private String bankName;
	
	//@Column(name = "amount_In_Fig", length = 100)
	private String amountInFig;
	
	//@Column(name = "amount_in_words", length = 200)
	private String amountInWords;
	
	//@Column(name = "consent_Letter", length = 200)
	private String consentLetter;
	
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
	
	private Object additionalDetails;
	private AuditDetails auditDetails;
	private LicenseServiceDao license;
}
