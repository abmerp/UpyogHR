package org.egov.tlcalculator.service;

import java.util.LinkedHashMap;
import java.util.List;
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

	private double areaInSqmtr(String arce) {
		return (AREA * Double.valueOf(arce));
	}

	public FeesTypeCalculationDto feesTypeCalculation(RequestInfo requestInfo, CalculatorRequest calculatorRequest) {
		double arce = Double.valueOf(calculatorRequest.getTotalLandSize());
		double area1 = (PERCENTAGE1 * Double.valueOf(calculatorRequest.getTotalLandSize()));
		double area2 = PERCENTAGE2 * Double.valueOf(calculatorRequest.getTotalLandSize());

		Map<String, List<String>> mdmsData;
		FeesTypeCalculationDto feesTypeCalculationDto = new FeesTypeCalculationDto();
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> mDMSCallPurposeCode = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>) landUtil
				.mDMSCallPurposeCode(requestInfo, requestInfo.getUserInfo().getTenantId(),
						calculatorRequest.getPurposeCode());

		mdmsData = valid.getAttributeValues(mDMSCallPurposeCode);
		List<Map<String, Object>> msp = (List) mdmsData.get("Purpose");
		Double externalDevelopmentCharges = null;
		Double scrutinyFeeCharges = null;
		Double conversionCharges = null;
		Double licenseFeeCharges = null;
		Double stateInfrastructureDevelopmentCharges = null;
		for (Map<String, Object> mm : msp) {

			scrutinyFeeCharges = Double.valueOf(String.valueOf(mm.get("scrutinyFeeCharges")));
			externalDevelopmentCharges = Double.valueOf(String.valueOf(mm.get("externalDevelopmentCharges")));
			conversionCharges = Double.valueOf(String.valueOf(mm.get("conversionCharges")));
			licenseFeeCharges = Double.valueOf(String.valueOf(mm.get("licenseFeeCharges")));
			stateInfrastructureDevelopmentCharges = Double
					.valueOf(String.valueOf(mm.get("stateInfrastructureDevelopmentCharges")));
		}
		switch (calculatorRequest.getPotenialZone()) {

		case ZONE_HYPER:

			switch (calculatorRequest.getPurposeCode()) {

			case PURPOSE_RPL:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal((AREA * area1 * 10) + AREA * area2 * scrutinyFeeCharges * 10);

				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * RATE) + (area2 * RATE1));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((area1 * 104.096 * 100000) + (area2 * 486.13 * 100000)));
				feesTypeCalculationDto.setConversionChargesCal((area1 * (AREA * 158)) + (area2 * AREA * 1470));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * AREA * 500) + (area2 * AREA * stateInfrastructureDevelopmentCharges * 1000));

				break;
			case PURPOSE_IPULP:
				break;

			case PURPOSE_ITC:

				feesTypeCalculationDto
						.setScrutinyFeeChargesCal((area1 * AREA * 2.5 * 10 + area2 * AREA * scrutinyFeeCharges * 0.1));
				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 250000 + area2 * 27000000);
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 347.682 * 100000 + area2 * 416.385 * 100000);
				feesTypeCalculationDto.setConversionChargesCal((area1 * AREA * 100) + (area2 * AREA * 1260));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((area1 * AREA * 250 * 2.5f)
						+ (area2 * AREA * stateInfrastructureDevelopmentCharges * 150 * 10));

				break;
			case PURPOSE_ITP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal((area1 * AREA * 2.5 * 10 + area2 * AREA * scrutinyFeeCharges * 0.1));
				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 250000 + area2 * 27000000);
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 347.682 * 100000 + area2 * 416.385 * 100000);
				feesTypeCalculationDto.setConversionChargesCal((area1 * AREA * 100) + (area2 * AREA * 1260));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((area1 * AREA * 250 * 2.5f)
						+ (area2 * AREA * stateInfrastructureDevelopmentCharges * 150 * 10));
				break;
			case PURPOSE_IPA:

				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (PART1 * AREA * scrutinyFeeCharges * 10
						+ PART2 * AREA * 1.75 * 10 + PART3 * AREA * scrutinyFeeCharges * 10));
				feesTypeCalculationDto
						.setLicenseFeeChargesCal((double) (PART1 * 250000 + PART2 * 4000000 + PART3 * 27000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PART1 * 208.192 * 100000 + PART2 * 416.385 * 100000 + PART3 * 416.385 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PART1 * AREA * 100 + PART2 * AREA * 158 + PART3 * AREA * 1260));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PART1 * AREA * 250 * stateInfrastructureDevelopmentCharges + PART2 * AREA * 625 * 1.75
								+ PART3 * AREA * 1000 * stateInfrastructureDevelopmentCharges));

				break;

			case PURPOSE_RGP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((AREA) * scrutinyFeeCharges * 1 * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0.995 * 4000000 + 0.005 * 27000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.995 * 312.289 * 100000 + area2 * 416.385 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (0.995 * AREA * 158 + 0.005 * AREA * 1260));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((double) (0.995 * AREA * 625 * 1.75
						+ 0.005 * AREA * 1000 * stateInfrastructureDevelopmentCharges));

				break;

			case PURPOSE_DDJAY_APHP:

				feesTypeCalculationDto
						.setScrutinyFeeChargesCal((area1 * 10 * AREA + area2 * AREA * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * RATE) + (area2 * RATE1));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 104.096 * 100000 + area2 * 486.13 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(area1 * AREA * 158 + area2 * AREA * 1470);
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * 500 * AREA + area2 * AREA * stateInfrastructureDevelopmentCharges * 1000));

				break;
			case PURPOSE_NILPC:
				break;
			case PURPOSE_TODGH:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (1 * AREA * 10 * scrutinyFeeCharges));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (4000000 * 0.995 * licenseFeeCharges / 1.75));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.995 * (Double.valueOf(calculatorRequest.getTotalLandSize()) * 416.385 * 100000
								* licenseFeeCharges / 1.75)));
				feesTypeCalculationDto.setConversionChargesCal(((double) (0.995 * 158 * AREA * conversionCharges / 1.75
						+ 0.005 * 1470 * AREA * conversionCharges / 1.75)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						((double) (0.995 * 625 * AREA * stateInfrastructureDevelopmentCharges
								+ 0.005 * 1000 * AREA * stateInfrastructureDevelopmentCharges)));

				break;

			case PURPOSE_MLU_CZ:
				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) ((double) 2 * (area1 * AREA * 10 + area2 * AREA * 1.75 * 10)));

				feesTypeCalculationDto.setLicenseFeeChargesCal((double) 2 * (area1 * 1250000 + area2 * 34000000));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.1 * (area1 * 104.096 * 100000 + area2 * 486.13 * 100000)));
				feesTypeCalculationDto
						.setConversionChargesCal((double) (2 * (area1 * AREA * 158 + area2 * AREA * 1470)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * AREA * 500) + (area2 * AREA * stateInfrastructureDevelopmentCharges * 1000));

				break;
			case PURPOSE_NILP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (area1 * AREA * 1.25 * 10 + area2 * AREA * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 4000000 * 5 / 7 + area2 * 34000000));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 312.289 * 100000 * 5 / 7 + area2 * 486.13 * 100000));
				feesTypeCalculationDto
						.setConversionChargesCal((double) (area1 * 158 * 5 / 7 * AREA + area2 * AREA * 1470));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (area1 * 625 * AREA * stateInfrastructureDevelopmentCharges * 5 / 7
								+ stateInfrastructureDevelopmentCharges * area2 * AREA * 1000));

				break;
			case PURPOSE_TODCOMM:

				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (1 * AREA * 10 * scrutinyFeeCharges));

				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (34000000 * licenseFeeCharges / 1.75));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						((double) ((Double.valueOf(calculatorRequest.getTotalLandSize()) * 486.13 * 100000
								* externalDevelopmentCharges / 1.75))));
				feesTypeCalculationDto.setConversionChargesCal(((double) (1470 * AREA * conversionCharges / 1.75)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						((double) (1000 * AREA * stateInfrastructureDevelopmentCharges)));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((AREA) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal(27000000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((Double.valueOf(calculatorRequest.getTotalLandSize()) * 416.385 * 100000)));
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * AREA * 1260));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * AREA * 1000 * stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_CPRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((AREA) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal(27000000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((Double.valueOf(calculatorRequest.getTotalLandSize()) * 416.385 * 100000)));
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * AREA * 1260));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * AREA * 1000 * stateInfrastructureDevelopmentCharges));
				break;
			case PURPOSE_CICS:

				feesTypeCalculationDto.setScrutinyFeeChargesCal((AREA) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (34000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal((double) (arce) * 486.13 * 100000);
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * AREA * 1470));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * AREA * 1000 * stateInfrastructureDevelopmentCharges));
				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((AREA) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (34000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal((double) (arce) * 486.13 * 100000);
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * AREA * 1470));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * AREA * 1000 * stateInfrastructureDevelopmentCharges));
				break;
			case PURPOSE_RHP:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(1 * AREA * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 104.096 * 100000 + area2 * 486.13 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (area1 * AREA * 158 + area2 * AREA * 1470));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((double) (0));
				break;
			}
			break;
