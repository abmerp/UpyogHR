package org.egov.tlcalculator.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tlcalculator.utils.LandUtil;
import org.egov.tlcalculator.validator.LandMDMSValidator;
import org.egov.tlcalculator.web.models.CalculatorRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalculatorImpl implements Calculator {

	@Autowired
	LandUtil landUtil;
	@Autowired
	LandMDMSValidator valid;
	@Autowired
	Calculator calculator;

	private BigDecimal areaInSqmtr(String acre) {
		return (AREA.multiply(new BigDecimal(acre)));
	}

	private BigDecimal areaInSqmtr(BigDecimal acre) {
		return (AREA.multiply(acre));
	}

	public FeesTypeCalculationDto feesTypeCalculation(RequestInfo requestInfo, CalculatorRequest calculatorRequest) {
		BigDecimal acre = new BigDecimal(calculatorRequest.getTotalLandSize());
		BigDecimal AREA1 = (PERCENTAGE1.multiply(new BigDecimal(calculatorRequest.getTotalLandSize())));
		BigDecimal AREA2 = PERCENTAGE2.multiply(new BigDecimal(calculatorRequest.getTotalLandSize()));
		BigDecimal far = new BigDecimal(1.0);
		if (calculatorRequest.getFar() != null)
			far = new BigDecimal(calculatorRequest.getFar());

		// ----------------------------

		Map<String, List<String>> mdmsData;
		FeesTypeCalculationDto feesTypeCalculationDto = new FeesTypeCalculationDto();
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> mDMSCallPurposeCode = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>) landUtil
				.mDMSCallPurposeCode(requestInfo, requestInfo.getUserInfo().getTenantId(),
						calculatorRequest.getPurposeCode());

		mdmsData = valid.getAttributeValues(mDMSCallPurposeCode);
		List<Map<String, Object>> msp = (List) mdmsData.get("Purpose");
		BigDecimal externalDevelopmentCharges = new BigDecimal(0);
		BigDecimal scrutinyFeeCharges = new BigDecimal(0);
		BigDecimal conversionCharges = new BigDecimal(0);
		BigDecimal licenseFeeCharges = new BigDecimal(0);
		BigDecimal stateInfrastructureDevelopmentCharges = new BigDecimal(0);
		String purposeName = "";
		String active = "";
		scrutinyFeeCharges = far;
		licenseFeeCharges = far;
		conversionCharges = far;
		externalDevelopmentCharges = far;
		stateInfrastructureDevelopmentCharges = far;
		for (Map<String, Object> mm : msp) {
			purposeName = String.valueOf(mm.get("name"));
			active = String.valueOf(mm.get("isActive"));
			// scrutinyFeeCharges = new
			// BigDecimal(String.valueOf(mm.get("scrutinyFeeCharges")));
			// externalDevelopmentCharges = new
			// BigDecimal(String.valueOf(mm.get("externalDevelopmentCharges")));
			// conversionCharges = new
			// BigDecimal(String.valueOf(mm.get("conversionCharges")));
			// licenseFeeCharges = new
			// BigDecimal(String.valueOf(mm.get("licenseFeeCharges")));
//			stateInfrastructureDevelopmentCharges = new BigDecimal(
//					String.valueOf(mm.get("stateInfrastructureDevelopmentCharges")));
		}
		feesTypeCalculationDto.setPurpose(purposeName);
		if (active.equals("1"))
			switch (calculatorRequest.getPotenialZone()) {
//--//----------hyper----------//
			case ZONE_HYPER:

				switch (calculatorRequest.getPurposeCode()) {

				case PURPOSE_RPL:
					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_158));
					feesTypeCalculationDto
							.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(acre).multiply(RATE500));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE + "(Rate)");
					feesTypeCalculationDto
							.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_158 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE5 + "(Rate)" + "(Rate)");

					break;
				case PURPOSE_IPULP:
					break;

				case PURPOSE_ITC:

					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(PERCENTAGE25).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE25));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE334));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_1260));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_250));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + PERCENTAGE25 + "(Multiplier)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE25 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE334 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_1260 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_250 + "(Rate)");

					break;
				case PURPOSE_ITP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(PERCENTAGE25).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE25));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE334));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_1260));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_250));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + PERCENTAGE25 + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE25 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE334 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_1260 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_250
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");

					break;
				case PURPOSE_IPA:

					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE25));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE167));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_100));
					feesTypeCalculationDto
							.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(acre).multiply(RATE_250));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE25 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE167 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_100 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_250
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");

					break;

				case PURPOSE_RGP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));					
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE40).multiply(PERCENTAGE0995));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE4));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_158));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_625));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE40 + "(Rate)"+"*"+PERCENTAGE0995+"(Multiplier)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE4 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_158 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_625
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");

					break;

				case PURPOSE_DDJAY_APHP:

					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE));

					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104));
					feesTypeCalculationDto
							.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_158));
					feesTypeCalculationDto
							.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(acre).multiply(RATE500));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE + "(Rate)");
					feesTypeCalculationDto
							.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(RATE2 +"(Multiplier)"+ "*" + AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_158 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_500 + "(Rate)");

					break;

				case PURPOSE_NILPC:
					break;

				case PURPOSE_TODGH:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10).multiply(scrutinyFeeCharges));

					feesTypeCalculationDto.setLicenseFeeChargesCal(
							(PERCENTAGE0995.multiply(RATE40).multiply(licenseFeeCharges).divide(PERCENTAGE175, 0)).add(
									RATE1.multiply(PERCENTAGE5).multiply(licenseFeeCharges).divide(PERCENTAGE175, 0)));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(new BigDecimal(0.995).multiply(
							(new BigDecimal(calculatorRequest.getTotalLandSize()).multiply(new BigDecimal(416.385))
									.multiply(new BigDecimal(100000)).multiply(licenseFeeCharges))
									.divide(new BigDecimal(1.75), 0)));
					feesTypeCalculationDto.setConversionChargesCal((new BigDecimal(0.995).multiply(new BigDecimal(158))
							.multiply(AREA).multiply(conversionCharges)).divide(new BigDecimal(1.75), 0)
							.add(new BigDecimal(0.005).multiply(new BigDecimal(1470)).multiply(AREA)
									.multiply(conversionCharges).divide(new BigDecimal(1.75), 0)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							new BigDecimal(0.995).multiply(new BigDecimal(625)).multiply(AREA)
									.multiply(stateInfrastructureDevelopmentCharges)
									.add(new BigDecimal(0.005).multiply(new BigDecimal(1000)).multiply(AREA)
											.multiply(stateInfrastructureDevelopmentCharges)));

					break;

				case PURPOSE_MLU_CZ:

//				if (maximumPermissible.compareTo(new BigDecimal(70)) > 0
//						|| maximumPermissible.equals(new BigDecimal(100))) {
//
//					feesTypeCalculationDto.setScrutinyFeeChargesCal(mixArea.multiply(RATE1));
//					feesTypeCalculationDto.setLicenseFeeChargesCal(mixArea.multiply(RATE1));
//					feesTypeCalculationDto.setConversionChargesCal(mixArea.multiply(RATE1));
//				}
//				if(!maximumPermissible.equals(new BigDecimal(100))) {
//					feesTypeCalculationDto.setScrutinyFeeChargesCal(mixArea.multiply(RATE1));
//					feesTypeCalculationDto.setLicenseFeeChargesCal(mixArea.multiply(RATE1));
//					feesTypeCalculationDto.setConversionChargesCal(mixArea.multiply(RATE1));
//				}
					break;

				case PURPOSE_LDEF:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(acre)));

					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE250));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE01));
					feesTypeCalculationDto
							.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_158));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(RATE500).multiply(RATE2));
					feesTypeCalculationDto.setScrutinyFormula(RATE2 + "*" + AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE250 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(
							acre + "(total applied Area in acre)" + "*" + RATE104 + "(Rate)" + "*" + RATE01 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(RATE2 + "*" + AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_158 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_500
									+ "(Rate)" + "*" + RATE2 + "(Multiplier)");
					break;

				case PURPOSE_NILP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10).multiply(PERCENTAGE125));
					feesTypeCalculationDto
							.setLicenseFeeChargesCal(acre.multiply(RATE40).multiply(RATE5).divide(RATE7, 0));

					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE467));
					feesTypeCalculationDto
							.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_158));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							(areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_625)
									.multiply(RATE5)).divide(RATE7, 0));
					feesTypeCalculationDto
							.setScrutinyFormula(PERCENTAGE125 + "(Multiplier)" + "*" + AREA + "(acre to sq. mt)" + "*"
									+ acre + "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto.setLicenceFormula("(" + acre + "(total applied Area in acre)" + "*" + RATE40
							+ "(Rate)" + "*" + RATE5 + "(Rate)" + ")" + "/" + RATE7 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(RATE2 + "(Multiplier)" + "*" + AREA + "(acre to sq. mt)"
							+ "*" + acre + "(total applied Area in acre)" + "*" + RATE_158 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula("(" + AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_625 + "(Rate)" + "*" + RATE5 + "(Rate)" + "*"
							+ stateInfrastructureDevelopmentCharges + "(FAR)" + ")" + "/" + RATE7 + "(Rate)");
					break;
				case PURPOSE_TODCOMM:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10).multiply(scrutinyFeeCharges));
					feesTypeCalculationDto
							.setLicenseFeeChargesCal((RATE1).multiply(licenseFeeCharges).divide(PERCENTAGE175, 0));
//				feesTypeCalculationDto.setScrutinyFeeChargesCal(totalFar.multiply(RATE1));
//				feesTypeCalculationDto.setLicenseFeeChargesCal(totalFar.multiply(RATE1));
//				feesTypeCalculationDto.setConversionChargesCal(totalFar.multiply(RATE1));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							(new BigDecimal(calculatorRequest.getTotalLandSize()).multiply(new BigDecimal(486.13))
									.multiply(new BigDecimal(100000)).multiply(externalDevelopmentCharges))
									.divide(new BigDecimal(1.75), 0));
					feesTypeCalculationDto
							.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_158));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							(RATE_1000).multiply(areaInSqmtr(acre)).multiply(stateInfrastructureDevelopmentCharges));

					break;
				case PURPOSE_TODIT:
					break;
				case PURPOSE_TODMUD:
					break;
				case PURPOSE_CPCS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE270.multiply(acre));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE4));

					feesTypeCalculationDto
							.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_1260)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							AREA.multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_1000));
					feesTypeCalculationDto
							.setScrutinyFormula(scrutinyFeeCharges + "(FAR)" + "*" + AREA + "(acre to sq. mt)" + "*"
									+ acre + "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE270 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(
							acre + "(total applied Area in acre)" + "*" + RATE104 + "(Rate)" + "*" + RATE4 + "(Rate)");

					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_1260 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + RATE_1000 + "(Rate)"
							+ "*" + stateInfrastructureDevelopmentCharges + "(FAR)");

					break;

				case PURPOSE_CPRS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE270.multiply(acre));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE4));

					feesTypeCalculationDto
							.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_1260)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							AREA.multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_1000));
					feesTypeCalculationDto
							.setScrutinyFormula(scrutinyFeeCharges + "(FAR)" + "*" + AREA + "(acre to sq. mt)" + "*"
									+ acre + "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE270 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(
							acre + "(total applied Area in acre)" + "*" + RATE104 + "(Rate)" + "*" + RATE4 + "(Rate)");

					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_1260 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + RATE_1000 + "(Rate)"
							+ "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_CICS:

					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE1.multiply(acre));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE467));
					feesTypeCalculationDto
							.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_1260)));

					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_1000));
					feesTypeCalculationDto
							.setScrutinyFormula(scrutinyFeeCharges + "(FAR)" + "*" + AREA + "(acre to sq. mt)" + "*"
									+ acre + "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE1 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_1260 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_1000
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");

					break;
				case PURPOSE_CIRS:

					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE1.multiply(acre));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE467));
					feesTypeCalculationDto
							.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_1260)));

					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_1000));
					feesTypeCalculationDto
							.setScrutinyFormula(scrutinyFeeCharges + "(FAR)" + "*" + AREA + "(acre to sq. mt)" + "*"
									+ acre + "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE1 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_1260 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_1000
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");

					break;
				case PURPOSE_RHP:
					break;

				case PURPOSE_AGH:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_158));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);
					feesTypeCalculationDto
							.setScrutinyFormula(scrutinyFeeCharges + "(FAR)" + "*" + AREA + "(acre to sq. mt)" + "*"
									+ acre + "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE0 + "(Rate)");
					feesTypeCalculationDto
							.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_158 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE0 + "(Rate)");
					break;
				}
				break;
