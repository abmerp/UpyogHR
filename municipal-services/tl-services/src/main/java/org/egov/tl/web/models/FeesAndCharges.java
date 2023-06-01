package org.egov.tl.web.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeesAndCharges {

	private String totalArea;
	private String purpose;
	private String potential;
	private String developmentPlan;
	private String licNumber;
	private String amount;
	private String amountAdjusted;
	private String amountPayable;
	private String scrutinyFee;
	private String licenseFee;
	private String conversionCharges;
	private String payableNow;
	private String remark;
	private String adjustFee;
	private String belongsDeveloper;
	private String consentLetter;
	private String stateInfrastructureDevelopmentCharges;
	private String IDW;
	private String EDC;
	private List<FeesTypeCalculationDto> feesTypeCalculationDto;
}