//---------------------------------HIGH 1----------------------------------------------------//
		case ZONE_HIG1: {
			switch (calculatorRequest.getPurposeCode()) {
			case PURPOSE_RPL:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal((AREA) * area1 * 10 + (AREA) * area2 * scrutinyFeeCharges * 10);

				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 950000) + (area2 * 27000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 93.687 * 100000 + area2 * 437.517 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((area1 * (AREA) * 125) + (area2 * (AREA) * 1225));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * (AREA) * 375) + (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 750));

				break;
			case PURPOSE_ITP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(area1 * (AREA) * 2.5f * 10 + area2 * (AREA) * scrutinyFeeCharges * 0.1));
				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 125000 + area2 * 23500000);
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 312.914f * 100000 + area2 * 374.747f * 100000);
				feesTypeCalculationDto.setConversionChargesCal((area1 * (AREA) * 80) + (area2 * (AREA) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((area1 * (AREA) * 190 * 2.5f)
						+ (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 7.5f));
				break;
			case PURPOSE_IPULP:
				break;

			case PURPOSE_ITC:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(area1 * (AREA) * 2.5f * 10 + area2 * (AREA) * scrutinyFeeCharges * 0.1));
				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 125000 + area2 * 23500000);
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 312.914f * 100000 + area2 * 374.747f * 100000);
				feesTypeCalculationDto.setConversionChargesCal((area1 * (AREA) * 80) + (area2 * (AREA) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((area1 * (AREA) * 190 * 2.5f)
						+ (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 7.5f));

				break;
			case PURPOSE_IPA:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (PART1 * (AREA) * scrutinyFeeCharges * 10
						+ PART2 * AREA * 1.75 * 10 + PART3 * AREA * scrutinyFeeCharges * 10));
				feesTypeCalculationDto
						.setLicenseFeeChargesCal((double) (PART1 * 125000 + PART2 * 1900000 + PART3 * 23500000));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PART1 * 187.374 * 100000 + PART2 * 374.747 * 100000 + PART3 * 374.747 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PART1 * (AREA) * 80 + PART2 * (AREA) * 125 + PART3 * (AREA) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PART1 * (AREA) * 190 * stateInfrastructureDevelopmentCharges
								+ PART2 * (AREA) * 460 * 1.75
								+ PART3 * (AREA) * 750 * stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_RGP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 1 * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0.995 * 1900000 + 0.005 * 23500000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.995 * 281.06 * 100000 + area2 * 374.747 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (0.995 * (AREA) * 125 + 0.005 * (AREA) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((double) (0.995 * (AREA) * 460 * 1.75
						+ 0.005 * (AREA) * 750 * stateInfrastructureDevelopmentCharges));

				break;

			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(area1 * 10 * (AREA) + area2 * (AREA) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal(
						area1 * 950000 * licenseFeeCharges + area2 * 27000000 * licenseFeeCharges);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((area1 * 93.687 * 100000 + area2 * 437.517 * 100000) * externalDevelopmentCharges));
				feesTypeCalculationDto.setConversionChargesCal(
						area1 * 125 * (AREA) * conversionCharges + area2 * (AREA) * conversionCharges * 122);
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((double) (area1 * 375 * (AREA) * 0.75
						+ area2 * (AREA) * stateInfrastructureDevelopmentCharges * 750 * 0.75));

				break;
			case PURPOSE_NILPC:
				break;
			case PURPOSE_TODGH:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(area1 * AREA * scrutinyFeeCharges * 10 + area2 * AREA * 1.75 * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 93.687 * 100000 + area2 * 437.517 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (area1 * AREA * 125 + area2 * AREA * 1225));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((double) (0));
				break;

			case PURPOSE_MLU_CZ:
				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) ((double) 2 * (area1 * (AREA) * 10 + area2 * (AREA) * 1.75 * 10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) 2 * (area1 * 950000 + area2 * 27000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.1 * (area1 * 93.687 * 100000 + area2 * 437.517 * 100000)));
				feesTypeCalculationDto
						.setConversionChargesCal((double) (2 * (area1 * (AREA) * 125 + area2 * (AREA) * 1225)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * (AREA) * 375) + (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 750));

				break;

			case PURPOSE_NILP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (area1 * (AREA) * 1.25 * 10 + area2 * (AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 1900000 * 5 / 7 + area2 * 27000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 281.06 * 100000 * 5 / 7 + area2 * 437.517 * 100000));
				feesTypeCalculationDto
						.setConversionChargesCal((double) (area1 * 125 * 5 / 7 * (AREA) + area2 * (AREA) * 1225));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (area1 * 460 * (AREA) * stateInfrastructureDevelopmentCharges * 5 / 7
								+ stateInfrastructureDevelopmentCharges * area2 * (AREA) * 750));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (1 * (AREA) * 10 * scrutinyFeeCharges));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:

				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA)) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal(23500000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((Double.valueOf(calculatorRequest.getTotalLandSize())) * 374.747 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * (AREA) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * (AREA) * 750 * stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_CPRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA)) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal(23500000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((Double.valueOf(calculatorRequest.getTotalLandSize())) * 374.747 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * (AREA) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * (AREA) * 750 * stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_CICS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(

						((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (27000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal((double) (1 * 437.517 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * AREA * 1225));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * AREA * 1.75 * stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(

						((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (27000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal((double) (1 * 437.517 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * AREA * 1225));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * AREA * 1.75 * stateInfrastructureDevelopmentCharges));

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
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal((AREA) * area1 * 10 + (AREA) * area2 * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 950000) + (area2 * 21000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 72.867 * 100000 + area2 * 340.291 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((area1 * (AREA) * 125) + (area2 * (AREA) * 1225));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * (AREA) * 375) + (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 750));

				break;

			case PURPOSE_ITP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(area1 * (AREA) * 2.5f * 10 + area2 * (AREA) * scrutinyFeeCharges * 0.1f));
				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 125000 + area2 * 14000000);

				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 243.377f * 100000 + area2 * 291.47f * 100000);
				feesTypeCalculationDto.setConversionChargesCal((area1 * (AREA) * 80) + (area2 * (AREA) * 1050));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((area1 * (AREA) * 190 * 2.5f)
						+ (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 7.5f));
				break;
			case PURPOSE_IPULP:
				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(area1 * (AREA) * 2.5f * 10 + area2 * (AREA) * scrutinyFeeCharges * 0.1f));
				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 125000 + area2 * 14000000);

				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 243.377f * 100000 + area2 * 291.47f * 100000);
				feesTypeCalculationDto.setConversionChargesCal((area1 * (AREA) * 80) + (area2 * (AREA) * 1050));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((area1 * (AREA) * 190 * 2.5f)
						+ (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 7.5f));

				break;
			case PURPOSE_IPA:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (PART1 * (AREA) * scrutinyFeeCharges * 10
						+ PART2 * AREA * 1.75 * 10 + PART3 * AREA * scrutinyFeeCharges * 10));
				feesTypeCalculationDto
						.setLicenseFeeChargesCal((double) (PART1 * 125000 + PART2 * 1900000 + PART3 * 14000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PART1 * 145.734 * 100000 + PART2 * 291.47 * 100000 + PART3 * 291.47 * 100000));

				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PART1 * (AREA) * 80 + PART2 * (AREA) * 125 + PART3 * (AREA) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PART1 * (AREA) * 190 * stateInfrastructureDevelopmentCharges
								+ PART2 * (AREA) * 460 * 1.75
								+ PART3 * (AREA) * 750 * stateInfrastructureDevelopmentCharges));

				break;

			case PURPOSE_RGP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 1 * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0.995 * 1900000 + 0.005 * 14000000));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.995 * 187.373 * 100000 + area2 * 249.831 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (0.995 * (AREA) * 125 + 0.005 * (AREA) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((double) (0.995 * (AREA) * 460 * 1.75
						+ 0.005 * (AREA) * 750 * stateInfrastructureDevelopmentCharges));

				break;

			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(area1 * 10 * (AREA) + area2 * (AREA) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal(
						area1 * 950000 * licenseFeeCharges + area2 * 27000000 * licenseFeeCharges);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((area1 * 72.867 * 100000 + area2 * 340.291 * 100000) * externalDevelopmentCharges));
				feesTypeCalculationDto.setConversionChargesCal(
						(area1 * 125 * (AREA) * conversionCharges + area2 * (AREA) * conversionCharges * 1225));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((double) (area1 * 375 * (AREA) * 0.75
						+ area2 * (AREA) * stateInfrastructureDevelopmentCharges * 750 * 0.75));

				break;
			case PURPOSE_NILPC:
				break;
			case PURPOSE_TODGH:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(area1 * AREA * scrutinyFeeCharges * 10 + area2 * AREA * 1.75 * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 72.867 * 100000 + area2 * 340.291 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (area1 * AREA * 125 + area2 * AREA * 1225));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((double) (0));
				break;

			case PURPOSE_MLU_CZ:
				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) ((double) 2 * (area1 * (AREA) * 10 + area2 * (AREA) * 1.75 * 10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) 2 * (area1 * 950000 + area2 * 21000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.1 * (area1 * 72.867 * 100000 + area2 * 340.291 * 100000)));
				feesTypeCalculationDto
						.setConversionChargesCal((double) (2 * (area1 * (AREA) * 125 + area2 * (AREA) * 1225)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * (AREA) * 375) + (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 750));

				break;
			case PURPOSE_NILP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (area1 * (AREA) * 1.25 * 10 + area2 * (AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 1900000 * 5 / 7 + area2 * 27000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 218.602 * 100000 * 5 / 7 + area2 * 340.291 * 100000));
				feesTypeCalculationDto
						.setConversionChargesCal((double) (area1 * 125 * 5 / 7 * (AREA) + area2 * (AREA) * 1225));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (1 * (AREA) * 10 * scrutinyFeeCharges));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(14000000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (Double.valueOf(calculatorRequest.getTotalLandSize())) * 291.47 * 100000);
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * (AREA) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * (AREA) * 750 * stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_CPRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(14000000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (Double.valueOf(calculatorRequest.getTotalLandSize())) * 291.47 * 100000);
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * (AREA) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * (AREA) * 750 * stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (21000000));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal((double) (1 * 340.291 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * AREA * 1225));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * AREA * 750 * stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_CICS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (21000000));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal((double) (1 * 340.291 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * AREA * 1225));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * AREA * 750 * stateInfrastructureDevelopmentCharges));

				break;

			case PURPOSE_RHP:
				break;
			}
			break;
		}

		// ---------------------medium--------------------///
		case ZONE_MDM: {
			switch (calculatorRequest.getPurposeCode()) {
			case PURPOSE_RPL:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal((AREA) * area1 * 10 + (AREA) * area2 * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 625000) + (area2 * 9500000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 62.458 * 100000 + area2 * 291.678 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((area1 * (AREA) * 80) + (area2 * (AREA) * 700));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * (AREA) * 250) + (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 500));

				break;
			case PURPOSE_ITP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(area1 * (AREA) * 2.5f * 10 + area2 * (AREA) * scrutinyFeeCharges * 0.1f));

				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 62500 + area2 * 6250000);
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 208.069f * 100000 + area2 * 249.833f * 100000);
				feesTypeCalculationDto.setConversionChargesCal((area1 * (AREA) * 50) + (area2 * (AREA) * 600));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * (AREA) * 125 * 2.5f) + (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 5));

				break;
			case PURPOSE_IPULP:
				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(area1 * (AREA) * 2.5f * 10 + area2 * (AREA) * scrutinyFeeCharges * 0.1f));

				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 62500 + area2 * 6250000);
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 208.069f * 100000 + area2 * 249.833f * 100000);
				feesTypeCalculationDto.setConversionChargesCal((area1 * (AREA) * 50) + (area2 * (AREA) * 600));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * (AREA) * 125 * 2.5f) + (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 5));

				break;
			case PURPOSE_IPA:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (PART1 * (AREA) * scrutinyFeeCharges * 10
						+ PART2 * AREA * 1.75 * 10 + PART3 * AREA * scrutinyFeeCharges * 10));
				feesTypeCalculationDto
						.setLicenseFeeChargesCal((double) (PART1 * 62500 + PART2 * 950000 + PART3 * 6250000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PART1 * 124.916 * 100000 + PART2 * 249.831 * 100000 + PART3 * 249.831 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PART1 * (AREA) * 50 + PART2 * (AREA) * 80 + PART3 * (AREA) * 600));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PART1 * (AREA) * 125 * stateInfrastructureDevelopmentCharges
								+ PART2 * (AREA) * 320 * 1.75
								+ PART3 * (AREA) * 500 * stateInfrastructureDevelopmentCharges));

				break;

			case PURPOSE_RGP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 1 * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0.995 * 950000 + 0.005 * 6250000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.995 * 187.373 * 100000 + area2 * 249.831 * 100000));

				feesTypeCalculationDto.setConversionChargesCal((double) (0.995 * (AREA) * 80 + 0.005 * (AREA) * 600));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((double) (0.995 * (AREA) * 320 * 1.75
						+ 0.005 * (AREA) * 500 * stateInfrastructureDevelopmentCharges));

				break;

			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(area1 * 10 * (AREA) + area2 * (AREA) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal(10000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((area1 * 62.458 * 100000 + area2 * 291.678 * 100000) * 0.5));
				feesTypeCalculationDto.setConversionChargesCal(0);
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(0);

				break;
			case PURPOSE_NILPC:
				break;
			case PURPOSE_TODGH:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(area1 * AREA * scrutinyFeeCharges * 10 + area2 * AREA * 1.75 * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 62.458 * 100000 + area2 * 291.678 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (area1 * AREA * 80 + area2 * AREA * 700));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((double) (0));
				break;

			case PURPOSE_MLU_CZ:
				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) ((double) 2 * (area1 * (AREA) * 10 + area2 * (AREA) * 1.75 * 10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) 2 * (area1 * 625000 + area2 * 9500000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.1 * (area1 * 62.458 * 100000 + area2 * 291.678 * 100000)));
				feesTypeCalculationDto
						.setConversionChargesCal((double) (2 * (area1 * (AREA) * 80 + area2 * (AREA) * 700)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * (AREA) * 250) + (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 500));

				break;
			case PURPOSE_NILP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (area1 * (AREA) * 1.25 * 10 + area2 * (AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 950000 * 5 / 7 + area2 * 9500000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 187.373 * 100000 * 5 / 7 + area2 * 291.678 * 100000));
				feesTypeCalculationDto
						.setConversionChargesCal((double) (area1 * 80 * 5 / 7 * (AREA) + area2 * (AREA) * 700));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (area1 * 320 * (AREA) * stateInfrastructureDevelopmentCharges * 5 / 7
								+ stateInfrastructureDevelopmentCharges * area2 * (AREA) * 500));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (1 * (AREA) * 10 * scrutinyFeeCharges));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(6250000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (Double.valueOf(calculatorRequest.getTotalLandSize()) * 249.831 * 100000));

				feesTypeCalculationDto.setConversionChargesCal((double) (1 * (AREA) * 600));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * (AREA) * 500 * stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_CPRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(6250000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (Double.valueOf(calculatorRequest.getTotalLandSize()) * 249.831 * 100000));

				feesTypeCalculationDto.setConversionChargesCal((double) (1 * (AREA) * 600));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * (AREA) * 500 * stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_CICS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (9500000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal((double) (1 * 291.678 * 100000));

				feesTypeCalculationDto.setConversionChargesCal((double) (1 * AREA * 700));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * AREA * 500 * stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (9500000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal((double) (1 * 291.678 * 100000));

				feesTypeCalculationDto.setConversionChargesCal((double) (1 * AREA * 700));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * AREA * 500 * stateInfrastructureDevelopmentCharges));

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
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal((AREA) * area1 * 10 + (AREA) * area2 * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 125000) + (area2 * 1900000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 52.048 * 100000 + area2 * 243.065f * 100000));
				feesTypeCalculationDto.setConversionChargesCal((area1 * (AREA) * 20) + (area2 * (AREA) * 175));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * (AREA) * 70) + (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 190));

				break;
			case PURPOSE_ITP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(area1 * (AREA) * 2.5f * 10 + area2 * (AREA) * scrutinyFeeCharges * 0.1f));
				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 12500 + area2 * 1250000);
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 173.841f * 100000 + area2 * 208.193f * 100000);
				feesTypeCalculationDto.setConversionChargesCal((area1 * (AREA) * 30) + (area2 * (AREA) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * (AREA) * 35 * 2.5f) + (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 1.9f));
				break;
			case PURPOSE_IPULP:
				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(area1 * (AREA) * 2.5f * 10 + area2 * (AREA) * scrutinyFeeCharges * 0.1f));
				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 12500 + area2 * 1250000);
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 173.841f * 100000 + area2 * 208.193f * 100000);
				feesTypeCalculationDto.setConversionChargesCal((area1 * (AREA) * 30) + (area2 * (AREA) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * (AREA) * 35 * 2.5f) + (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 1.9f));

				break;
			case PURPOSE_IPA:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (PART1 * (AREA) * scrutinyFeeCharges * 10
						+ PART2 * AREA * 1.75 * 10 + PART3 * AREA * scrutinyFeeCharges * 10));
				feesTypeCalculationDto
						.setLicenseFeeChargesCal((double) (PART1 * 12500 + PART2 * 250000 + PART3 * 1250000));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PART1 * 104.096 * 100000 + PART2 * 208.193 * 100000 + PART3 * 208.193 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PART1 * (AREA) * 30 + PART2 * (AREA) * 20 + PART3 * (AREA) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PART1 * (AREA) * 35 * stateInfrastructureDevelopmentCharges
								+ PART2 * (AREA) * 90 * 1.75
								+ PART3 * (AREA) * 190 * stateInfrastructureDevelopmentCharges));

				break;

			case PURPOSE_RGP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 1 * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0.995 * 250000 + 0.005 * 1250000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.995 * 156.145 * 100000 + area2 * 208.193 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (0.995 * (AREA) * 20 + 0.005 * (AREA) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((double) (0.995 * (AREA) * 90 * 1.75
						+ 0.005 * (AREA) * 190 * stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(area1 * 10 * (AREA) + area2 * (AREA) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal(10000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((area1 * 52.048 * 100000 + area2 * 243.065 * 100000) * 0.25));
				feesTypeCalculationDto.setConversionChargesCal(0);
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(0);

				break;
			case PURPOSE_NILPC:
				break;
			case PURPOSE_TODGH:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(area1 * AREA * scrutinyFeeCharges * 10 + area2 * AREA * 1.75 * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 52.048 * 100000 + area2 * 243.065 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (area1 * AREA * 20 + area2 * AREA * 175));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((double) (0));
				break;

			case PURPOSE_MLU_CZ:
				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) ((double) 2 * (area1 * (AREA) * 10 + area2 * (AREA) * 1.75 * 10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) 2 * (area1 * 125000 + area2 * 1900000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.1 * (area1 * 52.048 * 100000 + area2 * 243.065 * 100000)));
				feesTypeCalculationDto
						.setConversionChargesCal((double) (2 * (area1 * (AREA) * 20 + area2 * (AREA) * 175)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * (AREA) * 70) + (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 190));

				break;
			case PURPOSE_NILP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (area1 * (AREA) * 1.25 * 10 + area2 * (AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 250000 * 5 / 7 + area2 * 1900000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 156.145 * 100000 * 5 / 7 + area2 * 243.065 * 100000));
				feesTypeCalculationDto
						.setConversionChargesCal((double) (area1 * 20 * 5 / 7 * (AREA) + area2 * (AREA) * 175));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (area1 * 90 * (AREA) * stateInfrastructureDevelopmentCharges * 5 / 7
								+ stateInfrastructureDevelopmentCharges * area2 * (AREA) * 190));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (1 * (AREA) * 10 * scrutinyFeeCharges));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(1250000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (Double.valueOf(calculatorRequest.getTotalLandSize()) * 208.193 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * (AREA) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * (AREA) * 190 * stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_CPRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(1250000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (Double.valueOf(calculatorRequest.getTotalLandSize()) * 208.193 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * (AREA) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * (AREA) * 190 * stateInfrastructureDevelopmentCharges));

				break;

			case PURPOSE_RHP:
				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (1900000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal((double) (1 * 243.065 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * AREA * 175));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * AREA * 190 * stateInfrastructureDevelopmentCharges));
				break;
			case PURPOSE_CICS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (1900000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal((double) (1 * 243.065 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * AREA * 175));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * AREA * 190 * stateInfrastructureDevelopmentCharges));
				break;
			}

			break;
		}

		/// ------------------low2-------------------------------//
		case ZONE_LOW2: {
			switch (calculatorRequest.getPurposeCode()) {
			case PURPOSE_RPL:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal((AREA) * area1 * 10 + (AREA) * area2 * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 125000) + (area2 * 1900000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 41.639 * 100000 + area2 * 194.452 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((area1 * (AREA) * 20) + (area2 * (AREA) * 175));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * (AREA) * 70) + (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 190));

				break;
			case PURPOSE_ITP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(area1 * (AREA) * 2.5f * 10 + area2 * (AREA) * scrutinyFeeCharges * 0.1f));
				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 12500 + area2 * 1250000);

				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 139.073f * 100000 + area2 * 166.554f * 100000);
				feesTypeCalculationDto.setConversionChargesCal((area1 * (AREA) * 30) + (area2 * (AREA) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * (AREA) * 35 * 2.5f) + (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 1.9f));
				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(area1 * (AREA) * 2.5f * 10 + area2 * (AREA) * scrutinyFeeCharges * 0.1f));
				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 12500 + area2 * 1250000);

				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 139.073f * 100000 + area2 * 166.554f * 100000);
				feesTypeCalculationDto.setConversionChargesCal((area1 * (AREA) * 30) + (area2 * (AREA) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * (AREA) * 35 * 2.5f) + (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 1.9f));

				break;
			case PURPOSE_IPULP:
				break;

			case PURPOSE_IPA:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (PART1 * (AREA) * scrutinyFeeCharges * 10
						+ PART2 * AREA * 1.75 * 10 + PART3 * AREA * scrutinyFeeCharges * 10));
				feesTypeCalculationDto
						.setLicenseFeeChargesCal((double) (PART1 * 12500 + PART2 * 250000 + PART3 * 1250000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PART1 * 83.278 * 100000 + PART2 * 166.554 * 100000 + PART3 * 166.554 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PART1 * (AREA) * 30 + PART2 * (AREA) * 20 + PART3 * (AREA) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PART1 * (AREA) * 35 * stateInfrastructureDevelopmentCharges
								+ PART2 * (AREA) * 90 * 1.75
								+ PART3 * (AREA) * 190 * stateInfrastructureDevelopmentCharges));

				break;

			case PURPOSE_RGP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 1 * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0.995 * 250000 + 0.005 * 1250000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.995 * 124.916 * 100000 + area2 * 166.554 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (0.995 * (AREA) * 20 + 0.005 * (AREA) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((double) (0.995 * (AREA) * 90 * 1.75
						+ 0.005 * (AREA) * 190 * stateInfrastructureDevelopmentCharges));

				break;

			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(area1 * 10 * (AREA) + area2 * (AREA) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal(10000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((area1 * 41.639 * 100000 + area2 * 194.452 * 100000) * 0.25));
				feesTypeCalculationDto.setConversionChargesCal(0);
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(0);

				break;
			case PURPOSE_NILPC:
				break;
			case PURPOSE_TODGH:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(area1 * AREA * scrutinyFeeCharges * 10 + area2 * AREA * 1.75 * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 41.639 * 100000 + area2 * 194.452 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (area1 * AREA * 20 + area2 * AREA * 175));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal((double) (0));
				break;

			case PURPOSE_MLU_CZ:
				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) ((double) 2 * (area1 * (AREA) * 10 + area2 * (AREA) * 1.75 * 10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) 2 * (area1 * 125000 + area2 * 1900000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.1 * (area1 * 41.639 * 100000 + area2 * 194.452 * 100000)));
				feesTypeCalculationDto
						.setConversionChargesCal((double) (2 * (area1 * (AREA) * 20 + area2 * (AREA) * 175)));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(area1 * (AREA) * 70) + (area2 * (AREA) * stateInfrastructureDevelopmentCharges * 190));

				break;
			case PURPOSE_NILP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (area1 * (AREA) * 1.25 * 10 + area2 * (AREA) * scrutinyFeeCharges * 10));

				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 250000 * 5 / 7 + area2 * 1900000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 124.916 * 100000 * 5 / 7 + area2 * 194.452 * 100000));
				feesTypeCalculationDto
						.setConversionChargesCal((double) (area1 * 20 * 5 / 7 * (AREA) + area2 * (AREA) * 175));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (area1 * 90 * (AREA) * stateInfrastructureDevelopmentCharges * 5 / 7
								+ stateInfrastructureDevelopmentCharges * area2 * (AREA) * 190));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (1 * (AREA) * 10 * scrutinyFeeCharges));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(1250000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (Double.valueOf(calculatorRequest.getTotalLandSize()) * 166.554 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * (AREA) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * (AREA) * 190 * stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_CPRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(1250000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (Double.valueOf(calculatorRequest.getTotalLandSize()) * 166.554 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * (AREA) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * (AREA) * 190 * stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_RHP:
				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (1900000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal((double) (1 * 194.452 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * AREA * 175));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * AREA * 190 * stateInfrastructureDevelopmentCharges));
				break;
			case PURPOSE_CICS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(((AREA) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (1900000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal((double) (1 * 194.452 * 100000));
				feesTypeCalculationDto.setConversionChargesCal((double) (1 * AREA * 175));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * AREA * 190 * stateInfrastructureDevelopmentCharges));
				break;
			}

			break;
		}
		}

		return feesTypeCalculationDto;

	}
}