//---------------------------------HIGH 1----------------------------------------------------//
			case ZONE_HIG1: {
				switch (calculatorRequest.getPurposeCode()) {

				case PURPOSE_RPL:
					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));

					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE09));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_125));
					feesTypeCalculationDto
							.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(acre).multiply(RATE375));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(
							acre + "(total applied Area in acre)" + "*" + RATE104 + "(Rate)" + "*" + RATE09 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_125 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE375 + "(Rate)");
					break;
				case PURPOSE_ITP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(PERCENTAGE25).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE25));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE09).multiply(RATE334));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_1050));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + PERCENTAGE25 + "(Multiplier)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE25 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE09 + "(Rate)" + "*" + RATE334 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_1050 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_190
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_IPULP:
					break;

				case PURPOSE_ITC:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(PERCENTAGE25).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE25));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE09).multiply(RATE334));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_1050));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + PERCENTAGE25 + "(Multiplier)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE25 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE09 + "(Rate)" + "*" + RATE334 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_1050 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_190
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");

					break;
				case PURPOSE_IPA:
					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE25));

					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE09).multiply(RATE167));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_80));
					feesTypeCalculationDto
							.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(acre).multiply(RATE_190));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE25 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE09 + "(Rate)" + "*" + RATE167 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_80 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_190 + "(Rate)");

					break;
				case PURPOSE_RGP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE40).multiply(PERCENTAGE0995));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE09).multiply(RATE4));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_125));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE460));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE40 + "(Rate)"+"*"+PERCENTAGE0995+"(Multiplier)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE09 + "(Rate)" + "*" + RATE4 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_125 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE460
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");

					break;

				case PURPOSE_DDJAY_APHP:
					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE).multiply(PERCENTAGE075));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE09).multiply(PERCENTAGE075));
					feesTypeCalculationDto
							.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_125));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(RATE375).multiply(PERCENTAGE075));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE
							+ "(Rate)" + "(Rate)" + "*" + PERCENTAGE0995 + "(Multiplier)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE09 + "(Rate)" + "*" + PERCENTAGE0995 + "(Multiplier)");
					feesTypeCalculationDto.setConversionFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_125
									+ "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE375
									+ "(Rate)" + "*" + PERCENTAGE0995 + "(Multiplier)");

					break;
				case PURPOSE_NILPC:
					break;
				case PURPOSE_TODGH:
					break;

				case PURPOSE_AGH:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE09));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_125));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE0 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(
							acre + "(total applied Area in acre)" + "*" + RATE104 + "(Rate)" + "*" + RATE09 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_125 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE0 + "(Rate)");

					break;

				case PURPOSE_MLU_CZ:

					break;

				case PURPOSE_LDEF:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(acre)));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE250));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE09).multiply(RATE01));
					feesTypeCalculationDto
							.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_125));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(RATE375).multiply(RATE2));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE250 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE09 + "(Rate)" + "*" + RATE01 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_125
									+ "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE375 + "(Rate)" + "*" + RATE2 + "(Multiplier)");
					break;

				case PURPOSE_NILP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10).multiply(PERCENTAGE125));
					feesTypeCalculationDto
							.setLicenseFeeChargesCal(acre.multiply(RATE19).multiply(RATE5).divide(RATE7, 0));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(RATE104.multiply(RATE09).multiply(RATE467));
					feesTypeCalculationDto
							.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_125));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							(areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE460)
									.multiply(RATE5)).divide(RATE7, 0));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + PERCENTAGE125 + "(Multiplier)");
					feesTypeCalculationDto.setLicenceFormula("(" + acre + "(total applied Area in acre)" + "*" + RATE19
							+ "(Rate)" + "*" + RATE5 + "(Rate)" + ")" + "/" + RATE7 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE09 + "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_125
									+ "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto.setStateInfraFormula("(" + AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE460 + "(Rate)" + "*" + RATE5 + "(Rate)" + "*"
							+ stateInfrastructureDevelopmentCharges + "(FAR)" + ")" + "/" + RATE7 + "(Rate)");
					break;
				case PURPOSE_TODCOMM:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10).multiply(scrutinyFeeCharges));
					feesTypeCalculationDto
							.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_125));
