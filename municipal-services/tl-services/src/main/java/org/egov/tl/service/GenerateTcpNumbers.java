package org.egov.tl.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.tl.abm.newservices.contract.ApprovalStandardContract;
import org.egov.tl.abm.newservices.entity.ApprovalStandardEntity;
import org.egov.tl.util.LandUtil;
import org.egov.tl.validator.LandMDMSValidator;
import org.egov.tl.web.models.ElectricPlanContract;
import org.egov.tl.web.models.ElectricPlanRequest;
import org.egov.tl.web.models.LicenseDetails;
import org.egov.tl.web.models.RevisedPlan;
import org.egov.tl.web.models.RevisedPlanRequest;
import org.egov.tl.web.models.ServicePlanContract;
import org.egov.tl.web.models.ServicePlanInfoResponse;
import org.egov.tl.web.models.ServicePlanRequest;
import org.egov.tl.web.models.SurrendOfLicense;
import org.egov.tl.web.models.SurrendOfLicenseRequest;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseResponse;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.Transfer;
import org.egov.tl.web.models.TransferOfLicenseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GenerateTcpNumbers {

	@Autowired
	private TransferOfLicenseServices transferOfLicenseServices;
	@Autowired
	private RevisedPlanServices revisedPlanServices;
	@Autowired
	private SurrendOfLicenseServices surrendOfLicenseServices;
	@Autowired
	LandUtil landUtil;
	@Autowired
	TradeLicenseService tradeLicenseService;
	@Autowired
	ObjectMapper mapper;
	@Autowired
	LandMDMSValidator valid;
	@Autowired
	ThirPartyAPiCall thirPartyAPiCall;
	@Autowired
	ServicePlanService servicePlanService;
	@Autowired
	ElectricPlanService electricPlanService;
	@Autowired
	ApprovalStandardService approvalStandardService;

	public Map<String, Object> tcpNumbers(TradeLicenseSearchCriteria criteria, RequestInfo requestInfo) {
		Map<String, Object> rP = new HashMap<>();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
		LocalDateTime localDateTime = LocalDateTime.now();
		String date = formatter.format(localDateTime);

		String dairyNumber = null;
		String caseNumber = null;
		String tcpApplicationNmber = null;

		List<String> licenceNumber = criteria.getLicenseNumbers();

		criteria.setLicenseNumbers(licenceNumber);

		criteria.setLoiNumber(criteria.getLoiNumber());
		List<TradeLicense> tradeLicenses = tradeLicenseService.getLicensesWithOwnerInfo(criteria, requestInfo);
		log.info("tradeLicenses:\t)" + tradeLicenses);
		for (TradeLicense tradeLicense : tradeLicenses) {

			ObjectReader reader = mapper.readerFor(new TypeReference<List<LicenseDetails>>() {
			});

			Map<String, Object> authtoken = new HashMap<String, Object>();
			authtoken.put("UserId", "39");
			authtoken.put("TpUserId", "123");
			authtoken.put("EmailId", "mkthakur84@gmail.com");

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
							.mDMSCallPurposeCode(requestInfo, tradeLicense.getTenantId(),
									newobj.getApplicantPurpose().getPurpose());

					Map<String, List<String>> mdmsData;
					mdmsData = valid.getAttributeValues(mDMSCallPurposeId);

					List<Map<String, Object>> msp = (List) mdmsData.get("Purpose");

					int purposeId = 0;

					for (Map<String, Object> mm : msp) {

						purposeId = Integer.valueOf(String.valueOf(mm.get("purposeId")));
						log.info("purposeId" + purposeId);

					}

					LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> mDMSCallDistrictId = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>) landUtil
							.mDMSCallDistrictCode(requestInfo, tradeLicense.getTenantId(),
									newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getDistrict().getValue());

					Map<String, List<String>> mdmsDatadistrict;
					mdmsDatadistrict = valid.getAttributeValues(mDMSCallDistrictId);

					List<Map<String, Object>> mspDistrict = (List) mdmsDatadistrict.get("District");
					log.info("mspDistrict" + mspDistrict);
					int distCodeNIC = 0;

					for (Map<String, Object> mmDistrict : mspDistrict) {

						distCodeNIC = Integer.valueOf(String.valueOf(mmDistrict.get("distCodeNIC")));
						log.info("distCodeNIC" + distCodeNIC);

					}
					Map<String, Object> mapDNo = new HashMap<String, Object>();

					mapDNo.put("Village",
							newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getRevenueEstate());
					mapDNo.put("DiaryDate", date);
					mapDNo.put("ReceivedFrom", newobj.getApplicantInfo().getAuthorizedDeveloper());
					mapDNo.put("DistrictCode", distCodeNIC);

					dairyNumber = thirPartyAPiCall.generateDiaryNumber(mapDNo, authtoken).getBody().get("Value")
							.toString();
					// tradeLicense.setTcpDairyNumber(dairyNumber);

					/****************
					 * End Here
					 ***********/
					// case number
					Map<String, Object> mapCNO = new HashMap<String, Object>();
					mapCNO.put("DiaryNo", dairyNumber);
					mapCNO.put("DiaryDate", date);
					mapCNO.put("DeveloperId", "2");
					mapCNO.put("PurposeId", purposeId);
					mapCNO.put("StartDate", date);
					mapCNO.put("DistrictCode", distCodeNIC);
					mapCNO.put("Village",
							newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getRevenueEstate());
					mapCNO.put("ChallanAmount", newobj.getFeesAndCharges().getPayableNow());

					caseNumber = thirPartyAPiCall.generateCaseNumber(mapCNO, authtoken).getBody().get("Value")
							.toString();
					// tradeLicense.setTcpCaseNumber(caseNumber);

					/****************
					 * End Here
					 ***********/
					// application number
					Map<String, Object> mapANo = new HashMap<String, Object>();
					mapANo.put("DiaryNo", dairyNumber);
					mapANo.put("CaseId", caseNumber.split("~")[1]);
					mapANo.put("DiaryDate", date);
					mapANo.put("TotalArea", newobj.getApplicantPurpose().getTotalArea());
					mapANo.put("Village",
							newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getRevenueEstate());
					mapANo.put("PurposeId", purposeId);
					mapANo.put("NameofOwner",
							newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getLandOwner());
					mapANo.put("DateOfHearing", date);
					mapANo.put("DateForFilingOfReply", date);

					tcpApplicationNmber = thirPartyAPiCall.generateApplicationNumber(mapANo, authtoken).getBody()
							.get("Value").toString();
					// tradeLicense.setTcpApplicationNumber(tcpApplicationNmber);

				}

				rP.put("TCPApplicationNumber", tcpApplicationNmber);
				rP.put("TCPCaseNumber", caseNumber);
				rP.put("TCPDairyNumber", dairyNumber);

			}
		}
		return rP;

	}

}
