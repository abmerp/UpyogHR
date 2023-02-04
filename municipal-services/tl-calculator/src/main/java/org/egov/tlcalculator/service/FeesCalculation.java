package org.egov.tlcalculator.service;

import java.util.Map;
import javax.validation.Valid;
import org.egov.common.contract.request.RequestInfo;

import org.egov.tlcalculator.config.TLCalculatorConfigs;
import org.egov.tlcalculator.utils.CalculationUtils;
import org.egov.tlcalculator.web.models.CalculatorRequest;
import org.egov.tlcalculator.web.models.tradelicense.TradeLicense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

public class FeesCalculation {

	@Autowired
	CalculationUtils utils;

	@Autowired
	CalculatorImpl calculatorImpl;

	public Double payment(RequestInfo info, String applicationNo) {

		String applicationNumber = applicationNo;
		String tenantId = "hr";

		TradeLicense license = utils.getTradeLicense(info, applicationNo, tenantId);
		log.info("license" + license);
		JsonNode detailsLand = license.getTradeLicenseDetail().getAdditionalDetail();
		String purposeCode = "RPL";
		String purpose = detailsLand.get(0).get("ApplicantPurpose").get("purpose").textValue();
		String totalAreaScheme = detailsLand.get(0).get("DetailsofAppliedLand").get("DetailsAppliedLandPlot")
				.get("totalAreaScheme").textValue();
		
		if (purpose.equalsIgnoreCase(purposeCode)) {

			CalculatorRequest calculator = new CalculatorRequest();
			calculator.setApplicationNumber(applicationNo);
			calculator.setPotenialZone(detailsLand.get(0).get("ApplicantPurpose").get("AppliedLandDetails").get(0)
					.get("potential").textValue());
			calculator.setPurposeCode("RGP");
			calculator.setTotalLandSize("1");

			FeesTypeCalculationDto resultGH = calculatorImpl.feesTypeCalculation(info, calculator);
			log.info("result" + resultGH);
			Double scruitnyfeeGH = resultGH.getScrutinyFeeChargesCal();
			Double licenseFeeGh = resultGH.getLicenseFeeChargesCal();
			Double externalFeeGh = resultGH.getExternalDevelopmentChargesCal();
			Double conversionFeeGh = resultGH.getConversionChargesCal();
			Double stateInfraStructureFeeGh = resultGH.getStateInfrastructureDevelopmentChargesCal();
			Double totalFeeGH = resultGH.getTotalFee();

			CalculatorRequest calculator1 = new CalculatorRequest();
			calculator1.setApplicationNumber(applicationNo);
			calculator1.setPotenialZone(detailsLand.get(0).get("ApplicantPurpose").get("AppliedLandDetails").get(0)
					.get("potential").textValue());
			calculator1.setPurposeCode("CPRS");
			calculator1.setTotalLandSize("1");

			FeesTypeCalculationDto resultComm = calculatorImpl.feesTypeCalculation(info, calculator1);
			log.info("resultComm" + resultComm);

			Double scruitnyfeeComm = resultComm.getScrutinyFeeChargesCal();
			Double licenseFeeComm = resultComm.getLicenseFeeChargesCal();
			Double externalFeeComm = resultComm.getExternalDevelopmentChargesCal();
			Double conversionFeeComm = resultComm.getConversionChargesCal();
			Double stateInfraStructureFeeComm = resultComm.getStateInfrastructureDevelopmentChargesCal();
			Double totalFeeComm = resultComm.getTotalFee();

			Double GH = 1.5 * totalFeeGH;
			Double comm = 1.75 * totalFeeComm;
			Double total = (GH + comm);

			Double residentalArea = (Double.valueOf(totalAreaScheme) - total);
			log.info("residentalArea" + residentalArea);
			return residentalArea;

		}
		return null;

	}
}
