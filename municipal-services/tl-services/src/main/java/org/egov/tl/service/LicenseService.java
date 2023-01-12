package org.egov.tl.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.springframework.util.MultiValueMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.service.dao.LicenseServiceDao;
import org.egov.tl.service.repo.LicenseServiceRepo;
import org.egov.tl.util.LandUtil;
import org.egov.tl.util.TLConstants;
import org.egov.tl.validator.LandMDMSValidator;
import org.egov.tl.web.models.Transaction;
import org.egov.tl.web.models.LicenseDetails;
import org.egov.tl.web.models.LicenseServiceRequest;
import org.egov.tl.web.models.LicenseServiceResponseInfo;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LicenseService {

	@Autowired
	LandUtil landUtil;

	@Autowired
	RestTemplate rest;

	@Autowired
	TLConfiguration config;

	@Autowired
	LandMDMSValidator valid;

	@Autowired
	private ThirPartyAPiCall thirPartyAPiCall;
	@Autowired
	LicenseServiceRepo newServiceInfoRepo;
	@Autowired
	EntityManager em;
	private long id = 1;
	@Autowired
	TradeLicenseService tradeLicenseService;
	@Autowired
	ObjectMapper mapper;

	@Transactional
	public LicenseServiceResponseInfo createNewServic(LicenseServiceRequest newServiceInfo)
			throws JsonProcessingException {

		LicenseServiceResponseInfo objLicenseServiceRequestInfo = new LicenseServiceResponseInfo();
		LicenseServiceDao newServiceIn = new LicenseServiceDao();
		List<LicenseDetails> newServiceInfoDatas = null;
		User user = newServiceInfo.getRequestInfo().getUserInfo();
		// if (newServiceInfo.getId() != null && newServiceInfo.getId() > 0) {
		TradeLicenseSearchCriteria tradeLicenseRequest = new TradeLicenseSearchCriteria();
		if (!StringUtils.isEmpty(newServiceInfo.getApplicationNumber())) {
			tradeLicenseRequest.setApplicationNumber(newServiceInfo.getApplicationNumber());
			List<TradeLicense> tradeLicenses = tradeLicenseService.getLicensesWithOwnerInfo(tradeLicenseRequest,
					newServiceInfo.getRequestInfo());
			for (TradeLicense tradeLicense : tradeLicenses) {

				ObjectReader reader = mapper.readerFor(new TypeReference<List<LicenseDetails>>() {
				});
				// newServiceIn = em.find(LicenseServiceDao.class, newServiceInfo.getId());
				try {
					newServiceInfoDatas = reader.readValue(tradeLicense.getTradeLicenseDetail().getAdditionalDetail());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// newServiceInfoDatas = newServiceIn.getNewServiceInfoData();
				float cv = tradeLicense.getTradeLicenseDetail().getCurrentVersion() + 0.1f;

				for (LicenseDetails newobj : newServiceInfoDatas) {

					if (newobj.getVer() == tradeLicense.getTradeLicenseDetail().getCurrentVersion()) {

						switch (newServiceInfo.getPageName()) {
						case "ApplicantInfo": {
							newobj.setApplicantInfo(newServiceInfo.getLicenseDetails().getApplicantInfo());
							break;
						}
						case "ApplicantPurpose": {
							newobj.setApplicantPurpose(newServiceInfo.getLicenseDetails().getApplicantPurpose());
							break;
						}
						case "LandSchedule": {
							newobj.setLandSchedule(newServiceInfo.getLicenseDetails().getLandSchedule());
							break;
						}
						case "DetailsofAppliedLand": {
							newobj.setDetailsofAppliedLand(
									newServiceInfo.getLicenseDetails().getDetailsofAppliedLand());
							break;
						}
						case "FeesAndCharges": {
							newobj.setFeesAndCharges(newServiceInfo.getLicenseDetails().getFeesAndCharges());
							break;
						}
						}

						newobj.setVer(cv);
						newServiceInfoDatas.add(newobj);
						newServiceIn.setNewServiceInfoData(newServiceInfoDatas);
						break;
					}
				}
				String data = mapper.writeValueAsString(newServiceInfoDatas);
				JsonNode jsonNode = mapper.readTree(data);
				// tradeLicense.setAuditDetails(null);
				tradeLicense.getTradeLicenseDetail().setAdditionalDetail(jsonNode);
				newServiceIn.setTenantId(newServiceInfo.getRequestInfo().getUserInfo().getTenantId());
				newServiceIn.setUpdatedDate(new Date());
				//newServiceIn.setApplicationStatus(tradeLicense.getStatus());
				newServiceIn.setApplicationNumber(tradeLicense.getApplicationNumber());
				newServiceIn.setUpdateddBy(newServiceInfo.getRequestInfo().getUserInfo().getUuid());
				newServiceIn.setCurrentVersion(cv);
				tradeLicense.getTradeLicenseDetail().setCurrentVersion(cv);
				tradeLicense.setAction(newServiceInfo.getAction());
				tradeLicense.setWorkflowCode("NewTL");
				switch(tradeLicense.getAction()){
				case "INITIATE": {
					tradeLicense.setStatus("INITIATED");
					break;
				}
				case "PURPOSE": {
					tradeLicense.setStatus("PURPOSE");
					break;
				}
				case "LANDSCHEDULE": {
					tradeLicense.setStatus("LANDSCHEDULE");
					break;
				}
				case "LANDDETAILS": {
					tradeLicense.setStatus("LANDDETAILS");
					break;
				}
				case "FEESANDCHARGES": {
					tradeLicense.setStatus("FEESANDCHARGES");
					break;
				}
				case "PAID": {
					tradeLicense.setStatus("PAID");
					break;
				}
				}
					
				// tradeLicense.setAssignee(Arrays.asList("f9b7acaf-c1fb-4df2-ac10-83b55238a724"));

				TradeLicenseRequest tradeLicenseRequests = new TradeLicenseRequest();

				tradeLicenseRequests.addLicensesItem(tradeLicense);
				tradeLicenseRequests.setRequestInfo(newServiceInfo.getRequestInfo());
				tradeLicenseService.update(tradeLicenseRequests, "TL");
			}
		}
		
		// }
		else {
			newServiceInfoDatas = new ArrayList<>();
			// newServiceIn = new LicenseServiceDao();
			newServiceIn.setCreatedBy(newServiceInfo.getRequestInfo().getUserInfo().getUuid());
			newServiceIn.setCreatedDate(new Date());
			newServiceIn.setUpdatedDate(new Date());
			newServiceIn.setTenantId(newServiceInfo.getRequestInfo().getUserInfo().getTenantId());
			
			newServiceInfo.getLicenseDetails().setVer(0.1f);
			newServiceIn.setUpdateddBy(newServiceInfo.getRequestInfo().getUserInfo().getUuid());
			newServiceInfoDatas.add(newServiceInfo.getLicenseDetails());
			newServiceIn.setNewServiceInfoData(newServiceInfoDatas);
			newServiceIn.setCurrentVersion(0.1f);

			TradeLicenseRequest request = new TradeLicenseRequest();
			request.setRequestInfo(newServiceInfo.getRequestInfo());

			TradeLicense tradeLicense = new TradeLicense();

			TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
			tradeLicense.setId(String.valueOf(newServiceInfo.getId()));
			// tradeLicense.setStatus(newServiceInfo.getApplicationStatus());
			tradeLicense.setAction(newServiceInfo.getAction());
			tradeLicense.setApplicationDate(new Date().getTime());
			// tradeLicense.getApplicationNumber();
			tradeLicense.setApplicationType(TradeLicense.ApplicationTypeEnum.NEW);
			// tradeLicense.getAssignee();
			// tradeLicense.getAuditDetails();
			tradeLicense.setBusinessService("TL");
			// tradeLicense.getCalculation();
			// tradeLicense.getComment();
			// tradeLicense.getFileStoreId();
			tradeLicense.setFinancialYear("2022-23");
			tradeLicense.setIssuedDate(new Date().getTime());
		//	tradeLicense.setStatus("INITIATED");
			switch(tradeLicense.getAction()){
			case "INITIATE": {
				tradeLicense.setStatus("INITIATED");
				break;
			}
			case "PURPOSE": {
				tradeLicense.setStatus("PURPOSE");
				break;
			}
			case "LANDSCHEDULE": {
				tradeLicense.setStatus("LANDSCHEDULE");
				break;
			}
			case "LANDDETAILS": {
				tradeLicense.setStatus("LANDDETAILS");
				break;
			}
			case "FEESANDCHARGES": {
				tradeLicense.setStatus("FEESANDCHARGES");
				break;
			}
			case "PAID": {
				tradeLicense.setStatus("PAID");
				break;
			}
				
			}
			
			// tradeLicense.getLicenseNumber();
			tradeLicense.setLicenseType(TradeLicense.LicenseTypeEnum.PERMANENT);
			tradeLicense.setTenantId(newServiceInfo.getRequestInfo().getUserInfo().getTenantId());
			if(newServiceInfo.getLicenseDetails().getApplicantPurpose()!=null)
			tradeLicense.setTradeName(newServiceInfo.getLicenseDetails().getApplicantPurpose().getPurpose());
		
			tradeLicense.setAccountId(newServiceInfo.getRequestInfo().getUserInfo().getUuid());

//			tradeLicense.setValidFrom();
//			tradeLicense.setValidTo();
//			tradeLicense.setWfDocuments();
			tradeLicense.setWorkflowCode("NewTL");

			tradeLicense.setTradeLicenseDetail(tradeLicenseDetail);
			tradeLicenseDetail.setId(String.valueOf(newServiceInfo.getId()));
			tradeLicenseDetail.getAdditionalDetail();
			tradeLicenseDetail.getApplicationDocuments();
			tradeLicenseDetail.getChannel();
			tradeLicenseDetail.getOwners();
			tradeLicenseDetail.getVerificationDocuments();
			tradeLicenseDetail.setTradeType("NewTL");
			tradeLicenseDetail.setCurrentVersion(newServiceIn.getCurrentVersion());

			String data = mapper.writeValueAsString(newServiceInfoDatas);
			JsonNode jsonNode = mapper.readTree(data);
			tradeLicenseDetail.setAdditionalDetail(jsonNode);

			request.addLicensesItem(tradeLicense);
			List<TradeLicense> tradelicenses = tradeLicenseService.create(request, TLConstants.businessService_TL);
			// request.getLicenses().clear();
			request.setLicenses(tradelicenses);
			// tradeLicenseService.update(request, TLConstants.businessService_TL);
			newServiceIn.setApplicationNumber(tradelicenses.get(0).getApplicationNumber());
			newServiceIn.setApplicationStatus(tradeLicense.getStatus());
			//objLicenseServiceRequestInfo.setApplication_Status();
		}
		// newServiceIn = newServiceInfoRepo.save(newServiceIn);
		try {
			BeanUtils.copyProperties(objLicenseServiceRequestInfo, newServiceIn);
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		objLicenseServiceRequestInfo.setBusinessService(TLConstants.businessService_TL);
		return objLicenseServiceRequestInfo;
	}

	private String genrateTransactionNUmber(User user) {
		Map<String, Object> authtoken = new HashMap<String, Object>();
		Map<String, Object> mapTNum = new HashMap<String, Object>();

		authtoken.put("UserId", user.getId());
		authtoken.put("TpUserId", user.getId());
		authtoken.put("EmailId", user.getEmailId());

		mapTNum.put("UserLoginId", user.getId());
		mapTNum.put("TpUserId", user.getId());
		mapTNum.put("EmailId", user.getEmailId());
		mapTNum.put("MobNo", user.getMobileNumber());

		String transactionNumber;

		transactionNumber = thirPartyAPiCall.generateTransactionNumber(mapTNum, authtoken).getBody().get("Value")
				.toString();
		log.info("TransactionNumber\t" + transactionNumber);
		return transactionNumber;
	}

	public LicenseServiceResponseInfo getNewServicesInfoById1(String applicationNumber,RequestInfo info) {
		LicenseServiceResponseInfo licenseServiceResponseInfo = new LicenseServiceResponseInfo();
		LicenseServiceDao newServiceInfo = newServiceInfoRepo.getOne(id);
		System.out.println("new service info size : " + newServiceInfo.getNewServiceInfoData().size());
		for (int i = 0; i < newServiceInfo.getNewServiceInfoData().size(); i++) {
			if (newServiceInfo.getCurrentVersion() == newServiceInfo.getNewServiceInfoData().get(i).getVer()) {
				newServiceInfo.setNewServiceInfoData(Arrays.asList(newServiceInfo.getNewServiceInfoData().get(i)));
			}
		}
		try {
			BeanUtils.copyProperties(licenseServiceResponseInfo, newServiceInfo);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return licenseServiceResponseInfo;
	}

	public LicenseServiceResponseInfo getNewServicesInfoById(String applicationNumber,RequestInfo info) {
		LicenseServiceResponseInfo licenseServiceResponseInfo = new LicenseServiceResponseInfo();
		// LicenseServiceDao newServiceInfo = new
		// LicenseServiceDao();//newServiceInfoRepo.findByAppNumber(applicationNumber);

		TradeLicenseSearchCriteria tradeLicenseRequest = new TradeLicenseSearchCriteria();
		tradeLicenseRequest.setApplicationNumber(applicationNumber);

		List<LicenseDetails> newServiceInfoData = null;
		List<LicenseDetails> licenseDetails = new ArrayList<LicenseDetails>();
		List<TradeLicense> tradeLicenses = tradeLicenseService.getLicensesWithOwnerInfo(tradeLicenseRequest, info);		
		licenseServiceResponseInfo.setWorkFlowCode(tradeLicenses.get(0).getWorkflowCode());
		for (TradeLicense tradeLicense : tradeLicenses) {

			ObjectReader reader = mapper.readerFor(new TypeReference<List<LicenseDetails>>() {
			});

			Map<String, Object> authtoken = new HashMap<String, Object>();
			authtoken.put("UserId", "39");
			authtoken.put("TpUserId", "12356");
			authtoken.put("EmailId", "mkthakur84@gmail.com");

			// List<LicenseDetails> newServiceInfoData = null;
			try {
				newServiceInfoData = reader.readValue(tradeLicense.getTradeLicenseDetail().getAdditionalDetail());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// LicenseServiceDao newServiceIn = em.find(LicenseServiceDao.class,
			// applicationNumber);

			// newServiceInfoData = newServiceIn.getNewServiceInfoData();

			for (LicenseDetails newobj : newServiceInfoData) {

				if (newobj.getVer() == tradeLicense.getTradeLicenseDetail().getCurrentVersion()) {
					licenseDetails.add(newobj);
					licenseServiceResponseInfo.setNewServiceInfoData(licenseDetails);
					licenseServiceResponseInfo.setApplicationStatus(tradeLicense.getStatus());
					licenseServiceResponseInfo.setApplicationNumber(applicationNumber);
					licenseServiceResponseInfo.setBusinessService(tradeLicense.getBusinessService());
					licenseServiceResponseInfo.setCaseNumber(tradeLicense.getTcpCaseNumber());
					licenseServiceResponseInfo.setDairyNumber(tradeLicense.getTcpDairyNumber());

					break;
					// licenseServiceResponseInfo.setNewServiceInfoData(newServiceInfoData);
				}
			}
		}

		return licenseServiceResponseInfo;
	}

	public List<LicenseServiceDao> getNewServicesInfoAll() {
		return newServiceInfoRepo.findAll();
	}

	public List<String> getApplicantsNumber() {
		return this.newServiceInfoRepo.getApplicantsNumber();
	}

	public ResponseEntity<Object> postTransactionDeatil(MultiValueMap<String, String> requestParam) {
		
		String applicationNumber = requestParam.get(new String("Applicationnumber")).get(0);
		String grn=requestParam.get(new String("GRN")).get(0);
		String status=requestParam.get(new String("status")).get(0);
		String CIN=requestParam.get(new String("CIN")).get(0);
		String tdate=requestParam.get(new String("tdate")).get(0);
		String paymentType=requestParam.get(new String("payment_type")).get(0);
		String bankcode=requestParam.get(new String("bankcode")).get(0);
		String amount=requestParam.get(new String("amount")).get(0);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
		LocalDateTime localDateTime = LocalDateTime.now();
		String date = formatter.format(localDateTime);
		String dairyNumber;
		String caseNumber;
		String applicationNmber;
		String saveTransaction;
		String returnURL="";
		RequestInfo info= new RequestInfo();
		if(!status.isEmpty()&& status.equalsIgnoreCase("Failure"))
		{
			
		}else if(!status.isEmpty()&& status.equalsIgnoreCase("Success"))
		{
			TradeLicenseSearchCriteria tradeLicenseRequest = new TradeLicenseSearchCriteria();
			tradeLicenseRequest.setApplicationNumber(applicationNumber);
			List<TradeLicense> tradeLicenses = tradeLicenseService.getLicensesWithOwnerInfo(tradeLicenseRequest, info);
			for (TradeLicense tradeLicense : tradeLicenses) {

				ObjectReader reader = mapper.readerFor(new TypeReference<List<LicenseDetails>>() {
				});

				Map<String, Object> authtoken = new HashMap<String, Object>();
				authtoken.put("UserId", "39");
				authtoken.put("TpUserId", "12356");
				authtoken.put("EmailId", "mkthakur84@gmail.com");

				List<LicenseDetails> newServiceInfoData = null;
				try {
					newServiceInfoData = reader.readValue(tradeLicense.getTradeLicenseDetail().getAdditionalDetail());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// LicenseServiceDao newServiceIn = em.find(LicenseServiceDao.class,
				// applicationNumber);

				// newServiceInfoData = newServiceIn.getNewServiceInfoData();

				for (LicenseDetails newobj : newServiceInfoData) {

					if (newobj.getVer() == tradeLicense.getTradeLicenseDetail().getCurrentVersion()) {

						/****************
						 * Dairy Number End Here
						 ***********/
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

						Map<String, Object> mapDNo = new HashMap<String, Object>();

						mapDNo.put("Village", newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getRevenueEstate());
						mapDNo.put("DiaryDate", date);
						mapDNo.put("ReceivedFrom", "");
						mapDNo.put("UserId", "1234");
						mapDNo.put("DistrictCode", newobj.getApplicantPurpose().getDistrict());
						mapDNo.put("UserLoginId", "39");
						dairyNumber = thirPartyAPiCall.generateDiaryNumber(mapDNo, authtoken).getBody().get("Value")
								.toString();
						tradeLicense.setTcpDairyNumber(dairyNumber);

						/****************
						 * End Here
						 ***********/
						// case number
						Map<String, Object> mapCNO = new HashMap<String, Object>();
						mapCNO.put("DiaryNo", dairyNumber);
						mapCNO.put("DiaryDate", date);
						mapCNO.put("DeveloperId", 2);
						mapCNO.put("PurposeId", purposeId);
						mapCNO.put("StartDate", date);
						mapCNO.put("DistrictCode", newobj.getApplicantPurpose().getDistrict());
						mapCNO.put("Village", newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getRevenueEstate());
						mapCNO.put("ChallanAmount", newobj.getFeesAndCharges().getPayableNow());
						mapCNO.put("UserId", "2");
						mapCNO.put("UserLoginId", "39");
						caseNumber = thirPartyAPiCall.generateCaseNumber(mapCNO, authtoken).getBody().get("Value")
								.toString();
						tradeLicense.setTcpCaseNumber(caseNumber);

						/****************
						 * End Here
						 ***********/
						// application number
						Map<String, Object> mapANo = new HashMap<String, Object>();
						mapANo.put("DiaryNo", dairyNumber);
						mapANo.put("DiaryDate", date);
						mapANo.put("TotalArea", newobj.getFeesAndCharges().getTotalArea());
						mapANo.put("Village", newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getRevenueEstate());
						mapANo.put("PurposeId", purposeId);
						mapANo.put("NameofOwner", 12.5);
						mapANo.put("DateOfHearing", date);
						mapANo.put("DateForFilingOfReply", date);
						mapANo.put("UserId", "2");
						mapANo.put("UserLoginId", "39");
						applicationNmber = thirPartyAPiCall.generateApplicationNumber(mapANo, authtoken).getBody()
								.get("Value").toString();
						tradeLicense.setTcpApplicationNumber(applicationNumber);

						/****************
						 * End Here
						 ***********/
						/****************
						 * starttransaction data
						 ********/
						Map<String, Object> map3 = new HashMap<String, Object>();
						map3.put("UserName", "tcp");
						map3.put("EmailId", "mkthakur84@gmail.com");
						map3.put("MobNo", "1234567891");
						map3.put("TxnNo", grn);
						map3.put("TxnAmount", newobj.getFeesAndCharges().getPayableNow());
						map3.put("NameofOwner", newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getLandOwner());
						map3.put("LicenceFeeNla", newobj.getFeesAndCharges().getLicenseFee());
						map3.put("ScrutinyFeeNla", newobj.getFeesAndCharges().getScrutinyFee());
						map3.put("UserId", "39");
						map3.put("UserLoginId", "39");
						map3.put("TpUserId", "12356");
						// TODO Renu to Add these two vaues
						map3.put("PaymentMode", paymentType);
						map3.put("PayAgreegator", bankcode);

						map3.put("LcApplicantName", "tcp");
						map3.put("LcPurpose", newobj.getApplicantPurpose().getPurpose());
						// to do select development plan
						map3.put("LcDevelopmentPlan", newobj.getApplicantPurpose().getPotential());
						map3.put("LcDistrict", newobj.getApplicantPurpose().getDistrict());
						saveTransaction = thirPartyAPiCall.saveTransactionData(map3, authtoken).getBody().get("Value")
								.toString();
						tradeLicense.setTcpSaveTransactionNumber(saveTransaction);

						/****************
						 * End Here
						 ***********/
						tradeLicense.setAction("PAID");
						tradeLicense.setWorkflowCode("NewTL");
						tradeLicense.setAssignee(Arrays.asList("f9b7acaf-c1fb-4df2-ac10-83b55238a724"));

						TradeLicenseRequest tradeLicenseRequests = new TradeLicenseRequest();

						tradeLicenseRequests.addLicensesItem(tradeLicense);
						tradeLicenseRequests.setRequestInfo(info);
						tradeLicenseService.update(tradeLicenseRequests, "TL");
						try {
							String payment = rest.postForObject(config.getPgHost().concat(config.getPgPath()), requestParam,
									String.class);
							log.info("responses" + payment);
						} catch (Exception e) {

						}

						break;

					}
				}
			}

		}
		else if(!status.isEmpty()&& status.equalsIgnoreCase("Error"))
		{
			
		}
			
		MultiValueMap<String, String> params = UriComponentsBuilder.fromUriString(returnURL).build().getQueryParams();

		 HttpHeaders httpHeaders = new HttpHeaders();
		
			
		StringBuilder redirectURL = new StringBuilder();
        redirectURL.append(returnURL);
    
        httpHeaders.setLocation(UriComponentsBuilder.fromHttpUrl(redirectURL.toString())
                .queryParams(requestParam).build().encode().toUri());
		return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);

	}

	

	public LicenseServiceDao findNewServicesInfoById(String applicationNumber) {

		LicenseServiceDao newServiceInfo = newServiceInfoRepo.getOne(id);
		System.out.println("new service info size : " + newServiceInfo.getNewServiceInfoData().size());
		for (int i = 0; i < newServiceInfo.getNewServiceInfoData().size(); i++) {
			if (newServiceInfo.getCurrentVersion() == newServiceInfo.getNewServiceInfoData().get(i).getVer()) {
				newServiceInfo.setNewServiceInfoData(Arrays.asList(newServiceInfo.getNewServiceInfoData().get(i)));
			}
		}
		return newServiceInfo;
	}

	public LicenseServiceDao findByLoiNumber(String loiNumber) {
		return this.newServiceInfoRepo.findByLoiNumber(loiNumber);
	}

	public boolean existsByLoiNumber(String loiNumber) {
		return this.newServiceInfoRepo.existsByLoiNumber(loiNumber);
	}

	public boolean existsByApplicationNumber(String applicationNumber) {
		return this.newServiceInfoRepo.existsByApplicationNumber(applicationNumber);
	}
}