//				feesTypeCalculationDto.setScrutinyFeeChargesCal(totalFar.multiply(RATE270));
//				feesTypeCalculationDto.setLicenseFeeChargesCal(totalFar.multiply(RATE270));
//				feesTypeCalculationDto.setConversionChargesCal(totalFar.multiply(RATE270));
					break;
				case PURPOSE_TODIT:
					break;
				case PURPOSE_TODMUD:
					break;
				case PURPOSE_CPCS:

					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE235.multiply(acre));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE09).multiply(RATE4));
					feesTypeCalculationDto
							.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_1050)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_750));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE235 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE09 + "(Rate)" + "*" + RATE4 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_1260 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula("(" + AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_750 + "(Rate)" + "*"
							+ stateInfrastructureDevelopmentCharges + "(FAR)" + ")" + "/" + RATE7 + "(Rate)");

					break;
				case PURPOSE_CPRS:
					feesTypeCalculationDto
					.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
			feesTypeCalculationDto.setLicenseFeeChargesCal(RATE235.multiply(acre));
			feesTypeCalculationDto
					.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE09).multiply(RATE4));
			feesTypeCalculationDto
					.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_1050)));
			feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
					areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_750));
			feesTypeCalculationDto
					.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
							+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
			feesTypeCalculationDto
					.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE235 + "(Rate)");
			feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
					+ "(Rate)" + "*" + RATE09 + "(Rate)" + "*" + RATE4 + "(Rate)");
			feesTypeCalculationDto.setConversionFormula(
					"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
							+ RATE_1050 + "(Rate)" + ")");
			feesTypeCalculationDto.setStateInfraFormula("(" + AREA + "(acre to sq. mt)" + "*" + acre
					+ "(total applied Area in acre)" + "*" + RATE_750 + "(Rate)" + "*"
					+ stateInfrastructureDevelopmentCharges + "(FAR)" + ")" + "/" + RATE7 + "(Rate)");

			break;
					
				case PURPOSE_CICS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE270.multiply(acre));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE09).multiply(RATE467));
					feesTypeCalculationDto
					.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_1050)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_750));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE270 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE09 + "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_1050 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula("(" + AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_750 + "(Rate)" + "*"
							+ stateInfrastructureDevelopmentCharges + "(FAR)" + ")" + "/" + RATE7 + "(Rate)");

					break;
				case PURPOSE_CIRS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE270.multiply(acre));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE09).multiply(RATE467));
					feesTypeCalculationDto
					.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_1050)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_750));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE270 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE09 + "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_1050 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula("(" + AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_750 + "(Rate)" + "*"
							+ stateInfrastructureDevelopmentCharges + "(FAR)" + ")" + "/" + RATE7 + "(Rate)");

					break;
				case PURPOSE_RHP:
					break;
				}
				break;
			}

			// ------------------------------HIGH 2-----------------------------//
			case ZONE_HIG2: {
				switch (calculatorRequest.getPurposeCode()) {

				case PURPOSE_RPL:
					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));

					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE95));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE07));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_125));
					feesTypeCalculationDto
							.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(acre).multiply(RATE375));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE95 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(
							acre + "(total applied Area in acre)" + "*" + RATE104 + "(Rate)" + "*" + RATE07 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_125 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE375 + "(Rate)");
					break;

				case PURPOSE_ITP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(PERCENTAGE25).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE125));

					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE07).multiply(RATE334));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_1050));

					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + PERCENTAGE25 + "(Multiplier)");
					feesTypeCalculationDto.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE125);
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE07 + "(Rate)" + "*" + RATE334 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_1050 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_190
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_IPULP:
					break;
				case PURPOSE_ITC:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(PERCENTAGE25).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE125));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE07).multiply(RATE334));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_1050));

					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + PERCENTAGE25 + "(Multiplier)");
					feesTypeCalculationDto.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE125);
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE07 + "(Rate)" + "*" + RATE334 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_1050 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_190
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_IPA:
					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE125));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE07).multiply(RATE167));

					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_80));
					feesTypeCalculationDto
							.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(acre).multiply(RATE_190));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE125);
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE07 + "(Rate)" + "*" + RATE167 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_80 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_190 + "(Rate)");
					break;

				case PURPOSE_RGP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE19).multiply(PERCENTAGE0995));

					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE07).multiply(RATE4));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_125));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE460));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE19
							+ "(Rate)" + "*" + PERCENTAGE0995 + "(Multiplier)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE07 + "(Rate)" + "*" + RATE4 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_125 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE460
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;

				case PURPOSE_DDJAY_APHP:
					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE950).multiply(PERCENTAGE075));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE07).multiply(PERCENTAGE075));
					feesTypeCalculationDto
							.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_125));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(RATE375).multiply(PERCENTAGE075));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE950 + "*"
							+ PERCENTAGE0995 + "(Multiplier)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE07 + "(Rate)" + "*" + PERCENTAGE0995 + "(Multiplier)");
					feesTypeCalculationDto.setConversionFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_125
									+ "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ PERCENTAGE0995 + "(Multiplier)" + "*" + RATE375 + "(Rate)");

					break;
				case PURPOSE_NILPC:
					break;
				case PURPOSE_TODGH:
					break;

				case PURPOSE_AGH:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE07));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_125));

					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);

					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE0 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(
							acre + "(total applied Area in acre)" + "*" + RATE104 + "(Rate)" + "*" + RATE07 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_125 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE0 + "(Rate)");

					break;

				case PURPOSE_MLU_CZ:

					break;

				case PURPOSE_LDEF:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(acre)));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE19));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE07).multiply(RATE01));
					feesTypeCalculationDto
							.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_125));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(RATE375).multiply(RATE2));

					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE19 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE07 + "(Rate)" + "*" + RATE01 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_125
									+ "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE375 + "(Rate)" + "*" + RATE2 + "(Multiplier)");
					break;
				case PURPOSE_NILP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10).multiply(PERCENTAGE125));
					feesTypeCalculationDto
							.setLicenseFeeChargesCal(acre.multiply(RATE19).multiply(RATE5).divide(RATE7, 0));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE07).multiply(RATE467));
					feesTypeCalculationDto
							.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_125));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							(areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE460)
									.multiply(RATE5)).divide(RATE7, 0));

					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + PERCENTAGE125 + "(Multiplier)");
					feesTypeCalculationDto.setLicenceFormula("(" + acre + "(total applied Area in acre)" + "*" + RATE19
							+ "(Rate)" + "*" + RATE5 + "(Rate)" + ")" + "/" + RATE7 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE07 + "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_125
									+ "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto.setStateInfraFormula("(" + AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE460 + "(Rate)" + "*" + RATE5 + "(Rate)" + "*"
							+ stateInfrastructureDevelopmentCharges + "(FAR)" + ")" + "/" + RATE7 + "(Rate)");
					break;
				case PURPOSE_TODCOMM:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10).multiply(scrutinyFeeCharges));
					feesTypeCalculationDto
							.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_125));
