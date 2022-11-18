package org.egov.tl.web.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeesAndCharges {

	private String totalArea;
	private String purpose;
	private String devPlan;
	private String scrutinyFee;
	private String licenseFee;
	private String conversionCharges;
	private String payableNow;
	private String remark;
	private String adjustFee;
	private List<Document> docuemnts;
}
