package org.egov.tlcalculator.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tlcalculator.utils.CalculationUtils;
import org.egov.tlcalculator.utils.LandUtil;
import org.egov.tlcalculator.validator.LandMDMSValidator;
import org.egov.tlcalculator.web.models.CalculatorRequest;
import org.egov.tlcalculator.web.models.LicenseDetails;
import org.egov.tlcalculator.web.models.tradelicense.TradeLicense;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FeesCalculation implements Calculator {
	@Autowired
	LandMDMSValidator valid;
	@Autowired
	ObjectMapper mapper;
	@Autowired
	LandUtil landUtil;
	@Autowired
	CalculationUtils utils;

	@Autowired
	CalculatorImpl calculatorImpl;

	public List<FeesTypeCalculationDto> payment(RequestInfo info, String applicationNo) {

		String applicationNumber = applicationNo;
		String tenantId = "hr";
		List<FeesTypeCalculationDto> results = new ArrayList<FeesTypeCalculationDto>();
		TradeLicense tradeLicense = utils.getTradeLicense(info, applicationNo, tenantId);
		log.info("license" + tradeLicense);

		ObjectReader reader = mapper.readerFor(new TypeReference<List<LicenseDetails>>() {
		});
		List<LicenseDetails> newServiceInfoData = null;
		try {
			newServiceInfoData = reader.readValue(tradeLicense.getTradeLicenseDetail().getAdditionalDetail());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (LicenseDetails newobj : newServiceInfoData) {

			if (newobj.getVer() == tradeLicense.getTradeLicenseDetail().getCurrentVersion()) {

				LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> mDMSCallPurposeId = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>) landUtil
						.mDMSCallPurposeCode(info, tradeLicense.getTenantId(),
								newobj.getApplicantPurpose().getPurpose());

				Map<String, List<String>> mdmsData;
				mdmsData = valid.getAttributeValues(mDMSCallPurposeId);

				List<Map<String, Object>> msp = (List) mdmsData.get("Purpose");

				int purposeId = 0;

				for (Map<String, Object> mm : msp) {

					purposeId = Integer.valueOf(String.valueOf(mm.get("purposeId")));
					log.info("purposeId" + purposeId);

				}

				String purpose = newobj.getApplicantPurpose().getPurpose();
				String areaUnderGh = "";
				String commercial = "";
				String netPlannedArea = "";

				PaymentCalculationResponse paymentCalculationResponse = new PaymentCalculationResponse();
				CalculatorRequest calculator = new CalculatorRequest();

				// ---------------group housing--------------------//
				CalculatorRequest calculatorGh = new CalculatorRequest();
				calculatorGh.setApplicationNumber(applicationNo);
				calculatorGh
						.setPotenialZone(newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getPotential());
				calculatorGh.setPurposeCode("RGP");
				calculatorGh.setFar(newobj.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getFAR());
				areaUnderGh = newobj.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getAreaUnderGH();
				netPlannedArea = newobj.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getNetPlannedArea();
				Double areaUnderGhL = new Double("0");
				if (areaUnderGh != null) {
					areaUnderGhL = Double.valueOf(areaUnderGh);

					calculatorGh.setTotalLandSize(areaUnderGhL.toString());

					FeesTypeCalculationDto resultGH = calculatorImpl.feesTypeCalculation(info, calculatorGh);
					log.info("result" + resultGH);
					results.add(resultGH);

				}
				// --------------------commercial-----------------------//
				CalculatorRequest calculatorComm = new CalculatorRequest();
				calculatorComm.setApplicationNumber(applicationNo);
				calculatorComm
						.setPotenialZone(newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getPotential());
				calculatorComm.setPurposeCode("CPRS");
				calculatorComm.setFar(newobj.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getFAR());
				Double commercialL = new Double("0");
				commercial = newobj.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getCommercial();

				if (commercial != null) {

					commercialL = Double.valueOf(commercial);

					calculatorComm.setTotalLandSize(commercialL.toString());

					FeesTypeCalculationDto resultComm = calculatorImpl.feesTypeCalculation(info, calculatorComm);
					log.info("resultComm" + resultComm);
					results.add(resultComm);

				}
				// ----------different purposes----------//
				calculator.setApplicationNumber(applicationNo);
				calculator.setPotenialZone(newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getPotential());
				calculator.setPurposeCode(purpose);
				calculator.setFar(newobj.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getFAR());
				Double totalSchemeArea = Double
						.valueOf(newobj.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getTotalAreaScheme());
				totalSchemeArea = totalSchemeArea - commercialL - areaUnderGhL;
				calculator.setTotalLandSize(totalSchemeArea.toString());
				FeesTypeCalculationDto resultresid = calculatorImpl.feesTypeCalculation(info, calculator);
				log.info("resultComm" + resultresid);
				results.add(resultresid);

			}
		}

		return results;
	}
}