//				feesTypeCalculationDto.setScrutinyFeeChargesCal(totalFar.multiply(RATE210));
//				feesTypeCalculationDto.setLicenseFeeChargesCal(totalFar.multiply(RATE210));
//				feesTypeCalculationDto.setConversionChargesCal(totalFar.multiply(RATE210));
					break;
				case PURPOSE_TODIT:
					break;
				case PURPOSE_TODMUD:
					break;
				case PURPOSE_CPCS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE14000.multiply(acre));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE07).multiply(RATE4));
					feesTypeCalculationDto
							.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_1050)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_750));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE14000 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE07 + "(Rate)" + "*" + RATE4 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_1050 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_750
									+ "(Rate)" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_CPRS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE14000.multiply(acre));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE07).multiply(RATE4));
					feesTypeCalculationDto
							.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_1050)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_750));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE14000 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE07 + "(Rate)" + "*" + RATE4 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_1050 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_750
									+ "(Rate)" + stateInfrastructureDevelopmentCharges + "(FAR)");

					break;
				case PURPOSE_CIRS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE210.multiply(acre));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE07).multiply(RATE467));
					feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_1050)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_750));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE210 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE07 + "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_1050 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_750
									+ "(Rate)" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_CICS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE210.multiply(acre));

					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE07).multiply(RATE467));
					feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_1050)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_750));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE210 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE07 + "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_1050 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_750
									+ "(Rate)" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;

				case PURPOSE_RHP:
					break;
				}
				break;

			}

			// ---------------------medium--------------------///
			case ZONE_MDM:

			{
				switch (calculatorRequest.getPurposeCode()) {
				case PURPOSE_RPL:
					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));

					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE625));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE06));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_80));
					feesTypeCalculationDto
							.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(acre).multiply(RATE_250));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE625 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(
							acre + "(total applied Area in acre)" + "*" + RATE104 + "(Rate)" + "*" + RATE06 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_80 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_250 + "(Rate)");
					break;
				case PURPOSE_ITP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(PERCENTAGE25).multiply(RATE10));

					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE62500));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE06).multiply(RATE334));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_600));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_125));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + PERCENTAGE25 + "(Multiplier)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE62500 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE06 + "(Rate)" + "*" + RATE334 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_600 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_125
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_IPULP:
					break;
				case PURPOSE_ITC:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(PERCENTAGE25).multiply(RATE10));

					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE62500));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE06).multiply(RATE334));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_600));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_125));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + PERCENTAGE25 + "(Multiplier)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE62500 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE06 + "(Rate)" + "*" + RATE334 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_600 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_125
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");

					break;
				case PURPOSE_IPA:
					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE62500));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE06).multiply(RATE167));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_50));
					feesTypeCalculationDto
							.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(acre).multiply(RATE_125));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE62500 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE06 + "(Rate)" + "*" + RATE167 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_50 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_125 + "(Rate)");

					break;

				case PURPOSE_RGP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE95).multiply(PERCENTAGE0995));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE06).multiply(RATE4));

					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_80));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE320));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE95
							+ "(Rate)" + "*" + PERCENTAGE0995 + "(Multiplier)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE06 + "(Rate)" + "*" + RATE4 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_80 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE320
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;

				case PURPOSE_DDJAY_APHP:
					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE10000));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE06).multiply(PERCENTAGE075));
					feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_80));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE10000 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE06 + "(Rate)" + "*" + PERCENTAGE0995 + "(Multiplier)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_80 + "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE0 + "(Rate)");

					break;
				case PURPOSE_NILPC:
					break;
				case PURPOSE_TODGH:
					break;

				case PURPOSE_AGH:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE06));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_80));

					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE0 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(
							acre + "(total applied Area in acre)" + "*" + RATE104 + "(Rate)" + "*" + RATE06 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_80 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE0 + "(Rate)");
					break;

				case PURPOSE_MLU_CZ:
					break;

				case PURPOSE_LDEF:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(acre)));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE06).multiply(RATE01));
					feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_80));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(RATE_250).multiply(RATE2));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE06 + "(Rate)" + "*" + RATE01 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_80 + "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_250 + "*" + RATE2 + "(Multiplier)");
					break;
				case PURPOSE_NILP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10).multiply(PERCENTAGE125));
					feesTypeCalculationDto
							.setLicenseFeeChargesCal(acre.multiply(RATE95).multiply(RATE5).divide(RATE7, 0));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE06).multiply(RATE467));
					feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_80));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE320)
									.multiply(RATE5).divide(RATE7, 0));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + PERCENTAGE125 + "(Multiplier)");
					feesTypeCalculationDto.setLicenceFormula("(" + acre + "(total applied Area in acre)" + "*" + RATE_80
							+ "(Rate)" + "*" + RATE5 + "(Rate)" + ")" + "/" + RATE7 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE06 + "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_80 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE320
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_TODCOMM:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10).multiply(scrutinyFeeCharges));
					feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_80));

