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
import org.egov.tl.util.LandUtil;
import org.egov.tl.validator.LandMDMSValidator;
import org.egov.tl.web.models.ElectricPlanContract;
import org.egov.tl.web.models.ElectricPlanRequest;
import org.egov.tl.web.models.LicenseDetails;
import org.egov.tl.web.models.ServicePlanContract;
import org.egov.tl.web.models.ServicePlanInfoResponse;
import org.egov.tl.web.models.ServicePlanRequest;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseResponse;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
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
	private static final String BUSINESSSERVICE_SERVICE_PLAN = "SERVICE_PLAN";

	private static final String BUSINESSSERVICE_ELECTRICAL_PLAN = "ELECTRICAL_PLAN";

	private static final String BUSINESSSERVICE_SERVICE_PLAN_DEMACATION = "SERVICE_PLAN_DEMARCATION";
	private static final String BUSINESSSERVICE_APPROVAL_OF_STANDARD = "APPROVAL_OF_STANDARD";
	private static final String BUSINESSSERVICE_CHANGEBENEFICIAL = "CHANGE_OF_BENEFICIAL";
	private static final String BUSINESSSERVICE_TRANSFER = "TRANSFER_OF_LICIENCE";
	private static final String BUSINESSSERVICE_RENEWAL = "RENWAL_OF_LICENCE";
	private static final String BUSINESSSERVICE_REVISED = "REVISED_LAYOUT_PLAN";

	public Map<String, Object> tcpNumbers(TradeLicenseSearchCriteria criteria, RequestInfo requestInfo,
			String businessService) {
		Map<String, Object> rP = new HashMap<>();
		Map<String, Object> finalResponse = new HashMap<>();
		ResponseInfo responseInfo = new ResponseInfo();
		// ResponseEntity<?> result = null;
		List<ServicePlanRequest> resultServicePlan = null;
		List<ElectricPlanRequest> resultElectricPlan = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
		LocalDateTime localDateTime = LocalDateTime.now();
		String date = formatter.format(localDateTime);
		ResponseEntity<Map> dairy;
		ResponseEntity<Map> caseno;
		ResponseEntity<Map> application = null;
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

					Map<String, Object> mapDNo = new HashMap<String, Object>();

					mapDNo.put("Village",
							newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getRevenueEstate());
					mapDNo.put("DiaryDate", date);
					mapDNo.put("ReceivedFrom", newobj.getApplicantInfo().getAuthorizedDeveloper());
					mapDNo.put("UserId", "1265");
					mapDNo.put("DistrictCode",
							newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getDistrict());
					mapDNo.put("UserLoginId", "39");

					dairy = thirPartyAPiCall.generateDiaryNumber(mapDNo, authtoken);
					dairyNumber = dairy.getBody().get("Value").toString();
					log.info("dairyNumber:\t" + dairyNumber);

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
					mapCNO.put("DistrictCode",
							newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getDistrict());
					mapCNO.put("Village",
							newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getRevenueEstate());
					mapCNO.put("ChallanAmount", newobj.getFeesAndCharges().getPayableNow());
					mapCNO.put("UserId", "2");
					mapCNO.put("UserLoginId", "39");
					caseno = thirPartyAPiCall.generateCaseNumber(mapCNO, authtoken);
					caseNumber = caseno.getBody().get("Value").toString();

					log.info("caseNumber:\t" + caseNumber);

					/****************
					 * End Here
					 ***********/
					// application number
					Map<String, Object> mapANo = new HashMap<String, Object>();
					mapANo.put("DiaryNo", dairyNumber);
					mapANo.put("DiaryDate", date);
					mapANo.put("TotalArea", newobj.getApplicantPurpose().getTotalArea());
					mapANo.put("Village",
							newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getRevenueEstate());
					mapANo.put("PurposeId", purposeId);
					mapANo.put("NameofOwner",
							newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getLandOwner());
					mapANo.put("DateOfHearing", date);
					mapANo.put("DateForFilingOfReply", date);
					mapANo.put("UserId", "2");
					mapANo.put("UserLoginId", "39");
					application = thirPartyAPiCall.generateApplicationNumber(mapANo, authtoken);
					tcpApplicationNmber = application.getBody().get("Value").toString();

					log.info("application:" + tcpApplicationNmber);
				}

//				rP.put("TCPApplicationNumber",tcpApplicationNmber);
//				rP.put("TCPCaseNumber",caseNumber);
//				rP.put("TCPDairyNumber",dairyNumber);
				switch (businessService) {
				case BUSINESSSERVICE_SERVICE_PLAN:

					ServicePlanContract servicePlanContract = new ServicePlanContract();
					List<ServicePlanRequest> servicePlanRequestLists = new ArrayList<ServicePlanRequest>();
					List<ServicePlanRequest> servicePlanRequests = servicePlanService
							.searchServicePlan(criteria.getLoiNumber(), criteria.getApplicationNumber(), requestInfo);
					log.info("servicePlanRequest:\t" + servicePlanRequests);

					List<ServicePlanRequest> servicePlanRequestList = servicePlanRequests;

					for (ServicePlanRequest servicePlanRequest : servicePlanRequestList) {

						servicePlanRequest.setTcpApplicationNumber(tcpApplicationNmber);
						servicePlanRequest.setTcpCaseNumber(caseNumber);
						servicePlanRequest.setTcpDairyNumber(dairyNumber);
						servicePlanRequestLists.add(servicePlanRequest);
						servicePlanContract.setServicePlanRequest(servicePlanRequestLists);
						servicePlanContract.setRequestInfo(requestInfo);

					}
					resultServicePlan = servicePlanService.Update(servicePlanContract);

					break;
				case BUSINESSSERVICE_ELECTRICAL_PLAN:
					ElectricPlanContract electricPlanContract = new ElectricPlanContract();
					List<ElectricPlanRequest> electricPlanRequestLists = new ArrayList<ElectricPlanRequest>();
					List<ElectricPlanRequest> ElectricPlanRequests = electricPlanService
							.searchElectricPlan(criteria.getLoiNumber(), criteria.getApplicationNumber(), requestInfo);
					log.info("ElectricPlanRequests:\t" + ElectricPlanRequests);

					List<ElectricPlanRequest> electricPlanRequestList = ElectricPlanRequests;

					for (ElectricPlanRequest electricPlanRequest : electricPlanRequestList) {

						electricPlanRequest.setTcpApplicationNumber(tcpApplicationNmber);
						electricPlanRequest.setTcpCaseNumber(caseNumber);
						electricPlanRequest.setTcpDairyNumber(dairyNumber);
						electricPlanRequestLists.add(electricPlanRequest);
						electricPlanContract.setElectricPlanRequest(electricPlanRequestLists);
						electricPlanContract.setRequestInfo(requestInfo);

					}
					resultElectricPlan = electricPlanService.Update(electricPlanContract);

					break;

				}
				rP.put("servicePlan", resultServicePlan);
				rP.put("electricPlan", resultElectricPlan);
				finalResponse.put("ResponseInfo", responseInfo);
				for (Map.Entry e : rP.entrySet()) {
					if (e.getValue() != null) {
						finalResponse.put(e.getKey().toString(), e.getValue());

					}

				}
			}
		}
		return finalResponse;

	}

}
