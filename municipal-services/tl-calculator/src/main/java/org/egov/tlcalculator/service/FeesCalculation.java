package org.egov.tlcalculator.service;

import java.io.IOException;
import java.lang.reflect.Array;
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
import org.egov.tlcalculator.web.models.PurposeDetails;
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
	BigDecimal totalFee = new BigDecimal(0);
	BigDecimal totalScruitnyFee = new BigDecimal(0);
	BigDecimal totalLicenceFee = new BigDecimal(0);
	
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

		// List<FeesTypeCalculationDto> feesTypeCalculationDtoList = new
		// ArrayList<FeesTypeCalculationDto>();
		FeesTypeCalculationDto feesTypeCalculationDto = new FeesTypeCalculationDto();
		feesTypeCalculationDto.setFeesTypeCalculationDto(results);

		for (LicenseDetails newobj : newServiceInfoData) {

			if (newobj.getVer() == tradeLicense.getTradeLicenseDetail().getCurrentVersion()) {
//
//				LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> mDMSCallPurposeId = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>) landUtil
//						.mDMSCallPurposeCode(info, tradeLicense.getTenantId(),
//								newobj.getApplicantPurpose().getPurpose());
//
//				Map<String, List<String>> mdmsData;
//				mdmsData = valid.getAttributeValues(mDMSCallPurposeId);
//
//				List<Map<String, Object>> msp = (List) mdmsData.get("Purpose");
//
//				int purposeId = 0;
//
//				for (Map<String, Object> mm : msp) {
//
//					purposeId = Integer.valueOf(String.valueOf(mm.get("purposeId")));
//					log.info("purposeId" + purposeId);
//
//				}
			
			// String purpose = newobj.getApplicantPurpose().getPurpose();
			String zone = newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getPotential();
			String totalArea = newobj.getApplicantPurpose().getTotalArea();
			List<PurposeDetails> purposeDetail = newobj.getDetailsofAppliedLand().getPurposeDetails();
			
			log.info("purposeDetail" + purposeDetail);
			totalFee = new BigDecimal(0);
			totalScruitnyFee= new BigDecimal(0);
			totalLicenceFee= new BigDecimal(0);
			feesTypeCalculationDto = recursionMethod(info, applicationNo, totalArea, zone, purposeDetail.get(0));
			
			log.info("totalFee" + totalFee);
			feesTypeCalculationDto.setTotalFee(totalFee);
			feesTypeCalculationDto.setTotalScruitnyFee(totalScruitnyFee);
			if(purposeDetail.get(0).getCode().equals("AGH")) {	
				for(FeesTypeCalculationDto feesTypeCalculationDtoDetails:feesTypeCalculationDto.getFeesTypeCalculationDto()) {
					feesTypeCalculationDtoDetails.setLicenseFeeChargesCal(BigDecimal.ZERO);					
				}
			}else {
			feesTypeCalculationDto.setTotalLicenceFee(totalLicenceFee);
			}
			results.add(feesTypeCalculationDto);
			}
		}
		return results;
	}

	public FeesTypeCalculationDto recursionMethod(RequestInfo info, String applicationNo, String totalArea, String zone,
			PurposeDetails purposeDetailm) {
		
		CalculatorRequest calculator = new CalculatorRequest();
		calculator.setApplicationNumber(applicationNo);
		calculator.setPotenialZone(zone);
		calculator.setPurposeCode(purposeDetailm.getCode());
		calculator.setFar(purposeDetailm.getFar());
		if (purposeDetailm.getArea() != null) {
			calculator.setTotalLandSize(purposeDetailm.getArea());
		} else {
			calculator.setTotalLandSize(totalArea);
		}

		FeesTypeCalculationDto result = calculatorImpl.feesTypeCalculation(info, calculator);
		FeesTypeCalculationDto feesTypeCalculation = new FeesTypeCalculationDto();
		List<FeesTypeCalculationDto> feesTypeCalculationDtoList = new ArrayList<FeesTypeCalculationDto>();
		feesTypeCalculation.setFeesTypeCalculationDto(feesTypeCalculationDtoList);
		if(result.getScrutinyFeeChargesCal()!=null) {
			
			log.info("ScrutinyFee\t" + result.getScrutinyFeeChargesCal());
			
			 totalScruitnyFee = totalScruitnyFee.add(result.getScrutinyFeeChargesCal());
			BigDecimal licenseFee=result.getLicenseFeeChargesCal().multiply(new BigDecimal(.25));
			
			totalLicenceFee=totalLicenceFee.add(licenseFee);
			log.info("LicenseFee\t" + licenseFee);
			BigDecimal amount = result.getScrutinyFeeChargesCal().add(licenseFee);
			BigDecimal amounttmep = totalFee.add(amount);
			
			totalFee = new BigDecimal(amounttmep.setScale(0, BigDecimal.ROUND_UP).toString());
			//log.info("ScrutinyFee" + result.getScrutinyFeeChargesCal());
		
			result.setTotalFee(amount);
			result.setTotalScruitnyFee(totalScruitnyFee);
			result.setTotalLicenceFee(totalLicenceFee);
			log.info(purposeDetailm.getName()+"\t"+amount);
			log.info("Total "+"\t"+totalFee);
		}
		for (PurposeDetails purpose : purposeDetailm.getPurposeDetail()) {
			FeesTypeCalculationDto newResult = recursionMethod(info, applicationNo, totalArea, zone, purpose);
			feesTypeCalculationDtoList.add(newResult);
			result.setFeesTypeCalculationDto(feesTypeCalculationDtoList);

		}
		log.info("result" + result);

		return result;
	}
}