//				feesTypeCalculationDto.setScrutinyFeeChargesCal(totalFar.multiply(RATE95));
//				feesTypeCalculationDto.setLicenseFeeChargesCal(totalFar.multiply(RATE95));
//				feesTypeCalculationDto.setConversionChargesCal(totalFar.multiply(RATE95));
					break;
				case PURPOSE_TODIT:
					break;
				case PURPOSE_TODMUD:
					break;
				case PURPOSE_CPCS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE6250.multiply(acre));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE06).multiply(RATE4));

					feesTypeCalculationDto
							.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_600)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_500));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE6250 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE06 + "(Rate)" + "*" + RATE4 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_600 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_500
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");

					break;
				case PURPOSE_CPRS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE6250.multiply(acre));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE06).multiply(RATE4));

					feesTypeCalculationDto
							.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_600)));

					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_500));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE6250 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE06 + "(Rate)" + "*" + RATE4 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_600 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_500
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_CICS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE95.multiply(acre));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE06).multiply(RATE467));
					feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_600)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_500));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE95 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE06 + "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_600 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_500
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_CIRS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE95.multiply(acre));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE06).multiply(RATE467));
					feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_600)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_500));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE95 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE06 + "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_600 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_500
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_RHP:
					break;
				}
				break;

			}

			// -----------------low1----------------------//
			case ZONE_LOW: {
				switch (calculatorRequest.getPurposeCode()) {
				case PURPOSE_RPL:
					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE125));
					feesTypeCalculationDto.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE125);
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE05));
					feesTypeCalculationDto.setEdcFormula(
							acre + "(total applied Area in acre)" + "*" + RATE104 + "(Rate)" + "*" + RATE05);
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_20));
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_20 + "(Rate)");
					feesTypeCalculationDto
							.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(acre).multiply(RATE70));
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE70);

					break;
				case PURPOSE_ITP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(PERCENTAGE25).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE12500));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE05).multiply(RATE334));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_150));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE35));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + PERCENTAGE25 + "(Multiplier)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE12500 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE05 + "(Rate)" + "*" + RATE334 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_150 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE35
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;

				case PURPOSE_IPULP:
					break;
				case PURPOSE_ITC:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(PERCENTAGE25).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE12500));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE05).multiply(RATE334));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_150));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE35));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + PERCENTAGE25 + "(Multiplier)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE12500 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE05 + "(Rate)" + "*" + RATE334 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_150 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE35
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_IPA:
					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE12500));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE05).multiply(RATE167));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_30));
					feesTypeCalculationDto
							.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(acre).multiply(RATE35));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE12500 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE05 + "(Rate)" + "*" + RATE167 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_30 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE35);
					break;

				case PURPOSE_RGP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE25).multiply(PERCENTAGE0995));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE05).multiply(RATE4));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_20));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE90));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE25
							+ "(Rate)" + "*" + PERCENTAGE0995 + "(Multiplier)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE05 + "(Rate)" + "*" + RATE4 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_20 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE90
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");

					break;
				case PURPOSE_DDJAY_APHP:
					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE10000));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE05).multiply(PERCENTAGE075));
					feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_20));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE10000 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE05 + "(Rate)" + "*" + PERCENTAGE0995 + "(Multiplier)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_20 + "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE0 + "(Rate)");
					break;
				case PURPOSE_NILPC:
					break;
				case PURPOSE_TODGH:
					break;

				case PURPOSE_AGH:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE05));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_20));

					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE0 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(
							acre + "(total applied Area in acre)" + "*" + RATE104 + "(Rate)" + "*" + RATE05);
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_20 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE0 + "(Rate)");
					break;

				case PURPOSE_MLU_CZ:
					break;

				case PURPOSE_LDEF:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(acre)));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE25));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE05).multiply(RATE01));
					feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_20));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(RATE70).multiply(RATE2));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE25 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE05 + "(Rate)" + "*" + RATE01 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_20 + "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE70 + "*" + RATE2 + "(Multiplier)");
					break;
				case PURPOSE_NILP:

					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10).multiply(PERCENTAGE125));
					feesTypeCalculationDto
							.setLicenseFeeChargesCal(acre.multiply(RATE25).multiply(RATE5).divide(RATE7, 0));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE05).multiply(RATE467));
					feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_20));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							(areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE90)
									.multiply(RATE5)).divide(RATE7, 0));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + PERCENTAGE125 + "(Multiplier)");
					feesTypeCalculationDto.setLicenceFormula("(" + acre + "(total applied Area in acre)" + "*" + RATE25
							+ "(Rate)" + "*" + RATE5 + "(Rate)" + ")" + "/" + RATE7 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE05 + "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_20 + "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto.setStateInfraFormula("(" + AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE90 + "(Rate)" + "*" + RATE5 + "(Rate)" + "*"
							+ stateInfrastructureDevelopmentCharges + "(FAR)" + ")" + "/" + RATE7 + "(Rate)");
					break;
				case PURPOSE_TODCOMM:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10).multiply(scrutinyFeeCharges));
					feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_20));

