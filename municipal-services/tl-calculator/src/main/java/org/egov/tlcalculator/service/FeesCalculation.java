package org.egov.tlcalculator.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.egov.common.contract.request.RequestInfo;

import org.egov.tlcalculator.utils.CalculationUtils;
import org.egov.tlcalculator.web.models.CalculatorRequest;
import org.egov.tlcalculator.web.models.tradelicense.TradeLicense;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FeesCalculation implements Calculator {

	@Autowired
	CalculationUtils utils;

	@Autowired
	CalculatorImpl calculatorImpl;

	public PaymentCalculationResponse payment(RequestInfo info, String applicationNo) {

		String applicationNumber = applicationNo;
		String tenantId = "hr";
		List<FeesTypeCalculationDto> results = new ArrayList<FeesTypeCalculationDto>();
		TradeLicense license = utils.getTradeLicense(info, applicationNo, tenantId);
		log.info("license" + license);
		JsonNode detailsLand = license.getTradeLicenseDetail().getAdditionalDetail();
		
		String purpose = detailsLand.get(0).get("ApplicantPurpose").get("purpose").textValue();
		String totalAreaSchemeLand = detailsLand.get(0).get("DetailsofAppliedLand").get("DetailsAppliedLandPlot")
				.get("totalAreaScheme").textValue();
		BigDecimal totalAreaScheme = new BigDecimal(totalAreaSchemeLand);
		String areaUnderGh = "";
		String commercial = "";
		String netPlannedArea="";
		BigDecimal totalFeeComm = new BigDecimal("0.00");
		BigDecimal totalFeeGH = new BigDecimal("0.00");
		BigDecimal totalArea = new BigDecimal("0.00");
		BigDecimal totalGhCommArea = new BigDecimal("0.00");
		PaymentCalculationResponse paymentCalculationResponse = new PaymentCalculationResponse();

		// ---------------group housing--------------------//
		CalculatorRequest calculatorGh = new CalculatorRequest();
		calculatorGh.setApplicationNumber(applicationNo);
		calculatorGh.setPotenialZone(detailsLand.get(0).get("ApplicantPurpose").get("AppliedLandDetails").get(0)
				.get("potential").textValue());
		calculatorGh.setPurposeCode("RGP");
		areaUnderGh = detailsLand.get(0).get("DetailsofAppliedLand").get("DetailsAppliedLandPlot").get("areaUnderGH")
				.textValue();
		netPlannedArea=detailsLand.get(0).get("DetailsofAppliedLand").get("DetailsAppliedLandPlot").get("netPlannedArea")
				.textValue();
		if (areaUnderGh != null) {
			Double areaUnderGhL = Double.valueOf(areaUnderGh);
		//	areaUnderGhL = areaUnderGhL * 175;

			calculatorGh.setTotalLandSize(areaUnderGhL.toString());

			FeesTypeCalculationDto resultGH = calculatorImpl.feesTypeCalculation(info, calculatorGh);
			log.info("result" + resultGH);
			results.add(resultGH);
			Double scruitnyfeeGH = resultGH.getScrutinyFeeChargesCal();
			BigDecimal scruitnyfeeGHB = new BigDecimal(scruitnyfeeGH);
			Double licenseFeeGh = resultGH.getLicenseFeeChargesCal();
			BigDecimal licenseFeeGhB = new BigDecimal(licenseFeeGh);
			Double externalChargesFeeGh = resultGH.getExternalDevelopmentChargesCal();
			BigDecimal externalFeeGhB = new BigDecimal(externalChargesFeeGh);
			Double conversionFeeGh = resultGH.getConversionChargesCal();
			BigDecimal conversionFeeGhB = new BigDecimal(conversionFeeGh);
			Double stateInfraStructureFeeGh = resultGH.getStateInfrastructureDevelopmentChargesCal();
			BigDecimal stateInfraStructureFeeGhB = new BigDecimal(stateInfraStructureFeeGh);
			totalFeeGH = (scruitnyfeeGHB.add(licenseFeeGhB).add(externalFeeGhB).add(conversionFeeGhB)
					.add(stateInfraStructureFeeGhB));
			paymentCalculationResponse.setTotalFeeGH(totalFeeGH);
		}
		// --------------------commercial-----------------------//
		CalculatorRequest calculatorComm = new CalculatorRequest();
		calculatorComm.setApplicationNumber(applicationNo);
		calculatorComm.setPotenialZone(detailsLand.get(0).get("ApplicantPurpose").get("AppliedLandDetails").get(0)
				.get("potential").textValue());
		calculatorComm.setPurposeCode("CPRS");
		commercial = detailsLand.get(0).get("DetailsofAppliedLand").get("DetailsAppliedLandPlot").get("commercial")
				.textValue();
		if (commercial != null) {

			Double commercialL = Double.valueOf(commercial);
		//	commercialL = commercialL * 150;
			calculatorComm.setTotalLandSize(commercialL.toString());

			FeesTypeCalculationDto resultComm = calculatorImpl.feesTypeCalculation(info, calculatorComm);
			log.info("resultComm" + resultComm);
			results.add(resultComm);
			Double scruitnyfeeComm = resultComm.getScrutinyFeeChargesCal();
			BigDecimal scruitnyfeeCommB = new BigDecimal(scruitnyfeeComm);
			Double licenseFeeComm = resultComm.getLicenseFeeChargesCal();
			BigDecimal licenseFeeCommB = new BigDecimal(licenseFeeComm);
			Double externalChargesFeeComm = resultComm.getExternalDevelopmentChargesCal();
			BigDecimal externalChargesFeeCommB = new BigDecimal(externalChargesFeeComm);
			Double conversionFeeComm = resultComm.getConversionChargesCal();
			BigDecimal conversionFeeCommB = new BigDecimal(conversionFeeComm);
			Double stateInfraStructureFeeComm = resultComm.getStateInfrastructureDevelopmentChargesCal();
			BigDecimal stateInfraStructureFeeCommB = new BigDecimal(stateInfraStructureFeeComm);
			totalFeeComm = (scruitnyfeeCommB.add(licenseFeeCommB).add(stateInfraStructureFeeCommB)
					.add(externalChargesFeeCommB).add(conversionFeeCommB));
			paymentCalculationResponse.setTotalFeeComm(totalFeeComm);

		}

		switch (purpose) {

		// -------------residental plotted commercial case-------//

		case PURPOSE_RPL:
			
			netPlannedArea
	
			break;
		// ------------AGH--------------------//
		case PURPOSE_AGH:

			totalGhCommArea = (totalFeeGH.add(totalFeeComm));
			totalArea = (totalAreaScheme.subtract(totalGhCommArea));
			paymentCalculationResponse.setTotalArea(totalArea);
			log.info("totalArea" + totalArea);
			break;

		// -----------------------ddjay------------//
		case PURPOSE_DDJAY_APHP:

			totalGhCommArea = (totalFeeGH.add(totalFeeComm));
			totalArea = (totalAreaScheme.subtract(totalGhCommArea));
			paymentCalculationResponse.setTotalArea(totalArea);
			log.info("totalArea" + totalArea);
			break;

		// -------------------------commercial integrated----------------//
		case PURPOSE_CICS:

			totalArea = (totalAreaScheme);
			paymentCalculationResponse.setTotalArea(totalArea);
			log.info("totalArea" + totalArea);

			break;
		// -------------------------commercial integrated----------------//
		case PURPOSE_CIRS:

			totalArea = totalAreaScheme;
			paymentCalculationResponse.setTotalArea(totalArea);
			log.info("totalArea" + totalArea);

			// -------------commercial plotted------//
		case PURPOSE_CPCS:

			totalArea = totalAreaScheme;
			paymentCalculationResponse.setTotalArea(totalArea);
			log.info("totalArea" + totalArea);

			break;
		// -------------commercial plotted------//
		case PURPOSE_CPRS:

			totalArea = totalAreaScheme;
			paymentCalculationResponse.setTotalArea(totalArea);
			log.info("totalArea" + totalArea);
			break;
		case PURPOSE_IPULP:
			totalArea = totalAreaScheme;
			paymentCalculationResponse.setTotalArea(totalArea);
			log.info("totalArea" + totalArea);
			break;
		// ---------industrial colony-----//
		case PURPOSE_IPA:

			totalGhCommArea = (totalFeeGH.add(totalFeeComm));
			totalArea = (totalAreaScheme.subtract(totalGhCommArea));
			paymentCalculationResponse.setTotalArea(totalArea);
			log.info("totalArea" + totalArea);
			break;
		// ----------it colony----//
		case PURPOSE_ITC:
			totalArea = totalAreaScheme;
			paymentCalculationResponse.setTotalArea(totalArea);
			log.info("totalArea" + totalArea);
			break;
		// ----------it colony----//
		case PURPOSE_ITP:
			totalArea = totalAreaScheme;
			paymentCalculationResponse.setTotalArea(totalArea);
			log.info("totalArea" + totalArea);
			break;
		// ---low density-----//
		// --it is not in excel----//
		case PURPOSE_LDEF:

			totalArea = totalAreaScheme;
			paymentCalculationResponse.setTotalArea(totalArea);
			log.info("totalArea" + totalArea);
			break;
		case PURPOSE_MLU_CZ:

			totalGhCommArea = (totalFeeGH.add(totalFeeComm));
			paymentCalculationResponse.setTotalArea(totalArea);
			totalArea = (totalAreaScheme.subtract(totalGhCommArea));
			log.info("totalArea" + totalArea);
			break;

		case PURPOSE_NILPC:
			break;
		// -------------NILP--------//
		case PURPOSE_NILP:
			totalArea = totalAreaScheme;
			paymentCalculationResponse.setTotalArea(totalArea);
			log.info("totalArea" + totalArea);
			break;
		// -----------group housing----------------//
		case PURPOSE_RGP:

			totalGhCommArea = (totalFeeGH.add(totalFeeComm));
			totalArea = (totalAreaScheme.subtract(totalGhCommArea));
			paymentCalculationResponse.setTotalArea(totalArea);
			log.info("totalArea" + totalArea);

			break;
		case PURPOSE_RHP:
			totalGhCommArea = (totalFeeGH.add(totalFeeComm));
			totalArea = (totalAreaScheme.subtract(totalGhCommArea));
			paymentCalculationResponse.setTotalArea(totalArea);
			log.info("totalArea" + totalArea);
			break;

		/// -----------------tod commercial------//
		case PURPOSE_TODCOMM:
			totalArea = totalAreaScheme;
			paymentCalculationResponse.setTotalArea(totalArea);
			log.info("totalArea" + totalArea);
			break;
		case PURPOSE_TODIT:
			break;
		// -----------------Tod group housing---//
		case PURPOSE_TODGH:
			totalArea = totalAreaScheme;
			paymentCalculationResponse.setTotalArea(totalArea);
			log.info("totalArea" + totalArea);
			break;
		case PURPOSE_TODMUD:
			break;
		case PURPOSE_TODMGH:
			break;
		}
		return paymentCalculationResponse;
	}

}
