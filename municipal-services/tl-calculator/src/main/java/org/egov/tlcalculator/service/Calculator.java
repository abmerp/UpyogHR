package org.egov.tlcalculator.service;

import java.math.BigDecimal;

public interface Calculator {

	public BigDecimal AREA = new BigDecimal("4047");
	public BigDecimal PERCENTAGE1 = new BigDecimal(0.96);
	public BigDecimal PERCENTAGE2 = new BigDecimal(0.04);
	public BigDecimal RATE = new BigDecimal(1250000);
	public BigDecimal RATE1 = new BigDecimal(34000000);
	public BigDecimal PART1 =new BigDecimal( 0.8);
	public BigDecimal PART2 = new BigDecimal(0.15);
	public BigDecimal PART3 = new BigDecimal(0.05);

	String PURPOSE_AGH = "AGH";
	String PURPOSE_DDJAY_APHP = "DDJAY_APHP";
	String PURPOSE_CICS = "CICS";
	String PURPOSE_CIRS = "CIRS";
	String PURPOSE_CPCS = "CPCS";
	String PURPOSE_CPRS = "CPRS";
	String PURPOSE_IPULP = "IPULP";
	String PURPOSE_IPA = "IPA";
	String PURPOSE_ITC = "ITC";
	String PURPOSE_ITP = "ITP";
	String PURPOSE_LDEF = "LDEF";
	String PURPOSE_MLU_CZ = "MLU-CZ";
	String PURPOSE_NILPC = "NILPC";
	String PURPOSE_NILP = "NILP";
	String PURPOSE_RGP = "RGP";
	String PURPOSE_RPL = "RPL";
	String PURPOSE_RHP = "RHP";
	String PURPOSE_TODCOMM = "TODCOMM";
	String PURPOSE_TODIT = "TODIT";
	String PURPOSE_TODGH = "TODGH";
	String PURPOSE_TODMUD = "TODMUD";
	String PURPOSE_TODMGH = "TODMGH";

//	String ZONE_HYPER="HYP";
//	String ZONE_HIG1="HIG1";
//	String ZONE_HIG2="HIG2";
//	String ZONE_LOW = "LOW";
//	String ZONE_LOW2="LOW2";

	String ZONE_HYPER = "Hyper";
	String ZONE_HIG1 = "High";
	String ZONE_HIG2 = "High Potential";
	String ZONE_MDM = "Medium";
	String ZONE_LOW = "Low";
	String ZONE_LOW2 = "Low Potential";

}
