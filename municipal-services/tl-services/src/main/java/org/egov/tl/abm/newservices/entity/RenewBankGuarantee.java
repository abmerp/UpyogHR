package org.egov.tl.abm.newservices.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
//@Entity
//@Table(name="eg_renew_bank_guarantee")
public class RenewBankGuarantee {

	//@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;
	
	//@Column(name = "licence_number", length = 200)
	private String licenceNumber;
	
	//@Column(name = "amount_In_Fig", length = 200)
	private String amountInFig;
	
	//@Column(name = "amount_In_Words", length = 200)
	private String amountInWords;
	
	//@Column(name = "extended_Time", length = 200)
	private String extendedTime;
	
	//@Column(name = "bank_Name", length = 200)
	private String bankName;
	
	//@Column(name = "memo_Number", length = 200)
	private String memoNumber;
	
	//@Column(name = "hard_copy_Submitted", length = 200)
	private String hardcopySubmitted;
	
	//@Column(name = "upload_Bg", length = 200)
	private String uploadBg;
	
	//@Column(name = "validity", length = 200)
	private String validity;
	
	//@Column(name = "consent_Letter", length = 200)
	private String consentLetter;
	
	// @Column(name = "application_number", length = 32)
	private String applicationNumber;

	// @Column(name = "tenantId", length = 16)
	private String tenantId;

	// @Column(name = "status", length = 100)
	private String status;

	private Object additionalDetails;
	private AuditDetails auditDetails;
	private LicenseServiceDao license;
	
}
