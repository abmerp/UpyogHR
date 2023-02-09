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

	private BigDecimal areaInSqmtr(String arce) {
		return (AREA.multiply(new BigDecimal(arce)));
	}

	private BigDecimal areaInSqmtr(BigDecimal arce) {
		return (AREA.multiply(arce));
	}

	public FeesTypeCalculationDto feesTypeCalculation(RequestInfo requestInfo, CalculatorRequest calculatorRequest) {
		BigDecimal arce = new BigDecimal(calculatorRequest.getTotalLandSize());
		BigDecimal AREA1 = (PERCENTAGE1.multiply(new BigDecimal(calculatorRequest.getTotalLandSize())));
		BigDecimal AREA2 = PERCENTAGE2.multiply(new BigDecimal(calculatorRequest.getTotalLandSize()));
		BigDecimal far = new BigDecimal(calculatorRequest.getFar());

		Map<String, List<String>> mdmsData;
		FeesTypeCalculationDto feesTypeCalculationDto = new FeesTypeCalculationDto();
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> mDMSCallPurposeCode = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>) landUtil
				.mDMSCallPurposeCode(requestInfo, requestInfo.getUserInfo().getTenantId(),
						calculatorRequest.getPurposeCode());

		mdmsData = valid.getAttributeValues(mDMSCallPurposeCode);
		List<Map<String, Object>> msp = (List) mdmsData.get("Purpose");
		BigDecimal externalDevelopmentCharges = null;
		BigDecimal scrutinyFeeCharges = null;
		BigDecimal conversionCharges = null;
		BigDecimal licenseFeeCharges = null;
		BigDecimal stateInfrastructureDevelopmentCharges = null;
		String purposeName = "";
		scrutinyFeeCharges = far;
		licenseFeeCharges = far;

		for (Map<String, Object> mm : msp) {
			purposeName = String.valueOf(mm.get("name"));
			// scrutinyFeeCharges = new
			// BigDecimal(String.valueOf(mm.get("scrutinyFeeCharges")));
			externalDevelopmentCharges = new BigDecimal(String.valueOf(mm.get("externalDevelopmentCharges")));
			conversionCharges = new BigDecimal(String.valueOf(mm.get("conversionCharges")));
			// licenseFeeCharges = new
			// BigDecimal(String.valueOf(mm.get("licenseFeeCharges")));
			stateInfrastructureDevelopmentCharges = new BigDecimal(
					String.valueOf(mm.get("stateInfrastructureDevelopmentCharges")));
		}
		feesTypeCalculationDto.setPurpose(purposeName);
		switch (calculatorRequest.getPotenialZone()) {
//------------hyper----------//
		case ZONE_HYPER:

			switch (calculatorRequest.getPurposeCode()) {

			case PURPOSE_RPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(new BigDecimal(10)));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(AREA1.multiply(new BigDecimal(104.096)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(486.13)).multiply(new BigDecimal(100000)))));
				feesTypeCalculationDto.setConversionChargesCal(AREA1.multiply(AREA).multiply(new BigDecimal(158))
						.add(AREA2.multiply(AREA).multiply(new BigDecimal(1470))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(AREA1.multiply(AREA)).multiply(new BigDecimal(500)).add(AREA2.multiply(AREA)
								.multiply(stateInfrastructureDevelopmentCharges).multiply(new BigDecimal(1000))));

				break;
			case PURPOSE_IPULP:
				break;

			case PURPOSE_ITC:

				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						AREA1.multiply(new BigDecimal(347.682)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(416.385)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal(AREA1.multiply(AREA).multiply(new BigDecimal(100))
						.add(AREA2.multiply(AREA).multiply(new BigDecimal(1260))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						AREA1.multiply(AREA).multiply(new BigDecimal(250)).multiply(new BigDecimal(2.5f))
								.add(AREA2.multiply(AREA).multiply(stateInfrastructureDevelopmentCharges)
										.multiply(new BigDecimal(150)).multiply(new BigDecimal(10))));

				break;
			case PURPOSE_ITP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						AREA1.multiply(new BigDecimal(347.682)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(416.385)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal(AREA1.multiply(AREA).multiply(new BigDecimal(100))
						.add(AREA2.multiply(AREA).multiply(new BigDecimal(1260))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						AREA1.multiply(AREA).multiply(new BigDecimal(250)).multiply(new BigDecimal(2.5f))
								.add(AREA2.multiply(AREA).multiply(stateInfrastructureDevelopmentCharges)
										.multiply(new BigDecimal(150)).multiply(new BigDecimal(10))));
				break;
			case PURPOSE_IPA:

				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(PART1.multiply(new BigDecimal(208.192)).multiply(new BigDecimal(100000))
								.add(PART2.multiply(new BigDecimal(416.385)).multiply(new BigDecimal(100000))))
								.add(PART3.multiply(new BigDecimal(416.38)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal((PART1.multiply(AREA).multiply(new BigDecimal(100))
						.add(PART2.multiply(AREA).multiply(new BigDecimal(158)))
						.add(PART3.multiply(AREA).multiply(new BigDecimal(1260)))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((PART1.multiply(AREA)
						.multiply(new BigDecimal(250)).multiply(stateInfrastructureDevelopmentCharges)
						.add(PART2.multiply(AREA).multiply(new BigDecimal(625)).multiply(new BigDecimal(1.75))
								.add(PART3.multiply(AREA).multiply(new BigDecimal(1000))
										.multiply(stateInfrastructureDevelopmentCharges)))));
				break;

			case PURPOSE_RGP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(PERCENTAGE0995).multiply(RATE40));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						new BigDecimal(0.995).multiply(new BigDecimal(312.289)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(416.385)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto
						.setConversionChargesCal(new BigDecimal(0.995).multiply(AREA).multiply(new BigDecimal(58))
								.add(new BigDecimal(0.005).multiply(AREA).multiply(new BigDecimal(1260))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(0.995).multiply(AREA)
						.multiply(new BigDecimal(625)).multiply(new BigDecimal(1.75))
						.add(new BigDecimal(0.005).multiply(AREA).multiply(new BigDecimal(1000))
								.multiply(stateInfrastructureDevelopmentCharges)));

				break;

			case PURPOSE_DDJAY_APHP:

				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(AREA1.multiply(new BigDecimal(104.096)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(486.13)).multiply(new BigDecimal(100000)))));
				feesTypeCalculationDto.setConversionChargesCal(AREA1.multiply(AREA).multiply(new BigDecimal(158))
						.add(AREA2.multiply(AREA).multiply(new BigDecimal(1470))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						AREA1.multiply(new BigDecimal(500)).multiply(AREA).add(AREA2.multiply(AREA)
								.multiply(stateInfrastructureDevelopmentCharges).multiply(new BigDecimal(1000))));
				break;

			case PURPOSE_NILPC:
				break;

			case PURPOSE_TODGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(scrutinyFeeCharges));

				feesTypeCalculationDto.setLicenseFeeChargesCal(
						(PERCENTAGE0995.multiply(RATE40).multiply(licenseFeeCharges).divide(PERCENTAGE175, 0))
								.add(RATE1.multiply(PERCENTAGE5).multiply(licenseFeeCharges).divide(PERCENTAGE175, 0)));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(new BigDecimal(0.995).multiply(
						(new BigDecimal(calculatorRequest.getTotalLandSize()).multiply(new BigDecimal(416.385))
								.multiply(new BigDecimal(100000)).multiply(licenseFeeCharges))
								.divide(new BigDecimal(1.75), 0)));
				feesTypeCalculationDto.setConversionChargesCal(
						(new BigDecimal(0.995).multiply(new BigDecimal(158)).multiply(AREA).multiply(conversionCharges))
								.divide(new BigDecimal(1.75), 0)
								.add(new BigDecimal(0.005).multiply(new BigDecimal(1470)).multiply(AREA)
										.multiply(conversionCharges).divide(new BigDecimal(1.75), 0)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(0.995)
						.multiply(new BigDecimal(625)).multiply(AREA).multiply(stateInfrastructureDevelopmentCharges)
						.add(new BigDecimal(0.005).multiply(new BigDecimal(1000)).multiply(AREA)
								.multiply(stateInfrastructureDevelopmentCharges)));

				break;

			case PURPOSE_MLU_CZ:
				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(arce)));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE250));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(new BigDecimal(0.1).multiply(AREA1)
						.multiply(new BigDecimal(104.096)).multiply(new BigDecimal(100000)).add(AREA2)
						.multiply(new BigDecimal(486.13)).multiply(new BigDecimal(100000)));
				feesTypeCalculationDto.setConversionChargesCal(new BigDecimal(2).multiply((AREA1).multiply(AREA)
						.multiply(new BigDecimal(158)).add(AREA2).multiply(AREA).multiply(new BigDecimal(1470))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(AREA1).multiply(AREA).multiply(new BigDecimal(500)).add((AREA2.multiply(AREA)
								.multiply(stateInfrastructureDevelopmentCharges).multiply(new BigDecimal(1000)))));

				break;

			case PURPOSE_NILP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(PERCENTAGE125));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE40).multiply(RATE5).divide(RATE7, 0));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal((AREA1.multiply(new BigDecimal(312.289))
						.multiply(new BigDecimal(100000)).multiply(new BigDecimal(5))).divide(new BigDecimal(7), 0)
						.add(AREA2.multiply(new BigDecimal(486.13)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal(
						(AREA1).multiply(new BigDecimal(158)).multiply(new BigDecimal(5)).divide(new BigDecimal(7), 0)
								.multiply(AREA).add(AREA2.multiply(AREA).multiply(new BigDecimal(1470))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((AREA1).multiply(new BigDecimal(625))
						.multiply(AREA).multiply(stateInfrastructureDevelopmentCharges).multiply(new BigDecimal(5))
						.divide(new BigDecimal(7), 0).add(stateInfrastructureDevelopmentCharges.multiply(AREA2)
								.multiply(AREA).multiply(new BigDecimal(1000))));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(scrutinyFeeCharges));
				feesTypeCalculationDto
						.setLicenseFeeChargesCal((RATE1).multiply(licenseFeeCharges).divide(PERCENTAGE175, 0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(new BigDecimal(calculatorRequest.getTotalLandSize()).multiply(new BigDecimal(486.13))
								.multiply(new BigDecimal(100000)).multiply(externalDevelopmentCharges))
								.divide(new BigDecimal(1.75), 0));
				feesTypeCalculationDto
						.setConversionChargesCal((new BigDecimal(1470).multiply(AREA).multiply(conversionCharges))
								.divide(new BigDecimal(1.75), 0));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(new BigDecimal(1000).multiply(AREA).multiply(stateInfrastructureDevelopmentCharges)));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE1));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(((new BigDecimal(calculatorRequest.getTotalLandSize())
								.multiply(new BigDecimal(416.385)).multiply(new BigDecimal(100000)))));
				feesTypeCalculationDto.setConversionChargesCal(AREA.multiply(new BigDecimal(1260)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						AREA.multiply(new BigDecimal(1000).multiply(stateInfrastructureDevelopmentCharges)));

				break;

			case PURPOSE_CPRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE1));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(((new BigDecimal(calculatorRequest.getTotalLandSize())
								.multiply(new BigDecimal(416.385)).multiply(new BigDecimal(100000)))));
				feesTypeCalculationDto.setConversionChargesCal(AREA.multiply(new BigDecimal(1260)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						AREA.multiply(new BigDecimal(1000).multiply(stateInfrastructureDevelopmentCharges)));
				break;
			case PURPOSE_CICS:

				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE1));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(arce).multiply(new BigDecimal(486.13)).multiply(new BigDecimal(100000)));
				feesTypeCalculationDto
						.setConversionChargesCal(((new BigDecimal(1)).multiply(AREA).multiply(new BigDecimal(1470))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(((new BigDecimal(1)).multiply(AREA)
						.multiply(new BigDecimal(1000).multiply(stateInfrastructureDevelopmentCharges))));
				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE1));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(arce).multiply(new BigDecimal(486.13)).multiply(new BigDecimal(100000)));
				feesTypeCalculationDto
						.setConversionChargesCal(new BigDecimal(1).multiply(AREA).multiply(new BigDecimal(1470)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(((new BigDecimal(1)).multiply(AREA)
						.multiply(new BigDecimal(1000).multiply(stateInfrastructureDevelopmentCharges))));
				break;
			case PURPOSE_RHP:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(AREA1).multiply(new BigDecimal(104.096)).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(486.13)).multiply(new BigDecimal(100000)));
				feesTypeCalculationDto.setConversionChargesCal(AREA1.multiply(AREA).multiply(new BigDecimal(158))
						.add(AREA2).multiply(AREA).multiply(new BigDecimal(1470)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(0));
				break;
			}
			break;
//---------------------------------HIGH 1----------------------------------------------------//
		case ZONE_HIG1: {
			switch (calculatorRequest.getPurposeCode()) {

			case PURPOSE_RPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(new BigDecimal(10)));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						AREA1.multiply(new BigDecimal(72.867)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(340.291)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal(AREA1.multiply((AREA)).multiply(new BigDecimal(125))
						.add(AREA2.multiply((AREA)).multiply(new BigDecimal(1225))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						AREA1.multiply((AREA)).multiply(new BigDecimal(375)).add(AREA2.multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply(new BigDecimal(750))));

				break;
			case PURPOSE_ITP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						AREA1.multiply(new BigDecimal(312.914)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(374.747)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal(AREA1.multiply(AREA).multiply(new BigDecimal(80))
						.add(AREA2.multiply((AREA)).multiply(new BigDecimal(1050))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(AREA1.multiply(AREA)
						.multiply(new BigDecimal(190)).multiply(new BigDecimal(2.5)).add(AREA2.multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply((new BigDecimal(7.5)))));
				break;
			case PURPOSE_IPULP:
				break;

			case PURPOSE_ITC:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						AREA1.multiply(new BigDecimal(312.914)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(374.747)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal(AREA1.multiply((AREA)).multiply((new BigDecimal(80)))
						.add((AREA2).multiply((AREA)).multiply(new BigDecimal(1050))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((AREA1).multiply((AREA))
						.multiply((new BigDecimal(190)).multiply(new BigDecimal(2.5))).add(AREA2.multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply((new BigDecimal(7.5)))));

				break;
			case PURPOSE_IPA:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(PART1.multiply(new BigDecimal(187.374)).multiply(new BigDecimal(100000))
								.add(PART2.multiply(new BigDecimal(374.747)).multiply(new BigDecimal(100000)))
								.add(PART3.multiply(new BigDecimal(374.747)).multiply(new BigDecimal(100000)))));
				feesTypeCalculationDto.setConversionChargesCal((PART1.multiply((AREA)).multiply(new BigDecimal(80))
						.add(PART2.multiply((AREA)).multiply(new BigDecimal(125)))
						.add(PART3.multiply((AREA)).multiply(new BigDecimal(1050)))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((PART1.multiply((AREA))
						.multiply((new BigDecimal(190)).multiply(stateInfrastructureDevelopmentCharges)
								.add(PART2.multiply((AREA)).multiply(new BigDecimal(460))
										.multiply((new BigDecimal(1.75)))
										.add(PART3.multiply((AREA)).multiply(new BigDecimal(750))
												.multiply(stateInfrastructureDevelopmentCharges))))));

				break;
			case PURPOSE_RGP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(PERCENTAGE0995).multiply(RATE40));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(new BigDecimal(0.995).multiply(new BigDecimal(281.06)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(374.747)).multiply(new BigDecimal(100000)))));
				feesTypeCalculationDto
						.setConversionChargesCal(new BigDecimal(0.995).multiply((AREA)).multiply(new BigDecimal(125))
								.add(new BigDecimal(0.005).multiply((AREA)).multiply(new BigDecimal(1050))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(0.995)
						.multiply((AREA)).multiply(new BigDecimal(460)).multiply(new BigDecimal(1.75))
						.add(new BigDecimal(0.005).multiply((AREA)).multiply(new BigDecimal(750))
								.multiply(stateInfrastructureDevelopmentCharges)));

				break;

			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE).multiply(PERCENTAGE075));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						((AREA1.multiply(new BigDecimal(93.687)).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(437.517).multiply(new BigDecimal(100000)))
								.multiply(externalDevelopmentCharges))));
				feesTypeCalculationDto.setConversionChargesCal(
						AREA1.multiply(new BigDecimal(125)).multiply((AREA)).multiply(conversionCharges)
								.add(AREA2.multiply((AREA)).multiply(conversionCharges).multiply(new BigDecimal(122))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(AREA1.multiply(new BigDecimal(375)).multiply((AREA)).multiply(new BigDecimal(0.75))
								.add(AREA2.multiply((AREA)).multiply(stateInfrastructureDevelopmentCharges)
										.multiply(new BigDecimal(750)).multiply(new BigDecimal(0.75)))));

				break;
			case PURPOSE_NILPC:
				break;
			case PURPOSE_TODGH:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(AREA1).multiply(new BigDecimal(93.687)).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(437.517).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal((AREA1).multiply(AREA).multiply(new BigDecimal(125))
						.add(AREA2).multiply(AREA).multiply(new BigDecimal(1225)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(0));
				break;

			case PURPOSE_MLU_CZ:
				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(arce)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE250));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(new BigDecimal(0.1)
						.multiply((AREA1).multiply(new BigDecimal(93.687)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(437.517)).multiply(new BigDecimal(100000)))));
				feesTypeCalculationDto.setConversionChargesCal(new BigDecimal(2).multiply((AREA1).multiply((AREA))
						.multiply(new BigDecimal(125)).add(AREA2.multiply((AREA)).multiply(new BigDecimal(1225)))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(AREA1).multiply((AREA)).multiply(new BigDecimal(375)).add((AREA2.multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply(new BigDecimal(750)))));

				break;

			case PURPOSE_NILP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(PERCENTAGE125));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE19).multiply(RATE5).divide(RATE7, 0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal((AREA1.multiply(new BigDecimal(281.06))
						.multiply(new BigDecimal(100000)).multiply(new BigDecimal(5))).divide(new BigDecimal(7), 0)
						.add(AREA2.multiply(new BigDecimal(437.517)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal(
						(AREA1.multiply(new BigDecimal(125)).multiply(new BigDecimal(5))).divide(new BigDecimal(7), 0)
								.multiply((AREA)).add(AREA2.multiply((AREA)).multiply(new BigDecimal(1225))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((AREA1.multiply(new BigDecimal(460))
						.multiply((AREA)).multiply(stateInfrastructureDevelopmentCharges).multiply(new BigDecimal(5)))
						.divide(new BigDecimal(7), 0).add(stateInfrastructureDevelopmentCharges.multiply(AREA2)
								.multiply((AREA)).multiply(new BigDecimal(750))));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(scrutinyFeeCharges));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:

				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE270));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(((new BigDecimal(calculatorRequest.getTotalLandSize()))
								.multiply(new BigDecimal(374.747)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto
						.setConversionChargesCal(new BigDecimal(1).multiply(AREA).multiply(new BigDecimal(1050)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(((new BigDecimal(1)).multiply((AREA))
						.multiply(new BigDecimal(750)).multiply(stateInfrastructureDevelopmentCharges)));

				break;
			case PURPOSE_CPRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE270));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(((new BigDecimal(calculatorRequest.getTotalLandSize()))
								.multiply(new BigDecimal(374.747)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto
						.setConversionChargesCal(((new BigDecimal(1)).multiply((AREA)).multiply(new BigDecimal(1050))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(((new BigDecimal(1)).multiply((AREA))
						.multiply(new BigDecimal(750)).multiply(stateInfrastructureDevelopmentCharges)));

				break;
			case PURPOSE_CICS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE270));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						((new BigDecimal(1)).multiply(new BigDecimal(437.517)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto
						.setConversionChargesCal(((new BigDecimal(1)).multiply(AREA).multiply(new BigDecimal(1225))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((new BigDecimal(1)).multiply(AREA)
						.multiply(new BigDecimal(1.75)).multiply(stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE270));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						new BigDecimal(1).multiply(new BigDecimal(437.517)).multiply(new BigDecimal(100000)));
				feesTypeCalculationDto
						.setConversionChargesCal(new BigDecimal(1).multiply(AREA).multiply(new BigDecimal(1225)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(1).multiply(AREA)
						.multiply(new BigDecimal(1.75)).multiply(stateInfrastructureDevelopmentCharges));
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
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(new BigDecimal(10)));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE95));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						AREA1.multiply(new BigDecimal(72.867)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(340.291)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal(AREA1.multiply((AREA)).multiply(new BigDecimal(125))
						.add(AREA2.multiply((AREA)).multiply(new BigDecimal(1225))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						AREA1.multiply((AREA)).multiply(new BigDecimal(375)).add(AREA2.multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply(new BigDecimal(750))));

				break;

			case PURPOSE_ITP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE125));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						AREA1.multiply(new BigDecimal(243.377)).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(291.47)).multiply(new BigDecimal(100000)));
				feesTypeCalculationDto.setConversionChargesCal((AREA1).multiply((AREA)).multiply(new BigDecimal(80))
						.add((AREA2).multiply((AREA)).multiply(new BigDecimal(1050))));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((AREA1).multiply((AREA))
						.multiply((new BigDecimal(190)).multiply(new BigDecimal(2.5))).add((AREA2).multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply((new BigDecimal(7.5)))));
				break;
			case PURPOSE_IPULP:
				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE125));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						AREA1.multiply(new BigDecimal(243.377)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(291.47)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal((AREA1).multiply((AREA)).multiply((new BigDecimal(80)))
						.add((AREA2).multiply((AREA)).multiply(new BigDecimal(1050))));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((AREA1).multiply((AREA))
						.multiply((new BigDecimal(190)).multiply(new BigDecimal(2.5))).add((AREA2).multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply((new BigDecimal(7.5)))));

				break;
			case PURPOSE_IPA:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE125));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(PART1.multiply(new BigDecimal(145.734)).multiply(new BigDecimal(100000))
								.add(PART2.multiply(new BigDecimal(291.47)).multiply(new BigDecimal(100000)))
								.add(PART3.multiply(new BigDecimal(291.47)).multiply(new BigDecimal(100000)))));

				feesTypeCalculationDto.setConversionChargesCal(PART1.multiply((AREA)).multiply(new BigDecimal(80))
						.add(PART2.multiply((AREA)).multiply(new BigDecimal(125)))
						.add(PART3.multiply((AREA)).multiply(new BigDecimal(1050))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((PART1.multiply((AREA))
						.multiply(new BigDecimal(190)).multiply(stateInfrastructureDevelopmentCharges)
						.add(PART2.multiply((AREA)).multiply(new BigDecimal(460)).multiply((new BigDecimal(1.75)))
								.add(PART3.multiply((AREA)).multiply(new BigDecimal(750))
										.multiply(stateInfrastructureDevelopmentCharges)))));

				break;

			case PURPOSE_RGP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(PERCENTAGE0995).multiply(RATE19));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(new BigDecimal(0.995).multiply(new BigDecimal(187.373)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(249.831)).multiply(new BigDecimal(100000)))));
				feesTypeCalculationDto
						.setConversionChargesCal(new BigDecimal(0.995).multiply((AREA)).multiply(new BigDecimal(125))
								.add(new BigDecimal(0.005).multiply((AREA)).multiply(new BigDecimal(1050))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						new BigDecimal(0.995).multiply((AREA)).multiply(new BigDecimal(460))
								.multiply((new BigDecimal(1.75))
										.add(new BigDecimal(0.005).multiply((AREA)).multiply(new BigDecimal(750))
												.multiply(stateInfrastructureDevelopmentCharges))));

				break;

			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE950).multiply(PERCENTAGE075));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						((AREA1).multiply(new BigDecimal(72.867)).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(340.291)).multiply(new BigDecimal(100000)))
								.multiply(externalDevelopmentCharges));
				feesTypeCalculationDto.setConversionChargesCal(
						(AREA1).multiply(new BigDecimal(125)).multiply((AREA)).multiply(conversionCharges).add(
								AREA2.multiply((AREA)).multiply(conversionCharges).multiply(new BigDecimal(1225))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(AREA1).multiply(new BigDecimal(375)).multiply((AREA)).multiply(new BigDecimal(0.75))
								.add(AREA2.multiply((AREA)).multiply(stateInfrastructureDevelopmentCharges)
										.multiply(new BigDecimal(750)).multiply(new BigDecimal(0.75))));

				break;
			case PURPOSE_NILPC:
				break;
			case PURPOSE_TODGH:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(AREA1).multiply(new BigDecimal(72.867).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(340.291)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal((AREA1).multiply(AREA).multiply(new BigDecimal(125))
						.add(AREA2.multiply(AREA).multiply(new BigDecimal(1225))));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(0));
				break;

			case PURPOSE_MLU_CZ:
				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(arce)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE19));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal((new BigDecimal(0.1))
						.multiply((AREA1).multiply(new BigDecimal(72.867)).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(340.291)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal(new BigDecimal(2).multiply((AREA1).multiply((AREA))
						.multiply(new BigDecimal(125)).add(AREA2.multiply((AREA)).multiply(new BigDecimal(1225)))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(AREA1).multiply((AREA)).multiply(new BigDecimal(375).add((AREA2).multiply(AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply(new BigDecimal(750))));

				break;
			case PURPOSE_NILP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(PERCENTAGE125));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE19).multiply(RATE5).divide(RATE7, 0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal((AREA1.multiply(new BigDecimal(218.602))
						.multiply(new BigDecimal(100000)).multiply(new BigDecimal(5))).divide(new BigDecimal(7), 0)
						.add(AREA2.multiply(new BigDecimal(340.291)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal(
						(AREA1.multiply(new BigDecimal(125)).multiply(new BigDecimal(5))).divide(new BigDecimal(7), 0)
								.multiply((AREA)).add(AREA2).multiply((AREA)).multiply(new BigDecimal(1225)));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(scrutinyFeeCharges));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE210));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal((new BigDecimal(calculatorRequest.getTotalLandSize()))
								.multiply(new BigDecimal(291.47)).multiply(new BigDecimal(100000)));
				feesTypeCalculationDto
						.setConversionChargesCal(((new BigDecimal(1)).multiply((AREA)).multiply(new BigDecimal(1050))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(((new BigDecimal(1)).multiply((AREA))
						.multiply(new BigDecimal(750)).multiply(stateInfrastructureDevelopmentCharges)));

				break;
			case PURPOSE_CPRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE210));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(new BigDecimal(calculatorRequest.getTotalLandSize())
								.multiply(new BigDecimal(291.47).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto
						.setConversionChargesCal((new BigDecimal(1)).multiply((AREA)).multiply(new BigDecimal(1050)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(((new BigDecimal(1)).multiply((AREA))
						.multiply(new BigDecimal(750)).multiply(stateInfrastructureDevelopmentCharges)));

				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE210));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						((new BigDecimal(1)).multiply(new BigDecimal(340.291)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto
						.setConversionChargesCal(((new BigDecimal(1)).multiply(AREA).multiply(new BigDecimal(1225))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(((new BigDecimal(1)).multiply(AREA)
						.multiply(new BigDecimal(750)).multiply(stateInfrastructureDevelopmentCharges)));

				break;
			case PURPOSE_CICS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE210));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						((new BigDecimal(1)).multiply(new BigDecimal(340.291)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto
						.setConversionChargesCal((new BigDecimal(1)).multiply(AREA).multiply(new BigDecimal(1225)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(((new BigDecimal(1)).multiply(AREA)
						.multiply(new BigDecimal(750)).multiply(stateInfrastructureDevelopmentCharges)));

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
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(new BigDecimal(10)));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE625));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(AREA1.multiply(new BigDecimal(62.458)).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(291.678)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal((AREA1).multiply((AREA)).multiply((new BigDecimal(80)))
						.add((AREA2).multiply((AREA)).multiply(new BigDecimal(700))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(AREA1.multiply((AREA)).multiply(new BigDecimal(250)).add((AREA2.multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply(new BigDecimal(500))))));

				break;
			case PURPOSE_ITP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE62500));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						AREA1.multiply(new BigDecimal(208.069)).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(249.833)).multiply(new BigDecimal(100000)));
				feesTypeCalculationDto.setConversionChargesCal((AREA1).multiply((AREA)).multiply(new BigDecimal(50))
						.add((AREA2).multiply((AREA)).multiply(new BigDecimal(600))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((AREA1).multiply((AREA))
						.multiply(new BigDecimal(125)).multiply(new BigDecimal(2.5)).add((AREA2).multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply(new BigDecimal(5))));

				break;
			case PURPOSE_IPULP:
				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE62500));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						AREA1.multiply(new BigDecimal(208.069)).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(249.833)).multiply(new BigDecimal(100000)));
				feesTypeCalculationDto.setConversionChargesCal((AREA1).multiply((AREA)).multiply(new BigDecimal(50))
						.add((AREA2).multiply((AREA)).multiply(new BigDecimal(600))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((AREA1).multiply((AREA))
						.multiply(new BigDecimal(125)).multiply(new BigDecimal(2.5)).add((AREA2).multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply(new BigDecimal(5))));

				break;
			case PURPOSE_IPA:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE62500));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(PART1.multiply(new BigDecimal(124.916)).multiply(new BigDecimal(100000))
								.add(PART2.multiply(new BigDecimal(249.831)).multiply(new BigDecimal(100000)))
								.add(PART3.multiply(new BigDecimal(249.831)).multiply(new BigDecimal(100000)))));
				feesTypeCalculationDto.setConversionChargesCal(PART1.multiply((AREA)).multiply(new BigDecimal(50))
						.add(PART2.multiply((AREA)).multiply(new BigDecimal(80)))
						.add(PART3.multiply((AREA)).multiply(new BigDecimal(600))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((PART1.multiply((AREA))
						.multiply(new BigDecimal(125)).multiply(stateInfrastructureDevelopmentCharges)
						.add(PART2.multiply((AREA)).multiply(new BigDecimal(320)).multiply((new BigDecimal(1.75)))
								.add(PART3.multiply((AREA)).multiply(new BigDecimal(500))
										.multiply(stateInfrastructureDevelopmentCharges)))));

				break;

			case PURPOSE_RGP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(PERCENTAGE0995).multiply(RATE95));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						new BigDecimal(0.995).multiply(new BigDecimal(187.373)).multiply(new BigDecimal(100000))
								.add(AREA2).multiply(new BigDecimal(249.831)).multiply(new BigDecimal(100000)));

				feesTypeCalculationDto
						.setConversionChargesCal(new BigDecimal(0.995).multiply((AREA)).multiply(new BigDecimal(80))
								.add(new BigDecimal(0.005).multiply((AREA)).multiply(new BigDecimal(600))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(0.995)
						.multiply((AREA)).multiply(new BigDecimal(320)).multiply(new BigDecimal(1.75))
						.add(new BigDecimal(0.005).multiply((AREA)).multiply(new BigDecimal(500))
								.multiply(stateInfrastructureDevelopmentCharges)));

				break;

			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE10000));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal((new BigDecimal(0.995).multiply(new BigDecimal(62.458))
								.multiply(new BigDecimal(100000)).add(AREA2).multiply(new BigDecimal(291.678))
								.multiply(new BigDecimal(100000)).multiply(new BigDecimal(0.5))));
				feesTypeCalculationDto.setConversionChargesCal(new BigDecimal(0));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(0));

				break;
			case PURPOSE_NILPC:
				break;
			case PURPOSE_TODGH:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(AREA1).multiply(new BigDecimal(62.458)).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(291.678)).multiply(new BigDecimal(100000)));
				feesTypeCalculationDto.setConversionChargesCal((AREA1).multiply(AREA).multiply(new BigDecimal(80))
						.add(AREA2.multiply(AREA).multiply(new BigDecimal(700))));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(0));
				break;

			case PURPOSE_MLU_CZ:
				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(arce)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(new BigDecimal(0.1)
						.multiply((AREA1).multiply(new BigDecimal(62.458)).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(291.678)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal(new BigDecimal(2).multiply((AREA1).multiply((AREA))
						.multiply((new BigDecimal(80)).add(AREA2).multiply((AREA)).multiply(new BigDecimal(700)))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(AREA1).multiply((AREA)).multiply(new BigDecimal(250).add((AREA2).multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply(new BigDecimal(500)))));

				break;
			case PURPOSE_NILP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(PERCENTAGE125));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE95).multiply(RATE5).divide(RATE7, 0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(((AREA1).multiply(new BigDecimal(187.373))
						.multiply(new BigDecimal(100000)).multiply(new BigDecimal(5))).divide(new BigDecimal(7), 0)
						.add(AREA2.multiply(new BigDecimal(291.678)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal(
						(AREA1).multiply((new BigDecimal(80)).multiply(new BigDecimal(5))).divide(new BigDecimal(7), 0)
								.multiply((AREA)).add(AREA2.multiply((AREA)).multiply(new BigDecimal(700))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((AREA1).multiply(new BigDecimal(320))
						.multiply((AREA)).multiply(stateInfrastructureDevelopmentCharges).multiply(new BigDecimal(5))
						.divide(new BigDecimal(7), 0).add(stateInfrastructureDevelopmentCharges.multiply(AREA2)
								.multiply((AREA)).multiply(new BigDecimal(500))));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(scrutinyFeeCharges));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE95));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal((new BigDecimal(calculatorRequest.getTotalLandSize())
								.multiply(new BigDecimal(249.831)).multiply(new BigDecimal(100000))));

				feesTypeCalculationDto
						.setConversionChargesCal(((new BigDecimal(1)).multiply((AREA)).multiply(new BigDecimal(600))));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(((new BigDecimal(1)).multiply((AREA))
						.multiply(new BigDecimal(500)).multiply(stateInfrastructureDevelopmentCharges)));

				break;
			case PURPOSE_CPRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE95));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal((new BigDecimal(calculatorRequest.getTotalLandSize())
								.multiply(new BigDecimal(249.831)).multiply(new BigDecimal(100000))));

				feesTypeCalculationDto
						.setConversionChargesCal(new BigDecimal(1).multiply((AREA)).multiply(new BigDecimal(600)));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(((new BigDecimal(1)).multiply((AREA))
						.multiply(new BigDecimal(500)).multiply(stateInfrastructureDevelopmentCharges)));

				break;
			case PURPOSE_CICS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE95));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(new BigDecimal(1)).multiply(new BigDecimal(291.678)).multiply(new BigDecimal(100000)));

				feesTypeCalculationDto
						.setConversionChargesCal(((new BigDecimal(1)).multiply(AREA).multiply(new BigDecimal(700))));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(((new BigDecimal(1)).multiply(AREA)
						.multiply(new BigDecimal(500)).multiply(stateInfrastructureDevelopmentCharges)));

				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE95));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						((new BigDecimal(1)).multiply(new BigDecimal(291.678)).multiply(new BigDecimal(100000))));

				feesTypeCalculationDto
						.setConversionChargesCal(((new BigDecimal(1)).multiply(AREA).multiply(new BigDecimal(700))));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(((new BigDecimal(1)).multiply(AREA)
						.multiply(new BigDecimal(500)).multiply(stateInfrastructureDevelopmentCharges)));

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
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(new BigDecimal(10)));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE125));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(AREA1).multiply(new BigDecimal(52.048).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(243.065f).multiply(new BigDecimal(100000)))));
				feesTypeCalculationDto.setConversionChargesCal((AREA1).multiply((AREA)).multiply(new BigDecimal(20))
						.add((AREA2).multiply((AREA)).multiply(new BigDecimal(175))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(AREA1).multiply((AREA)).multiply(new BigDecimal(70)).add((AREA2).multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply((new BigDecimal(190)))));

				break;
			case PURPOSE_ITP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE12500));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						AREA1.multiply(new BigDecimal(173.841)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(208.193)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal(AREA1.multiply((AREA)).multiply(new BigDecimal(30))
						.add(AREA2.multiply((AREA)).multiply(new BigDecimal(150))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((AREA1).multiply(
						(AREA).multiply(new BigDecimal(35)).multiply(new BigDecimal(2.5)).add((AREA2).multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply(new BigDecimal(1.9)))));
				break;

			case PURPOSE_IPULP:
				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE12500));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						AREA1.multiply(new BigDecimal(173.841)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(208.193)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal(AREA1.multiply((AREA)).multiply(new BigDecimal(30))
						.add((AREA2.multiply((AREA)).multiply(new BigDecimal(150)))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((AREA1).multiply((AREA))
						.multiply(new BigDecimal(35).multiply(new BigDecimal(2.5)).add(AREA2.multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply(new BigDecimal(1.9)))));

				break;
			case PURPOSE_IPA:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE12500));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(PART1.multiply(new BigDecimal(104.096)).multiply(new BigDecimal(100000))
								.add(PART2.multiply(new BigDecimal(208.193)).multiply(new BigDecimal(100000)))
								.add(PART3.multiply(new BigDecimal(208.193)).multiply(new BigDecimal(100000)))));
				feesTypeCalculationDto.setConversionChargesCal((PART1.multiply((AREA)).multiply(new BigDecimal(30))
						.add(PART2.multiply((AREA)).multiply(new BigDecimal(20)))
						.add(PART3.multiply((AREA)).multiply(new BigDecimal(150)))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((PART1.multiply((AREA))
						.multiply(new BigDecimal(35).multiply(stateInfrastructureDevelopmentCharges)
								.add(PART2.multiply((AREA)).multiply(new BigDecimal(90)).multiply(new BigDecimal(1.75)))
								.add(PART3.multiply((AREA)).multiply(new BigDecimal(190))
										.multiply(stateInfrastructureDevelopmentCharges)))));

				break;

			case PURPOSE_RGP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(PERCENTAGE0995).multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						new BigDecimal(0.995).multiply(new BigDecimal(156.145)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(208.193)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto
						.setConversionChargesCal(new BigDecimal(0.995).multiply((AREA)).multiply(new BigDecimal(20))
								.add(new BigDecimal(0.005).multiply((AREA)).multiply(new BigDecimal(150))));
				feesTypeCalculationDto
						.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(0.995).multiply((AREA))
								.multiply(new BigDecimal(90).multiply((new BigDecimal(1.75))
										.add(new BigDecimal(0.005).multiply((AREA)).multiply(new BigDecimal(190))
												.multiply(stateInfrastructureDevelopmentCharges)))));

				break;
			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE10000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						((AREA1).multiply(new BigDecimal(52.048).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(243.065).multiply(new BigDecimal(100000)))
								.multiply(new BigDecimal(0.25)))));
				feesTypeCalculationDto.setConversionChargesCal(new BigDecimal(0));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(0));

				break;
			case PURPOSE_NILPC:
				break;
			case PURPOSE_TODGH:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(AREA1).multiply(new BigDecimal(52.048).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(243.065).multiply(new BigDecimal(100000)))));
				feesTypeCalculationDto.setConversionChargesCal((AREA1).multiply(AREA)
						.multiply(new BigDecimal(20).add(AREA2).multiply(AREA).multiply(new BigDecimal(175))));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(0));
				break;

			case PURPOSE_MLU_CZ:
				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(arce)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(new BigDecimal(0.1)
						.multiply((AREA1).multiply(new BigDecimal(52.048)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(243.065)).multiply(new BigDecimal(100000)))));
				feesTypeCalculationDto.setConversionChargesCal(new BigDecimal(2).multiply((AREA1).multiply((AREA))
						.multiply(new BigDecimal(20)).add(AREA2.multiply((AREA)).multiply(new BigDecimal(175)))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(AREA1).multiply((AREA)).multiply(new BigDecimal(70)).add((AREA2.multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply((new BigDecimal(190))))));

				break;
			case PURPOSE_NILP:

				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(PERCENTAGE125));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25).multiply(RATE5).divide(RATE7, 0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal((AREA1.multiply(new BigDecimal(156.145))
						.multiply(new BigDecimal(100000)).multiply(new BigDecimal(5))).divide(new BigDecimal(7), 0)
						.add(AREA2.multiply(new BigDecimal(243.065)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal(
						(AREA1.multiply(new BigDecimal(20).multiply(new BigDecimal(5))).divide(new BigDecimal(7), 0)
								.multiply((AREA)).add(AREA2).multiply((AREA)).multiply(new BigDecimal(175))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((AREA1)
						.multiply(new BigDecimal(90).multiply((AREA)).multiply(stateInfrastructureDevelopmentCharges)
								.multiply(new BigDecimal(5)).divide(new BigDecimal(7), 0))
						.add(stateInfrastructureDevelopmentCharges.multiply(AREA2).multiply((AREA))
								.multiply((new BigDecimal(190)))));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(scrutinyFeeCharges));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE19));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(new BigDecimal(calculatorRequest.getTotalLandSize())
								.multiply(new BigDecimal(208.193).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto
						.setConversionChargesCal(new BigDecimal(1).multiply((AREA)).multiply(new BigDecimal(150)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((new BigDecimal(1)).multiply((AREA))
						.multiply((new BigDecimal(190)).multiply(stateInfrastructureDevelopmentCharges)));

				break;
			case PURPOSE_CPRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE19));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(new BigDecimal(calculatorRequest.getTotalLandSize())
								.multiply(new BigDecimal(208.193)).multiply(new BigDecimal(100000)));
				feesTypeCalculationDto
						.setConversionChargesCal(new BigDecimal(1).multiply((AREA)).multiply(new BigDecimal(150)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(1).multiply((AREA))
						.multiply((new BigDecimal(190)).multiply(stateInfrastructureDevelopmentCharges)));

				break;

			case PURPOSE_RHP:
				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE19));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						new BigDecimal(1).multiply(new BigDecimal(243.065).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto
						.setConversionChargesCal(new BigDecimal(1).multiply(AREA).multiply(new BigDecimal(175)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(1).multiply(AREA)
						.multiply((new BigDecimal(190)).multiply(stateInfrastructureDevelopmentCharges)));
				break;
			case PURPOSE_CICS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE19));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						new BigDecimal(1).multiply(new BigDecimal(243.065).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto
						.setConversionChargesCal(new BigDecimal(1).multiply(AREA).multiply(new BigDecimal(175)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(1).multiply(AREA)
						.multiply((new BigDecimal(190)).multiply(stateInfrastructureDevelopmentCharges)));
				break;
			}

			break;
		}

		/// ------------------low2-------------------------------//
		case ZONE_LOW2: {
			switch (calculatorRequest.getPurposeCode()) {
			case PURPOSE_RPL:

				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(new BigDecimal(10)));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE125));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(AREA1).multiply(new BigDecimal(41.639)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(194.452)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal((AREA1).multiply((AREA)).multiply(new BigDecimal(20))
						.add(AREA2.multiply((AREA)).multiply(new BigDecimal(175))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(AREA1).multiply((AREA)).multiply(new BigDecimal(70)).add((AREA2).multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply((new BigDecimal(190)))));

				break;
			case PURPOSE_ITP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE12500));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						AREA1.multiply(new BigDecimal(139.073)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(166.554)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal((AREA1).multiply((AREA)).multiply(new BigDecimal(30))
						.add((AREA2).multiply((AREA)).multiply(new BigDecimal(150))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((AREA1).multiply((AREA))
						.multiply(new BigDecimal(35)).multiply(new BigDecimal(2.5)).add((AREA2).multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply((new BigDecimal(1.9)))));
				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE12500));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						AREA1.multiply(new BigDecimal(139.073)).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(166.554)).multiply(new BigDecimal(100000)));
				feesTypeCalculationDto.setConversionChargesCal((AREA1).multiply((AREA)).multiply(new BigDecimal(30))
						.add(AREA2.multiply((AREA)).multiply(new BigDecimal(150))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((AREA1).multiply((AREA))
						.multiply(new BigDecimal(35)).multiply(new BigDecimal(2.5)).add(AREA2.multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply((new BigDecimal(1.9)))));

				break;
			case PURPOSE_IPULP:
				break;

			case PURPOSE_IPA:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE12500));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(PART1.multiply(new BigDecimal(83.278)).multiply(new BigDecimal(100000))
								.add(PART2.multiply(new BigDecimal(166.554)).multiply(new BigDecimal(100000)))
								.add(PART3.multiply(new BigDecimal(166.554)).multiply(new BigDecimal(100000)))));
				feesTypeCalculationDto.setConversionChargesCal((PART1.multiply((AREA)).multiply(new BigDecimal(30))
						.add(PART2.multiply((AREA)).multiply(new BigDecimal(20)))
						.add(PART3.multiply((AREA)).multiply(new BigDecimal(150)))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(PART1.multiply((AREA))
						.multiply(new BigDecimal(35).multiply(stateInfrastructureDevelopmentCharges)
								.add(PART2.multiply((AREA)).multiply(new BigDecimal(90)).multiply(new BigDecimal(1.75)))
								.add(PART3.multiply((AREA)).multiply(new BigDecimal(190))
										.multiply(stateInfrastructureDevelopmentCharges))));

				break;

			case PURPOSE_RGP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(PERCENTAGE0995).multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						new BigDecimal(0.995).multiply(new BigDecimal(124.916)).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(166.554)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto
						.setConversionChargesCal((new BigDecimal(0.995).multiply((AREA)).multiply(new BigDecimal(20))
								.add(new BigDecimal(0.005).multiply((AREA)).multiply(new BigDecimal(150)))));
				feesTypeCalculationDto
						.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(0.995).multiply((AREA))
								.multiply(new BigDecimal(90).multiply((new BigDecimal(1.75))
										.add(new BigDecimal(0.005).multiply((AREA)).multiply(new BigDecimal(190))
												.multiply(stateInfrastructureDevelopmentCharges)))));

				break;

			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE10000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal((AREA1).multiply(new BigDecimal(41.639)
						.multiply(new BigDecimal(100000)).add(AREA2.multiply(new BigDecimal(194.452))
								.multiply(new BigDecimal(100000)).multiply(new BigDecimal(0.25)))));
				feesTypeCalculationDto.setConversionChargesCal(new BigDecimal(0));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(0));

				break;
			case PURPOSE_NILPC:
				break;
			case PURPOSE_TODGH:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(AREA1).multiply(new BigDecimal(41.639)).multiply(new BigDecimal(100000)).add(AREA2)
								.multiply(new BigDecimal(194.452).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto.setConversionChargesCal((AREA1).multiply(AREA)
						.multiply(new BigDecimal(20).add(AREA2).multiply(AREA).multiply(new BigDecimal(175))));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(0));
				break;

			case PURPOSE_MLU_CZ:
				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(arce)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(new BigDecimal(0.1).multiply(AREA1)
						.multiply(new BigDecimal(41.639).multiply(new BigDecimal(100000))
								.add(AREA2.multiply(new BigDecimal(194.452)).multiply(new BigDecimal(100000)))));
				feesTypeCalculationDto.setConversionChargesCal(new BigDecimal(2).multiply(AREA1).multiply((AREA))
						.multiply(new BigDecimal(20)).add(AREA2.multiply((AREA)).multiply(new BigDecimal(175))));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(AREA1).multiply((AREA)).multiply(new BigDecimal(70)).add(AREA2.multiply((AREA))
								.multiply(stateInfrastructureDevelopmentCharges).multiply((new BigDecimal(190)))));

				break;
			case PURPOSE_NILP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(PERCENTAGE125));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25).multiply(RATE5).divide(RATE7, 0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal((AREA1.multiply(new BigDecimal(124.916))
						.multiply(new BigDecimal(100000)).multiply(new BigDecimal(5))).divide(new BigDecimal(7), 0)
						.add(AREA2.multiply(new BigDecimal(194.452).multiply(new BigDecimal(100000)))));
				feesTypeCalculationDto.setConversionChargesCal(
						(AREA1.multiply(new BigDecimal(20).multiply(new BigDecimal(5))).divide(new BigDecimal(7), 0)
								.multiply((AREA)).add(AREA2).multiply((AREA)).multiply(new BigDecimal(175))));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((AREA1)
						.multiply(new BigDecimal(90).multiply((AREA)).multiply(stateInfrastructureDevelopmentCharges)
								.multiply(new BigDecimal(5)))
						.divide(new BigDecimal(7), 0).add(stateInfrastructureDevelopmentCharges.multiply(AREA2)
								.multiply((AREA)).multiply(new BigDecimal(190))));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(scrutinyFeeCharges));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE19));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal((new BigDecimal(calculatorRequest.getTotalLandSize())
								.multiply(new BigDecimal(166.554)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto
						.setConversionChargesCal(new BigDecimal(1).multiply((AREA)).multiply(new BigDecimal(150)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(((new BigDecimal(1)).multiply((AREA))
						.multiply(new BigDecimal(190)).multiply(stateInfrastructureDevelopmentCharges)));

				break;
			case PURPOSE_CPRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE19));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal((new BigDecimal(calculatorRequest.getTotalLandSize())
								.multiply(new BigDecimal(166.554)).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto
						.setConversionChargesCal(new BigDecimal(1).multiply((AREA)).multiply(new BigDecimal(150)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(((new BigDecimal(1)).multiply((AREA))
						.multiply(new BigDecimal(190)).multiply(stateInfrastructureDevelopmentCharges)));

				break;
			case PURPOSE_RHP:
				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE19));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						new BigDecimal(1).multiply(new BigDecimal(194.452).multiply(new BigDecimal(100000))));
				feesTypeCalculationDto
						.setConversionChargesCal(new BigDecimal(1).multiply(AREA).multiply(new BigDecimal(175)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(1).multiply(AREA)
						.multiply((new BigDecimal(190)).multiply(stateInfrastructureDevelopmentCharges)));
				break;
			case PURPOSE_CICS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE19));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(new BigDecimal(1)).multiply(new BigDecimal(194.452)).multiply(new BigDecimal(100000)));
				feesTypeCalculationDto
						.setConversionChargesCal((new BigDecimal(1)).multiply(AREA).multiply(new BigDecimal(175)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((new BigDecimal(1).multiply(AREA)
						.multiply(new BigDecimal(190)).multiply(stateInfrastructureDevelopmentCharges)));
				break;
			}

			break;
		}
		}

		return feesTypeCalculationDto;

	}

}