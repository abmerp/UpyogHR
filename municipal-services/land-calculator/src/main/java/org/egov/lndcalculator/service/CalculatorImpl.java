package org.egov.lndcalculator.service;

		
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.lndcalculator.utils.LandUtil;
import org.egov.lndcalculator.validator.LandMDMSValidator;
import org.egov.lndcalculator.web.models.CalculatorRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalculatorImpl implements Calculator {

	@Autowired
	LandUtil landUtil;	
	@Autowired
	LandMDMSValidator valid;

	private double areaInSqmtr(String arce) {
		return (AREA * Double.valueOf(arce));
	}

	public FeesTypeCalculationDto feesTypeCalculation(CalculatorRequest calculatorRequest) {

		double area1 = (PERCENTAGE1 * Double.valueOf(calculatorRequest.getTotalLandSize()));
		double area2 = PERCENTAGE2 * Double.valueOf(calculatorRequest.getTotalLandSize());

		Map<String, List<String>> mdmsData;
		FeesTypeCalculationDto feesTypeCalculationDto = new FeesTypeCalculationDto();
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> mDMSCallPurposeCode = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>) landUtil
				.mDMSCallPurposeCode(calculatorRequest.getRequestInfo(),
						calculatorRequest.getRequestInfo().getUserInfo().getTenantId(),
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
				feesTypeCalculationDto.setScrutinyFeeChargesCal((areaInSqmtr(calculatorRequest.getTotalLandSize()))
						* PERCENTAGE1 * 10
						+ areaInSqmtr(calculatorRequest.getTotalLandSize()) * PERCENTAGE2 * scrutinyFeeCharges * 10);

				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * RATE) + (area2 * RATE1));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((area1 * 104.096 * 100000) + (area2 * 486.13 * 100000)));
				feesTypeCalculationDto.setConversionChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 158)
								+ (PERCENTAGE2 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 1470));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 500)
								+ (PERCENTAGE2 * areaInSqmtr(calculatorRequest.getTotalLandSize())
										* stateInfrastructureDevelopmentCharges * 1000));

				break;

			case PURPOSE_ITP:
				break;
			case PURPOSE_ITC:

				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(PERCENTAGE1 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 2.5 * 10 + PERCENTAGE2
								* areaInSqmtr(calculatorRequest.getTotalLandSize()) * scrutinyFeeCharges * 0.1));
				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 250000 + area2 * 27000000);
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 347.682 * 100000 + area2 * 416.385 * 100000);
				feesTypeCalculationDto
						.setConversionChargesCal((PERCENTAGE1 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 100)
								+ (PERCENTAGE2 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 1260));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 250 * 2.5f)
								+ (PERCENTAGE2 * areaInSqmtr(calculatorRequest.getTotalLandSize())
										* stateInfrastructureDevelopmentCharges * 150 * 10));

				break;
			case PURPOSE_IPL:

				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (PART1 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * scrutinyFeeCharges * 10
								+ PART2 * AREA * 1.75 * 10 + PART3 * AREA * scrutinyFeeCharges * 10));
				feesTypeCalculationDto
						.setLicenseFeeChargesCal((double) (PART1 * 250000 + PART2 * 4000000 + PART3 * 27000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PART1 * 208.192 * 100000 + PART2 * 416.385 * 100000 + PART3 * 416.385 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PART1 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 100
								+ PART2 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 158
								+ PART3 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 1260));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PART1 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 250
								* stateInfrastructureDevelopmentCharges
								+ PART2 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 625 * 1.75
								+ PART3 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 1000
										* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_IPA:
				break;
			case PURPOSE_RGP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 1 * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0.995 * 4000000 + 0.005 * 27000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.995 * 312.289 * 100000 + PERCENTAGE2 * 416.385 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (0.995 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 158
								+ 0.005 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 1260));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (0.995 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 625 * 1.75
								+ 0.005 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 1000
										* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_SGC:
				break;
			case PURPOSE_DDJAY_APHP:

				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(PERCENTAGE1 * 10 * areaInSqmtr(calculatorRequest.getTotalLandSize()) + PERCENTAGE2
								* areaInSqmtr(calculatorRequest.getTotalLandSize()) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * RATE) + (area2 * RATE1));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 104.096 * 100000 + area2 * 486.13 * 100000));
				feesTypeCalculationDto
						.setConversionChargesCal(PERCENTAGE1 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 158
								+ PERCENTAGE2 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 1470);
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * 500 * areaInSqmtr(calculatorRequest.getTotalLandSize())
								+ PERCENTAGE2 * areaInSqmtr(calculatorRequest.getTotalLandSize())
										* stateInfrastructureDevelopmentCharges * 1000));

				break;
			case PURPOSE_NILP:
				break;
			case PURPOSE_TODGH:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (1 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 10 * scrutinyFeeCharges));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (4000000 * 0.995 * licenseFeeCharges / 1.75));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.995 * (Double.valueOf(calculatorRequest.getTotalLandSize()) * 416.385 * 100000
								* licenseFeeCharges / 1.75)));
				feesTypeCalculationDto.setConversionChargesCal(((double) (0.995 * 158
						* areaInSqmtr(calculatorRequest.getTotalLandSize()) * conversionCharges / 1.75
						+ 0.005 * 1470 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * conversionCharges
								/ 1.75)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(((double) (0.995 * 625
						* areaInSqmtr(calculatorRequest.getTotalLandSize()) * stateInfrastructureDevelopmentCharges
						+ 0.005 * 1000 * areaInSqmtr(calculatorRequest.getTotalLandSize())
								* stateInfrastructureDevelopmentCharges)));

				break;
			case PURPOSE_CIR:
				break;
			case PURPOSE_AHP:
				break;
			case PURPOSE_CIS:
				break;
			case PURPOSE_MLU_CZ:
				break;
			case PURPOSE_MLU_RZ:
				break;
			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) ((double) 2 * (PERCENTAGE1 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 10
								+ PERCENTAGE2 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 1.75 * 10)));

				feesTypeCalculationDto.setLicenseFeeChargesCal((double) 2 * (area1 * 1250000 + area2 * 34000000));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.1 * (area1 * 104.096 * 100000 + area2 * 486.13 * 100000)));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (2 * (PERCENTAGE1 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 158
								+ PERCENTAGE2 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 1470)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 500)
								+ (PERCENTAGE2 * areaInSqmtr(calculatorRequest.getTotalLandSize())
										* stateInfrastructureDevelopmentCharges * 1000));

				break;
			case PURPOSE_NILPC:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (PERCENTAGE1
						* areaInSqmtr(calculatorRequest.getTotalLandSize()) * 1.25 * 10
						+ PERCENTAGE2 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 4000000 * 5 / 7 + area2 * 34000000));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PERCENTAGE1 * 312.289 * 100000 * 5 / 7 + PERCENTAGE2 * 486.13 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PERCENTAGE1 * 158 * 5 / 7 * areaInSqmtr(calculatorRequest.getTotalLandSize())
								+ PERCENTAGE2 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 1470));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PERCENTAGE1 * 625 * areaInSqmtr(calculatorRequest.getTotalLandSize())
								* stateInfrastructureDevelopmentCharges * 5 / 7
								+ stateInfrastructureDevelopmentCharges * PERCENTAGE2
										* areaInSqmtr(calculatorRequest.getTotalLandSize()) * 1000));

				break;
			case PURPOSE_TODCOMM:

				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (1 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 10 * scrutinyFeeCharges));

				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (34000000 * licenseFeeCharges / 1.75));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						((double) ((Double.valueOf(calculatorRequest.getTotalLandSize()) * 486.13 * 100000
								* externalDevelopmentCharges / 1.75))));
				feesTypeCalculationDto.setConversionChargesCal(((double) (1470
						* areaInSqmtr(calculatorRequest.getTotalLandSize()) * conversionCharges / 1.75)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(((double) (1000
						* areaInSqmtr(calculatorRequest.getTotalLandSize()) * stateInfrastructureDevelopmentCharges)));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal(27000000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((Double.valueOf(calculatorRequest.getTotalLandSize()) * 416.385 * 100000)));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (1 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 1260));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * areaInSqmtr(calculatorRequest.getTotalLandSize()) * 1000
								* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_SPRPRGH:
				break;
			case PURPOSE_DRH:
				break;
			case PURPOSE_RHP:
				break;
			}
			break;