//				feesTypeCalculationDto.setScrutinyFeeChargesCal(totalFar.multiply(RATE19));
//				feesTypeCalculationDto.setLicenseFeeChargesCal(totalFar.multiply(RATE19));
//				feesTypeCalculationDto.setConversionChargesCal(totalFar.multiply(RATE19));
					break;
				case PURPOSE_TODIT:
					break;
				case PURPOSE_TODMUD:
					break;
				case PURPOSE_CPCS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE.multiply(acre));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE05).multiply(RATE4));
					feesTypeCalculationDto
							.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_150)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE05 + "(Rate)" + "*" + RATE4 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_150 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_190
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");

					break;
				case PURPOSE_CPRS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE.multiply(acre));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE05).multiply(RATE4));
					feesTypeCalculationDto
							.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_150)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE05 + "(Rate)" + "*" + RATE4 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_150 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_190
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;

				case PURPOSE_RHP:
					break;
				case PURPOSE_CIRS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE19.multiply(acre));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE05).multiply(RATE467));
					feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_150)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE19 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE05 + "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_150 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_190
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_CICS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE19.multiply(acre));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE05).multiply(RATE467));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_150));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE19 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE05 + "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_150 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_190
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				}

				break;
			}

			/// ------------------low2-------------------------------//
			case ZONE_LOW2: {
				switch (calculatorRequest.getPurposeCode()) {
				case PURPOSE_RPL:

					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));

					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE125));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE04));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_20));
					feesTypeCalculationDto
							.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(acre).multiply(RATE70));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE125);
					feesTypeCalculationDto.setEdcFormula(
							acre + "(total applied Area in acre)" + "*" + RATE104 + "(Rate)" + "*" + RATE04 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_20 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE70 + "*"
									+ stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_ITP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(PERCENTAGE25).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE12500));

					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE04).multiply(RATE334));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_150));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE35));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + PERCENTAGE25 + "(Multiplier)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE12500 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE04 + "(Rate)" + "*" + RATE334 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_150 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE35
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_ITC:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(PERCENTAGE25).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE12500));

					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE04).multiply(RATE334));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_150));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE35));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + PERCENTAGE25 + "(Multiplier)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE12500 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE04 + "(Rate)" + "*" + RATE334 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_150 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE35
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_IPULP:
					break;

				case PURPOSE_IPA:
					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE12500));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE04).multiply(RATE167));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_30));
					feesTypeCalculationDto
							.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(acre).multiply(RATE35));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + PERCENTAGE25 + "(Multiplier)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE12500 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE04 + "(Rate)" + "*" + RATE334 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_150 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE35
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;

				case PURPOSE_RGP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE25).multiply(PERCENTAGE0995));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE04).multiply(RATE4));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_20));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE90));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE25
							+ "(Rate)" + "*" + PERCENTAGE0995 + "(Multiplier)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE04 + "(Rate)" + "*" + RATE4 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_20 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE90
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;

				case PURPOSE_DDJAY_APHP:
					feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE10000));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE04).multiply(PERCENTAGE075));
					feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_20));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE10000 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE04 + "(Rate)" + "*" + PERCENTAGE0995 + "(Multiplier)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_20 + "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE0 + "(Rate)");
					break;
				case PURPOSE_NILPC:
					break;
				case PURPOSE_TODGH:
					break;

				case PURPOSE_AGH:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE04));
					feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_20));

					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE0 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(
							acre + "(total applied Area in acre)" + "*" + RATE104 + "(Rate)" + "*" + RATE04 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_20 + "(Rate)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE0 + "(Rate)");
					break;

				case PURPOSE_MLU_CZ:
					break;

				case PURPOSE_LDEF:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(acre)));
					feesTypeCalculationDto.setLicenseFeeChargesCal(acre.multiply(RATE25));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE04).multiply(RATE01));
					feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_20));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(RATE70).multiply(RATE2));
					feesTypeCalculationDto.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE10 + "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE25 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE04 + "(Rate)" + "*" + RATE01 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_20 + "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto.setStateInfraFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE70 + "*" + RATE2 + "(Multiplier)");
					break;
				case PURPOSE_NILP:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10).multiply(PERCENTAGE125));
					feesTypeCalculationDto
							.setLicenseFeeChargesCal(acre.multiply(RATE25).multiply(RATE5).divide(RATE7, 0));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE04).multiply(RATE467));
					feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_20));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							(areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE90)
									.multiply(RATE5)).divide(RATE7, 0));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + PERCENTAGE125 + "(Multiplier)");
					feesTypeCalculationDto.setLicenceFormula("(" + acre + "(total applied Area in acre)" + "*" + RATE25
							+ "(Rate)" + "*" + RATE5 + "(Rate)" + ")" + "/" + RATE7 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE04 + "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE_20 + "(Rate)" + "*" + RATE2 + "(Multiplier)");
					feesTypeCalculationDto.setStateInfraFormula("(" + AREA + "(acre to sq. mt)" + "*" + acre
							+ "(total applied Area in acre)" + "*" + RATE90 + "(Rate)" + "*" + RATE5 + "(Rate)" + "*"
							+ stateInfrastructureDevelopmentCharges + "(FAR)" + ")" + "/" + RATE7 + "(Rate)");
				case PURPOSE_TODCOMM:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(RATE10).multiply(scrutinyFeeCharges));
					feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(acre)).multiply(RATE_20));

