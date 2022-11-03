package org.egov.pg.service.gateways.nic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionPojo {

	public String UserName;
	public String MobNo;
	public String CaseTypeId;
	public String AppTypeId;
	public String ChargesTypeId;
	public String TxnAmount;
	public String LicenceFeeNla;
	public String ScrutinyFeeNla;
	public String PaymentMode;
	public String PayAgreegator;
	public String LcApplicantName;
	public String LcPurpose;
	public String LcDevelopmentPlan;
	public String LcDistrict;

}