//---------------------------------HIGH 1----------------------------------------------------//
		case ZONE_HIG1: {
			switch (calculatorRequest.getPurposeCode()) {
			case PURPOSE_RPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((areaInSqmtr(calculatorRequest.getTotalLandSize()))
						* PERCENTAGE1 * 10
						+ (areaInSqmtr(calculatorRequest.getTotalLandSize())) * PERCENTAGE2 * scrutinyFeeCharges * 10);

				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 950000) + (area2 * 27000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 93.687 * 100000 + PERCENTAGE2 * 437.517 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 125)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1225));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 375)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 750));

				break;
			case PURPOSE_ITP:
				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 2.5f * 10 + PERCENTAGE2
								* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 0.1));
				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 125000 + area2 * 23500000);
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 312.914f * 100000 + area2 * 374.747f * 100000);
				feesTypeCalculationDto.setConversionChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 80)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 190 * 2.5f)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 7.5f));

				break;
			case PURPOSE_IPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (PART1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10
								+ PART2 * AREA * 1.75 * 10 + PART3 * AREA * scrutinyFeeCharges * 10));
				feesTypeCalculationDto
						.setLicenseFeeChargesCal((double) (PART1 * 125000 + PART2 * 1900000 + PART3 * 23500000));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PART1 * 187.374 * 100000 + PART2 * 374.747 * 100000 + PART3 * 374.747 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PART1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 80
								+ PART2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 125
								+ PART3 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PART1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 190
								* stateInfrastructureDevelopmentCharges
								+ PART2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 460 * 1.75
								+ PART3 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 750
										* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_IPA:
				break;
			case PURPOSE_RGP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						((areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 1 * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0.995 * 1900000 + 0.005 * 23500000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.995 * 281.06 * 100000 + PERCENTAGE2 * 374.747 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (0.995 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 125
								+ 0.005 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (0.995 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 460 * 1.75
								+ 0.005 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 750
										* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_SGC:
				break;
			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						PERCENTAGE1 * 10 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) + PERCENTAGE2
								* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal(
						area1 * 950000 * licenseFeeCharges + area2 * 27000000 * licenseFeeCharges);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((area1 * 93.687 * 100000 + area2 * 437.517 * 100000) * externalDevelopmentCharges));
				feesTypeCalculationDto.setConversionChargesCal(PERCENTAGE1 * 125
						* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * conversionCharges
						+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * conversionCharges * 122);
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PERCENTAGE1 * 375 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 0.75
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 750 * 0.75));

				break;
			case PURPOSE_NILP:
				break;
			case PURPOSE_TODGH:
				break;
			case PURPOSE_CIR:
				break;
			case PURPOSE_AHP:
				break;
			case PURPOSE_CIS:
				break;
			case PURPOSE_MLU_CZ:
				break;
			case PURPOSE_MLU_RZ:
				break;
			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) ((double) 2 * (PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 10
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1.75 * 10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) 2 * (area1 * 950000 + area2 * 27000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.1 * (area1 * 93.687 * 100000 + PERCENTAGE2 * 437.517 * 100000)));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (2 * (PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 125
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1225)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 375)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 750));

				break;

			case PURPOSE_NILPC:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (PERCENTAGE1
						* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1.25 * 10
						+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 1900000 * 5 / 7 + area2 * 27000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PERCENTAGE1 * 281.06 * 100000 * 5 / 7 + PERCENTAGE2 * 437.517 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PERCENTAGE1 * 125 * 5 / 7 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1225));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PERCENTAGE1 * 460 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
								* stateInfrastructureDevelopmentCharges * 5 / 7
								+ stateInfrastructureDevelopmentCharges * PERCENTAGE2
										* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 750));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 10 * scrutinyFeeCharges));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPL:

				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						((areaInSqmtr(calculatorRequest.getTotalLandSize()))) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal(23500000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((Double.valueOf(calculatorRequest.getTotalLandSize())) * 374.747 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 750
								* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_SPRPRGH:
				break;
			case PURPOSE_DRH:
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
				feesTypeCalculationDto.setScrutinyFeeChargesCal((areaInSqmtr(calculatorRequest.getTotalLandSize()))
						* PERCENTAGE1 * 10
						+ (areaInSqmtr(calculatorRequest.getTotalLandSize())) * PERCENTAGE2 * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 950000) + (area2 * 21000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 72.867 * 100000 + PERCENTAGE2 * 340.291 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 125)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1225));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 375)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 750));

				break;

			case PURPOSE_ITP:
				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 2.5f * 10 + PERCENTAGE2
								* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 0.1f));
				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 125000 + area2 * 14000000);

				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 243.377f * 100000 + area2 * 291.47f * 100000);
				feesTypeCalculationDto.setConversionChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 80)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1050));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 190 * 2.5f)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 7.5f));

				break;
			case PURPOSE_IPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (PART1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10
								+ PART2 * AREA * 1.75 * 10 + PART3 * AREA * scrutinyFeeCharges * 10));
				feesTypeCalculationDto
						.setLicenseFeeChargesCal((double) (PART1 * 125000 + PART2 * 1900000 + PART3 * 14000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PART1 * 145.734 * 100000 + PART2 * 291.47 * 100000 + PART3 * 291.47 * 100000));

				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PART1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 80
								+ PART2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 125
								+ PART3 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PART1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 190
								* stateInfrastructureDevelopmentCharges
								+ PART2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 460 * 1.75
								+ PART3 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 750
										* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_IPA:
				break;
			case PURPOSE_RGP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						((areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 1 * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0.995 * 1900000 + 0.005 * 14000000));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.995 * 187.373 * 100000 + PERCENTAGE2 * 249.831 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (0.995 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 125
								+ 0.005 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (0.995 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 460 * 1.75
								+ 0.005 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 750
										* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_SGC:
				break;
			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						PERCENTAGE1 * 10 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) + PERCENTAGE2
								* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal(
						area1 * 950000 * licenseFeeCharges + area2 * 27000000 * licenseFeeCharges);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((area1 * 72.867 * 100000 + area2 * 340.291 * 100000) * externalDevelopmentCharges));
				feesTypeCalculationDto.setConversionChargesCal(
						(PERCENTAGE1 * 125 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * conversionCharges
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * conversionCharges
										* 1225));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PERCENTAGE1 * 375 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 0.75
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 750 * 0.75));

				break;
			case PURPOSE_NILP:
				break;
			case PURPOSE_TODGH:
				break;
			case PURPOSE_CIR:
				break;
			case PURPOSE_AHP:
				break;
			case PURPOSE_CIS:
				break;
			case PURPOSE_MLU_CZ:
				break;
			case PURPOSE_MLU_RZ:
				break;
			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) ((double) 2 * (PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 10
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1.75 * 10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) 2 * (area1 * 950000 + area2 * 21000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.1 * (area1 * 72.867 * 100000 + PERCENTAGE2 * 340.291 * 100000)));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (2 * (PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 125
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1225)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 375)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 750));

				break;
			case PURPOSE_NILPC:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (PERCENTAGE1
						* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1.25 * 10
						+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 1900000 * 5 / 7 + area2 * 27000000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PERCENTAGE1 * 218.602 * 100000 * 5 / 7 + PERCENTAGE2 * 340.291 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PERCENTAGE1 * 125 * 5 / 7 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1225));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 10 * scrutinyFeeCharges));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						((areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(14000000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (Double.valueOf(calculatorRequest.getTotalLandSize())) * 291.47 * 100000);
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 750
								* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_SPRPRGH:
				break;
			case PURPOSE_DRH:
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
				feesTypeCalculationDto.setScrutinyFeeChargesCal((areaInSqmtr(calculatorRequest.getTotalLandSize()))
						* PERCENTAGE1 * 10
						+ (areaInSqmtr(calculatorRequest.getTotalLandSize())) * PERCENTAGE2 * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 625000) + (area2 * 9500000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 62.458 * 100000 + PERCENTAGE2 * 291.678 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 80)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 700));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 250)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 500));

				break;
			case PURPOSE_ITP:
				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 2.5f * 10 + PERCENTAGE2
								* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 0.1f));

				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 62500 + area2 * 6250000);
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 208.069f * 100000 + area2 * 249.833f * 100000);
				feesTypeCalculationDto.setConversionChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 50)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 600));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 125 * 2.5f)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 5));

				break;
			case PURPOSE_IPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (PART1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10
								+ PART2 * AREA * 1.75 * 10 + PART3 * AREA * scrutinyFeeCharges * 10));
				feesTypeCalculationDto
						.setLicenseFeeChargesCal((double) (PART1 * 62500 + PART2 * 950000 + PART3 * 6250000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PART1 * 124.916 * 100000 + PART2 * 249.831 * 100000 + PART3 * 249.831 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PART1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 50
								+ PART2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 80
								+ PART3 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 600));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PART1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 125
								* stateInfrastructureDevelopmentCharges
								+ PART2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 320 * 1.75
								+ PART3 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 500
										* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_IPA:
				break;
			case PURPOSE_RGP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						((areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 1 * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0.995 * 950000 + 0.005 * 6250000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.995 * 187.373 * 100000 + PERCENTAGE2 * 249.831 * 100000));

				feesTypeCalculationDto.setConversionChargesCal(
						(double) (0.995 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 80
								+ 0.005 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 600));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (0.995 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 320 * 1.75
								+ 0.005 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 500
										* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_SGC:
				break;
			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						PERCENTAGE1 * 10 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) + PERCENTAGE2
								* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal(10000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((area1 * 62.458 * 100000 + area2 * 291.678 * 100000) * 0.5));
				feesTypeCalculationDto.setConversionChargesCal(0);
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(0);

				break;
			case PURPOSE_NILP:
				break;
			case PURPOSE_TODGH:
				break;
			case PURPOSE_CIR:
				break;
			case PURPOSE_AHP:
				break;
			case PURPOSE_CIS:
				break;
			case PURPOSE_MLU_CZ:
				break;
			case PURPOSE_MLU_RZ:
				break;
			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) ((double) 2 * (PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 10
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1.75 * 10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) 2 * (area1 * 625000 + area2 * 9500000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.1 * (area1 * 62.458 * 100000 + PERCENTAGE2 * 291.678 * 100000)));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (2 * (PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 80
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 700)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 250)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 500));

				break;
			case PURPOSE_NILPC:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (PERCENTAGE1
						* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1.25 * 10
						+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 950000 * 5 / 7 + area2 * 9500000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PERCENTAGE1 * 187.373 * 100000 * 5 / 7 + PERCENTAGE2 * 291.678 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PERCENTAGE1 * 80 * 5 / 7 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 700));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PERCENTAGE1 * 320 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
								* stateInfrastructureDevelopmentCharges * 5 / 7
								+ stateInfrastructureDevelopmentCharges * PERCENTAGE2
										* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 500));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 10 * scrutinyFeeCharges));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						((areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(6250000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (Double.valueOf(calculatorRequest.getTotalLandSize()) * 249.831 * 100000));

				feesTypeCalculationDto.setConversionChargesCal(
						(double) (1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 600));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 500
								* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_SPRPRGH:
				break;
			case PURPOSE_DRH:
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
				feesTypeCalculationDto.setScrutinyFeeChargesCal((areaInSqmtr(calculatorRequest.getTotalLandSize()))
						* PERCENTAGE1 * 10
						+ (areaInSqmtr(calculatorRequest.getTotalLandSize())) * PERCENTAGE2 * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 125000) + (area2 * 1900000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 52.048 * 100000 + PERCENTAGE2 * 243.065f * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 20)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 175));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 70)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 190));

				break;
			case PURPOSE_ITP:
				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 2.5f * 10 + PERCENTAGE2
								* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 0.1f));
				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 12500 + area2 * 1250000);
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 173.841f * 100000 + area2 * 208.193f * 100000);
				feesTypeCalculationDto.setConversionChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 30)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 35 * 2.5f)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 1.9f));

				break;
			case PURPOSE_IPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (PART1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10
								+ PART2 * AREA * 1.75 * 10 + PART3 * AREA * scrutinyFeeCharges * 10));
				feesTypeCalculationDto
						.setLicenseFeeChargesCal((double) (PART1 * 12500 + PART2 * 250000 + PART3 * 1250000));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PART1 * 104.096 * 100000 + PART2 * 208.193 * 100000 + PART3 * 208.193 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PART1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 30
								+ PART2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 20
								+ PART3 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PART1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 35
								* stateInfrastructureDevelopmentCharges
								+ PART2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 90 * 1.75
								+ PART3 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 190
										* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_IPA:
				break;
			case PURPOSE_RGP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						((areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 1 * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0.995 * 250000 + 0.005 * 1250000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.995 * 156.145 * 100000 + PERCENTAGE2 * 208.193 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (0.995 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 20
								+ 0.005 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (0.995 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 90 * 1.75
								+ 0.005 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 190
										* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_SGC:
				break;
			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						PERCENTAGE1 * 10 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) + PERCENTAGE2
								* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal(10000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((area1 * 52.048 * 100000 + area2 * 243.065 * 100000) * 0.25));
				feesTypeCalculationDto.setConversionChargesCal(0);
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(0);

				break;
			case PURPOSE_NILP:
				break;
			case PURPOSE_TODGH:
				break;
			case PURPOSE_CIR:
				break;
			case PURPOSE_AHP:
				break;
			case PURPOSE_CIS:
				break;
			case PURPOSE_MLU_CZ:
				break;
			case PURPOSE_MLU_RZ:
				break;
			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) ((double) 2 * (PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 10
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1.75 * 10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) 2 * (area1 * 125000 + area2 * 1900000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.1 * (area1 * 52.048 * 100000 + PERCENTAGE2 * 243.065 * 100000)));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (2 * (PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 20
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 175)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 70)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 190));

				break;
			case PURPOSE_NILPC:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (PERCENTAGE1
						* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1.25 * 10
						+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 250000 * 5 / 7 + area2 * 1900000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PERCENTAGE1 * 156.145 * 100000 * 5 / 7 + PERCENTAGE2 * 243.065 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PERCENTAGE1 * 20 * 5 / 7 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 175));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PERCENTAGE1 * 90 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
								* stateInfrastructureDevelopmentCharges * 5 / 7
								+ stateInfrastructureDevelopmentCharges * PERCENTAGE2
										* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 190));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 10 * scrutinyFeeCharges));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						((areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(1250000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (Double.valueOf(calculatorRequest.getTotalLandSize()) * 208.193 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 190
								* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_SPRPRGH:
				break;
			case PURPOSE_DRH:
				break;
			case PURPOSE_RHP:
				break;
			}
			break;
		}

		/// ------------------low2-------------------------------//
		case ZONE_LOW2: {
			switch (calculatorRequest.getPurposeCode()) {
			case PURPOSE_RPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((areaInSqmtr(calculatorRequest.getTotalLandSize()))
						* PERCENTAGE1 * 10
						+ (areaInSqmtr(calculatorRequest.getTotalLandSize())) * PERCENTAGE2 * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 125000) + (area2 * 1900000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (area1 * 41.639 * 100000 + PERCENTAGE2 * 194.452 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 20)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 175));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 70)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 190));

				break;
			case PURPOSE_ITP:
				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 2.5f * 10 + PERCENTAGE2
								* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 0.1f));
				feesTypeCalculationDto.setLicenseFeeChargesCal(area1 * 12500 + area2 * 1250000);

				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(area1 * 139.073f * 100000 + area2 * 166.554f * 100000);
				feesTypeCalculationDto.setConversionChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 30)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 35 * 2.5f)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 1.9f));

				break;
			case PURPOSE_IPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (PART1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10
								+ PART2 * AREA * 1.75 * 10 + PART3 * AREA * scrutinyFeeCharges * 10));
				feesTypeCalculationDto
						.setLicenseFeeChargesCal((double) (PART1 * 12500 + PART2 * 250000 + PART3 * 1250000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PART1 * 83.278 * 100000 + PART2 * 166.554 * 100000 + PART3 * 166.554 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PART1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 30
								+ PART2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 20
								+ PART3 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PART1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 35
								* stateInfrastructureDevelopmentCharges
								+ PART2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 90 * 1.75
								+ PART3 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 190
										* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_IPA:
				break;
			case PURPOSE_RGP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						((areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 1 * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) (0.995 * 250000 + 0.005 * 1250000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.995 * 124.916 * 100000 + PERCENTAGE2 * 166.554 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (0.995 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 20
								+ 0.005 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (0.995 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 90 * 1.75
								+ 0.005 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 190
										* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_SGC:
				break;
			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						PERCENTAGE1 * 10 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) + PERCENTAGE2
								* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10);
				feesTypeCalculationDto.setLicenseFeeChargesCal(10000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) ((area1 * 41.639 * 100000 + area2 * 194.452 * 100000) * 0.25));
				feesTypeCalculationDto.setConversionChargesCal(0);
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(0);

				break;
			case PURPOSE_NILP:
				break;
			case PURPOSE_TODGH:
				break;
			case PURPOSE_CIR:
				break;
			case PURPOSE_AHP:
				break;
			case PURPOSE_CIS:
				break;
			case PURPOSE_MLU_CZ:
				break;
			case PURPOSE_MLU_RZ:
				break;
			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) ((double) 2 * (PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 10
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1.75 * 10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal((double) 2 * (area1 * 125000 + area2 * 1900000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (0.1 * (area1 * 41.639 * 100000 + PERCENTAGE2 * 194.452 * 100000)));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (2 * (PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 20
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 175)));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(PERCENTAGE1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 70)
								+ (PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
										* stateInfrastructureDevelopmentCharges * 190));

				break;
			case PURPOSE_NILPC:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((double) (PERCENTAGE1
						* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 1.25 * 10
						+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10));

				feesTypeCalculationDto.setLicenseFeeChargesCal((area1 * 250000 * 5 / 7 + area2 * 1900000));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (PERCENTAGE1 * 124.916 * 100000 * 5 / 7 + PERCENTAGE2 * 194.452 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (PERCENTAGE1 * 20 * 5 / 7 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
								+ PERCENTAGE2 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 175));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (PERCENTAGE1 * 90 * (areaInSqmtr(calculatorRequest.getTotalLandSize()))
								* stateInfrastructureDevelopmentCharges * 5 / 7
								+ stateInfrastructureDevelopmentCharges * PERCENTAGE2
										* (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 190));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						(double) (1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 10 * scrutinyFeeCharges));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						((areaInSqmtr(calculatorRequest.getTotalLandSize())) * scrutinyFeeCharges * 10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(1250000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(double) (Double.valueOf(calculatorRequest.getTotalLandSize()) * 166.554 * 100000));
				feesTypeCalculationDto.setConversionChargesCal(
						(double) (1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(double) (1 * (areaInSqmtr(calculatorRequest.getTotalLandSize())) * 190
								* stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_SPRPRGH:
				break;
			case PURPOSE_DRH:
				break;
			case PURPOSE_RHP:
				break;
			}
			break;
		}
		}

		return feesTypeCalculationDto;

	}
}