//				feesTypeCalculationDto.setScrutinyFeeChargesCal(totalFar.multiply(RATE19));
//				feesTypeCalculationDto.setLicenseFeeChargesCal(totalFar.multiply(RATE19));
//				feesTypeCalculationDto.setConversionChargesCal(totalFar.multiply(RATE19));
					break;
				case PURPOSE_TODIT:
					break;
				case PURPOSE_TODMUD:
					break;
				case PURPOSE_CPCS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE.multiply(acre));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE04).multiply(RATE4));
					feesTypeCalculationDto
							.setConversionChargesCal(areaInSqmtr(acre).multiply(RATE_150));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE04 + "(Rate)" + "*" + RATE4 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_150 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_190
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_CPRS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE.multiply(acre));
					feesTypeCalculationDto
							.setExternalDevelopmentChargesCal(acre.multiply(RATE104).multiply(RATE04).multiply(RATE4));
					feesTypeCalculationDto
							.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_150)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE04 + "(Rate)" + "*" + RATE4 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_150 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_190
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_RHP:
					break;
				case PURPOSE_CIRS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE19.multiply(acre));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE04).multiply(RATE467));
					feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_150)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE19 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE04 + "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_150 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_190
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				case PURPOSE_CICS:
					feesTypeCalculationDto
							.setScrutinyFeeChargesCal(areaInSqmtr(acre).multiply(scrutinyFeeCharges).multiply(RATE10));
					feesTypeCalculationDto.setLicenseFeeChargesCal(RATE19.multiply(acre));
					feesTypeCalculationDto.setExternalDevelopmentChargesCal(
							acre.multiply(RATE104).multiply(RATE04).multiply(RATE467));
					feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(acre).multiply(RATE_150)));
					feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
							areaInSqmtr(acre).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));
					feesTypeCalculationDto
							.setScrutinyFormula(AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)"
									+ "*" + RATE10 + "(Rate)" + "*" + scrutinyFeeCharges + "(FAR)");
					feesTypeCalculationDto
							.setLicenceFormula(acre + "(total applied Area in acre)" + "*" + RATE19 + "(Rate)");
					feesTypeCalculationDto.setEdcFormula(acre + "(total applied Area in acre)" + "*" + RATE104
							+ "(Rate)" + "*" + RATE04 + "(Rate)" + "*" + RATE467 + "(Rate)");
					feesTypeCalculationDto.setConversionFormula(
							"(" + AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*"
									+ RATE_150 + "(Rate)" + ")");
					feesTypeCalculationDto.setStateInfraFormula(
							AREA + "(acre to sq. mt)" + "*" + acre + "(total applied Area in acre)" + "*" + RATE_190
									+ "(Rate)" + "*" + stateInfrastructureDevelopmentCharges + "(FAR)");
					break;
				}

				break;
			}
			}

		return feesTypeCalculationDto;

	}

}