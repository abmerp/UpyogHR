package org.egov.tlcalculator.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tlcalculator.utils.CalculationUtils;
import org.egov.tlcalculator.utils.LandUtil;
import org.egov.tlcalculator.validator.LandMDMSValidator;
import org.egov.tlcalculator.web.models.LicenseDetails;
import org.egov.tlcalculator.web.models.PurposeDetails;
import org.egov.tlcalculator.web.models.tradelicense.TradeLicense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class CalculationPurpose {
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

	public List<FeesTypeCalculationDto> paymentPurpose(RequestInfo info, String applicationNo) {

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

		String purposeCodeComm = "";
		String purposeCodeGh = "";
		String code = "";
		String nameRes = "";
		BigDecimal maximumPermissibleComm = null;
		BigDecimal maximumPermissibleGh = null;
		BigDecimal maximumPermissibleCommGh = null;
		BigDecimal Rate100 = new BigDecimal(100);
		List<PurposeDetails> purposeDetailList = new ArrayList<PurposeDetails>();
		PurposeDetails purposeDetail = new PurposeDetails();
		purposeDetail.setPurposeDetail(purposeDetailList);
		for (LicenseDetails newobj : newServiceInfoData) {

			if (newobj.getVer() == tradeLicense.getTradeLicenseDetail().getCurrentVersion()) {
				//PurposeDetails purposeDetails = new PurposeDetails();
				purposeDetail=recursionMethod(info,tradeLicense.getTenantId(),newobj.getApplicantPurpose().getPurpose(),new BigDecimal(newobj.getApplicantPurpose().getTotalArea()),purposeDetail);
//				LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> mDMSCallPurposeId = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>) landUtil
//						.mDMSCallPurposeCode(info, tradeLicense.getTenantId(),
//								newobj.getApplicantPurpose().getPurpose());
//
//				Map<String, List<String>> mdmsData;
//				mdmsData = valid.getAttributeValues(mDMSCallPurposeId);
//
//				List<Map<String, Object>> msp = (List) mdmsData.get("Purpose");
//
//				for (Map<String, Object> mm : msp) {
//					code = String.valueOf(mm.get("purposeCode"));
//					nameRes = String.valueOf(mm.get("name"));
//					log.info("code:\t" + code);
//					List<Map<String, Object>> purpose = (List<Map<String, Object>>) (mm.get("purposes"));
//
//					for (Map<String, Object> mmm : purpose) {
//						String purposeCodes = (String.valueOf(mmm.get("purposeCode")));
//						/*
//						 * List<Map<String, Object>> purposesComm = (List<Map<String, Object>>)
//						 * (mmm.get("purposeComm")); List<Map<String, Object>> purposeGroupHousing =
//						 * (List<Map<String, Object>>) (mmm .get("purposeGH"));
//						 * 
//						 * for (Map<String, Object> mmmm : purposesComm) { purposeCodeComm =
//						 * (String.valueOf(mmmm.get("purposeCode"))); maximumPermissibleComm = new
//						 * BigDecimal(String.valueOf(mmmm.get("maximunPermissible")));
//						 * log.info("purposeCode" + purposeCodeComm);
//						 * 
//						 * } for (Map<String, Object> mmmm : purposeGroupHousing) { purposeCodeGh =
//						 * (String.valueOf(mmmm.get("purposeCode"))); maximumPermissibleGh = new
//						 * BigDecimal(String.valueOf(mmmm.get("maximunPermissible")));
//						 * log.info("purposeCode" + purposeCodeGh);
//						 * 
//						 * }
//						 */
//
//					}
//
//				}
//
//				
//
//				// ----------gh---------------//
//
//				LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> mDMSCallPurposeId1 = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>) landUtil
//						.mDMSCallPurposeCode(info, tradeLicense.getTenantId(), purposeCodeGh);
//
//				Map<String, List<String>> mdmsData1 = null;
//				mdmsData1 = valid.getAttributeValues(mDMSCallPurposeId1);
//
//				List<Map<String, Object>> msp1 = (List) mdmsData1.get("Purpose");
//				String name = "";
//
//				for (Map<String, Object> mm : msp1) {
//					name = String.valueOf(mm.get("name"));
//					String codeGH=String.valueOf(mm.get("purposeCode"));
//					
//					List<Map<String, Object>> purpose = (List<Map<String, Object>>) (mm.get("purposes"));
//
//					for (Map<String, Object> mmm : purpose) {
//						List<Map<String, Object>> purposesComm = (List<Map<String, Object>>) (mmm.get("purposeComm"));
//
//						for (Map<String, Object> mmmm : purposesComm) {
//							purposeCodeComm = (String.valueOf(mmmm.get("purposeCode")));
//							maximumPermissibleCommGh = new BigDecimal(String.valueOf(mmmm.get("maximunPermissible")));
//							log.info("purposeCode" + purposeCodeComm);
//						}
//
//					}
//				}
//				
//				maximumPermissibleCommGh = maximumPermissibleCommGh.divide(Rate100);
//				maximumPermissibleCommGh = maximumPermissibleCommGh.multiply(maximumPermissibleGh);

			}
			

			// -----------gh end---------//
		}
		log.info("purposeDetail"+purposeDetail);
		return results;
		
	}
	public PurposeDetails recursionMethod(RequestInfo info,String tenantId,String purposeCode,BigDecimal totalArea,PurposeDetails purposeDetailm) {
			
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> mDMSCallPurposeId1 = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>) landUtil
				.mDMSCallPurposeCode(info, tenantId, purposeCode);

		Map<String, List<String>> mdmsData1 = null;
		mdmsData1 = valid.getAttributeValues(mDMSCallPurposeId1);
		
		List<Map<String, Object>> msp = (List) mdmsData1.get("Purpose");
		for (Map<String, Object> mm : msp) {
			String code = String.valueOf(mm.get("purposeCode"));
			String nameRes = String.valueOf(mm.get("name"));
			log.info("code:\t" + code);
			
			purposeDetailm.setCode(code);
			purposeDetailm.setName(nameRes);

			List<Map<String, Object>> purpose = (List<Map<String, Object>>) (mm.get("purposes"));
			if(purpose!=null)
			for (Map<String, Object> mmm : purpose) {
				PurposeDetails purposeDetail = new PurposeDetails();
				List<PurposeDetails> purposeDetailList = new ArrayList<PurposeDetails>();
				purposeDetail.setPurposeDetail(purposeDetailList);
				String purposeCodes = (String.valueOf(mmm.get("purposeCode")));
				String maximunPermissible =String.valueOf(mmm.get("maximunPermissible"));
					log.info("purpose"+purposeCodes);
					if(maximunPermissible!=null) {
					purposeDetail.setArea(totalArea.multiply(new BigDecimal(maximunPermissible )).toString());
					log.info("total area"+purposeDetail.getArea());
					recursionMethod(info,tenantId,purposeCodes,new BigDecimal(purposeDetail.getArea()),purposeDetail);
					}else{
						recursionMethod(info,tenantId,purposeCodes,totalArea,purposeDetail);
					}
					purposeDetailm.getPurposeDetail().add(purposeDetail);
			}
			
		}
		return purposeDetailm;
	}
}
